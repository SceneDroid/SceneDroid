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
package android.goal.explorer.analysis.value.factory;

import android.goal.explorer.analysis.value.PropagationModel;
import android.goal.explorer.analysis.value.identifiers.Argument;
import android.goal.explorer.analysis.value.values.propagation.BasePropagationValue;
import heros.EdgeFunction;
import heros.edgefunc.EdgeIdentity;
import org.pmw.tinylog.Logger;
import soot.Local;
import soot.PointsToAnalysis;
import soot.PointsToSet;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;

import java.util.Arrays;

/**
 * A factory for call-to-return edge functions. Call-to-return edge functions indicate how values
 * are propagated in parallel to a call statement.
 */
public class CallToReturnEdgeFunctionFactory {

  /**
   * Returns a call-to-return edge function.
   * 
   * @param callSite A statement that is the source of a call edge. Most of the time, this should be
   *          a call statement, but sometimes it can be a field access that is connected to a class
   *          initializer.
   * @param callNode The symbol (variable) before the call.
   * @param returnSite The statement to which the call returns.
   * @param returnSideNode The symbol after the call.
   * @param pointsToAnalysis The pointer analysis.
   * @return A call-to-return edge function.
   */
  public EdgeFunction<BasePropagationValue> getCallToReturnEdgeFunction(Unit callSite, Value callNode,
                                                                        Unit returnSite, Value returnSideNode,
                                                                        PointsToAnalysis pointsToAnalysis) {
    Stmt callStmt = (Stmt) callSite;
    InvokeExpr invokeExpr = callStmt.containsInvokeExpr() ? callStmt.getInvokeExpr() : null;

    if (invokeExpr != null) {
      // The statement contains a method invocation.
      Argument[] arguments = PropagationModel.v().getArgumentsForMethod(invokeExpr);

      if (arguments != null) {
        // The call statement is a modifier.
        if (invokeExpr instanceof InstanceInvokeExpr) {
          InstanceInvokeExpr instanceInvokeExpr = (InstanceInvokeExpr) invokeExpr;
          Value base = instanceInvokeExpr.getBase();
          PointsToSet basePointsToSet = pointsToAnalysis.reachingObjects((Local) base);
          PointsToSet callNodePointsToSet = FunctionFactoryUtils.getPointsToSetForValue(callNode);

          if (basePointsToSet.hasNonEmptyIntersection(callNodePointsToSet)) {
              Logger.debug("Call node " + callNode);
              Logger.debug("Stmt " + callSite);
              Logger.debug("Returning "
                  + PropagationTransformerFactory.makeTransformer(callSite, arguments,
                      !callNode.equals(base)));

            return PropagationTransformerFactory.makeTransformer(callSite, arguments,
                !callNode.equals(base));
          }
        }
      } else {
        arguments = PropagationModel.v().getArgumentsForGenMethod(invokeExpr);

        if (arguments != null) {
          if (callStmt instanceof DefinitionStmt) {
            DefinitionStmt definitionStmt = (DefinitionStmt) callStmt;
            Value leftValue = definitionStmt.getLeftOp();

            if (leftValue.equals(returnSideNode)) {
              return PropagationTransformerFactory.makeTransformer(callSite, arguments, false);
            }
          }
        } else if (callSite instanceof DefinitionStmt) {
          DefinitionStmt definitionStmt = (DefinitionStmt) callSite;

          arguments =
              PropagationModel.v().getArgumentsForCopyConstructor(invokeExpr.getMethodRef().getSignature());
          if (arguments != null) {
            if (callNode.equals(invokeExpr.getArg(arguments[0].getArgNum()[0]))
                && returnSideNode.equals(definitionStmt.getLeftOp())) {
              Argument[] extraArguments = Arrays.copyOfRange(arguments, 1, arguments.length);
              return PropagationTransformerFactory.makeTransformer(callSite, extraArguments, false);
            }
          }
        }
      }
    }
    return EdgeIdentity.v();
  }
}
