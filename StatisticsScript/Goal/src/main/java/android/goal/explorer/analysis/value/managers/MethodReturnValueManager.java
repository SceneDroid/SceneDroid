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
package android.goal.explorer.analysis.value.managers;

import android.goal.explorer.analysis.value.PropagationModel;
import android.goal.explorer.analysis.value.analysis.MethodReturnValueAnalysis;
import android.goal.explorer.analysis.value.analysis.SourceMethodReturnValueAnalysis;
import android.goal.explorer.analysis.value.analysis.strings.LanguageConstraints;
import android.goal.explorer.analysis.value.values.propagation.PropagationConstants;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A singleton manager for method return values.
 */
public class MethodReturnValueManager {
  private static final Map<String, MethodReturnValueAnalysis> methodReturnValueAnalysisMap =
      new HashMap<>();
  private static final SourceMethodReturnValueAnalysis sourceMethodReturnValueAnalysis =
      new SourceMethodReturnValueAnalysis();
  private static final MethodReturnValueManager instance = new MethodReturnValueManager();

  private MethodReturnValueManager() {
  }

  public static MethodReturnValueManager v() {
    return instance;
  }

  /**
   * Registers a method return value analysis.
   * 
   * @param subSignature A method subsignature (name and argument types).
   * @param analysis A {@link MethodReturnValueAnalysis}.
   */
  public void registerMethodReturnValueAnalysis(String subSignature,
      MethodReturnValueAnalysis analysis) {
    methodReturnValueAnalysisMap.put(subSignature, analysis);
  }

  /**
   * Registers default method return value analyses.
   */
  public void registerDefaultMethodReturnValueAnalyses() {
    registerMethodReturnValueAnalysis("java.lang.String getName()",
        new MethodReturnValueAnalysis() {
          @Override
          public Set<Object> computeMethodReturnValues(LanguageConstraints.Call call) {
            InvokeExpr invokeExpr = call.stmt.getInvokeExpr();

            if (invokeExpr instanceof InstanceInvokeExpr) {
              InstanceInvokeExpr instanceInvokeExpr = (InstanceInvokeExpr) invokeExpr;
              if (invokeExpr.getMethod().getDeclaringClass().getName().equals("java.lang.Class")) {
                return ArgumentValueManager.v()
                    .getArgumentValueAnalysis(PropagationConstants.DefaultArgumentTypes.Scalar.CLASS)
                    .computeVariableValues(instanceInvokeExpr.getBase(), call.stmt);
              }
            }

            return null;
          }
        });
  }

  /**
   * Returns the possible return values for a given method call.
   * 
   * @param call A method call.
   * @return The possible return values for the method call.
   */
  public Set<Object> getMethodReturnValues(LanguageConstraints.Call call) {
    Stmt stmt = call.stmt;
    if (!stmt.containsInvokeExpr()) {
      throw new RuntimeException("Statement does not contain invoke expression: " + stmt);
    }

    InvokeExpr invokeExpr = stmt.getInvokeExpr();
    // First consider the registered method return value analyses.
    MethodReturnValueAnalysis analysis =
        methodReturnValueAnalysisMap.get(invokeExpr.getMethod().getSubSignature());
    if (analysis != null) {
      return analysis.computeMethodReturnValues(call);
    } else if (PropagationModel.v().getArgumentsForSource(invokeExpr) != null) {
      // Then consider the declared COAL sources.
      return sourceMethodReturnValueAnalysis.computeMethodReturnValues(call);
    }

    return null;
  }
}
