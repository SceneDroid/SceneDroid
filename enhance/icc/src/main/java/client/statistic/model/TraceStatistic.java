package main.java.client.statistic.model;

import java.util.HashMap;
import java.util.Set;

public class TraceStatistic {

	private HashMap<Integer, Set<String>> methodTraceNum2MethodSet;
	private HashMap<Integer, Set<String>> methodTraceDepth2MethodSet;

	public TraceStatistic() {
		methodTraceNum2MethodSet = new HashMap<Integer, Set<String>>();
		methodTraceDepth2MethodSet = new HashMap<Integer, Set<String>>();
	}

	public HashMap<Integer, Set<String>> getMethodTraceNum2MethodSet() {
		return methodTraceNum2MethodSet;
	}

	public HashMap<Integer, Set<String>> getMethodTraceDepth2MethodSet() {
		return methodTraceDepth2MethodSet;
	}

	public void setMethodTraceNum2MethodSet(HashMap<Integer, Set<String>> methodTraceNum2MethodSet) {
		this.methodTraceNum2MethodSet = methodTraceNum2MethodSet;
	}

	public void setMethodTraceDepth2MethodSet(HashMap<Integer, Set<String>> methodTraceDepth2MethodSet) {
		this.methodTraceDepth2MethodSet = methodTraceDepth2MethodSet;
	}

}
