package android.goal.explorer.analysis;

import android.goal.explorer.analysis.value.Constants;
import android.goal.explorer.analysis.value.analysis.ArgumentValueAnalysis;
import android.goal.explorer.analysis.value.managers.ArgumentValueManager;
import android.goal.explorer.cmdline.GlobalConfig;
import android.goal.explorer.data.android.AndroidClass;


import java.util.*;
import java.util.stream.Collectors;

import android.goal.explorer.model.App;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

import org.pmw.tinylog.Logger;

import soot.IntType;
import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.*;
import soot.jimple.infoflow.util.SystemClassHandler;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.toolkits.callgraph.Edge;
import soot.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;


import static android.goal.explorer.analysis.AnalysisUtils.getIntValue;


public class FragmentChangeAnalysis{
    //add comments to explain that it is for control flow analysis
    //Where to put BriefUnitGraph ?
    private SootMethod sootMethod;
    private GlobalConfig config;
	private LocalDefs localDefs;
    private Set<List<Edge>> edges;
    private int depth;
    private List<Unit> transactions;
    private Map<Value, List<Unit>> transactionsToCommit;
    private static Map<SootClass, Map<Integer, SootClass>> adapterToFragmentPages = new HashMap<>();
    private static Map<SootClass, SootClass> viewPagerToAdapters = new HashMap<>(); //for now, we assume one to one mapping
    // TODO deal with multiple view pagers (or reuse I guess)
    private static String TAG = "ScreenBuilder: FragmentAnalysis";

    //map from method to it's transactions and visited status
    //or  unique encoding for each state (obly works for recursion not duplication, different paths leading to same node ie)

    //TODO custom map with max elements getCount, then access with a default element for anything not in the map

    private static String appPackageName;

    public FragmentChangeAnalysis(GlobalConfig config, SootMethod sootMethod, Set<List<Edge>> edges, int depth){
        this.config = config;
        this.sootMethod = sootMethod;
	    this.localDefs = new SimpleLocalDefs(new BriefUnitGraph(sootMethod.retrieveActiveBody()));
        this.edges = edges;
        this.depth = depth;
        this.transactions = new ArrayList<>();
        this.transactionsToCommit = new HashMap<>();
    }

    public FragmentChangeAnalysis(GlobalConfig config, SootMethod sootMethod, Set<List<Edge>> edges){
        this(config, sootMethod, edges, 1);
    }

    public void setAppPackageName(String appPackageName){
        this.appPackageName = appPackageName;
    }

    
    /**
     * Calculates the chain of fragment transactions that are possibly applied when callback is executed
     * @return transaction chain
     */
    public List<List<Unit>> calculateFragmentTransactions(){
        Logger.debug("Starting fragment change analysis on {}", sootMethod);
        //Iterate through all the units inside the method
        //Need to keep a map of found values for fragment transactions, and add to transactions on remove
        for(Unit unit: sootMethod.getActiveBody().getUnits()){
            processStatement(unit);
        }
        Logger.debug("Done with fragment change analysis on {}", sootMethod);
        List<List<Unit>> fragmentTransactions = new ArrayList<>();
        fragmentTransactions.add(transactions);
        return fragmentTransactions;    
    }

    public boolean hasFoundTransactions(){
        return !this.transactions.isEmpty();
    }
   
    public void processStatement( Unit unit){
        //Check the statement in the current control flow and process accordingly
        //Logger.warn("Inside flow through for {}", unit);
        //Logger.debug("Flow through for {}", unit);
        if (isBeginTransaction(unit))
            processBeginTransaction(unit); //declares start for a chain of transactions, think about how to get rid of this
        else if (isFragmentTransaction(unit))
            processFragmentTransaction( unit); //declares a transaction, should I separate the dialog case ?
        else if (isShowDialogFragmentTransaction(unit))
            processDialogFragmentTransaction(unit);
        else if (isCommitTransaction(unit))
            processCommitTransaction(unit); //executes transactions of a chain
        else if( isSetAdapterTransaction(unit))
            processSetAdapterTransaction(unit);
        else if (isSetCurrentItemTransaction(unit))
            processSetCurrentItemForAdapter(unit);
        //ordering matters here, only process other function calls if there are no matches
        //analyze possible state changes from called function
        else if (isMethodCall(unit))
            processMethodCall(unit);
        //Logger.warn("Done with flow through for {} ", unit);

    }

    /**
     * Looks for stmt similar to
     * $r3 = virtualinvoke $r2.<android.support.v4.app.FragmentManager: android.support.v4.app.FragmentTransaction beginTransaction()>()
     * @param stmt
     * @return
     */
    private boolean isBeginTransaction(Unit stmt){
        return isDefinitionStmt(stmt) && invokesMethod(stmt, FragmentOperationsUtils.BEGIN_TRANSACTION_METHODS);
    }

    /**
     * Looks for stmt similar to
     * $r3 = virtualinvoke $r3.<android.support.v4.app.FragmentTransaction: android.support.v4.app.FragmentTransaction replace(int,android.support.v4.app.Fragment)>(2131165396, $r6);
     * @param stmt
     * @return
     */
    private boolean isFragmentTransaction(Unit stmt){
        return  (isAddTransaction(stmt) || isReplaceTransaction(stmt) || isRemoveTransaction(stmt));
    }

    private boolean isAddTransaction(Unit stmt){
        return invokesMethod(stmt, FragmentOperationsUtils.ADD_FRAGMENT_METHODS);
    }

    private boolean isShowTransaction(Unit stmt){
        return isAddTransaction(stmt) && invokesMethod(stmt, "show");
    }


    private boolean isReplaceTransaction(Unit stmt){
        return invokesMethod(stmt, FragmentOperationsUtils.REPLACE_FRAGMENT_METHODS);
    }

    private boolean isRemoveTransaction(Unit stmt){
        return invokesMethod(stmt, FragmentOperationsUtils.REMOVE_FRAGMENT_METHODS);
    }

    private boolean isShowDialogFragmentTransaction(Unit stmt){
        return invokesMethod(stmt, FragmentOperationsUtils.SHOW_DIALOG_FRAGMENT_METHODS);
        //probably put in one function
    }


    /**
     * Looks for stmt similar to virtualinvoke $r3.<android.support.v4.app.FragmentTransaction: void commitNow()>();
     * @param stmt
     * @return
     */
    private boolean isCommitTransaction(Unit stmt){
        return invokesMethod(stmt, FragmentOperationsUtils.COMMIT_TRANSACTION_METHODS);
    }

    private boolean isSetAdapterTransaction(Unit stmt){
        return invokesMethod(stmt, FragmentOperationsUtils.SET_ADAPTER_METHODS) ;//for now assume it's the right method?
    }

    private boolean isSetCurrentItemTransaction(Unit stmt) {
        if(!invokesMethod(stmt, "setCurrentItem"))
            return false;
        InvokeExpr expr = ((Stmt)stmt).getInvokeExpr();
        if(expr instanceof InstanceInvokeExpr){
            InstanceInvokeExpr  iExpr = (InstanceInvokeExpr) expr;
            if(iExpr.getBase().getType() instanceof RefType) {
                SootClass pagerClass = ((RefType) iExpr.getBase().getType()).getSootClass();
                //Value caller = iExpr.getBase().getType().getSoot;
                if (viewPagerToAdapters.containsKey(pagerClass))
                    return true;
                return false;
            }
        }
        return invokesMethod(stmt, FragmentOperationsUtils.SET_CURRENT_ITEM_METHODS);
    }


    /**
     * Checks if another function is being called within the analysis function
     * @param stmt
     * @return
     */
    private boolean isMethodCall(Unit stmt){
        return (stmt instanceof Stmt && ((Stmt)stmt).containsInvokeExpr());
    }


    /**
     Checks that the stmt invokes a method present in methods
    * Makes sure of such by matching with function name and making sure that the declaring class is a subclass
    * and the return type and params match making it an override or the exact function
        */
    private boolean invokesMethod(Unit stmt, Set<SootMethod> methods){
        return (stmt instanceof Stmt && ((Stmt)stmt).containsInvokeExpr() && methods.contains(((Stmt)stmt).getInvokeExpr().getMethod()));
    }

    private boolean invokesMethod(Unit stmt, String method){
        return (stmt instanceof Stmt && ((Stmt)stmt).containsInvokeExpr() && method.equals(((Stmt)stmt).getInvokeExpr().getMethod().getName()));
    }

    private boolean isDefinitionStmt(Unit stmt){
        return stmt instanceof DefinitionStmt;
    }

    private boolean isFragmentTransactionInstance(Unit stmt){
        return true;
    }

    private boolean isNotOverridenDialogFragmentInstance(Unit stmt){
        return true;
    }


    /**
     * Main work is to extract the jimple register that starts the chain of transactions
     * @param unit the stmt which start transactions
     */
    private void processBeginTransaction(Unit unit){
        Logger.debug("{} found to start fragment transactions", unit);
        // An example of the jimple instruction that starts the transaction is
        // $r3 = virtualinvoke $r2.<android.support.v4.app.FragmentManager: android.support.v4.app.FragmentTransaction beginTransaction()>()
    
        Value leftOp = ((DefinitionStmt)unit).getLeftOp();
        transactionsToCommit.put(leftOp, new ArrayList<>());
    } 

    private void processFragmentTransaction( Unit unit){
        Logger.debug("{} found to be a fragment transaction", unit);

        // Example jimple instruction
        // $r3 = virtualinvoke $r3.<android.support.v4.app.FragmentTransaction: android.support.v4.app.FragmentTransaction replace(int,android.support.v4.app.Fragment)>(2131165396, $r6);
        //Go with the invocation instead, as this is more certain
        try{
            Value transactionOp = null;
            Stmt stmt = (Stmt)unit;
            if (stmt instanceof DefinitionStmt){
                transactionOp = ((DefinitionStmt)stmt).getLeftOp();
            }
            else{//contains
                if(stmt.containsInvokeExpr()){
                    transactionOp = ((InstanceInvokeExpr)stmt.getInvokeExpr()).getBase(); //maybe swap the two
                 }
                 else {
                     Logger.error("{} does not contain any invocation", unit);
                     Logger.debug("{} does not contain any invocation", unit);
                 }
            }
            
            final Value leftOp = transactionOp;
            List<Unit> transactionList = null;
            if (!transactionsToCommit.containsKey(leftOp)){
                Logger.error("Transaction set was not started in current method, not registering {} as a declared transaction", unit); //why tho, what about interprocedural definitions ?
                //Logger.warn("Adding anyways to handle interprocedural");
                transactionList = new ArrayList<>();
            }
            else
                transactionList = transactionsToCommit.get(leftOp);
            transactionList.add(unit);
            transactionsToCommit.put(leftOp, transactionList);
            
        }
        catch(Exception e){
            Logger.error(e.toString());
            Logger.error("Could not process fragment transaction {}", unit);
        }
    }

    private void processDialogFragmentTransaction(Unit unit){//maybe merge with previous and add check for isShowDialogFragmentTransaction
        Logger.debug("{} found to execute a dialog fragment transaction", unit);
        transactions.add(unit);
    }

    private void processCommitTransaction( Unit unit){
        Logger.debug("{} found to execute fragment transactions", unit);
        // breaks down virtualinvoke $r3.<android.support.v4.app.FragmentTransaction: void commitNow()>() to $r3
            
        Value leftOp = ((InstanceInvokeExpr)((InvokeStmt)unit).getInvokeExpr()).getBase();
        if(!transactionsToCommit.containsKey(leftOp)){
            Logger.error("Transactions not started before commit found");
            Logger.debug("Transactions not started before commit found");
        }
        else{
            Logger.debug("Transactions defined by {} committed", unit);
            transactions.addAll(transactionsToCommit.get(leftOp));
            transactionsToCommit.remove(leftOp);
        }
        
    }

    private void processSetAdapterTransaction( Unit unit) {
        Logger.debug("{} found to set a fragment adapter ", unit);
        //Here check we have the right information
        //transactions.add(unit);
        //Analyze getItem for potential fragments
        Value adapter = ((Stmt)unit).getInvokeExpr().getArg(0);
        if(!(adapter.getType() instanceof RefType)) {
            Logger.error("Issue processing set adapter transaction {}. Dropping ...", unit);
            return;
        }
        RefType refType = (RefType)adapter.getType();
        SootMethod getItem = refType.getSootClass().getMethodUnsafe(AndroidClass.v().scSupportFragment+" getItem(int)");
        if(getItem == null || !getItem.isConcrete()){
            Logger.error("Get item method not found in adapter class {}", refType);
            return;
        }
        Body b = getItem.retrieveActiveBody();
        if(b == null) {
            Logger.error("Issue processing getItem method not found in adapter class {}. No body, dropping ...", refType);
            return;
        }
        Map<Integer, SootClass> fragmentMap = adapterToFragmentPages.get(refType.getSootClass());
        if(fragmentMap != null)
            return;
        else fragmentMap = new HashMap<>();
        //TODO here switch for register
        for(Unit item: b.getUnits()) {
            if(item instanceof ReturnStmt){
                ReturnStmt rStmt = (ReturnStmt) item;
                Value op = rStmt.getOp();
                Set<SootClass> possibleFragmentTypes = calculatePossibleTypes(op, rStmt)
                        .stream()
                        .map(type -> ((RefType)type).getSootClass())
                        .collect(Collectors.toSet());
                if(possibleFragmentTypes != null && !possibleFragmentTypes.isEmpty()){
                    if(possibleFragmentTypes.size() > 1){
                        Logger.warn("Multiple possible matches found for fragment page {}", possibleFragmentTypes);
                    }
                    fragmentMap.put(0, possibleFragmentTypes.stream().findFirst().get());
                    break;
                }
                else {
                    Logger.debug("No fragments found for adapter {}", unit);
                }
            }
        }
        SootClass pager = ((RefType)((InstanceInvokeExpr) ((Stmt)unit).getInvokeExpr()).getBase().getType()).getSootClass();
        viewPagerToAdapters.put(pager, refType.getSootClass());
        adapterToFragmentPages.put(refType.getSootClass(), fragmentMap);
    }

    private void processSetCurrentItemForAdapter(Unit unit) {
        Logger.debug("{} found to set the current fragment item ", unit);
        //Here check we have the right information
        /*InstanceInvokeExpr expr = (InstanceInvokeExpr) ((Stmt)unit).getInvokeExpr();
        if(expr.getBase().getType() instanceof RefType){
            if(!adapterToFragmentPages.containsKey(expr.getBase().getType().get)){
                Logger.error("No corresponding registered adapter for page switch. Dropping ...");
                return;
            }
            transactions.add(unit);
        }*/
        transactions.add(unit);

    }

    private void processGetCountForAdapter(Unit unit) {
        Logger.debug("{} found to get number of fragment items ", unit);
        //Here check we have the right information
        //transactions.add(unit);
    }


    private void processMethodCall( Unit unit){
        SootMethod invokedMethod = ((Stmt)unit).getInvokeExpr().getMethod();
        if(stopProcessing(invokedMethod)){
            return;
        }

        Logger.debug("{} extends analysis to new function at depth {}", unit, depth + 1);
        Logger.debug("{}", invokedMethod.getDeclaringClass());
        //Assuming that fragment transactions are not interprocedural
        //Need some optimization to reduce calls to function or make it multi-threaded
        List<List<Unit>> transactionsWithinInvoked = new FragmentChangeAnalysis(config, invokedMethod, edges, depth + 1).calculateFragmentTransactions();
        //Logger.debug("Returned from intra-procedural analysis for {} in method {} with transactions size {}", unit, sootMethod, transactionsWithinInvoked.size());
        //Logger.debug("Returned with all the transaction states {}", transactionsWithinInvoked);

        transactionsWithinInvoked.forEach(executedTransactions -> transactions.addAll(executedTransactions));
    }

    private boolean stopProcessing(SootMethod method){
        if (depth >= 15){
            Logger.debug("Analysis depth hit, now stopping");
            return true;
        }
        return isExcludedMethod(method);
    }

    private boolean isExcludedMethod(SootMethod method){
        //TO-DO: check if not a system class
        //Add whather DUling for reachability analysis
        String className = method.getDeclaringClass().getName();
        //return any method that doesn't have the same package name as current ?
        return !className.startsWith(appPackageName) || SystemClassHandler.v().isClassInSystemPackage(className) ||  method.equals(sootMethod) //needed for recursion since the analysis is not path sensitive
         || !method.hasActiveBody(); //for now

    }


    /**
     * Calculates the different layouts that can result from the callback execution
     * @return possible layouts
     */
    public List<Map<Integer, SootClass>> calculateFragmentChanges(Map<Integer, SootClass> baseLayout, List<List<Unit>> transactionsChains){
        
        return transactionsChains.stream()/*.filter(transactionChain -> !(transactionChain.isEmpty()))*/.flatMap(transactionChain -> applyTransactions(baseLayout, transactionChain).stream()).distinct()
        .collect(Collectors.toList());
    }

    //TODO: Map each container id to an ordered set/stack instead
    // For each add operation, put on top of stack
    //Hide moves the element at the end of the stack
    //Show moves the element at the beginning of the stack
    //Only element at top of the stack is added in screen (need to make sure to store stacks in screen but only use top for equality (currently visible fragment))
    public List<Map<Integer, SootClass>> applyTransactions(Map<Integer, SootClass> initialLayout, List<Unit> transactions){
        Logger.debug("The initial layout {} and transactions {}", initialLayout, transactions);
        List<Map<Integer, SootClass>> possibleLayouts = Lists.newArrayList(initialLayout);
        for (Unit transaction: transactions) {
            Logger.debug("The transaction to apply: {}", transaction);
            if(isAddTransaction(transaction) || isReplaceTransaction(transaction))
                possibleLayouts = applyAddOrReplaceTransaction(possibleLayouts, transaction);   
            else if(isRemoveTransaction(transaction))
                possibleLayouts = applyRemoveTransaction(possibleLayouts, transaction);
            else if(isShowDialogFragmentTransaction(transaction)) //should I check instance of InstanceInvokeExpr here ?
                possibleLayouts = applyShowDialogFragmentTransaction(possibleLayouts, transaction);
            /*else if(isSetAdapterTransaction(transaction)) //we wanna
                possibleLayouts = applySetAdapterTransaction(possibleLayouts, transaction);*/
            else if(isSetCurrentItemTransaction(transaction)) //we wanna get only the relevant one I guess? should we store all the relevant fragments somewhere?"
                possibleLayouts = applySetCurrentItemTransaction(possibleLayouts, transaction);
            //else Unknow transaction type found , thrown an exception ?
            Logger.debug("New layouts after applying transaction: {}", possibleLayouts);
        };
        return possibleLayouts;   
    }

    public List<Map<Integer, SootClass>> applyAddOrReplaceTransaction(List<Map<Integer, SootClass>> baseLayouts, Unit transaction){
        List<Value> methodArgs = ((Stmt)transaction).getInvokeExpr().getArgs();
        int containerId = extractFragmentContainerId(transaction);
        // TODO currently undecided whether to do anything special for fragments added to id 0
        Logger.debug("The arguments {} {}", containerId, methodArgs);
        Value fragmentArg = (methodArgs.size() > 2 || containerId != 0)?methodArgs.get(1): methodArgs.get(0);
        
        Set<SootClass> possibleFragmentTypes = calculatePossibleTypes(fragmentArg, transaction)
                                                                            .stream()
                                                                            .map(type -> ((RefType)type).getSootClass())
                                                                            .collect(Collectors.toSet());
        Logger.debug("All the possible types for the current fragment {}", possibleFragmentTypes);
        
        return baseLayouts.stream().flatMap(layoutMap -> possibleFragmentTypes.stream().map(possibleFragment -> {
            Map<Integer, SootClass> layoutCopy = new HashMap<>(layoutMap); //this is a shallow copy//maybe replace with stream.collect ?
            layoutCopy.put(containerId, possibleFragment);
            return layoutCopy;//create a new map with the additional value
        })).distinct().collect(Collectors.toList());                                                                

        /*return possibleFragmentTypes.stream().map(possibleFragment -> 
                                                                baseLayouts.stream().flatMap(layoutMap -> 
                                                                                                        layoutMap.put(containerId, possibleFragment)).collect(Collectors.toList())).collectors(Collectors.toList());*/
    }

    public List<Map<Integer, SootClass>> applyShowTransaction(List<Map<Integer, SootClass>> baseLayouts, Unit transaction){
        //Extract the fragment from invocation args
        Value fragmentArg = ((Stmt)transaction).getInvokeExpr().getArg(0);
        // Extract possible concrete types for fragment with pointer analysis
        Set<SootClass> possibleFragmentTypes = calculatePossibleTypes(fragmentArg, transaction)
                                                                            .stream()
                                                                            .map(type -> ((RefType)type).getSootClass())
                                                                            .collect(Collectors.toSet());
        
        //For now, search for all instances of fragment and remove everything else if contained in the map 
        return baseLayouts.stream().flatMap(layoutMap -> possibleFragmentTypes.stream().map(possibleFragment -> layoutMap.entrySet().stream()
                                                                                                                        .filter(entry -> ((SootClass)entry.getValue()).equals(possibleFragment))
                                                                                                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))))
                                                                                        .distinct().collect(Collectors.toList());
                                                                                                            

    }

    public List<Map<Integer, SootClass>> applyRemoveTransaction(List<Map<Integer, SootClass>> baseLayouts, Unit transaction){
        //Extract the fragment from invocation args
        Value fragmentArg = ((Stmt)transaction).getInvokeExpr().getArg(0);
        // Extract possible concrete types for fragment with pointer analysis
        Set<SootClass> possibleFragmentTypes = calculatePossibleTypes(fragmentArg, transaction)
                                                                            .stream()
                                                                            .map(type -> ((RefType)type).getSootClass())
                                                                            .collect(Collectors.toSet());
        
        return baseLayouts.stream().flatMap(layoutMap -> possibleFragmentTypes.stream().map(possibleFragment -> layoutMap.entrySet().stream()
                                                                                                                        .filter(entry -> !((SootClass)entry.getValue()).equals(possibleFragment))
                                                                                                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))))
                                                                                        .distinct().collect(Collectors.toList());

    }

    public List<Map<Integer, SootClass>> applySetCurrentItemTransaction(List<Map<Integer, SootClass>> baseLayouts, Unit transaction) {
        //Extract the current item from invocation args
        InstanceInvokeExpr inv = (InstanceInvokeExpr) ((Stmt)transaction).getInvokeExpr();
        Value currentItem = ((Stmt)transaction).getInvokeExpr().getArg(0);
        SootClass viewPager = ((RefType)inv.getBase().getType()).getSootClass();
        Integer itemId = getIntValue(currentItem, transaction);
        if(!viewPagerToAdapters.containsKey(viewPager)){
            Logger.error("Issue while resolving fragment type for view pager {}",viewPager.getType());
            return baseLayouts;
            //System.exit(1); //should I exit here?
        }
        SootClass fragmentAdapter = viewPagerToAdapters.get(viewPager);
        if(itemId == null || !adapterToFragmentPages.get(fragmentAdapter).containsKey(itemId)) {
            Logger.error("Could not resolve current page id for adapter {}. Defaulting to 0", transaction);
            itemId = 0;
        }
        SootClass newFragment = adapterToFragmentPages.get(fragmentAdapter).get(itemId);
        Set<Integer> fragmentResIds = new HashSet<>();
        if(App.v().getFragmentByClass(newFragment) == null)
            fragmentResIds.add(0);
        else fragmentResIds.addAll(App.v().getFragmentByClass(newFragment).getResourceIds()); //TODO check for NPE

        if(fragmentResIds != null && !fragmentResIds.isEmpty()){
            return baseLayouts.stream().flatMap(layoutMap -> fragmentResIds.stream().map(resId -> {
                Map<Integer, SootClass> layoutCopy = new HashMap<>(layoutMap); //this is a shallow copy//maybe replace with stream.collect ?
                layoutCopy.put(resId, newFragment);
                return layoutCopy;
            })).distinct().collect(Collectors.toList());
        }
        return baseLayouts;

    }

    public List<Map<Integer, SootClass>> applyShowDialogFragmentTransaction(List<Map<Integer, SootClass>> baseLayouts, Unit transaction){
        int containerId = 0;
        if(transaction instanceof Stmt && ((Stmt)transaction).containsInvokeExpr()){
            InvokeExpr expr = ((InvokeExpr)((Stmt)transaction).getInvokeExpr());
            if(expr instanceof InstanceInvokeExpr){
                InstanceInvokeExpr iExpr = (InstanceInvokeExpr)expr;
                Value fragmentArg = iExpr.getBase();
                Set<SootClass> possibleFragmentTypes = calculatePossibleTypes(fragmentArg, transaction)
                                                                                .stream()
                                                                                .map(type -> ((RefType)type).getSootClass())
                                                                                .collect(Collectors.toSet());
                return baseLayouts.stream().flatMap(layoutMap -> possibleFragmentTypes.stream().map(possibleFragment -> {
                    Map<Integer, SootClass> layoutCopy = new HashMap<>(layoutMap); //this is a shallow copy
                    layoutCopy.put(containerId, possibleFragment);
                    return layoutCopy;//create a new map with the additional value
                })).distinct().collect(Collectors.toList());
            }
        }
        Logger.debug("Tried to apply a showDialog transaction but the statement was not the right type {}", transaction);
        return baseLayouts;
        
    }

    

    private int extractFragmentContainerId(Unit stmt){
        InvokeExpr transaction = ((Stmt)stmt).getInvokeExpr();
        List<Value> methodArgs = transaction.getArgs();
        int containerId = 0;
        if(methodArgs.get(0) instanceof IntConstant)
            containerId = ((IntConstant)methodArgs.get(0)).value;
        else if(methodArgs.get(0).getType() instanceof IntType || (methodArgs.get(0) instanceof JimpleLocal)){//should probably search for def, no matter the type/use Duling's implementation
            Logger.debug("The type {}", methodArgs.get(0).getType());
            if(methodArgs.get(0) instanceof JimpleLocal){
                JimpleLocal local = (JimpleLocal)methodArgs.get(0);
                Logger.debug("Type of Jimple Local {} {}", local.getType(), (local.getType() instanceof IntType));
            }
            Integer valueFromLastDef = getIntFromLastDefOf(((Local)methodArgs.get(0)), stmt);
            if (valueFromLastDef != null){
                Logger.debug("The container id {} and the returned def {}", containerId, (valueFromLastDef != null)?valueFromLastDef:"");
                containerId = valueFromLastDef;
            }
            else{
                Logger.debug("Attempting with argument value analysis");
                ArgumentValueAnalysis analysis = ArgumentValueManager.v().getArgumentValueAnalysis(Constants.DefaultArgumentTypes.Scalar.INT);
                Set<Object> possibleValues = analysis.computeVariableValues(methodArgs.get(0), (Stmt)stmt, edges);
                for (Object possibleValue : possibleValues) {
                    if (possibleValue instanceof Integer) {
                        Logger.debug("Found a possible value {}", possibleValue);
                        containerId = (Integer)possibleValue;
                        break;
                    }
                }
                //TO-DO: try fragment analysis with argument value manager as well
                Logger.debug("The obtained container id {}", containerId);
                if(containerId < 0)
                    containerId = 0; //to-remove
            }
        }        
        Logger.debug("The container id for the fragment {} and its type {}  {} {}", containerId, methodArgs.get(0), methodArgs.get(0).getClass(), methodArgs.get(0).getType());
        return containerId;
    }

	private Integer getIntFromLastDefOf(Local l, Unit stmt){
		List<Unit> defs = localDefs.getDefsOfAt(l, stmt);
		//List<Unit> defs2 = localDefs.getDefsOf(l);
		Logger.debug("The defs for the local {} with {} context", l, defs);
		if(!defs.isEmpty()){
			Unit lastDef = defs.get(defs.size() - 1);
			Value containerId = ((DefinitionStmt)lastDef).getRightOp();
			Logger.debug("The value inside the container id {} and its type {}", containerId, containerId.getClass());
			if(containerId instanceof IntConstant){
				return ((IntConstant)containerId).value;
			}
            else if(containerId instanceof StaticFieldRef){
                SootField staticField = ((StaticFieldRef)containerId).getField(); //maybe check if the name is like R.id and then look up the value in ResourceValueProvider ?
                Logger.debug("The container id tag, {} and declaration {}", staticField.getTags(), staticField.getDeclaration());
                if(!staticField.getTags().isEmpty()){
                    int finalValue = Ints.fromByteArray(staticField.getTags().get(0).getValue());
                    return finalValue;
                }
                
            }
            else if(containerId instanceof Local)
			    return getIntFromLastDefOf((Local)containerId, lastDef);
		}
		return null;
	}

    //TODO: how to map the branch for definition to the right switch stmt ?
    //Backward data analysis ?
    //not sure yet, check the statements stored in the transaction state and maybe use those to find the type instead ?

    private Set<Type> calculatePossibleTypes(Value arg, Unit stmt){ //TO-DO: check if arg instance of local
        Set<Type> possibleTypes = new HashSet<>();
        if (edges!=null && !edges.isEmpty() && config.getPointToType() == GlobalConfig.PointToType.CONTEXT){
            Logger.debug("Using point-to analysis with context and callgraph edges");
            for(List<Edge> edgeList: edges){
                Edge[] edgeContext = new Edge[edgeList.size()];
                edgeContext = edgeList.toArray(edgeContext);
                // Reveal the possible types of this fragment
                possibleTypes = TypeAnalyzer.v().getContextPointToPossibleTypes(arg, edgeContext);
            }
        }
        else {
            Logger.debug("Using directly without context for point-to");
            if(arg instanceof NullConstant)
                return possibleTypes;
            possibleTypes = TypeAnalyzer.v().getPointToPossibleTypes(arg);
            Logger.debug("All the possible fragments for {} {} {}", arg, arg.getType(), possibleTypes);
            //maybe try to see the definition of this fragment and return the type of the previous if cast, otherwise just return Fragment
            if(possibleTypes.isEmpty()){//not maybe this should be always executed, and added to set
                Set<Type> fragmentTypes = Sets.newHashSet(AndroidClass.v().scFragment.getType(), AndroidClass.v().scSupportFragment.getType()); //To-do add androidx later
                possibleTypes = TypeAnalyzer.v().getPossibleTypesByBackwardAnalysis(arg, (Stmt)stmt, fragmentTypes, localDefs);
                if(possibleTypes.isEmpty()){
                    Logger.debug("No suitable type found by backward analysis for {}", arg);
                    //check the type of fragment
                    if(TypeAnalyzer.v().extendsDefaultType(arg, fragmentTypes))
                        possibleTypes.add(arg.getType()); //default fragment class
                    else {
                        Logger.error("Incompatible type for fragment {}", arg.getType());
                        //System.exit(1); //TODO not exit
                        //throw new Exception("Incompatible type for fragment "+ arg.getType());
                    }
                }
            }
        }
        return possibleTypes;
    }


    /*private Set<Type> calculatePossibleTypes(Value arg, Unit stmt){ //TO-DO: check if arg instance of local
        Set<Type> possibleTypes = new HashSet<>();
        if (edges!=null && !edges.isEmpty() && config.getPointToType() == GlobalConfig.PointToType.CONTEXT){
            Logger.debug("Using point-to analysis with context and callgraph edges");
            for(List<Edge> edgeList: edges){
                Edge[] edgeContext = new Edge[edgeList.size()];
                edgeContext = edgeList.toArray(edgeContext);
                // Reveal the possible types of this fragment
                possibleTypes = TypeAnalyzer.v().getContextPointToPossibleTypes(arg, edgeContext);
            }
        }
        else {
            Logger.debug("Using directly without context for point-to");
            possibleTypes = TypeAnalyzer.v().getPointToPossibleTypes(arg);
            Logger.debug("All the possible fragments for {} {} {}", arg, arg.getType(), possibleTypes);
            //maybe try to see the definition of this fragment and return the type of the previous if cast, otherwise just return Fragment
            if(possibleTypes.isEmpty()){
                if(arg instanceof Local){
                    List<Unit> defs = localDefs.getDefsOfAt((Local)arg, stmt);
                    Logger.debug("The previous defs of {} {}", arg, defs);
                    if(defs.size() > 0){
                        Unit def = defs.get(defs.size() - 1);
                        if (def instanceof DefinitionStmt){
                            DefinitionStmt definitionStmt = (DefinitionStmt)def;
                            Value rightOp = definitionStmt.getRightOp();
                            if (rightOp instanceof CastExpr){
                                 possibleTypes = TypeAnalyzer.v().getPointToPossibleTypes(((CastExpr)rightOp).getOp());
                                 Logger.debug("The possible types for the cast {}", possibleTypes);
                                 List<Unit> defs2 = localDefs.getDefsOfAt(((Local)((CastExpr)rightOp).getOp()), definitionStmt);
                                 Logger.debug("The defs of the previous stmt {}", defs2);
                                 for (Unit unit: defs2){
                                     if(unit instanceof DefinitionStmt){
                                         DefinitionStmt defStmt = (DefinitionStmt)unit;
                                         if(defStmt.getRightOp() instanceof Local){
                                             Logger.debug("The possible types for the local {} {} {} at {}", defStmt.getRightOp(), defStmt.getRightOp().getType(), TypeAnalyzer.v().getPointToPossibleTypes(defStmt.getRightOp()), unit);
                                             List<Unit> defs3 = localDefs.getDefsOfAt((Local)defStmt.getRightOp(), defStmt);
                                             Logger.debug("The defs of the local {} {}", defStmt.getRightOp(), defs3);
                                             for (Unit unit2: defs3){
                                                 if (unit instanceof DefinitionStmt){
                                                     Value leftOp2 = ((DefinitionStmt)unit2).getLeftOp();
                                                     Value rightOp2 = ((DefinitionStmt)unit2).getRightOp();
                                                     Logger.debug("The type of the new defs {}", rightOp2.getType());
                                                 }
                                             }
                                         }
                                     }
                                 }
                            }
                        }
                    }

                }
               
            }
        }
        return possibleTypes;
    }*/


    static class FragmentOperationsUtils {
        //"<android.car.Car: android.car.Car createCar(android.content.Context,android.content.ServiceConnection)>"
        //TO-DO: use subsignature instead ?
        private static Set<String> BEGIN_TRANSACTION_METHODS_NAMES = Sets.newHashSet("beginTransaction","openTransaction");
        private static Set<String> ADD_FRAGMENT_METHODS_NAMES = Sets.newHashSet("add");//TO-DO: deal with show later, issue with containerId, "attach", "show");
                                                                                        //likely for show need to search the fragment with same id in the layout list and use that ?, but what if it's gone already ??
        private static Set<String> REMOVE_FRAGMENT_METHODS_NAMES = Sets.newHashSet("remove");//, "detach"); //TO-DO: deal with hilde
        private static Set<String> REPLACE_FRAGMENT_METHODS_NAMES = Sets.newHashSet("replace");
        private static Set<String> COMMIT_TRANSACTION_METHODS_NAMES = Sets.newHashSet("commit", "commitAllowingStateLoss", "commitNow", "commitNowAllowingStateLoss");
        private static Set<String> SHOW_DIALOG_FRAGMENT_METHODS_NAMES = Sets.newHashSet("show");
        private static Set<String> SET_ADAPTER_METHODS_NAMES = Sets.newHashSet("setAdapter");
        private static Set<String> SET_CURRENT_ITEM_METHODS_NAMES = Sets.newHashSet("setCurrentItem");
        private static Set<String> GET_CURRENT_ITEM_METHODS_NAMES = Sets.newHashSet("getCurrentItem");

        private static Set<SootMethod> BEGIN_TRANSACTION_METHODS = initializeMethods(AndroidClass.v().scSupportFragmentManager, BEGIN_TRANSACTION_METHODS_NAMES);
        private static Set<SootMethod> ADD_FRAGMENT_METHODS = initializeMethods(AndroidClass.v().scSupportFragmentTransaction, ADD_FRAGMENT_METHODS_NAMES);
        private static Set<SootMethod> REMOVE_FRAGMENT_METHODS = initializeMethods(AndroidClass.v().scSupportFragmentTransaction, REMOVE_FRAGMENT_METHODS_NAMES);
        private static Set<SootMethod> REPLACE_FRAGMENT_METHODS = initializeMethods(AndroidClass.v().scSupportFragmentTransaction, REPLACE_FRAGMENT_METHODS_NAMES);
        private static Set<SootMethod> COMMIT_TRANSACTION_METHODS = initializeMethods(AndroidClass.v().scSupportFragmentTransaction, COMMIT_TRANSACTION_METHODS_NAMES);
        private static Set<SootMethod> SHOW_DIALOG_FRAGMENT_METHODS = initializeMethods(AndroidClass.v().scSupportDialogFragment, SHOW_DIALOG_FRAGMENT_METHODS_NAMES);
        private static Set<SootMethod> SET_ADAPTER_METHODS = initializeMethods(AndroidClass.v().scSupportViewPager, SET_ADAPTER_METHODS_NAMES);
        private static Set<SootMethod> SET_CURRENT_ITEM_METHODS = initializeMethods(AndroidClass.v().scFragmentPagerAdapter, SET_CURRENT_ITEM_METHODS_NAMES);

        //private static Set<SootMethod> GET_CURRENT_ITEM_METHODS = initializeMethods(AndroidClass.v().scSupportFragment, GET_CURRENT_ITEM_METHODS_NAMES);

        static {
            BEGIN_TRANSACTION_METHODS.addAll(initializeMethods(AndroidClass.v().scFragmentManager, BEGIN_TRANSACTION_METHODS_NAMES));
            ADD_FRAGMENT_METHODS.addAll(initializeMethods(AndroidClass.v().scFragmentTransaction, ADD_FRAGMENT_METHODS_NAMES));
            REMOVE_FRAGMENT_METHODS.addAll(initializeMethods(AndroidClass.v().scFragmentTransaction, REMOVE_FRAGMENT_METHODS_NAMES));
            REPLACE_FRAGMENT_METHODS.addAll(initializeMethods(AndroidClass.v().scFragmentTransaction, REPLACE_FRAGMENT_METHODS_NAMES));
            COMMIT_TRANSACTION_METHODS.addAll(initializeMethods(AndroidClass.v().scFragmentTransaction, COMMIT_TRANSACTION_METHODS_NAMES));
            SHOW_DIALOG_FRAGMENT_METHODS.addAll(initializeMethods(AndroidClass.v().scDialogFragment, SHOW_DIALOG_FRAGMENT_METHODS_NAMES));
            SET_CURRENT_ITEM_METHODS.addAll(initializeMethods(AndroidClass.v().scFragmentPagerAdapter, SET_CURRENT_ITEM_METHODS_NAMES));
        }
        
        

        private static Set<SootMethod> initializeMethods(SootClass sootClass, Set<String> methodNames){
            Set<SootMethod> methods = new HashSet<>();
            if(sootClass != null){
                methods.addAll(sootClass.getMethods().stream().filter(method -> methodNames.contains(method.getName())).collect(Collectors.toSet()));
            }
            return methods;
        }
    }

    
}


