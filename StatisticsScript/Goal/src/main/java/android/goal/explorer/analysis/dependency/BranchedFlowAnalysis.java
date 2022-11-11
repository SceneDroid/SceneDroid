package android.goal.explorer.analysis.dependency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.pmw.tinylog.Logger;

import android.goal.explorer.analysis.value.AnalysisParameters;
import android.goal.explorer.analysis.value.Constants;
import android.goal.explorer.analysis.value.analysis.ArgumentValueAnalysis;
import android.goal.explorer.analysis.value.managers.ArgumentValueManager;
import android.goal.explorer.utils.InvokeExprHelper;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.ConditionExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.EqExpr;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.NeExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.graph.pdg.HashMutablePDG;

public class BranchedFlowAnalysis {
    protected UnitGraph methodGraph;
    protected SootMethod method;
    protected String methodToSwitchOver;
    protected List<Integer> lookupValues;
    protected Set<List<Edge>> contextEdges;

    public BranchedFlowAnalysis(SootMethod method){
        this.method = method;
        this.methodGraph = new BriefUnitGraph(method.retrieveActiveBody());

    }

    public BranchedFlowAnalysis(SootMethod method, String methodToSwitchOver, List<Integer> lookupValues){
        this.method = method;
        this.methodGraph = new BriefUnitGraph(method.retrieveActiveBody());
        this.methodToSwitchOver = methodToSwitchOver;
        this.lookupValues = lookupValues;
    }

    public BranchedFlowAnalysis(SootMethod method, String methodToSwitchOver, List<Integer> lookupValues, Set<List<Edge>> contextEdges){
        this.method = method;
        this.methodGraph = new BriefUnitGraph(method.retrieveActiveBody());
        this.methodToSwitchOver = methodToSwitchOver;
        this.lookupValues = lookupValues;
        this.contextEdges = contextEdges;
    }

    public Map<Integer, List<Unit>> performBranchedAnalysis(){
        //Initialize the map with each lookup value mapped to an empty list
        Map<Integer, List<Unit>> branchesMap = new HashMap<>();
        lookupValues.stream().forEach(id -> branchesMap.put(id, null));
        Value registerToSwitchOver = null;
        List<Unit> sharedUnits = new ArrayList<>();
        Iterator<Unit> iterator = method.retrieveActiveBody().getUnits().iterator();

        Logger.debug("All units : {}", method.retrieveActiveBody().getUnits());

        while(iterator.hasNext()){
            Unit unit = iterator.next();
            Stmt stmt = ((Stmt)unit);
            if (registerToSwitchOver == null){
                sharedUnits.add(unit);
                if(stmt.containsInvokeExpr()){
                    if (invokesMethodOfInterest(stmt.getInvokeExpr())){
                        Logger.debug("Found invocation of method of interest {} in {}", methodToSwitchOver, stmt);
                        DefinitionStmt definitionStmt = (DefinitionStmt)stmt; //double check
                        registerToSwitchOver = definitionStmt.getLeftOp();
                    }
                }
            }
            else if(stmt instanceof LookupSwitchStmt || stmt instanceof IfStmt){ //We check whether the current statement is a conditional and uses the stored register
                if(stmt instanceof LookupSwitchStmt){
                    Logger.debug("Found a switch statement {}", stmt);
                    handleSwitchStmt(iterator, stmt, registerToSwitchOver, sharedUnits, branchesMap);
                    
                }
                if(stmt instanceof IfStmt) {
                    Logger.debug("Found if statement {}", stmt);
                    for(Integer id: branchesMap.keySet()){
                        branchesMap.put(id, new ArrayList<>(sharedUnits));
                    }
                    handleIfStmt(iterator, stmt, registerToSwitchOver, branchesMap, new HashSet<>());
                    
                }
                return branchesMap;
            }
            else
                sharedUnits.add(unit);
           
        }
        branchesMap.replaceAll((id, units) -> sharedUnits);
        return branchesMap;
    }
        
        

    
    private void handleSwitchStmt(Iterator<Unit> iterator, Stmt stmt, Value registerToSwitchOver, List<Unit> sharedUnits, Map<Integer, List<Unit>> branchesMap){
        LookupSwitchStmt switchStmt = (LookupSwitchStmt)stmt;
        if(switchStmt.getKey().equals(registerToSwitchOver)){//do we need to handle redefinition ?
            //extract the lookup values and map
            
            for(int index = 0; index < switchStmt.getTargetCount(); index ++){
                Integer lookUp = switchStmt.getLookupValue(index);
                List<Unit> succs = new ArrayList<>(sharedUnits);
                succs.addAll(getUnitAndSuccessorsExceptBranches(switchStmt.getTarget(index), registerToSwitchOver, lookUp));
                //succs.addAll(getUnitAndSuccessorsOfUni(switchStmt.getTarget(index)));

                if(!branchesMap.containsKey(lookUp)){
                    Logger.error("Key in switch case not extracted by layout analysis {}", lookUp);
                }
                
                branchesMap.put(lookUp, succs);
            }
            //handle default case for all not handled (if target count )
            //anything that was not catched already goes to default, if backstage provided a count
            //TODO: there might be a switch in the default branch, needs handling
            branchesMap.entrySet().stream().filter(entry -> entry.getValue() == null).forEach(entry -> {
                    Logger.debug("Parsing default branch for entry {}", entry);
                    entry.setValue(new ArrayList<>(sharedUnits));
                    entry.getValue().addAll(getUnitAndSuccessorsExceptBranches(switchStmt.getDefaultTarget(), registerToSwitchOver, entry.getKey()));
            });
        }
    }

    private void handleIfStmt(Iterator<Unit> iterator, Stmt stmt, Value registerToCheck,  Map<Integer, List<Unit>> branchesMap, Set<Integer> visitedBranches){
        //handle the target
        //recursive on the else, until I don"t reach a if statement, and then add for everything that wasn"t handled already ?
        //how to keep track of what is added already and handle combination (maybe deal with that later)
        Logger.debug("Handling if stmt or fallthrough {} with visitedBranches {}", stmt, visitedBranches);
        if(stmt instanceof IfStmt){
            IfStmt ifStmt = (IfStmt)stmt;
            Value condition = ifStmt.getCondition();
            if (condition instanceof ConditionExpr){
                Value leftOp = ((ConditionExpr)condition).getOp1();
                Value rightOp = ((ConditionExpr)condition).getOp2();
               
                Integer branchId = extractBranchingId(condition, registerToCheck, stmt);
                if(branchId != null){
                    Stmt targetStmt = ifStmt.getTarget();
                    Logger.debug("Extracted branch id {}", branchId);
                    Logger.debug("Target stmt for if {}", targetStmt);
                    
                    Set<Integer> newVisitedBranches = new HashSet<>();
                    newVisitedBranches.addAll(visitedBranches);
                    if (condition instanceof EqExpr){
                        List<Unit> succs = branchesMap.get(branchId);
                        //maybe if there is a case that wasn't extracted from layout, how tho ? dead code
                        //should not be null
                        //new ArrayList<>(sharedUnits);
                        if (succs != null)
                            succs.addAll(getUnitAndSuccessorsExceptBranches(targetStmt, registerToCheck, branchId));
                        branchesMap.put(branchId, succs);
                        newVisitedBranches.add(branchId);
                    }
                    else if (condition instanceof NeExpr){
                        branchesMap.entrySet().stream().filter(entry -> !(entry.getKey().equals(branchId)))
                                                        .forEach(entry -> {
                                                            Logger.debug("Adding successors for not filtered key {}", entry.getKey());
                                                            entry.getValue().addAll(getUnitAndSuccessorsExceptBranches(targetStmt, registerToCheck, entry.getKey()));
                                                            newVisitedBranches.add(entry.getKey());
                                                            Logger.debug("Set of newly visited branches {}", newVisitedBranches);
                                                            //listRef.get().add(entry.getKey());
                                                        });
                    }
                    if(iterator.hasNext())
                        handleIfStmt(iterator, (Stmt)iterator.next(), registerToCheck, branchesMap, newVisitedBranches); 
                    return;
                }
            }
        } //fall through case
        //else of if stmt unrelated to register
        branchesMap.entrySet().stream().filter(entry -> !visitedBranches.contains(entry.getKey())).forEach(entry -> {
            if(entry.getValue() != null){
                entry.getValue().addAll(getUnitAndSuccessorsExceptBranches(stmt, registerToCheck, entry.getKey()));
            }
        });
}

    private List<Unit> getUnitAndSuccessors(Unit unit){
        List<Unit> succs = getSuccessorsOfUnit(unit);
        succs.add(0, unit);
        return succs;
    }

    private List<Unit> getUnitAndSuccessorsExceptBranches(Unit unit, Value register, Integer branchId){
        /*ArrayList<Unit>[] succs = getSuccessorsOfUnitExceptBranches(unit, register, branchId);
        List<Unit> finalSuccs = new ArrayList<>(succs[0]);
        finalSuccs.addAll(succs[1]);
        finalSuccs.add(0, unit);*/
        List<Unit> finalSuccs = new ArrayList<>();
        getSuccessorsOfUnitWithoutSwitchBranches(unit, register, branchId, finalSuccs);
        finalSuccs.add(0, unit);
        return finalSuccs;
    }

    private List<Unit> getSuccessorsOfUnit(Unit unit){
        List<Unit> successors = new ArrayList<>();
        for(Unit succ: methodGraph.getSuccsOf(unit)){
            successors.add(succ);
           successors.addAll(getSuccessorsOfUnit(succ));
        }
        return successors;
    }

    //TO-DO: track variable definitions
    public void getSuccessorsOfUnitWithoutSwitchBranches(Unit unit, Value register, Integer branchId, List<Unit> successorsSoFar){
        for(Unit succ: methodGraph.getSuccsOf(unit)){
            Logger.debug("The succ of {} {}",  unit, succ);
            //HERE SHOULD BE THE SUCCESSOR
            //if the successors branches on a different value, do not add it even
            if(branchesOnDifferentValue(branchId, register, succ)){ //uses same variable expecting different value
                Logger.debug("Successor branches on a different value");
                continue;
            }
            //Why do we need both
            //If checks the value of the register of interest
            if(checksRegisterValue(register, succ)){
                IfStmt ifStmt = ((IfStmt)((Stmt)succ));
                succ = ifStmt.getTarget(); //no need to add the branching condition, satisfied
                successorsSoFar.add(succ);
                getSuccessorsOfUnitWithoutSwitchBranches(succ, register, branchId, successorsSoFar); 
                continue;
                //break;    
            }
            //If the predecessor was a if statement
            if(unit instanceof IfStmt){
                IfStmt ifStmt = ((IfStmt)((Stmt)unit));
                if(!succ.equals(ifStmt.getTarget())){
                    Logger.debug("Found a ifstmt, following the then branch {}, {}", ifStmt, succ);
                }
               else {//ELSE branch
                    Logger.debug("Found a ifstmt, inside else branch {}, {}", ifStmt, succ);
                    List<Unit> predecessors = methodGraph.getPredsOf(succ);
                    if(/*return?*/successorsSoFar.contains(succ) && predecessors.size() > 1){ //fallout branch: you can reach it even if you don't go inside the else ?
                        Logger.debug("Found a if succeeded by its target (fallout branch), ignoring target {}", unit);
                        continue;
                    }
                }
            }
            else if(unit instanceof GotoStmt){
                GotoStmt gotoStmt = ((GotoStmt)((Stmt)unit));
                if(!succ.equals(gotoStmt.getTarget())){//impossible
                    Logger.debug("Found a goto with a targt != successor, should be impossible {}, {}", gotoStmt, succ);
                }
                else{
                    List<Unit> predecessors = methodGraph.getPredsOf(succ);
                    if(predecessors.size() > 1){ //fallout branch: you can reach it even if you don't go inside the else ?
                        //Check if there's any predecessor that is not the goto and already in the list of successors
                        Logger.debug("The successors so far {}", successorsSoFar);
                        if(successorsSoFar.contains(succ))
                            continue;
                        //can we check the index or smth
                        //if(unit.ge)
                        //If there are any predecessors that are not go-to, we assume the statement will be reached in any case
                        //edge case: the target is in another switch case (i.e default), so it needs to be added within the current
                        //vs: the target is in the fallout branch, how to differentiate ?
                        if(predecessors.stream().anyMatch(predecessor -> !(predecessor instanceof GotoStmt ) && successorsSoFar.contains(predecessor) )){
                            Logger.debug("Found a goto with predecessors {} going to a fallout stmt, ignoring target {}", predecessors, unit);
                            continue;
                        }
                        //we care about the index I guess?
                        //If we have multiple go to, we only want to append it for the last one?
                        /*if(predecessors.stream().anyMatch(predecessor -> !predecessors.equals(unit) && successorsSoFar.contains(predecessor))) {//what if it's in a different branch?
                            Logger.debug("There is a potential goto at a later point, not adding succ {}", succ);
                            continue;
                        }*/
                    }
                }
            }
            //The unit is already contained in there
            else if(successorsSoFar.contains(succ)){
                Logger.debug("Found duplicate statement {}", succ);
                //a typical unit
                //if the unit has multiple predecessors, we can just remove it and all its predecessors
                //if one of the predecessors is a goto
                int index = successorsSoFar.indexOf(succ);
                if(index > 0 && (successorsSoFar.get(index - 1) instanceof GotoStmt)){
                    //we delete the stmt after the goto as it was a fallout branch
                    //only need to delete one stmt, the next one will be handled in the following iteration
                    Logger.debug("Duplicate stmt due to goto, removing from goto {} {}", successorsSoFar.get(index -1), succ);
                    successorsSoFar.remove(index);
                }
            }
            successorsSoFar.add(succ);
            getSuccessorsOfUnitWithoutSwitchBranches(succ, register, branchId, successorsSoFar);
        }
    }


    //TO-DO: track variable definitions
    public List<Unit> getSuccessorsOfUnitWithoutSwitchBranches(Unit unit, Value register, Integer branchId){
        List<Unit> successors = new ArrayList<>();
        for(Unit succ: methodGraph.getSuccsOf(unit)){
            Logger.debug("The succ of {} {}",  unit, succ);
            //HERE SHOULD BE THE SUCCESSOR
            //if the successors branches on a different value, do not add it even
            if(branchesOnDifferentValue(branchId, register, succ)){ //uses same variable expecting different value
                Logger.debug("Successor branches on a different value");
                continue;
            }
            if(checksRegisterValue(register, succ)){
                IfStmt ifStmt = ((IfStmt)((Stmt)succ));
                succ = ifStmt.getTarget(); //no need to add the branching condition, satisfied
                successors.add(succ);
                successors.addAll(getSuccessorsOfUnitWithoutSwitchBranches(succ, register, branchId)); 
                continue;
                //break;    
            }
            if(unit instanceof IfStmt){
                IfStmt ifStmt = ((IfStmt)((Stmt)unit));
                if(!succ.equals(ifStmt.getTarget())){//impossible
                    Logger.debug("Found a ifstmt, following the then branch {}, {}", ifStmt, succ);
                }
                if(succ.equals(ifStmt.getTarget())){//ELSE branch
                    Logger.debug("Found a ifstmt, inside then branch {}, {}", ifStmt, succ);
                    List<Unit> predecessors = methodGraph.getPredsOf(succ);
                    if(/*return?*/predecessors.size() > 1){ //fallout branch: you can reach it even if you don't go inside the else ?
                        Logger.debug("Found a if succeeded by its target (fallout branch), ignoring target {}", unit);
                        continue;
                    }
                }
            }
            else if(unit instanceof GotoStmt){
                GotoStmt gotoStmt = ((GotoStmt)((Stmt)unit));
                if(!succ.equals(gotoStmt.getTarget())){//impossible
                    Logger.debug("Found a goto with a targt != successor, should be impossible {}, {}", gotoStmt, succ);
                }
                else{
                    List<Unit> predecessors = methodGraph.getPredsOf(succ);
                    if(/*return?*/predecessors.size() > 1){ //fallout branch: you can reach it even if you don't go inside the else ?
                        //Check if there's any predecessor that is not the goto and already in the list of successors
                        //Logger.debug("The successors so far {}", successorsSoFar);
                        if(successors.contains(succ))
                            continue;
                        if(predecessors.stream().anyMatch(predecessor -> !(predecessor instanceof GotoStmt ) && successors.contains(predecessor))){
                            Logger.debug("Found a goto with predecessors {} going to a fallout stmt, ignoring target {}", predecessors, unit);
                            continue;
                        }  
                    }
                }
            }
            successors.add(succ);
            //successorsSoFar.add(succ);
            successors.addAll(getSuccessorsOfUnitWithoutSwitchBranches(succ, register, branchId));    
     
            /*if(!branchesOnDifferentValue(branchId, register, unit)){ //only uses the same or unrelated variables
                //Logger.debug("Does not branch of a different value from {} {}", branchId, unit);
                //only consider the target if uses register
                if(checksRegisterValue(register, unit)){
                    Logger.debug("If stmt checking the register value");
                    IfStmt ifStmt = ((IfStmt)((Stmt)unit));
                    succ = ifStmt.getTarget();//no need to add the branching condition, satisfied
                    successors.add(succ);
                    successors.addAll(getSuccessorsOfUnitWithoutSwitchBranches(succ, register, branchId)); 
                    break; //ignore the else
                }
                else if(unit instanceof IfStmt){
                    IfStmt ifStmt = ((IfStmt)((Stmt)unit));
                    if(!succ.equals(ifStmt.getTarget())){//impossible
                        Logger.debug("Found a ifstmt, following the then branch {}, {}", ifStmt, succ);
                    }
                    if(succ.equals(ifStmt.getTarget())){//ELSE branch
                        Logger.debug("Found a ifstmt, inside then branch {}, {}", ifStmt, succ);
                        List<Unit> predecessors = methodGraph.getPredsOf(succ);
                        if(predecessors.size() > 1){ //fallout branch: you can reach it even if you don't go inside the else ?
                            Logger.debug("Found a if succeeded by its target (fallout branch), ignoring target {}", unit);
                            continue;
                        }
                    }
                }
                else if(unit instanceof GotoStmt){
                    GotoStmt gotoStmt = ((GotoStmt)((Stmt)unit));
                    if(!succ.equals(gotoStmt.getTarget())){//impossible
                        Logger.debug("Found a goto with a targt != successor, should be impossible {}, {}", gotoStmt, succ);
                    }
                    else{
                        List<Unit> predecessors = methodGraph.getPredsOf(succ);
                        if(predecessors.size() > 1){ //fallout branch: you can reach it even if you don't go inside the else ?
                            //Check if there's any predecessor that is not the goto and already in the list of successors
                            if(successors.contains(succ))
                                continue;
                            if(predecessors.stream().anyMatch(predecessor -> !(predecessor instanceof GotoStmt ) && successors.contains(predecessor))){
                                Logger.debug("Found a goto with predecessors {} going to a fallout stmt, ignoring target {}", predecessors, unit);
                                continue;
                            }  
                        }
                    }
                }
                successors.add(succ);
                successors.addAll(getSuccessorsOfUnitWithoutSwitchBranches(succ, register, branchId));  
            }else{
                Logger.debug("Branches on different value");
            }*/
        }
     return successors;
    }


    public ArrayList<Unit>[] getSuccessorsOfUnitExceptBranches(Unit unit, Value register, Integer branchId){
        ArrayList<Unit> successorsThen = new ArrayList<>(), successorsElse = new ArrayList<>();
        for(Unit succ: methodGraph.getSuccsOf(unit)){
            Logger.debug("The succ {} {}", succ, unit);
            if(!branchesOnDifferentValue(branchId, register, unit)){ //only uses the same or unrelated variables
                //Logger.debug("Does not branch of a different value from {} {}", branchId, unit);
                //only consider the target if uses register
                if(checksRegisterValue(register, unit)){
                    //Logger.debug("If stmt checking the register value");
                    IfStmt ifStmt = ((IfStmt)((Stmt)unit));
                    succ = ifStmt.getTarget();
                }
                else if(unit instanceof IfStmt){
                    IfStmt ifStmt = ((IfStmt)((Stmt)unit));
                    if(!succ.equals(ifStmt.getTarget())){//then branch
                        successorsThen.add(succ);
                        successorsThen.addAll(getSuccessorsOfUnitExceptBranches(succ, register, branchId)[0]);
                    }
                    else{ //else branch
                        List<Unit> predecessors = methodGraph.getPredsOf(succ);
                        Logger.debug("Predecessor {} of {}, might be fallout", methodGraph.getPredsOf(succ), succ);
                        if(/*return?*/predecessors.size() > 1){ //fallout branch: you can reach it even if you don't go inside the else ?
                            Logger.debug("Found a if succeeded by its target, ignoring target {}", unit);
                            continue;
                        }
                        successorsElse.add(succ);
                        successorsElse.addAll(getSuccessorsOfUnitExceptBranches(succ, register, branchId)[0]);
                    }
                }
                else if(unit instanceof GotoStmt){
                    GotoStmt gotoStmt = ((GotoStmt)((Stmt)unit));
                    if(!succ.equals(gotoStmt.getTarget())){//impossible
                        successorsThen.add(succ);
                        successorsThen.addAll(getSuccessorsOfUnitExceptBranches(succ, register, branchId)[0]);
                    }
                    else{ //else branch
                        List<Unit> predecessors = methodGraph.getPredsOf(succ);
                        Logger.debug("Predecessor {} of {}, might be fallout", methodGraph.getPredsOf(succ), succ);
                        if(/*return?*/predecessors.size() > 1){ //fallout branch: you can reach it even if you don't go inside the else ?
                            Logger.debug("Found a goto succeeded by its target, ignoring target {}", unit);
                            continue;
                        }
                        successorsElse.add(succ);
                        successorsElse.addAll(getSuccessorsOfUnitExceptBranches(succ, register, branchId)[0]);
                    }
                }
                /*else if(isGoToBeforeFallout(unit, succ)){
                     //We assume if the stmt to go-to directly succedes the go-to,
                    //Then it was already added by another block which jumps to it
                    // (Since the currently blocks reaches the stmt by natural execution)
                    //Then we'll skip the successor
                    Logger.debug("Found a goto succeeded by its target, ignoring target {}", unit);
                    continue;
                }*/
                else{ //
                    successorsThen.add(succ);
                    //successorsThen.addAll(getSuccessorsOfUnitExceptBranches(succ, register, branchId)[0]);
                    //what about the last else ?
                    ArrayList<Unit>[] allSuccs = getSuccessorsOfUnitExceptBranches(succ, register, branchId);
                    successorsThen.addAll(allSuccs[0]);
                    successorsElse.addAll(allSuccs[1]);
                }
            }
            Logger.debug("For unit {}, succ {}, \nthe then branch {},\n the else branch {}", unit, succ, successorsThen, successorsElse);
       
        }
        
        ArrayList<Unit>[] successors = new ArrayList[2];
        successors[1] = new ArrayList<>(successorsElse);
        successors[0] = new ArrayList<>(successorsThen);
        return successors;
    }

    private Integer extractBranchingId(Value condition, Value registerToCheck, Stmt stmt){
        Integer constant = null;
        Logger.debug("Extracting the branching id for {} and {}", condition, registerToCheck);
        if(!(condition instanceof ConditionExpr)){
            Logger.debug("Attempting to extract branching id without condition check on {}", condition);
            return null;
        }
        else {
            Value leftOp = ((ConditionExpr)condition).getOp1();
            Value rightOp = ((ConditionExpr)condition).getOp2();
            if(!(leftOp.equals(registerToCheck) || rightOp.equals(registerToCheck)))
                return null;

           
            if(leftOp instanceof IntConstant)
                constant = ((IntConstant)leftOp).value;
                
            else if (rightOp instanceof IntConstant)
                constant = ((IntConstant)rightOp).value;
                
            else if(rightOp instanceof Local){
                ArgumentValueAnalysis analysis = ArgumentValueManager.v().
                        getArgumentValueAnalysis(Constants.DefaultArgumentTypes.Scalar.INT);
                Set<Object> possibleValues = analysis.computeVariableValues(rightOp, stmt, contextEdges);
                for (Object possibleValue : possibleValues) {
                    if (possibleValue instanceof Integer) {
                        constant = (Integer)possibleValue;
                        
                    }
                }
            }
        }
        
        return constant;
    }

    private boolean isGoToBeforeFallout(Unit unit, Unit succ){
       Stmt stmt = (Stmt)unit;
       if(stmt instanceof GotoStmt){ //for now, ignore all go to stmts
            GotoStmt gotoStmt = (GotoStmt)stmt;
            Logger.debug("GotoStmt {}, the succ {}, the target {}", gotoStmt, succ, gotoStmt.getTarget());
            return !(succ instanceof ReturnStmt) && gotoStmt.getTarget().equals(succ);
       }
       if(stmt instanceof IfStmt){
           IfStmt ifStmt = (IfStmt)stmt;
           Logger.debug("IfStmt {}, the succ {}, the target {}", ifStmt, succ, ifStmt.getTarget());
            if((succ instanceof ReturnStmt) || !ifStmt.getTarget().equals(succ)){//we're not in the else branch ?
                return false;
            }
            //we're in the else branch
            List<Unit> predecessors = methodGraph.getPredsOf(succ);
            Logger.debug("Predecessor {} of {}, might be fallout", methodGraph.getPredsOf(succ), succ);
            if(predecessors.size() > 0){ //fallout branch: you can reach it even if you don't go inside the else ?
                return true;
            }
            return false;
            //return !(succ instanceof ReturnStmt) && ifStmt.getTarget().equals(succ);
       }
       return false;
    }


    private boolean checksRegisterValue(Value registerToCheck, Unit unit){
        Stmt stmt = (Stmt)unit;
        if(!(stmt instanceof IfStmt)){
            return false;
        }
        IfStmt ifStmt = (IfStmt)stmt;
        Value condition = ifStmt.getCondition();
        if(!(condition instanceof ConditionExpr)){
            Logger.debug("Attempting to extract branching id without condition check on {}", condition);
            return false;
        }
        else {
            Value leftOp = ((ConditionExpr)condition).getOp1();
            Value rightOp = ((ConditionExpr)condition).getOp2();
            if(!(leftOp.equals(registerToCheck) || rightOp.equals(registerToCheck)))
                return false;
            return true;
        }
    }


    private boolean branchesOnDifferentValue(Integer branchId, Value register, Unit unit){
        if(branchId == null){
            return false;
        }
        Stmt stmt = (Stmt)unit;
        if(!(stmt instanceof IfStmt)){
            return false;
        }
        IfStmt ifStmt = (IfStmt)stmt;
        Value condition = ifStmt.getCondition();
        Integer newBranchId = extractBranchingId(condition, register, stmt);
        Logger.debug("The branching id {} {}", newBranchId, condition.getType());
        if(newBranchId == null)
            return false;
        return (condition instanceof EqExpr && !branchId.equals(newBranchId)) || (condition instanceof NeExpr && branchId.equals(newBranchId));
    }


    private boolean invokesMethodOfInterest(InvokeExpr inv){
        String methodSig = inv.getMethod().getSubSignature();
        Logger.debug("Method sub signature {}", methodSig);
        return InvokeExprHelper.invokesGetItemId(inv);
        //return methodSig.equals(methodToSwitchOver);
    }

    /**/

    /*if (stmt.containsInvokeExpr()) {
        InvokeExpr inv = stmt.getInvokeExpr();
        if (InvokeExprHelper.invokesGetItemId(inv)){
            Logger.debug("Found get item id");
            mappingIdToAction = PDGUtils.findConditionalMapping(unit, new HashMutablePDG((UnitGraph)AnalysisParameters.v().getIcfg().getOrCreateUnitGraph(body)));
            //break;
            foundMethod = true;
        }
    }*/

}