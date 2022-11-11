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

import android.goal.explorer.analysis.value.analysis.strings.LanguageConstraints;

import java.util.Set;

/**
 * An abstract analysis for method return values. Subclasses should indicate how to infer return
 * values for known methods.
 */
public abstract class MethodReturnValueAnalysis {

  /**
   * Returns the possible return values of a given method.
   * 
   * @param call A method call.
   * @return The possible method return values.
   */
  public abstract Set<Object> computeMethodReturnValues(LanguageConstraints.Call call);

}
