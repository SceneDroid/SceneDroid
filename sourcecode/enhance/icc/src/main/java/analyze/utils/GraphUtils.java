package main.java.analyze.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import main.java.analyze.model.analyzeModel.Condition;
import main.java.analyze.model.analyzeModel.ConditionLeaf;
import soot.jimple.Stmt;
import soot.jimple.internal.JIfStmt;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;

public class GraphUtils {
	/**
	 * record the condition of each bloack could be described in paper
	 * 
	 * @param g1
	 * @return
	 */
	public static Map<Block, Condition> getConditionOfCFG(BlockGraph briefGraph) {
		Map<Block, Condition> conditionMap = new HashMap<Block, Condition>();
		for (Block current : briefGraph.getBlocks()) {
			if (current.getTail() instanceof JIfStmt) {
				JIfStmt ifUnit = (JIfStmt) current.getTail();
				Stmt target = ifUnit.getTarget();
				for (Block subsuccBlock : current.getSuccs()) {
					if (subsuccBlock.getHead().equals(target)) {
						add2ConditionMap(conditionMap, subsuccBlock, ifUnit, true, "union");
					} else {
						add2ConditionMap(conditionMap, subsuccBlock, ifUnit, false, "union");
					}
				}
			}
		}
		return conditionMap;
	}

	/**
	 * add2ConditionMap
	 * 
	 * @param conditionMap
	 * @param subsucc
	 * @param pair
	 */
	public static void add2ConditionMap(Map<Block, Condition> conditionMap, Block block, JIfStmt ifUnit,
			boolean satisfy, String operation) {
		ConditionLeaf leaf = new ConditionLeaf(ifUnit, ifUnit.getCondition(), satisfy, operation, null);
		if (leaf.toString() == null)
			return;
		if (conditionMap.containsKey(block)) {
			Condition oldCondition = conditionMap.get(block);
			Condition newCondition = new Condition(oldCondition, leaf, operation);
			conditionMap.put(block, newCondition);
		} else
			conditionMap.put(block, leaf);

	}

	/**
	 * printConditionMap
	 * 
	 * @param conditionMap
	 */
	public static void printConditionMap(Map<Block, Condition> conditionMap) {
		for (Entry<Block, Condition> en : conditionMap.entrySet()) {
			System.out.println("Block " + en.getKey().getIndexInMethod() + ", condition: " + en.getValue().toString());
		}
	}

	/**
	 * generateDotPng use graphviz
	 * 
	 * @param input
	 * @param output
	 * @param allowNonComponentNode
	 */
	public static void generateDotFile(String name, String type) {
		String command = "dot -T" + type + " " + name + ".dot" + " -o " + name + "." + type;
//		System.out.println(command);
		ExecuteUtils.exec(command, 300);
	}
}
