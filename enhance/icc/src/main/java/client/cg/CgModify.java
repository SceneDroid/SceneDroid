package main.java.client.cg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import main.java.Analyzer;
import main.java.MyConfig;
import main.java.SummaryLevel;
import main.java.analyze.utils.SootUtils;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

public class CgModify extends Analyzer {

	public CgModify() {
		super();
	}

	@Override
	public void analyze() {
		boolean lightMode = false;
		if(lightMode){
			addTopoForSupplySingle();
		}else{
			addEdgesByOurAnalyze(appModel.getCg());
			// System.out.println("addEdgesByOurAnalyze");
			removeExlibEdge(appModel.getCg());
			// removeNotActiveEdge(appModel.getCg());
			removeSameEdge(appModel.getCg());
			removeSelfEdge(appModel.getCg());
			// System.out.println("removeSelfEdge");
			if (MyConfig.getInstance().getMySwithch().getSummaryStrategy().equals(SummaryLevel.none)) {
				addTopoForSupplySingle();
	
			} else if (MyConfig.getInstance().getMySwithch().isCgAnalyzeGroupedStrategy()) {
				/** multiple topo queue **/
				List<CallGraph> cgs = new ArrayList<CallGraph>();
				List<Set<SootMethod>> methodSets = new ArrayList<Set<SootMethod>>();
				seperateCG2multiple(appModel.getCg(), methodSets, cgs);
				// System.out.println("seperateCG2multiple"+cgs.size());
				for (int i = 0; i < cgs.size(); i++) {
					CallGraph cg = cgs.get(i);
					Set<SootMethod> methodSet = methodSets.get(i);
					Map<SootMethod, Integer> inDegreeMap = constructInDregreeMap(cg, methodSet);
					removeCirclefromCG(inDegreeMap, cg);
					Map<SootMethod, Integer> outDegreeMap = constructOutDregreeMap(cg, methodSet);
					sortCG(outDegreeMap, cg);
					// System.out.println("sortCG");
				}
				addTopoForSupplyMulti();
			} else {
				/** single topo queue **/
				Map<SootMethod, Integer> inDegreeMap = constructInDregreeMap(appModel.getCg());
				removeCirclefromCG(inDegreeMap, appModel.getCg());
				Map<SootMethod, Integer> outDegreeMap = constructOutDregreeMap(appModel.getCg());
				sortCG(outDegreeMap, appModel.getCg());
				addTopoForSupplySingle();
			}
		}
		System.out.println("Call Graph has " + appModel.getCg().size() + " edges.");
	}

	/**
	 * addTopoForSupplyMulti
	 */
	private void addTopoForSupplyMulti() {
		List<SootMethod> subTopo = new ArrayList<SootMethod>();
		appModel.getTopoMethodQueueSet().add(subTopo);
		for (SootClass sc : Scene.v().getApplicationClasses()) {
			if (!MyConfig.getInstance().getMySwithch().allowLibCodeSwitch()) {
				if (!SootUtils.isNonLibClass(sc.getName()))
					continue;
			}
			for (SootMethod sm : sc.getMethods()) {
				if (SootUtils.hasSootActiveBody(sm) == false)
					continue;
				if (!appModel.getTopoMethodQueue().contains(sm)) {
					appModel.getTopoMethodQueue().add(0, sm);
					subTopo.add(0, sm);
				}
			}
		}
	}

	/**
	 * addTopoForSupplyMulti
	 */
	private void addTopoForSupplySingle() {
		if (appModel.getTopoMethodQueueSet().size() == 0)
			appModel.getTopoMethodQueueSet().add(new ArrayList<SootMethod>());
		for (List<SootMethod> subTopo : appModel.getTopoMethodQueueSet()) {
			for (SootClass sc : Scene.v().getApplicationClasses()) {
				if (!MyConfig.getInstance().getMySwithch().allowLibCodeSwitch()) {
					if (!SootUtils.isNonLibClass(sc.getName()))
						continue;
				}
				for (SootMethod sm : sc.getMethods()) {
					if (SootUtils.hasSootActiveBody(sm) == false)
						continue;
					if (!appModel.getTopoMethodQueue().contains(sm)) {
						appModel.getTopoMethodQueue().add(0, sm);
						subTopo.add(0, sm);
					}
				}
			}
			break;
		}
	}

	/**
	 * Separate big cg to a set of connected small one
	 * 
	 * @param callGraph
	 * @return
	 */
	private void seperateCG2multiple(CallGraph callGraph, List<Set<SootMethod>> methodSets, List<CallGraph> cgs) {
		for (Edge edge : callGraph) {
			int srcId = -1, tgtId = -1;
			SootMethod srcMtd = edge.getSrc().method();
			SootMethod tgtMtd = edge.getTgt().method();
			for (int i = 0; i < methodSets.size(); i++) {
				if (methodSets.get(i).contains(srcMtd)) {
					srcId = i;
					break;
				}
			}
			for (int i = 0; i < methodSets.size(); i++) {
				if (methodSets.get(i).contains(tgtMtd)) {
					tgtId = i;
					break;
				}
			}
			if (srcId == -1 && tgtId == -1) {
				Set<SootMethod> newSet = new HashSet<SootMethod>();
				newSet.add(edge.getSrc().method());
				newSet.add(edge.getTgt().method());
				methodSets.add(newSet);
				CallGraph cg = new CallGraph();
				Edge edgeCopy = new Edge(edge.getSrc(), edge.srcStmt(), edge.getTgt(), edge.kind());
				cg.addEdge(edgeCopy);
				cgs.add(cg);
			} else if (srcId != -1 && tgtId == -1) {
				methodSets.get(srcId).add(edge.getTgt().method());
				Edge edgeCopy = new Edge(edge.getSrc(), edge.srcStmt(), edge.getTgt(), edge.kind());
				cgs.get(srcId).addEdge(edgeCopy);
			} else if (srcId == -1 && tgtId != -1) {
				methodSets.get(tgtId).add(edge.getSrc().method());
				Edge edgeCopy = new Edge(edge.getSrc(), edge.srcStmt(), edge.getTgt(), edge.kind());
				cgs.get(tgtId).addEdge(edgeCopy);
			} else if (srcId != -1 && tgtId != -1) {
				Edge edgeCopy = new Edge(edge.getSrc(), edge.srcStmt(), edge.getTgt(), edge.kind());
				cgs.get(srcId).addEdge(edgeCopy);
				if (srcId != tgtId) {
					for (SootMethod sig : methodSets.get(tgtId))
						methodSets.get(srcId).add(sig);
					methodSets.remove(tgtId);
					for (Edge tarEdge : cgs.get(tgtId)) {
						Edge targetCopy = new Edge(tarEdge.getSrc(), tarEdge.srcStmt(), tarEdge.getTgt(),
								tarEdge.kind());
						cgs.get(srcId).addEdge(targetCopy);
					}
					cgs.remove(tgtId);
				}
			}
		}
	}

	/**
	 * add edges in cg for topo sort
	 */
	private void addEdgesByOurAnalyze(CallGraph callGraph) {
		for (SootClass sc : Scene.v().getApplicationClasses()) {
			if (!MyConfig.getInstance().getMySwithch().allowLibCodeSwitch()) {
				if (!SootUtils.isNonLibClass(sc.getName()))
					continue;
			}
			ArrayList<SootMethod> methodList = new ArrayList<SootMethod>(sc.getMethods());
			for (SootMethod sm : methodList) {
				if (SootUtils.hasSootActiveBody(sm) == false)
					continue;
				Iterator<Unit> it = SootUtils.getSootActiveBody(sm).getUnits().iterator();
				while (it.hasNext()) {
					Unit u = it.next();
					InvokeExpr exp = SootUtils.getInvokeExp(u);
					if (exp == null)
						continue;
					InvokeExpr invoke = SootUtils.getSingleInvokedMethod(u);
					if (invoke != null) { // u is invoke stmt
						Set<SootMethod> targetSet = SootUtils.getInvokedMethodSet(sm, u);
						for (SootMethod target : targetSet) {
							Edge e = new Edge(sm, (Stmt) u, target);
							callGraph.addEdge(e);
						}
					}
				}
			}
		}
	}

	/**
	 * if an edge not in package, remove it
	 */
	private void removeExlibEdge(CallGraph callGraph) {
		if (!MyConfig.getInstance().getMySwithch().allowLibCodeSwitch()) {
			Set<Edge> toBeDeletedSet = new HashSet<Edge>();
			Iterator<Edge> it = callGraph.iterator();
			while (it.hasNext()) {
				Edge edge = it.next();
				if (edge.src() == null || edge.tgt() == null)
					toBeDeletedSet.add(edge);
				else if (!SootUtils.isNonLibClass(edge.src().getDeclaringClass().getName()))
					toBeDeletedSet.add(edge);
				else if (!SootUtils.isNonLibClass(edge.tgt().getDeclaringClass().getName()))
					toBeDeletedSet.add(edge);
			}
			for (Edge tbEdge : toBeDeletedSet)
				callGraph.removeEdge(tbEdge);
		}
	}

	/**
	 * remove a -- a
	 */
	private void removeSelfEdge(CallGraph callGraph) {
		Set<Edge> toBeDeletedSet = new HashSet<Edge>();
		Iterator<Edge> it = callGraph.iterator();
		while (it.hasNext()) {
			Edge edge = it.next();
			if (edge.getSrc().method() == edge.getTgt().method()) {
				toBeDeletedSet.add(edge);
			}
		}
		for (Edge tbEdge : toBeDeletedSet)
			callGraph.removeEdge(tbEdge);
	}

	/**
	 * remove same edges
	 */
	private void removeSameEdge(CallGraph callGraph) {
		Set<String> edgeSet = new HashSet<String>();
		Set<Edge> toBeDeletedSet = new HashSet<Edge>();
		Iterator<Edge> it = callGraph.iterator();
		while (it.hasNext()) {
			Edge edge = it.next();
			String sig = edge.src().getSignature() + edge.tgt().getSignature();
			if (edgeSet.contains(sig))
				toBeDeletedSet.add(edge);
			else
				edgeSet.add(sig);
		}
		for (Edge tbEdge : toBeDeletedSet)
			callGraph.removeEdge(tbEdge);
	}

	/**
	 * remove same edges
	 * 
	 * @param callGraph
	 */
	private void removeNotActiveEdge(CallGraph callGraph) {
		Set<Edge> toBeDeletedSet = new HashSet<Edge>();
		Iterator<Edge> it = callGraph.iterator();
		while (it.hasNext()) {
			Edge edge = it.next();
			SootMethod me = edge.getTgt().method();
			if (SootUtils.hasSootActiveBody(me) == false)
				toBeDeletedSet.add(edge);
		}
		for (Edge tbEdge : toBeDeletedSet)
			callGraph.removeEdge(tbEdge);
	}

	/**
	 * remove Circle from cg use stack based dfs, fast
	 * 
	 * @param cg
	 * 
	 * @param outDegreeMap
	 */
	private void removeCirclefromCG(Map<SootMethod, Integer> inDegreeMap, CallGraph callGraph) {
		Set<Edge> toBeDeletedSet = new HashSet<Edge>();
		Set<SootMethod> mcSrcs = new HashSet<SootMethod>(inDegreeMap.keySet());
		for (SootMethod mcSrc : mcSrcs) {
			if (inDegreeMap.get(mcSrc) == 0) {// for nodes whose indegree are 0,
												// start from them
				Stack<SootMethod> stack = new Stack<SootMethod>();
				stack.add(mcSrc);
				Map<SootMethod, Integer> nodeStatus = new HashMap<SootMethod, Integer>(); // null
																							// for
																							// new
																							// node,
																							// 1
																							// for
				nodeStatus.put(mcSrc, -1);
				while (!stack.isEmpty()) {
					SootMethod topMethod = stack.peek();
					Iterator<Edge> it = callGraph.edgesOutOf(topMethod);
					boolean allVisited = true;
					while (it.hasNext()) {
						Edge edge = it.next();
						SootMethod nextMethod = edge.getTgt().method();
						if (!nodeStatus.containsKey(nextMethod)) {
							allVisited = false;
							nodeStatus.put(nextMethod, -1);
							stack.push(nextMethod);
							break;
						} else if (nodeStatus.get(nextMethod) == -1) {
							toBeDeletedSet.add(edge);
						} else {
							continue;
						}
					}
					if (allVisited) {
						nodeStatus.put(topMethod, 1);
						stack.pop();
					}
				}
			}
		}
		for (Edge tbEdge : toBeDeletedSet)
			callGraph.removeEdge(tbEdge);
	}

	/**
	 * sort cg and get a sorted node list method that has no active body is
	 * excluded
	 * 
	 * @param outDegreeMap
	 * @param callGraph
	 */
	private void sortCG(Map<SootMethod, Integer> outDegreeMap, CallGraph callGraph) {
		if (callGraph == null)
			return;
		Set<SootMethod> queueSet = new HashSet<SootMethod>();
		List<SootMethod> subTopo = new ArrayList<SootMethod>();
		appModel.getTopoMethodQueueSet().add(subTopo);

		// put node whose outdegree is 0 to queue
		int lastTurn = 0;
		while (true) {
			Set<SootMethod> mcTars = new HashSet<SootMethod>(outDegreeMap.keySet());
			for (SootMethod mcTar : mcTars) {
				if (outDegreeMap.get(mcTar) == 0) {// for nodes whose outdegree
													// are 0, remove them
					outDegreeMap.remove(mcTar);
					if (!queueSet.contains(mcTar)) {
						queueSet.add(mcTar);
						subTopo.add(mcTar);
						appModel.getTopoMethodQueue().add(mcTar);
						Iterator<Edge> it2 = callGraph.edgesInto(mcTar);
						while (it2.hasNext()) {// for nodes whose link to
												// removed nodes, outdegree -1
							SootMethod mcSource = it2.next().getSrc().method();
							if (!outDegreeMap.containsKey(mcSource))
								continue;
							outDegreeMap.put(mcSource, outDegreeMap.get(mcSource) - 1);
						}
					}
				}
			}
			if (outDegreeMap.size() == lastTurn)
				for (SootMethod key : outDegreeMap.keySet())
					outDegreeMap.put(key, 0);
			lastTurn = outDegreeMap.size();
			if (outDegreeMap.size() == 0)
				break;
		}
	}

	/**
	 * constructOutDregreeMap
	 * 
	 * @param callGraph
	 * @param methodSet
	 * 
	 * @return
	 */
	private Map<SootMethod, Integer> constructOutDregreeMap(CallGraph callGraph) {
		Map<SootMethod, Integer> outDegreeMap = new HashMap<SootMethod, Integer>();// get
																					// outDegreeMap
		for (SootClass sc : Scene.v().getApplicationClasses()) {
			for (SootMethod m : sc.getMethods()) {
				if (SootUtils.hasSootActiveBody(m) == false)
					continue;
				outDegreeMap.put(m, 0); // initial outDegreeMap
			}
		}
		Iterator<Edge> it = callGraph.iterator();
		while (it.hasNext()) {
			Edge edge = it.next();
			if (SootUtils.hasSootActiveBody(edge.getTgt().method()) == false)
				continue;
			if (outDegreeMap.containsKey(edge.getSrc().method()))
				outDegreeMap.put(edge.getSrc().method(), outDegreeMap.get(edge.getSrc()) + 1);
		}
		return outDegreeMap;
	}

	/**
	 * constructInDregreeMap
	 * 
	 * @param callGraph
	 * @param methodSet
	 * 
	 * @return
	 */
	private Map<SootMethod, Integer> constructInDregreeMap(CallGraph callGraph) {
		Map<SootMethod, Integer> inDegreeMap = new HashMap<SootMethod, Integer>();// get
																					// outDegreeMap
		for (SootClass sc : Scene.v().getApplicationClasses()) {
			for (SootMethod m : sc.getMethods()) {
				if (SootUtils.hasSootActiveBody(m) == false)
					continue;
				inDegreeMap.put(m, 0); // initial outDegreeMap
			}
		}
		Iterator<Edge> it = callGraph.iterator();
		while (it.hasNext()) {
			Edge edge = it.next();
			if (SootUtils.hasSootActiveBody(edge.getSrc().method()) == false)
				continue;
			if (inDegreeMap.containsKey(edge.getTgt().method()))
				inDegreeMap.put(edge.getTgt().method(), inDegreeMap.get(edge.getTgt()) + 1);
		}
		return inDegreeMap;
	}

	/**
	 * constructOutDregreeMap
	 * 
	 * @param callGraph
	 * @param methodSet
	 * 
	 * @return
	 */
	private Map<SootMethod, Integer> constructOutDregreeMap(CallGraph callGraph, Set<SootMethod> methodSet) {
		Map<SootMethod, Integer> outDegreeMap = new HashMap<SootMethod, Integer>();// get
																					// outDegreeMap
		for (SootMethod m : methodSet) {
			if (SootUtils.hasSootActiveBody(m) == false)
				continue;
			outDegreeMap.put(m, 0); // initial outDegreeMap
		}
		Iterator<Edge> it = callGraph.iterator();
		while (it.hasNext()) {
			Edge edge = it.next();
			if (SootUtils.hasSootActiveBody(edge.getTgt().method()) == false)
				continue;
			if (outDegreeMap.containsKey(edge.getSrc().method()))
				outDegreeMap.put(edge.getSrc().method(), outDegreeMap.get(edge.getSrc()) + 1);
		}
		return outDegreeMap;
	}

	/**
	 * constructInDregreeMap
	 * 
	 * @param callGraph
	 * @param methodSet
	 * 
	 * @return
	 */
	private Map<SootMethod, Integer> constructInDregreeMap(CallGraph callGraph, Set<SootMethod> methodSet) {
		Map<SootMethod, Integer> inDegreeMap = new HashMap<SootMethod, Integer>();// get
																					// outDegreeMap
		for (SootMethod m : methodSet) {
			if (SootUtils.hasSootActiveBody(m) == false)
				continue;
			inDegreeMap.put(m, 0); // initial outDegreeMap
		}
		Iterator<Edge> it = callGraph.iterator();
		while (it.hasNext()) {
			Edge edge = it.next();
			if (SootUtils.hasSootActiveBody(edge.getSrc().method()) == false)
				continue;
			if (inDegreeMap.containsKey(edge.getTgt().method()))
				inDegreeMap.put(edge.getTgt().method(), inDegreeMap.get(edge.getTgt()) + 1);
		}
		return inDegreeMap;
	}
}