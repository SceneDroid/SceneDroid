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

import android.goal.explorer.analysis.value.AnalysisParameters;
import android.goal.explorer.analysis.value.PropagationModel;
import android.goal.explorer.analysis.value.identifiers.Argument;
import heros.FlowFunction;
import heros.flowfunc.Identity;
import org.pmw.tinylog.Logger;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A factory for call-to-return flow functions. Call-to-return flow functions indicate how symbols
 * (variables) are propagated in parallel to a call statement.
 */
public class CallToReturnFlowFunctionFactory {
  /**
   * Returns a call-to-return flow function.
   * 
   * @param call A statement that is the source of an edge in the call graph. Most of the time, this
   *          should be a call statement, but sometimes it can be a field access that is connected
   *          to a class initializer.
   * @param returnSite The statement to which the call returns.
   * @param zeroValue The zero value for the problem, which represents the absence of a data flow
   *          fact.
   * @param icfg The interprocedural control flow graph.
   * @return A call-to-return flow function.
   */
  public FlowFunction<Value> getCallToReturnFlowFunction(final Unit call, Unit returnSite,
                                                         final Value zeroValue, JimpleBasedInterproceduralCFG icfg) {
    Stmt stmt = (Stmt) call;

    Logger.debug("C2R: " + stmt);

    if (!stmt.containsInvokeExpr()) {
      // We are only potentially interested in modifiers (which are invoking a method). In all other
      // cases, simply propagate everything as it is.
      return Identity.v();
    }

    final InvokeExpr invokeExpr = stmt.getInvokeExpr();
    SootMethodRef methodRef = invokeExpr.getMethodRef();

    if (call instanceof DefinitionStmt) {
      // Look for generating modifiers before potentially skipping a method.
      final DefinitionStmt definitionStmt = (DefinitionStmt) call;
      if (PropagationModel.v().getArgumentsForGenMethod(invokeExpr) != null) {
        Logger.debug("Detected gen modifier: " + call);
        final Value leftValue = definitionStmt.getLeftOp();

        return new FlowFunction<Value>() {

          @Override
          public Set<Value> computeTargets(Value source) {
            Logger.debug("source: " + source);
            if (source.equals(zeroValue)) {
              // Generate a new value for the variable on the left.
              Logger.debug("Returning " + leftValue);
              return Collections.singleton(leftValue);
            } else if (!source.equals(leftValue)) {
              // All other variables: propagate.
              Logger.debug("Returning " + source);
              return Collections.singleton(source);
            } else {
              // Kill the previous values of the variable on the left.
              Logger.debug("Returning empty set");
              return Collections.emptySet();
            }
          }
        };
      } else if (PropagationModel.v().getArgumentsForCopyMethod(invokeExpr) != null
          && invokeExpr instanceof InstanceInvokeExpr) {
        final Value leftValue = definitionStmt.getLeftOp();
        InstanceInvokeExpr instanceInvokeExpr = (InstanceInvokeExpr) invokeExpr;
        final Value base = instanceInvokeExpr.getBase();

        return new FlowFunction<Value>() {

          @Override
          public Set<Value> computeTargets(Value source) {
            Set<Value> result = new HashSet<>();

            if (source.equals(base)) {
              result.add(leftValue);
            }
            if (!source.equals(leftValue)) {
              result.add(source);
            }

            return result;
          }
        };
      } else {
        final Argument[] arguments = PropagationModel.v().getArgumentsForCopyConstructor(methodRef);

        if (arguments != null) {
          return new FlowFunction<Value>() {
            @Override
            public Set<Value> computeTargets(Value source) {
              Logger.debug("Copy constructor " + call);
              Logger.debug("Source: " + source);
              Set<Value> result = new HashSet<Value>();
              Value argument = invokeExpr.getArg(arguments[0].getArgNum()[0]);
              if (!source.equivTo(definitionStmt.getLeftOp())) {
                result.add(source);
              }
              if (argument.equivTo(source)) {
                result.add(definitionStmt.getLeftOp());
              }
              Logger.debug("Returning " + result);
              return result;
            }
          };
        }
      }
    }

    if (!AnalysisParameters.v().isAnalysisClass(methodRef.declaringClass().getName())) {
      return Identity.v();
    }

    if (call instanceof InvokeStmt) {
      if (PropagationModel.v().getArgumentsForQuery((Stmt) call) != null
          || PropagationModel.v().getArgumentsForMethod(invokeExpr) != null) {
        return Identity.v();
      }
      final Argument[] copyConstructorArguments =
          PropagationModel.v().getArgumentsForCopyConstructor(invokeExpr.getMethodRef());
      if (copyConstructorArguments != null) {
        return new FlowFunction<Value>() {
          @Override
          public Set<Value> computeTargets(Value source) {
            Logger.debug("Copy constructor " + call);
            Logger.debug("Source: " + source);
            Set<Value> result = new HashSet<Value>();
            InstanceInvokeExpr instanceInvokeExpr = (InstanceInvokeExpr) invokeExpr;
            Value base = instanceInvokeExpr.getBase();
            Value argument = instanceInvokeExpr.getArg(copyConstructorArguments[0].getArgNum()[0]);
            if (!source.equivTo(base)) {
              result.add(source);
            }
            if (argument.equivTo(source)) {
              result.add(base);
            }
            Logger.debug("Returning " + result);
            return result;
          }
        };
      } else {
        if (icfg.getCalleesOfCallAt(call).size() == 0) {
          // For some reason the call does not resolve to anything.
          // Do not kill anything.
          return Identity.v();
        }

        // Case where an argument is one of the values: kill it on the call to
        // return edge, keep it on the call and return edges.
        // TODO: Need to look at points-to sets?
        // I think so, but do not kill the call to return value, just in case we
        // have a false positive in the points-to analysis.
        return new FlowFunction<Value>() {
          @Override
          public Set<Value> computeTargets(Value source) {
            for (Value arg : invokeExpr.getArgs()) {
              if (arg.equivTo(source)) {
                return Collections.emptySet();
              }
            }
            return Collections.singleton(source);
          }
        };
      }
    } else if (call instanceof DefinitionStmt) {
      Logger.debug("ctr def stmt: " + call);
      final DefinitionStmt definitionStmt = (DefinitionStmt) call;

      if (PropagationModel.v().getArgumentsForQuery(definitionStmt) != null
          || PropagationModel.v().getArgumentsForMethod(invokeExpr) != null) {

        if (methodRef.isStatic()) {
          return Identity.v();
        }

        // Cases like intent2 = intent.setAction("ACTION").
        return new FlowFunction<Value>() {
          @Override
          public Set<Value> computeTargets(Value source) {
            Value leftOp = definitionStmt.getLeftOp();
            Set<Value> res = new HashSet<Value>();
            String typeString = leftOp.getType().toString();
            if (!(leftOp.equivTo(source) && PropagationModel.v().isModeledType(typeString))) {
              res.add(source);
            }
            SootMethod method = invokeExpr.getMethod();
            if (invokeExpr instanceof VirtualInvokeExpr && method.isConcrete()) {

              if (!method.hasActiveBody()) {
                method.retrieveActiveBody();
              }

              for (Unit unit : method.getActiveBody().getUnits()) {
                if (unit instanceof ReturnStmt) {
                  Value retLocal = ((ReturnStmt) unit).getOp();
                  if (retLocal.equivTo(invokeExpr.getMethod().getActiveBody().getThisLocal())
                      && source.equivTo(((VirtualInvokeExpr) invokeExpr).getBase())) {
                    res.add(leftOp);
                  }
                }
              }
            }
            Logger.debug("Returning: " + res);
            return res;
          }
        };

        // Do not traverse modeled API calls.
        // return Identity.v();
      }

      if (icfg.getCalleesOfCallAt(call).size() == 0) {
        // For some reason the call does not resolve to anything.
        // Do not kill anything.
        return Identity.v();
      }

      return new FlowFunction<Value>() {
        @Override
        public Set<Value> computeTargets(Value source) {
          for (Value arg : invokeExpr.getArgs()) {
            if (arg.equivTo(source)) {
              return Collections.emptySet();
            }
          }

          Value left = definitionStmt.getLeftOp();
          if (left.equivTo(source) || left.toString().equals(source.toString())) {
            return Collections.emptySet();
          }
          return Collections.singleton(source);
        }
      };
    }
    return Identity.v();
  }
}
