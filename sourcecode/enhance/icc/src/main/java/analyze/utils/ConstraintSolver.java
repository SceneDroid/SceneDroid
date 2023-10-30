package main.java.analyze.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import main.java.analyze.model.analyzeModel.Attribute;

public class ConstraintSolver {
	
	/**
	 * eliminateConfictedPaths
	 * 
	 * @param globalPath
	 * @return
	 */
	public static Set<String> eliminateConfictedPaths(List<Attribute> globalPath) {
		Set<String> res = new HashSet<String>();
		List<StringBuilder> stmtStrs = createSTMTFileList(globalPath);

		for (StringBuilder stmtStr : stmtStrs) {
			String modelofPath = checkSatisfiablity(stmtStr.toString(), globalPath);
			String modelStr = modelofPath.toString().replace("\n", " ").replace("(", "").replace(")", "");
			while (modelStr.contains("  "))
				modelStr = modelStr.replace("  ", " ");
			if (!res.contains(modelStr)) {
				res.add(modelStr);
			}
		}
		return res;
	}

	/**
	 * checkSatisfiablity of stmtStr
	 * 
	 * @param stmtStr
	 * @param globalPath
	 * @return
	 */
	public static String checkSatisfiablity(String stmtStr, List<Attribute> globalPath) {
		String res = "";
		HashMap<String, String> cfg = new HashMap<String, String>();
		cfg.put("model", "true");
		Context ctx = new Context(cfg);
		Solver s = null;
		try {
			s = ctx.mkSolver();
			BoolExpr stmt = ctx.mkAnd(ctx.parseSMTLIB2String(stmtStr, null, null, null, null));
			s.add(stmt);
			Status result = s.check();
			if (result == Status.SATISFIABLE) {
				res = s.getModel().toString();
			} else {
				res = "unsat!";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ctx.close();
			ctx = null;
			s = null;
			// System.gc();
		}

		return res;
	}

	/**
	 * create createSTMTFileList from globalPath
	 * 
	 * @param globalPath
	 * @return
	 */
	public static List<StringBuilder> createSTMTFileList(List<Attribute> globalPath) {
		List<StringBuilder> sbs = new ArrayList<StringBuilder>();
		sbs.add(new StringBuilder("(set-option :smt.string_solver z3str3)\n"));
		// !!!!
		sbs.add(new StringBuilder("(set-option :timeout 10000)\n"));
		for (Attribute att : globalPath) {
			ListIterator<StringBuilder> iterator = sbs.listIterator();
			while (iterator.hasNext()) {
				StringBuilder sb = iterator.next();
				String ss[] = att.getValue().split(", ");
				for (int i = 0; i < ss.length; i++) {
					if (i != ss.length - 1) {
						StringBuilder sb_new = new StringBuilder(sb);
						completeSb(att, sb_new, i);
						iterator.add(sb_new);
					} else {
						completeSb(att, sb, i);
					}
				}
			}
		}
		return sbs;
	}

	/**
	 * complete String builder
	 * 
	 * @param att
	 * @param sb
	 * @param i
	 */
	private static void completeSb(Attribute att, StringBuilder sb, int i) {
		if (!att.getType().equals("extra")) {
			addADTStmt2Sb(att, sb, i);
			// Utils.printInfo(att);
		}
	}

	/**
	 * add ADTStmt to String builder
	 * 
	 * @param att
	 * @param sb
	 * @param i
	 */
	public static void addADTStmt2Sb(Attribute att, StringBuilder sb, int i) {
		String declareStr = "(declare-const " + att.getType() + " String)\n";
		if (!sb.toString().contains(declareStr))
			sb.append(declareStr);
		String leftVal = getLeftCondition(att);
		String assertStr = "";
		if (att.getValue().length() == 0) {
			assertStr = "(= (str.len " + leftVal + ") 0)";
		} else {
			if (att.getCondition().equals("equals") || att.getCondition().equals("contentEquals")
					|| att.getCondition().equals("equalsIgnoreCase")) {
				String ss[] = att.getValue().split(", ");
				assertStr = "(= " + leftVal + " " + ss[i] + ")";
			} else if (att.getCondition().equals("contains")) {
				String ss[] = att.getValue().split(", ");
				assertStr = "(= " + leftVal + " " + ss[i] + ")";
			} else if (att.getCondition().equals("startsWith")) {
				String ss[] = att.getValue().split(", ");
				assertStr = " (str.prefixof " + ss[i] + " " + leftVal + ")";
			} else if (att.getCondition().equals("endsWith")) {
				String ss[] = att.getValue().split(", ");
				assertStr = " (str.suffixof " + ss[i] + " " + leftVal + ")";
				;
			} else if (att.getCondition().equals("contains")) {
				String ss[] = att.getValue().split(", ");
				assertStr = "(= " + leftVal + " " + ss[i] + ")";
			} else if (att.getCondition().equals("nullChecker")) {
				assertStr = "(= (str.len " + leftVal + ") 0)";
			} else if (att.getCondition().equals("isEmpty")) {
				assertStr = "(= (str.len " + leftVal + ") 0)";
			}
		}
		if (assertStr.length() > 0) {
			if (att.getIsSatisfy() > 0)
				assertStr = "(assert " + assertStr + ")\n";
			else
				assertStr = "(assert (not " + assertStr + "))\n";
		}
		sb.append(assertStr);
	}

	/**
	 * getLeftCondition of attribute
	 * 
	 * @param att
	 * @return
	 */
	private static String getLeftCondition(Attribute att) {
		String leftVal = att.getType();
		String conds[] = att.getConditionOfLeft().split(",");
		for (String cond : conds) {
			if (cond.length() == 0)
				continue;
			String condLeft = cond.split(" ")[0];
			if (condLeft.contains("substring")) {
				String[] ss = cond.split(" ");
				// (str.substr action 0 3)
				if (ss.length == 2) {
					int e = 0;
					try {
						e = att.getValue().length() - Integer.parseInt(ss[1]);
					} catch (Exception NumberFormatException) {
					}
					leftVal = "(str.substr " + leftVal + " " + ss[1] + " " + e + ")";
				} else {
					leftVal = "(str.substr " + leftVal + " " + ss[1] + " " + ss[2] + ")";
				}
			} else if (condLeft.contains("charAt")) {
				// (str.at action 2)
				String[] ss = cond.split(" ");
				leftVal = "(str.at " + leftVal + " " + ss[1] + ")";
				try {
					int charInt = 0;
					try {
						charInt = Integer.parseInt(att.getValue().replace("\"", ""));
					} catch (Exception NumberFormatException) {
					}
					char ch = (char) charInt;
					att.setValue("\"" + String.valueOf(ch) + "\"");
				} catch (NumberFormatException e) {
					// nothing
				}
			} else if (condLeft.contains("concat")) {
				// (str.++ action 2)
				String[] ss = cond.split(" ");
				leftVal = "(str.++ " + leftVal + " \"" + ss[1] + "\")";
			}
		}
		return leftVal;
	}
 }
