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
package android.goal.explorer.analysis.value;

import android.goal.explorer.analysis.value.analysis.StringValueAnalysis;
import android.goal.explorer.analysis.value.analysis.problem.PropagationProblem;
import android.goal.explorer.analysis.value.analysis.solvers.IterationSolver;
import android.goal.explorer.analysis.value.analysis.solvers.PropagationSolver;
import android.goal.explorer.analysis.value.results.PropagationSceneTransformerPrinter;
import android.goal.explorer.analysis.value.results.ResultBuilder;
import org.pmw.tinylog.Logger;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;

import java.util.Map;

/**
 * The scene transformer for the propagation problem.
 */
public class PropagationSceneTransformer extends SceneTransformer {
    private static final String TAG = "ConstantPropagation";

    private static final int MAX_ITERATIONS = 15;
    private final PropagationSceneTransformerPrinter printer;
    private final ResultBuilder resultBuilder;

    /**
     * Constructor for the scene transformer.
     * @param resultBuilder A {@link ResultBuilder} that describes how the result is generated once
     *          the problem solution is found.
     * @param printer       A {@link PropagationSceneTransformerPrinter} that prints the output of the
     *                      analysis when debugging is enabled. If null, nothing gets printed.
     */
    public PropagationSceneTransformer(ResultBuilder resultBuilder, PropagationSceneTransformerPrinter printer) {
        this.resultBuilder = resultBuilder;
        this.printer = printer;
    }

    @Override
    protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
        StringValueAnalysis.initialize();

        JimpleBasedInterproceduralCFG iCfg = new PropagationIcfg();
        AnalysisParameters.v().setIcfg(iCfg);
        PropagationProblem problem = new PropagationProblem(iCfg);
        for (SootMethod ep : Scene.v().getEntryPoints()) {
            if (ep.isConcrete()) {
                problem.getInitialSeeds().add(ep.getActiveBody().getUnits().getFirst());
            }
        }

        int iterationCounter = 0;
        PropagationSolver solver = null;

        while (iterationCounter < MAX_ITERATIONS) {
            IterationSolver.v().initialize(solver);

            solver = new PropagationSolver(problem);

            Logger.info("[{}] Solving propagation problem (iteration " + iterationCounter + ")", TAG);
            solver.solve();

            if (!AnalysisParameters.v().isIterative() || IterationSolver.v().hasFoundFixedPoint()) {
                iterationCounter = MAX_ITERATIONS;
            } else {
                ++iterationCounter;
            }
        }

        Logger.info("[{}] Reached a fixed point!", TAG);
//        if (Logger.getLevel() == Level.DEBUG) {
//            CallGraph cg = Scene.v().getCallGraph();
//
//            Iterator<Edge> it = cg.listener();
//            while (it.hasNext()) {
//                Edge e = (Edge) it.next();
//                Logger.debug("" + e.src() + e.srcStmt() + " =" + e.kind() + "=> " + e.tgt());
//            }
//
//            if (printer != null) {
//                printer.print(solver);
//            }
//        }
    }
}
