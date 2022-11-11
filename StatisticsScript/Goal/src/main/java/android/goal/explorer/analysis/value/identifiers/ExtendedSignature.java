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
package android.goal.explorer.analysis.value.identifiers;

/**
 * An extended signature, which consists in a method signature (name and argument types) and a
 * superclass (fully qualified) name. The idea is that this represents a method that is possibly
 * overridden by subclasses.
 */
public class ExtendedSignature {
  private final String signature;
  private final String superclass;

  public ExtendedSignature(String signature, String superclass) {
    this.signature = signature;
    this.superclass = superclass;
  }

  /**
   * Gets the method signature.
   * 
   * @return The method signature.
   */
  public String getSignature() {
    return this.signature;
  }

  /**
   * Gets the fully qualified superclass name.
   * 
   * @return The fully qualified superclass name.
   */
  public String getSuperclass() {
    return this.superclass;
  }
}
