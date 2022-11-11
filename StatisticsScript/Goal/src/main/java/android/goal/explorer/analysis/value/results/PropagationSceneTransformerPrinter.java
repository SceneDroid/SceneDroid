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
package android.goal.explorer.analysis.value.results;


import android.goal.explorer.analysis.value.analysis.solvers.PropagationSolver;

/**
 * A printer for {@link android.goal.explorer.analysis.value.PropagationSceneTransformer}. Subclasses should indicate how to print
 * debugging output after the constant propagation is over.
 */
public interface PropagationSceneTransformerPrinter {

  /**
   * Prints the results of a constant propagation solver. This is executed when debugging output is
   * activated for {@link android.goal.explorer.analysis.value.PropagationSceneTransformer}.
   * 
   * @param solver A constant propagation solver.
   */
  public void print(PropagationSolver solver);

}
