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
package android.goal.explorer.analysis.value.transformers.field;

import android.goal.explorer.analysis.value.factory.FieldNullTransformerFactory;
import android.goal.explorer.analysis.value.factory.FieldScalarReplaceTransformerFactory;
import android.goal.explorer.analysis.value.factory.FieldTransformerFactory;
import android.goal.explorer.analysis.value.transformers.set.FieldAddSequenceElementTransformerFactory;
import android.goal.explorer.analysis.value.transformers.set.FieldAddTransformerFactory;
import android.goal.explorer.analysis.value.transformers.set.FieldClearTransformerFactory;
import android.goal.explorer.analysis.value.transformers.set.FieldRemoveTransformerFactory;
import android.goal.explorer.analysis.value.transformers.set.FieldReplaceTransformerFactory;
import android.goal.explorer.analysis.value.values.propagation.PropagationConstants;
import soot.Value;
import soot.jimple.Stmt;

import java.util.HashMap;
import java.util.Map;

/**
 * A manager singleton for field transformers, which is the entry point for generating field
 * transformers.
 */
public class FieldTransformerManager {
    private static FieldTransformerManager instance = new FieldTransformerManager();
    private final Map<String, FieldTransformerFactory> fieldTransformerFactoryMap = new HashMap<>();

    private FieldTransformerManager() {
    }

    /**
     * Returns the singleton instance for the class.
     *
     * @return The singleton instance for this class.
     */
    public static FieldTransformerManager v() {
        return instance;
    }

    /**
     * Registers a field transformer factory for a given operation.
     *
     * @param action                  An operation that can be performed on fields.
     * @param fieldTransformerFactory A field transformer factory for the operation.
     */
    public void registerFieldTransformerFactory(String action,
                                                FieldTransformerFactory fieldTransformerFactory) {
        fieldTransformerFactoryMap.put(action, fieldTransformerFactory);
    }

    /**
     * Registers the field transformer factories for the field operations that are supported by COAL
     * by default. Operations that are supported by default are add, remove, clear, replace and value
     * composition.
     */
    public void registerDefaultFieldTransformerFactories() {
        registerFieldTransformerFactory(PropagationConstants.DefaultActions.Set.ADD,
                new FieldAddTransformerFactory());
        registerFieldTransformerFactory(PropagationConstants.DefaultActions.Set.ADD_ALL,
                new FieldAddTransformerFactory());
        registerFieldTransformerFactory(PropagationConstants.DefaultActions.Set.REMOVE,
                new FieldRemoveTransformerFactory());
        registerFieldTransformerFactory(PropagationConstants.DefaultActions.Set.CLEAR,
                new FieldClearTransformerFactory());
        registerFieldTransformerFactory(PropagationConstants.DefaultActions.Set.REPLACE_ALL,
                new FieldReplaceTransformerFactory());
        registerFieldTransformerFactory(PropagationConstants.DefaultActions.COMPOSE,
                new FieldAddSequenceElementTransformerFactory());
        registerFieldTransformerFactory(PropagationConstants.DefaultActions.Scalar.NULL,
                new FieldNullTransformerFactory());
        registerFieldTransformerFactory(PropagationConstants.DefaultActions.Scalar.REPLACE,
                new FieldScalarReplaceTransformerFactory());
    }

    /**
     * Generates a field transformer for a given field operation and a given argument value.
     *
     * @param action A field operation. This should be one of the field operations registered with
     *               {@link #registerFieldTransformerFactory(String, FieldTransformerFactory)}.
     * @param value  An argument value.
     * @return A field transformer for the operation and argument.
     */
    public FieldTransformer makeFieldTransformer(String action, Object value) {
        FieldTransformerFactory factory = fieldTransformerFactoryMap.get(action);
        if (factory == null) {
            throw new RuntimeException("No factory for action " + action);
        }
        return factory.makeFieldTransformer(value).intern();
    }

    /**
     * Generates a field transformer for a given field operation, COAL symbol, COAL modifier and a
     * composed operation. This is to be used for COAL value composition in non-iterative analyses
     * only.
     *
     * @param action A field operation.
     * @param symbol A COAL symbol.
     * @param stmt   A COAL modifier.
     * @param op     A field operation to be performed with the referenced COAL value.
     * @return A field transformer that represents the influence of composing a COAL value with
     * another.
     */
    public FieldTransformer makeFieldTransformer(String action, Value symbol, Stmt stmt, String op) {
        FieldTransformerFactory factory = fieldTransformerFactoryMap.get(action);
        if (factory == null) {
            throw new RuntimeException("No factory for action " + action);
        }
        return factory.makeFieldTransformer(symbol, stmt, op).intern();
    }

}
