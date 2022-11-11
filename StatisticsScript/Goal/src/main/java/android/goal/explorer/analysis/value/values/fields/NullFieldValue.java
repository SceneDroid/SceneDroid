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
package android.goal.explorer.analysis.value.values.fields;


public class NullFieldValue extends FieldValue {
  private static NullFieldValue instance = new NullFieldValue();

  private NullFieldValue() {
  }

  public static NullFieldValue v() {
    return instance;
  }

  @Override
  public Object getValue() {
    return null;
  }

  @Override
  public String toString() {
    return "null";
  }
}
