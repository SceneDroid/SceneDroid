// package android.goal.explorer.analysis;

// import android.R.integer;
// import android.R.layout;
// import android.app.Fragment;
// import android.goal.explorer.analysis.TypeAnalyzer;
// import android.goal.explorer.analysis.value.identifiers.Argument;
// import android.goal.explorer.analysis.value.managers.ArgumentValueManager;
// import android.goal.explorer.cmdline.GlobalConfig;
// import android.goal.explorer.data.android.AndroidClass;
// import android.graphics.drawable.Drawable.Callback;
// import android.text.Layout;
// import android.util.Pair;


// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.HashMap;
// import java.util.HashSet;
// import java.util.Iterator;
// import java.util.LinkedHashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Set;
// import java.util.concurrent.atomic.AtomicInteger;
// import java.util.stream.Collectors;

// import com.beust.jcommander.internal.Maps;
// import com.google.common.collect.Lists;
// import com.google.common.collect.Sets;

// import org.pmw.tinylog.Logger;

// import afu.org.checkerframework.common.reflection.qual.Invoke;
// import soot.IntType;
// import soot.RefType;
// import soot.Scene;
// import soot.SootClass;
// import soot.SootMethod;
// import soot.Type;
// import soot.Unit;
// import soot.Value;
// import soot.ValueBox;
// import soot.jimple.DefinitionStmt;
// import soot.jimple.InstanceInvokeExpr;
// import soot.jimple.IntConstant;
// import soot.jimple.InvokeExpr;
// import soot.jimple.InvokeStmt;
// import soot.jimple.Stmt;
// import soot.jimple.infoflow.util.SystemClassHandler;
// import soot.jimple.toolkits.callgraph.Edge;
// import soot.toolkits.scalar.ArraySparseSet;
// import soot.toolkits.scalar.FlowSet;
// import soot.toolkits.scalar.ForwardFlowAnalysis;
// import soot.*;
// import soot.toolkits.graph.*;
// import soot.util.*;
// import java.util.*;

// import soot.toolkits.graph.interaction.*;
// import soot.options.*;

public class ConditionalFragmentChangeAnalysis /*extends  ForwardFlowAnalysis<Unit, FlowSet<ConditionalFragmentChangeAnalysis.TransactionState>>*/{
}

//     protected SootMethod sootMethod;
//     protected String methodToSwitchOver;
//     protected GlobalConfig config;
//     protected Set<List<Edge>> edges;
//     protected int depth;
//     private String TAG = "ScreenBuilder: ConditionalFragmentChangeAnalysis";


//     public ConditionalFragmentChangeAnalysis(GlobalConfig config, SootMethod sootMethod, String methodToSwitchOver, Set<List<Edge>> edges, int depth) {
//         super(new BriefUnitGraph(sootMethod.retrieveActiveBody()));
//         this.config = config;
//         this.sootMethod = sootMethod;
//         this.edges = edges;
//         this.depth = depth;
//         this.methodToSwitchOver = methodToSwitchOver;
//     }

//     public ConditionalFragmentChangeAnalysis(GlobalConfig config, SootMethod sootMethod, String methodToSwitchOver, Set<List<Edge>> edges) {
//         this(config, sootMethod, methodToSwitchOver, edges, 1);
//     }

//     class TransactionState {
//         Value switchCaseValue;
//         List<Unit> executed;
//         Map<Value, List<Unit>> stored;

//         public TransactionState(Value switchCaseValue, List<Unit> executed, Map<Value, List<Unit>> stored){ 
//             this.switchCaseValue = switchCaseValue;
//             this.executed = executed;
//             this.stored = stored;
//         }

//         public TransactionState(List<Unit> executed, Map<Value, List<Unit>> stored){ 
//             this(null, executed, stored);
//             }

//         @Override
//         public int hashCode() {
//             final int prime = 31;
//             int result = 1;
//             result = prime * result + ((switchCaseValue == null) ? 0 : switchCaseValue.hashCode());
//             result = prime * result + ((executed == null) ? 0 : executed.hashCode());
//             result = prime * result + ((stored == null) ? 0 : stored.hashCode());
//             return result;
//         }

//         @Override
//         public boolean equals(Object obj) {
//             if (this == obj)
//                 return true;
//             if (obj == null)
//                 return false;
//             if (getClass() != obj.getClass())
//                 return false;

//             TransactionState other = (TransactionState) obj;
//             if (switchCaseValue == null) {
//                 if (other.switchCaseValue != null)
//                     return false;
//             }
//             else if (!switchCaseValue.equals(other.switchCaseValue))
//                 return false;
//             if (executed == null) {
//                 if (other.executed != null)
//                     return false;
//             }
//             else if (!executed.equals(other.executed))
//                 return false;
//             if (stored == null) {
//                 if (other.stored != null)
//                     return false;
//             }
//             else if (!stored.equals(other.stored)) 
//                 return false;
//             return true;
            
//         }
//     }


//     /**
//      * Returns the flow object corresponding to the initial values for each graph node
//      * @return flow for initial values
//      */
//     @Override
//     public FlowSet<TransactionState> newInitialFlow(){
//     return new ArraySparseSet<TransactionState>();
//     }

//     /**
//      * Returns the flow object corresponding to the flow for entrypoints
//      * @return flow for entry points
//      */
//     @Override
//     public FlowSet<TransactionState> entryInitialFlow(){
//     ArraySparseSet<TransactionState> initFlow = new ArraySparseSet<>();
//     //Add in a newly created transaction state to mark the state before any statements are executed
//     initFlow.add(new TransactionState(new ArrayList<>(), new HashMap<>()));
//     return initFlow;
//     }

//     /**
//      * Computes the merge of the `in1`and ìn2` sets, putting the result into `out`
//      * The behavior of this function depends on the implementation (it may be necessary to check whether `in1`and ìn2` are equal or aliased)
//      * Used by doAnalysis
//      * @param in1
//      * @param in2
//      * @param out
//      */
//     public void merge(FlowSet<TransactionState> in1, FlowSet<TransactionState> in2, FlowSet<TransactionState> out){
//     in1.union(in2, out);;
//     }

//     /**
//      * Creates a deep copy of the `source` flow object in `dest`
//      */
//     @Override
//     public void copy(FlowSet<TransactionState> source, FlowSet<TransactionState> dest){
//     source.copy(dest);
//     }


//     protected boolean isForward()
//     {
//     return true;
//     }

    
//     /**
//      * Calculates the [out] set from the given [in] set
//      * 
//      * For our analysis, the [out] set is only changed when some variation of commit or commitNow is called to execute
//      * all pending FragmentTransactions (or when we have a dialog fragment)
//      * 
//      * @param in the input flow
//      * @param unit the current node/statement
//      * @param out the returned flow
//      */
//     @Override
//     public void flowThrough(FlowSet<TransactionState> in, Unit unit, FlowSet<TransactionState> out){
//     //Check the statement in the current control flow and process accordingly
//     if (isMethodToSwitchOver(unit))
//         processMethodToSwitchOver(int, unit, out);
//     if (isBeginTransaction(unit))
//         processBeginTransaction(in, unit, out); //declares start for a chain of transactions, think about how to get rid of this
//     else if (isFragmentTransaction(unit))
//         processFragmentTransaction(in, unit, out); //declares a transaction, should I separate the dialog case ?
//     else if (isShowDialogFragmentTransaction(unit))
//         processDialogFragmentTransaction(in, unit, out);
//     else if (isCommitTransaction(unit))
//         processCommitTransaction(in, unit, out); //executes transactions of a chain
//     //ordering matters here, only process other function calls if there are no matches
//     //analyze possible state changes from called function
//     else if (isMethodCall(unit))
//         processMethodCall(in, unit, out);
//     else  //shallow copy to save space
//         in.copy(out);
//     }

//     /**
//      * Looks for stmt containing an invocation of the targeted method for the switch
//      * e.g $i0 = interfaceinvoke $r1.<android.view.MenuItem: int getItemId()>()
//      */
//     private boolean isMethodToSwitchOver(Unit stmt){
//         return invokesMethod(stmt, methodToSwitchOver);
//     }

//     /**
//      * Looks for stmt similar to
//      * $r3 = virtualinvoke $r2.<android.support.v4.app.FragmentManager: android.support.v4.app.FragmentTransaction beginTransaction()>()
//      * @param stmt
//      * @return
//      */
//     private boolean isBeginTransaction(Unit stmt){
//     return isDefinitionStmt(stmt) && invokesMethod(stmt, FragmentOperationsUtils.BEGIN_TRANSACTION_METHODS);
//     }

//     /**
//      * Looks for stmt similar to
//      * $r3 = virtualinvoke $r3.<android.support.v4.app.FragmentTransaction: android.support.v4.app.FragmentTransaction replace(int,android.support.v4.app.Fragment)>(2131165396, $r6);
//      * @param stmt
//      * @return
//      */
//     private boolean isFragmentTransaction(Unit stmt){
//     return  (isAddTransaction(stmt) || isReplaceTransaction(stmt) || isRemoveTransaction(stmt));
//     }

//     private boolean isAddTransaction(Unit stmt){
//     return invokesMethod(stmt, FragmentOperationsUtils.ADD_FRAGMENT_METHODS);
//     }

//     private boolean isShowTransaction(Unit stmt){
//     return isAddTransaction(stmt) && invokesMethod(stmt, "show");
//     }


//     private boolean isReplaceTransaction(Unit stmt){
//     return invokesMethod(stmt, FragmentOperationsUtils.REPLACE_FRAGMENT_METHODS);
//     }

//     private boolean isRemoveTransaction(Unit stmt){
//     return invokesMethod(stmt, FragmentOperationsUtils.REMOVE_FRAGMENT_METHODS);
//     }

//     private boolean isShowDialogFragmentTransaction(Unit stmt){
//     return invokesMethod(stmt, FragmentOperationsUtils.SHOW_DIALOG_FRAGMENT_METHODS);
//     //probably put in one function
//     }


//     /**
//      * Looks for stmt similar to virtualinvoke $r3.<android.support.v4.app.FragmentTransaction: void commitNow()>();
//      * @param stmt
//      * @return
//      */
//     private boolean isCommitTransaction(Unit stmt){
//     return invokesMethod(stmt, FragmentOperationsUtils.COMMIT_TRANSACTION_METHODS);
//     }

//     /**
//      * Checks if another function is being called within the analysis function
//      * @param stmt
//      * @return
//      */
//     private boolean isMethodCall(Unit stmt){
//     return (stmt instanceof Stmt && ((Stmt)stmt).containsInvokeExpr());
//     }


//     /**
//      Checks that the stmt invokes a method present in methods
//     * Makes sure of such by matching with function name and making sure that the declaring class is a subclass
//     * and the return type and params match making it an override or the exact function
//     */
//     private boolean invokesMethod(Unit stmt, Set<SootMethod> methods){
//     return (stmt instanceof Stmt && ((Stmt)stmt).containsInvokeExpr() && methods.contains(((Stmt)stmt).getInvokeExpr().getMethod()));
//     }

//     private boolean invokesMethod(Unit stmt, String method){
//     return (stmt instanceof Stmt && ((Stmt)stmt).containsInvokeExpr() && method.equals(((Stmt)stmt).getInvokeExpr().getMethod().getName()));
//     }

//     private boolean isDefinitionStmt(Unit stmt){
//     return stmt instanceof DefinitionStmt;
//     }

//     private boolean isFragmentTransactionInstance(Unit stmt){
//     return true;
//     /*if(stmt instance Stmt){
//         Stmt stmt = (Stmt)stmt;
//         if(stmt.containsInvokeExpr()){
//             InvokeExpr expr = 
//         }
//     }
//     return (stmt instance of Stmt && ((Stmt)stmt).containsInvokeExpr() && ()*/
//     }

//     private boolean isNotOverridenDialogFragmentInstance(Unit stmt){
//     return true;
//     }

//     //Steps
//     //We find the invocation of the method of interested
//     //We store the register
//     //We track for 
//         //either if condition with equality check on register
//         //switch stmt with register as key, lookupValues are the switchValueCases and 
//         //or we store the entire switch stmt and 

    /**
     * Note, the case values are only available in the switch stmt
     * SO parse lookupswitchstmt with key is register, to extract lookupValues and targets
     * Need to map those ids to transaction states ? 
     * Store the mapping from lookup to targets
     * Then go through the following stmt, if the stmt is contained in one of the targets (starting from the top as order is probably the same), then add lookup to state and remove this particular case
     * 
     */

//     /**
//      * Main work is to extract the jimple register that stores the method of interest
//      * We search for a switch stmt 
//      */
//     private void processMethodToSwitchOver(FlowSet<TransactionState> in, Unit unit, FlowSet<TransactionState> out) {

//     }


//     /**
//      * Main work is to extract the jimple register that starts the chain of transactions
//      * @param in state before executing the stmt
//      * @param unit the stmt which start transactions
//      * @param out state after executing the stmt
//      */
//     private void processBeginTransaction(FlowSet<TransactionState> in, Unit unit, FlowSet<TransactionState> out){
//     Logger.debug("{} found to start fragment transactions with flow set {}", unit, in);
//     // An example of the jimple instruction that starts the transaction is
//     // $r3 = virtualinvoke $r2.<android.support.v4.app.FragmentManager: android.support.v4.app.FragmentTransaction beginTransaction()>()

//     Value leftOp = ((DefinitionStmt)unit).getLeftOp();
//     in.forEach(transactionState -> {
//         if (transactionState.stored.containsKey(leftOp)) {
//             Logger.warn("Another set of transactions have already been started, discarding pending");
//         }
//         Map<Value, List<Unit>> updatedStored = new HashMap(transactionState.stored);
//         updatedStored.put(leftOp, new ArrayList<>());
//         out.add(new TransactionState(transactionState.executed, updatedStored));
//     });
//     } 

//     private void processFragmentTransaction(FlowSet<TransactionState> in, Unit unit, FlowSet<TransactionState> out){
//     Logger.debug("{} found to be a fragment transaction with flow set {}", unit, in);

//     // Example jimple instruction
//     // $r3 = virtualinvoke $r3.<android.support.v4.app.FragmentTransaction: android.support.v4.app.FragmentTransaction replace(int,android.support.v4.app.Fragment)>(2131165396, $r6);
//     //Go with the invocation instead, as this is more certain
//     try{
//         Value transactionOp = null;
//         Stmt stmt = (Stmt)unit;
//         if (stmt instanceof DefinitionStmt){
//             transactionOp = ((DefinitionStmt)stmt).getLeftOp();
//         }
//         else{//contains
//             if(stmt.containsInvokeExpr()){
//                 transactionOp = ((InstanceInvokeExpr)stmt.getInvokeExpr()).getBase(); //maybe swap the two
//                 }
//                 else {
//                     Logger.error("{} does not contain any invocation", unit);
//                     Logger.debug("{} does not contain any invocation", unit);
//                 }
//         }
        
//         final Value leftOp = transactionOp;
//         in.forEach(transactionState -> {
//             Map<Value, List<Unit>> updatedStored = new HashMap<>(transactionState.stored); //should I clone this ?
//             List<Unit> transactionList = null;
//             if(!transactionState.stored.containsKey(leftOp)){
//                 Logger.error("Transaction set was not started in current method, not registering {} as a declared transaction", unit); //why tho, what about interprocedural definitions ?
//                 Logger.warn("Adding anyways to handle interprocedural");
//                 transactionList = new ArrayList<>();
//             }
//             else{
//                 transactionList = updatedStored.get(leftOp);
//             }
//             transactionList.add(unit);
//             updatedStored.put(leftOp, transactionList);
//             out.add(new TransactionState(transactionState.executed, updatedStored));
            
//         });
//     }
//     catch(Exception e){
//         Logger.error(e.toString());
//         Logger.error("Could not process fragment transaction {}", unit);
//     }
//     }

//     private void processDialogFragmentTransaction(FlowSet<TransactionState> in, Unit unit, FlowSet<TransactionState> out){//maybe merge with previous and add check for isShowDialogFragmentTransaction
//     Logger.debug("{} found to execute a dialog fragment transaction", unit);
//     in.forEach(transactionState -> {
//         List<Unit> updatedExecuted = new ArrayList<>(transactionState.executed); //to clone
//         updatedExecuted.add(unit);
//         out.add(new TransactionState(updatedExecuted, transactionState.stored));
//     });
//     }

//     private void processCommitTransaction(FlowSet<TransactionState> in, Unit unit, FlowSet<TransactionState> out){
//     Logger.debug("{} found to execute fragment transactions with flow set {}", unit, in);
//     // breaks down virtualinvoke $r3.<android.support.v4.app.FragmentTransaction: void commitNow()>() to $r3
        
//     Value leftOp = ((InstanceInvokeExpr)((InvokeStmt)unit).getInvokeExpr()).getBase();
//     in.forEach(transactionState -> {
//         if(!transactionState.stored.containsKey(leftOp)){
//             Logger.error("Transactions not started before commit found");
//             Logger.debug("Transactions not started before commit found");
//         }
//         else{
//             Logger.debug("Transactions defined by {} committed", unit);
//             List<Unit> updatedExecuted = new ArrayList<>(transactionState.executed);
//             updatedExecuted.addAll(transactionState.stored.get(leftOp));
//             Map<Value, List<Unit>> updatedStored = new HashMap<>(transactionState.stored);
//             updatedStored.remove(leftOp);
//             out.add(new TransactionState(updatedExecuted, updatedStored));
//         }
//     });
//     }

//     private void processMethodCall(FlowSet<TransactionState> in, Unit unit, FlowSet<TransactionState> out){
//     SootMethod invokedMethod = ((Stmt)unit).getInvokeExpr().getMethod();

//     if(stopProcessing(invokedMethod)){
//         in.copy(out);
//         return;
//     }

//     Logger.debug("{} extends analysis to new function at depth {}", unit, depth + 1);
//     Logger.debug("{}", invokedMethod.getDeclaringClass());
//     //Assuming that fragment transactions are not interprocedural
//     //Need some optimization to reduce calls to function or make it multi-threaded
//     List<List<Unit>> transactionsWithinInvoked = new FragmentChangeAnalyzer(config, invokedMethod, edges, depth + 1).calculateFragmentTransactions();
//     Logger.debug("Returned from intra-procedural analysis for {} in method {} with transactions size {} and in and out {} {}", unit, sootMethod, transactionsWithinInvoked.size(), in.size(), out.size());
//     Logger.debug("Returned with all the transaction states {}", transactionsWithinInvoked);

//     AtomicInteger indexIn = new AtomicInteger();

//     in.forEach(transactionState -> {
//         Logger.warn("Merging the transactions states for transactionState {} at index {}", transactionState, indexIn.getAndIncrement());
//         AtomicInteger index = new AtomicInteger();
//         transactionsWithinInvoked.forEach(executedTransactions -> {
//             Logger.warn("Merging the execution state for the new transactions  at index {}", index.getAndIncrement());
//             //if(!executedTransactions.isEmpty())
//             // Logger.debug("Merging the execution state for the new transactions at index {}", index.getAndIncrement());
//             List<Unit> updatedExecuted = new ArrayList<>(transactionState.executed); //how to parallelize this and keep the order ?
//             updatedExecuted.addAll(executedTransactions);
//             out.add(new TransactionState(updatedExecuted, transactionState.stored));
//         });
//     });
//     }

//     private boolean stopProcessing(SootMethod method){
//     if (depth >= 15){
//         Logger.debug("Analysis depth hit, now stopping");
//         return true;
//     }
//     return isExcludedMethod(method);
//     }

//     private boolean isExcludedMethod(SootMethod method){
//     //TO-DO: check if not a system class
//     //Add whather DUling for reachability analysis
//     String className = method.getDeclaringClass().getName();
//     return SystemClassHandler.isClassInSystemPackage(className) //||  method.equals(sootMethod)
//         || !method.hasActiveBody(); //for now

//     }


//     /**
//      * Calculates the chain of fragment transactions that are possibly applied when callback is executed
//      * @return transaction chain
//      */
//     public List<List<Unit>> calculateFragmentTransactions(){
//     Logger.debug("Starting fragment change analysis on {}", sootMethod);
//     doAnalysis();
//     Logger.debug("Done with fragment change analysis on {}", sootMethod);
//     //For each exit point in the method graph, associate the executed statements ?
//     //Logger.debug("States after the last unit {}, {}", getLastUnit(), getFlowAfter(getLastUnit()));
//     return graph.getTails().stream().flatMap(
//         //Find the transaction at the end of the control flow
//         //Return the list of executed fragment transactions
//         unit -> getFlowAfter(unit).toList().stream() //not sure about unit
//                                             .map( transactionState -> transactionState.executed) //too verbose, to rewrite --also not sure about unit
//     ).distinct().collect(Collectors.toList()); //need a distinct
//     }

//     private Unit getLastUnit(){
//     Unit last = null;
//     for(Unit unit: graph){
//         last = unit;
//     }
//     return last;
//     }

//     /**
//      * Calculates the different layouts that can result from the callback execution
//      * @return possible layouts
//      */
//     public List<Map<Integer, SootClass>> calculateFragmentChanges(Map<Integer, SootClass> baseLayout, List<List<Unit>> transactionsChains){

//     return transactionsChains.stream()/*.filter(transactionChain -> !(transactionChain.isEmpty()))*/.flatMap(transactionChain -> applyTransactions(baseLayout, transactionChain).stream()).distinct()
//     .collect(Collectors.toList());
//     }

//     //TO-DO: Map each container id to an ordered set/stack instead
//     // For each add operation, put on top of stack
//     //Hide moves the element at the end of the stack
//     //Show moves the element at the beginning of the stack
//     //Only element at top of the stack is added in screen (need to make sure to store stacks in screen but only use top for equality (currently visible fragment))
//     public List<Map<Integer, SootClass>> applyTransactions(Map<Integer, SootClass> initialLayout, List<Unit> transactions){
//     Logger.debug("The initial layout {} and transactions {}", initialLayout, transactions);
//     List<Map<Integer, SootClass>> possibleLayouts = Lists.newArrayList(initialLayout);
//     for (Unit transaction: transactions) {
//         Logger.debug("The transaction to apply: {}", transaction);
//         if(isAddTransaction(transaction) || isReplaceTransaction(transaction))
//             possibleLayouts = applyAddOrReplaceTransaction(possibleLayouts, transaction);   
//         else if(isRemoveTransaction(transaction))
//             possibleLayouts = applyRemoveTransaction(possibleLayouts, transaction);
//         else if(isShowDialogFragmentTransaction(transaction)) //should I check instance of InstanceInvokeExpr here ?
//             possibleLayouts = applyShowDialogFragmentTransaction(possibleLayouts, transaction);
//         //else Unknow transaction type found , thrown an exception ?
//         Logger.debug("New layouts after applying transaction: {}", possibleLayouts);
//     };
//     return possibleLayouts;   
//     }

//     public List<Map<Integer, SootClass>> applyAddOrReplaceTransaction(List<Map<Integer, SootClass>> baseLayouts, Unit transaction){
//     List<Value> methodArgs = ((Stmt)transaction).getInvokeExpr().getArgs();
//     int containerId = extractFragmentContainerId(transaction);
//     // TODO currently undecided whether to do anything special for fragments added to id 0
//     Value fragmentArg = (containerId == 0)?methodArgs.get(0): methodArgs.get(1);
//     Set<SootClass> possibleFragmentTypes = calculatePossibleTypes(fragmentArg)
//                                                                         .stream()
//                                                                         .map(type -> ((RefType)type).getSootClass())
//                                                                         .collect(Collectors.toSet());
//     Logger.debug("All the possible types for the current fragment {}", possibleFragmentTypes);

//     return baseLayouts.stream().flatMap(layoutMap -> possibleFragmentTypes.stream().map(possibleFragment -> {
//         Map<Integer, SootClass> layoutCopy = new HashMap<>(layoutMap); //this is a shallow copy//maybe replace with stream.collect ?
//         layoutCopy.put(containerId, possibleFragment);
//         return layoutCopy;//create a new map with the additional value
//     })).distinct().collect(Collectors.toList());                                                                

//     /*return possibleFragmentTypes.stream().map(possibleFragment -> 
//                                                             baseLayouts.stream().flatMap(layoutMap -> 
//                                                                                                     layoutMap.put(containerId, possibleFragment)).collect(Collectors.toList())).collectors(Collectors.toList());*/
//     }

//     public List<Map<Integer, SootClass>> applyShowTransaction(List<Map<Integer, SootClass>> baseLayouts, Unit transaction){
//     //Extract the fragment from invocation args
//     Value fragmentArg = ((Stmt)transaction).getInvokeExpr().getArg(0);
//     // Extract possible concrete types for fragment with pointer analysis
//     Set<SootClass> possibleFragmentTypes = calculatePossibleTypes(fragmentArg)
//                                                                         .stream()
//                                                                         .map(type -> ((RefType)type).getSootClass())
//                                                                         .collect(Collectors.toSet());

//     //For now, search for all instances of fragment and remove everything else if contained in the map 
//     return baseLayouts.stream().flatMap(layoutMap -> possibleFragmentTypes.stream().map(possibleFragment -> layoutMap.entrySet().stream()
//                                                                                                                     .filter(entry -> ((SootClass)entry.getValue()).equals(possibleFragment))
//                                                                                                                     .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))))
//                                                                                     .distinct().collect(Collectors.toList());
                                                                                                        

//     }

//     public List<Map<Integer, SootClass>> applyRemoveTransaction(List<Map<Integer, SootClass>> baseLayouts, Unit transaction){
//     //Extract the fragment from invocation args
//     Value fragmentArg = ((Stmt)transaction).getInvokeExpr().getArg(0);
//     // Extract possible concrete types for fragment with pointer analysis
//     Set<SootClass> possibleFragmentTypes = calculatePossibleTypes(fragmentArg)
//                                                                         .stream()
//                                                                         .map(type -> ((RefType)type).getSootClass())
//                                                                         .collect(Collectors.toSet());

//     return baseLayouts.stream().flatMap(layoutMap -> possibleFragmentTypes.stream().map(possibleFragment -> layoutMap.entrySet().stream()
//                                                                                                                     .filter(entry -> !((SootClass)entry.getValue()).equals(possibleFragment))
//                                                                                                                     .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))))
//                                                                                     .distinct().collect(Collectors.toList());
                                                                                                        

//     }

//     public List<Map<Integer, SootClass>> applyShowDialogFragmentTransaction(List<Map<Integer, SootClass>> baseLayouts, Unit transaction){
//     int containerId = 0;
//     if(transaction instanceof Stmt && ((Stmt)transaction).containsInvokeExpr()){
//         InvokeExpr expr = ((InvokeExpr)((Stmt)transaction).getInvokeExpr());
//         if(expr instanceof InstanceInvokeExpr){
//             InstanceInvokeExpr iExpr = (InstanceInvokeExpr)expr;
//             Value fragmentArg = iExpr.getBase();
//             Set<SootClass> possibleFragmentTypes = calculatePossibleTypes(fragmentArg)
//                                                                             .stream()
//                                                                             .map(type -> ((RefType)type).getSootClass())
//                                                                             .collect(Collectors.toSet());
//             return baseLayouts.stream().flatMap(layoutMap -> possibleFragmentTypes.stream().map(possibleFragment -> {
//                 Map<Integer, SootClass> layoutCopy = new HashMap<>(layoutMap); //this is a shallow copy
//                 layoutCopy.put(containerId, possibleFragment);
//                 return layoutCopy;//create a new map with the additional value
//             })).distinct().collect(Collectors.toList());
//         }
//     }
//     Logger.debug("Tried to apply a showDialog transaction but the statement was not the right type {}", transaction);
//     return baseLayouts;

//     }



//     private int extractFragmentContainerId(Unit stmt){
//     InvokeExpr transaction = ((Stmt)stmt).getInvokeExpr();
//     List<Value> methodArgs = transaction.getArgs();
//     int containerId = (methodArgs.get(0) instanceof IntConstant) ?
//                             ((IntConstant)methodArgs.get(0)).value: // e.g add(R.id.fragmentContainer, Fragment, TAG)
//                             0;
//     return containerId;
//     }

//     private Set<Type> calculatePossibleTypes(Value arg){ //TO-DO: check if arg instance of local
//     Set<Type> possibleTypes = new HashSet<>();
//     if (edges!=null && !edges.isEmpty() && config.getPointToType() == GlobalConfig.PointToType.CONTEXT){
//         Logger.debug("Using point-to analysis with context and callgraph edges");
//         for(List<Edge> edgeList: edges){
//             Edge[] edgeContext = new Edge[edgeList.size()];
//             edgeContext = edgeList.toArray(edgeContext);
//             // Reveal the possible types of this fragment
//             possibleTypes = TypeAnalyzer.v().getContextPointToPossibleTypes(arg, edgeContext);
//         }
//     }
//     else {
//         Logger.debug("Using directly without context for point-to");
//         possibleTypes = TypeAnalyzer.v().getPointToPossibleTypes(arg);
//     }
//     return possibleTypes;
//     }


//     static class FragmentOperationsUtils {
//     //"<android.car.Car: android.car.Car createCar(android.content.Context,android.content.ServiceConnection)>"
//     //TO-DO: use subsignature instead ?
//     private static Set<String> BEGIN_TRANSACTION_METHODS_NAMES = Sets.newHashSet("beginTransaction","openTransaction");
//     private static Set<String> ADD_FRAGMENT_METHODS_NAMES = Sets.newHashSet("add");//TO-DO: deal with show later, issue with containerId, "attach", "show");
//                                                                                     //likely for show need to search the fragment with same id in the layout list and use that ?, but what if it's gone already ??
//     private static Set<String> REMOVE_FRAGMENT_METHODS_NAMES = Sets.newHashSet("remove");//, "detach"); //TO-DO: deal with hilde
//     private static Set<String> REPLACE_FRAGMENT_METHODS_NAMES = Sets.newHashSet("replace");
//     private static Set<String> COMMIT_TRANSACTION_METHODS_NAMES = Sets.newHashSet("commit", "commitAllowingStateLoss", "commitNow", "commitNowAllowingStateLoss");
//     private static Set<String> SHOW_DIALOG_FRAGMENT_METHODS_NAMES = Sets.newHashSet("show");


//     private static Set<SootMethod> BEGIN_TRANSACTION_METHODS = initializeMethods(AndroidClass.v().scSupportFragmentManager, BEGIN_TRANSACTION_METHODS_NAMES);
//     {
//         BEGIN_TRANSACTION_METHODS.addAll(initializeMethods(AndroidClass.v().scFragmentManager, BEGIN_TRANSACTION_METHODS_NAMES));
//     }

//     private static Set<SootMethod> ADD_FRAGMENT_METHODS = initializeMethods(AndroidClass.v().scSupportFragmentTransaction, ADD_FRAGMENT_METHODS_NAMES);
//     {
//         ADD_FRAGMENT_METHODS.addAll(initializeMethods(AndroidClass.v().scFragmentTransaction, ADD_FRAGMENT_METHODS_NAMES));
//     }

//     private static Set<SootMethod> REMOVE_FRAGMENT_METHODS = initializeMethods(AndroidClass.v().scSupportFragmentTransaction, REMOVE_FRAGMENT_METHODS_NAMES);
//     {
//         REMOVE_FRAGMENT_METHODS.addAll(initializeMethods(AndroidClass.v().scFragmentTransaction, REMOVE_FRAGMENT_METHODS_NAMES));
//     }

//     private static Set<SootMethod> REPLACE_FRAGMENT_METHODS = initializeMethods(AndroidClass.v().scSupportFragmentTransaction, REPLACE_FRAGMENT_METHODS_NAMES);
//     {
//         REPLACE_FRAGMENT_METHODS.addAll(initializeMethods(AndroidClass.v().scFragmentTransaction, REPLACE_FRAGMENT_METHODS_NAMES));
//     }

//     private static Set<SootMethod> COMMIT_TRANSACTION_METHODS = initializeMethods(AndroidClass.v().scSupportFragmentTransaction, COMMIT_TRANSACTION_METHODS_NAMES);
//     {
//         COMMIT_TRANSACTION_METHODS.addAll(initializeMethods(AndroidClass.v().scFragmentTransaction, COMMIT_TRANSACTION_METHODS_NAMES));
//     }

//     private static Set<SootMethod> SHOW_DIALOG_FRAGMENT_METHODS = initializeMethods(AndroidClass.v().scSupportDialogFragment, SHOW_DIALOG_FRAGMENT_METHODS_NAMES);
//     {
//         SHOW_DIALOG_FRAGMENT_METHODS.addAll(initializeMethods(AndroidClass.v().scDialogFragment, SHOW_DIALOG_FRAGMENT_METHODS_NAMES));
//     }



//     private static Set<SootMethod> initializeMethods(SootClass sootClass, Set<String> methodNames){
//         Set<SootMethod> methods = new HashSet<>();
//         if(sootClass != null){
//             methods.addAll(sootClass.getMethods().stream().filter(method -> methodNames.contains(method.getName())).collect(Collectors.toSet()));
//         }
//         return methods;
//     }
//     }

//     /*@Override
//     protected void doAnalysis()
//     {
//     final Map<Unit, Integer> numbers = new HashMap<Unit, Integer>();
//     List orderedUnits = new PseudoTopologicalOrderer().newList(graph,false);
//     {
//         int i = 1;
//         for( Iterator uIt = orderedUnits.iterator(); uIt.hasNext(); ) {
//             final Unit u = (Unit) uIt.next();
//             numbers.put(u, new Integer(i));
//             i++;
//         }
//     }

//     TreeSet<Unit> changedUnits = new TreeSet<Unit>( new Comparator() {
//         public int compare(Object o1, Object o2) {
//             Integer i1 = numbers.get(o1);
//             Integer i2 = numbers.get(o2);
//             return (i1.intValue() - i2.intValue());
//         }
//     } );

//     Map<Unit, ArrayList> unitToIncomingFlowSets = new HashMap<Unit, ArrayList>(graph.size() * 2 + 1, 0.7f);
//     List heads = graph.getHeads();
//     int numNodes = graph.size();
//     int numComputations = 0;
//     int maxBranchSize = 0;

//     // initialize unitToIncomingFlowSets
//     {
//         Logger.warn("Initializing the incoming flow sets");
//         Iterator it = graph.iterator();

//         while (it.hasNext())
//         {
//             Unit s = (Unit) it.next();

//             unitToIncomingFlowSets.put(s, new ArrayList());
//         }
//         Logger.warn("Done initializign the incoming flow sets");
//     }

//     // Set initial values and nodes to visit.
//     // WARNING: DO NOT HANDLE THE CASE OF THE TRAPS
//     {
//         Chain sl = ((UnitGraph)graph).getBody().getUnits();
//         Iterator it = graph.iterator();
//         Logger.warn("Setting the initial nodes to visit");

//         while(it.hasNext())
//         {
//             Unit s = (Unit) it.next();

//             changedUnits.add(s);

//             unitToBeforeFlow.put(s, newInitialFlow());

//             if (s.fallsThrough())
//             {
//                 ArrayList<FlowSet<FragmentChangeAnalyzer.TransactionState>> fl = new ArrayList<A>();

//                 fl.add((newInitialFlow()));
//                 unitToAfterFallFlow.put(s, fl);

//         Unit succ=(Unit) sl.getSuccOf(s);
//         // it's possible for someone to insert some (dead) 
//         // fall through code at the very end of a method body
//         if(succ!=null) {
//         List<Object> l = (unitToIncomingFlowSets.get(sl.getSuccOf(s)));
//         l.addAll(fl);
//         }
//             }
//             else
//                 unitToAfterFallFlow.put(s, new ArrayList<FlowSet<FragmentChangeAnalyzer.TransactionState>>());

//             if (s.branches())
//             {
//                 ArrayList<FlowSet<FragmentChangeAnalyzer.TransactionState>> l = new ArrayList<FlowSet<FragmentChangeAnalyzer.TransactionState>>();
//                 List<FlowSet<FragmentChangeAnalyzer.TransactionState>> incList;
//                 Iterator boxIt = s.getUnitBoxes().iterator();

//                 while (boxIt.hasNext())
//                 {
//                     FlowSet<FragmentChangeAnalyzer.TransactionState> f = (newInitialFlow());

//                     l.add(f);
//                     Unit ss = ((UnitBox) (boxIt.next())).getUnit();
//                     incList = (unitToIncomingFlowSets.get(ss));
                                        
//                     incList.add(f);
//                 }
//                 unitToAfterBranchFlow.put(s, l);
//             }
//             else
//                 unitToAfterBranchFlow.put(s, new ArrayList<FlowSet<FragmentChangeAnalyzer.TransactionState>>());
//             if (s.getUnitBoxes().size() > maxBranchSize)
//                 maxBranchSize = s.getUnitBoxes().size();
//         }
//         Logger.warn("Done setting the initial nodes to visit");
//     }

//     // Feng Qian: March 07, 2002
//     // init entry points
//     {
//         Iterator<Unit> it = heads.iterator();
//         Logger.warn("Setting the entry points");

//         while (it.hasNext()) {
//             Unit s = it.next();
//             // this is a forward flow analysis
//             unitToBeforeFlow.put(s, entryInitialFlow());
//         }
//         Logger.warn("Done setting the entry pointss");
//     }

//     if (treatTrapHandlersAsEntries())
//     {
//         Logger.warn("Looking for trap units ");
//         Iterator trapIt = ((UnitGraph)graph).getBody().
//                                 getTraps().iterator();
//         while(trapIt.hasNext()) {
//             Trap trap = (Trap) trapIt.next();
//             Unit handler = trap.getHandlerUnit();
//             unitToBeforeFlow.put(handler, entryInitialFlow());
//         }
//         Logger.warn("Done with the trap unit");
//     }

//     // Perform fixed point flow analysis
//     {
//         List<Object> previousAfterFlows = new ArrayList<Object>(); 
//         List<Object> afterFlows = new ArrayList<Object>();
//         FlowSet<FragmentChangeAnalyzer.TransactionState>[] flowRepositories = (FlowSet<FragmentChangeAnalyzer.TransactionState>[]) new Object[maxBranchSize+1];
//         for (int i = 0; i < maxBranchSize+1; i++)
//             flowRepositories[i] = newInitialFlow();
//             FlowSet<FragmentChangeAnalyzer.TransactionState>[] previousFlowRepositories = (FlowSet<FragmentChangeAnalyzer.TransactionState>[])new Object[maxBranchSize+1];
//         for (int i = 0; i < maxBranchSize+1; i++)
//             previousFlowRepositories[i] = newInitialFlow();

//         while(!changedUnits.isEmpty())
//         {
//             Logger.warn("Waiting to reach a fix point");
//             FlowSet<FragmentChangeAnalyzer.TransactionState> beforeFlow;

//             Unit s = changedUnits.first();
//             changedUnits.remove(s);
//             boolean isHead = heads.contains(s);

//             accumulateAfterFlowSets(s, previousFlowRepositories, previousAfterFlows);

//             // Compute and store beforeFlow
//             {
//                 List<FlowSet<FragmentChangeAnalyzer.TransactionState>> preds = unitToIncomingFlowSets.get(s);

//                 beforeFlow = unitToBeforeFlow.get(s);

//                 if(preds.size() == 1)
//                     copy(preds.get(0), beforeFlow);
//                 else if(preds.size() != 0)
//                 {
//                     Iterator<FlowSet<FragmentChangeAnalyzer.TransactionState>> predIt = preds.iterator();

//                     copy(predIt.next(), beforeFlow);

//                     while(predIt.hasNext())
//                     {
//                         Logger.warn("Looking for predecessor flows");
//                         FlowSet<FragmentChangeAnalyzer.TransactionState> otherBranchFlow = predIt.next();
//                         FlowSet<FragmentChangeAnalyzer.TransactionState> newBeforeFlow = newInitialFlow();
//                         merge(s, beforeFlow, otherBranchFlow, newBeforeFlow);
//                         copy(newBeforeFlow, beforeFlow);
//                     }
//                     Logger.warn("Done collecting predecessors flows");

//                 }

//                 if(isHead && preds.size() != 0)
//                     mergeInto(s, beforeFlow, entryInitialFlow());
//             }

//             // Compute afterFlow and store it.
//             {
//                 ArrayList<FlowSet<FragmentChangeAnalyzer.TransactionState>> afterFallFlow = unitToAfterFallFlow.get(s);
//                 ArrayList<FlowSet<FragmentChangeAnalyzer.TransactionState>> afterBranchFlow = unitToAfterBranchFlow.get(s);
//                 if (Options.v().interactive_mode()){
//                     FlowSet<FragmentChangeAnalyzer.TransactionState> savedFlow = newInitialFlow();
//                     copy(beforeFlow, savedFlow);
//                     FlowInfo fi = new FlowInfo(savedFlow, s, true);
//                     if (InteractionHandler.v().getStopUnitList() != null && InteractionHandler.v().getStopUnitList().contains(s)){
//                         InteractionHandler.v().handleStopAtNodeEvent(s);
//                     }
//                     InteractionHandler.v().handleBeforeAnalysisEvent(fi);
//                 }
//                 Logger.warn("Before calling flow through");
//                 flowThrough(beforeFlow, s, (List) afterFallFlow, (List) afterBranchFlow);
//                 Logger.warn("After calling flow through");
//                 if (Options.v().interactive_mode()){
//                     ArrayList l = new ArrayList();
//                     if (!((List)afterFallFlow).isEmpty()){
//                         l.addAll((List)afterFallFlow);
//                     }
//                     if (!((List)afterBranchFlow).isEmpty()){
//                         l.addAll((List)afterBranchFlow);
//                     }
                    
//                     //if (s instanceof soot.jimple.IfStmt){
//                     //    l.addAll((List)afterFallFlow);
//                     //    l.addAll((List)afterBranchFlow);
//                     //}
//                     //else {
//                         l.addAll((List)afterFallFlow);
//                     }////
//                     FlowInfo fi = new FlowInfo(l, s, false);
//                     InteractionHandler.v().handleAfterAnalysisEvent(fi);
//                 }
//                 numComputations++;
//             }

//             accumulateAfterFlowSets(s, flowRepositories, afterFlows);

//             // Update queue appropriately
//             if(!afterFlows.equals(previousAfterFlows))
//             {
//                 Iterator succIt = graph.getSuccsOf(s).iterator();

//                 while(succIt.hasNext())
//                 {
//                     Logger.warn("Looking for predecessors");
//                     Unit succ = (Unit) succIt.next();
                        
//                     changedUnits.add(succ);
//                 }
//                 Logger.warn("Done finding successors");
//             }
//         }
//         Logger.debug("Done with the fix point analysis");
//     }

//     // G.v().out.println(graph.getBody().getMethod().getSignature() + " numNodes: " + numNodes + 
//     //    " numComputations: " + numComputations + " avg: " + Main.truncatedOf((double) numComputations / numNodes, 2));

//     Timers.v().totalFlowNodes += numNodes;
//     Timers.v().totalFlowComputations += numComputations;

//     } // end doAnalysis

//     */


// }



