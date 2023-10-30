package main.java.client.obj.model.component;

import heros.solver.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import main.java.analyze.utils.output.PrintUtils;

public class BackStack {
	private Stack<Pair<String, List<String>>> backStack;

	public BackStack() {
		setBackStack(new Stack<Pair<String, List<String>>>());
	}

	public boolean addComponent(String name, String status) {
		// 栈顶直接加上去，不管它是什么
		if (backStack.size() > 0) {
			if (getTopKey(1).equals(name)) {
				addStatus2Topn(1, status);
				return false;
			}
		}
		for (int i = 2; i <= backStack.size(); i++) {
			// 如果是第二层，只要不是结束相关的，就加到栈顶
			if (getTopKey(i).equals(name)) {
				if (status.equals("onDestroy") || status.equals("onStop")) {
					addStatus2Topn(i, status);
				} else {
					addStatus2Topn(i, status);
					Pair<String, List<String>> p = backStack.get(backStack.size() - i);
					backStack.remove(p);
					backStack.add(p);
				}
				return false;
			}
		}
		// 只要不是结束相关的，就加到栈顶
		if (status.equals("onDestroy") || status.equals("onStop")) {
			return false;
		}
		Pair<String, List<String>> p = new Pair<String, List<String>>(name, new ArrayList<String>());
		p.getO2().add(status);
		backStack.add(p);
		// if(backStack.size()==7)
		// backStack.remove(0);
		if (backStack.size() == 1)
			return false;
		return true;
	}

	public boolean addComponentBack(String name, String status) {
		if (status.equals("onDestroy") || status.equals("onStop") || status.equals("onPause")) {
			return false;
		}
		for (int i = backStack.size() - 1; i >= 0; i--) {
			String key = backStack.get(i).getO1();
			List<String> statuslist = backStack.get(i).getO2();
			if (key.equals(name)) {
				if (status.equals("onCreate")) {
					if (i == backStack.size() - 1) {
						statuslist.add(status);
						return false;
					} else if (backStack.size() > 1 && i == backStack.size() - 2) {
						statuslist.add(status);
						backStack.pop();
						return false;
					} else {
						break;
					}
				} else if (status.equals("onStart") || status.equals("onResume")) {
					if (i == backStack.size() - 1) {
						statuslist.add(status);
						return false;
					} else {
						break;
					}
				} else {
					return false;
				}

			}
		}
		Pair<String, List<String>> p = new Pair<String, List<String>>(name, new ArrayList<String>());
		p.getO2().add(status);
		backStack.add(p);
		if (backStack.size() == 5)
			backStack.remove(0);
		if (backStack.size() == 1)
			return false;
		return true;
	}

	/**
	 * @return the backStack
	 */
	public Stack<Pair<String, List<String>>> getBackStack() {
		return backStack;
	}

	/**
	 * @param backStack
	 *            the backStack to set
	 */
	public void setBackStack(Stack<Pair<String, List<String>>> backStack) {
		this.backStack = backStack;
	}

	public void addStatus2Topn(int n, String status) {
		List<String> statuslist = getTopList(n);
		if (!statuslist.get(statuslist.size() - 1).equals(status))
			statuslist.add(status);
	}

	public String getTopKey(int n) {
		if (backStack.size() > n - 1) {
			return backStack.get(backStack.size() - n).getO1();
		}
		return null;
	}

	public List<String> getTopList(int n) {
		if (backStack.size() > n - 1) {
			return backStack.get(backStack.size() - n).getO2();
		}
		return null;
	}

	@Override
	public String toString() {
		String res = "";
		for (int i = backStack.size() - 1; i >= 0; i--) {
			res += i + " {" + backStack.get(i).getO1() + "\t";
			res += "[" + PrintUtils.printList(backStack.get(i).getO2()) + "]}\n";
		}
		return res;
	}
}
