/*
 * Copyright (C) 2015 The Pennsylvania State University and the University of Wisconsin
 * Systems and Internet Infrastructure Security Laboratory
 *
 * Author: Damien Octeau
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package android.goal.explorer.analysis.value.analysis;

import android.goal.explorer.analysis.value.identifiers.Argument;
import android.goal.explorer.analysis.value.managers.ArgumentValueManager;

import android.goal.explorer.analysis.value.AnalysisParameters;
import android.goal.explorer.analysis.value.analysis.strings.ConstraintCollector;
import android.goal.explorer.analysis.value.analysis.strings.LanguageConstraints;
import android.goal.explorer.analysis.value.analysis.strings.RecursiveDAGSolverVisitorLC;
import android.goal.explorer.analysis.value.managers.ArgumentValueManager;
import android.goal.explorer.analysis.value.managers.MethodReturnValueManager;
import android.goal.explorer.analysis.value.values.propagation.PropagationConstants;
import android.goal.explorer.data.value.ResourceValueProvider;

import soot.Local;
import soot.Value;
import soot.jimple.FieldRef;
import soot.jimple.NullConstant;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.FieldRef;
import soot.jimple.LongConstant;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.Edge;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.jimple.infoflow.util.SystemClassHandler;

import org.pmw.tinylog.Logger;

import static android.goal.explorer.analysis.AnalysisUtils.extractIntArgumentFrom;
/**
 * An argument value analysis for string types.
 */
public class StringValueAnalysis extends BackwardValueAnalysis {
    private static final String TOP_VALUE = PropagationConstants.ANY_STRING;

    /**
     * Initializes the string argument value analysis. This should be called before using the
     * analysis.
     */
    public static void initialize() {
        // CCExprVisitor.verbose_level = 20;
        // CCVisitor.verbose_level = 20;
        // DBG.verbose_level = 20;
        ConstraintCollector.globalCollection(new ConstraintCollector.CCModelInterface() {
            public boolean isExcludedClass(String class_name) {
                return false;
            }
        });
    }

    @Override
    public Set<Object> computeInlineArgumentValues(String[] inlineValues) {
        return new HashSet<Object>(Arrays.asList(inlineValues));
    }

    /**
     * Returns the string values of a variable used at a given statement.
     *
     * @param value The value or variable that should be determined.
     * @param stmt  The statement that uses the variable whose values should be determined.
     * @return The set of possible values.
     */
    @Override
    public Set<Object> computeVariableValues(Value value, Stmt stmt) {
        Logger.debug("Computing string value for {} in {}", value, stmt);
        if (value instanceof StringConstant) {
            return Collections.singleton((Object) ((StringConstant) value).value.intern());
        } else if (value instanceof NullConstant) {
            return Collections.singleton((Object) "<NULL>");
        } else if (value instanceof Local) {
            Local local = (Local) value;

            //Here need to add, if the method is in package and it's getText
            //extract the int value of the argument
            //then check in resource value provider

            ConstraintCollector constraintCollector =
                    new ConstraintCollector(new ExceptionalUnitGraph(AnalysisParameters.v().getIcfg()
                            .getMethodOf(stmt).getActiveBody()));
            LanguageConstraints.Box lcb = constraintCollector.getConstraintOfAt(local, stmt);
            RecursiveDAGSolverVisitorLC dagvlc =
                    new RecursiveDAGSolverVisitorLC(5, null,
                            new RecursiveDAGSolverVisitorLC.MethodReturnValueAnalysisInterface() {
                                @Override
                                public Set<Object> getMethodReturnValues(LanguageConstraints.Call call) {
                                    Logger.debug("Using the string constraint solver");
                                    return MethodReturnValueManager.v().getMethodReturnValues(call);
                                }
                            });

            if (dagvlc.solve(lcb)) {
                // boolean flag = false;
                // if (dagvlc.result.size() == 0 || flag == true) {
                // System.out.println("ID: " + lcb.uid);
                // // int dbg = 10;
                // // while (dbg == 10) {
                // System.out.println("Returning " + dagvlc.result);
                // System.out.println("Returning.. " + lcb);
                // dagvlc.solve(lcb);
                // System.out.println("done");
                // // }
                // }
                // System.out.println("Returning " + dagvlc.result);
                return new HashSet<Object>(dagvlc.getResult());
            } else {
                return Collections.singleton((Object) TOP_VALUE);
            }
        } else {
            return Collections.singleton((Object) TOP_VALUE);
        }
    }

    @Override
    public Set<Object> computeVariableValues(Value value, Stmt callSite, Set<List<Edge>> edges) {
        // Not implemented
        Logger.debug("Computing string value for {} in {}", value, callSite);
        if (value instanceof StringConstant) {
            return Collections.singleton((Object) ((StringConstant) value).value.intern());
        } else if (value instanceof NullConstant) {
            return Collections.singleton((Object) "<NULL>");
        } else if (value instanceof FieldRef){

        } 
        else if (value instanceof Local) {
            Local local = (Local) value;
            return findAssignmentsForLocal(local, callSite, edges);
        }
        return Collections.singleton((Object) TOP_VALUE);    
    }

    private Set<Object> findAssignmentsForLocal(Local value, Stmt callSite, Set<List<Edge>> edges){
        List<DefinitionStmt> assignStmts = findAssignmentsForLocalOrFieldRef(callSite, value, true, new HashSet<>());
        Set<Object> result = new HashSet<>(assignStmts.size());

        for(DefinitionStmt assignStmt: assignStmts){
            Value rhsValue = assignStmt.getRightOp();
            if(rhsValue instanceof StringConstant){

            } else if (rhsValue instanceof NullConstant) {

            } else if (rhsValue instanceof ParameterRef) {

            }
            else if (rhsValue instanceof InvokeExpr) {
                SootMethod sm = ((InvokeExpr) rhsValue).getMethod(); //TO-DO: need to find the target of the method call and look its definitions
                if (SystemClassHandler.v().isClassInSystemPackage(sm.method().getDeclaringClass().getName()))
                    return findStringResourceFromFrameworkMethod(value, assignStmt, (InvokeExpr)rhsValue, sm, edges);
                
            }
        }
        return Collections.singleton((Object) TOP_VALUE);   
    }

    private Set<Object> findStringResourceFromFrameworkMethod(Local local, Stmt stmt,  InvokeExpr expr, SootMethod frameworkMethod, Set<List<Edge>> contextEdges){
        if(frameworkMethod.getName().equals("getText")){//to refine with signature
            Logger.debug("Parsing framework method {}", frameworkMethod);
            Value intValue = expr.getArg(0);
            Integer resId = null;
            //extract the resId
            Argument arg = extractIntArgumentFrom(expr);
            Set<Object> values = ArgumentValueManager.v().getArgumentValues(arg, stmt, null);
            if (values!=null && !values.isEmpty()) {
                Object value = values.iterator().next();
                if (value instanceof Integer) {
                    Logger.debug("The text resource id for the item {}", (Integer)value);
                    resId = (Integer)value;
                }
            }
            //Get the resource id
            if(resId != null && resId != -1){
                String resource = ResourceValueProvider.v().getStringById(resId);
                return Collections.singleton((Object) resource);
            }

        }
        return Collections.singleton((Object) TOP_VALUE);
    }

    @Override
    public Object getTopValue() {
        return TOP_VALUE;
    }
}
