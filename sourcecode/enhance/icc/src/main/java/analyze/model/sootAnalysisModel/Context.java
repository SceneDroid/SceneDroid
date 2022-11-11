package main.java.analyze.model.sootAnalysisModel;

import java.util.ArrayList;
import java.util.List;

/**
 * contexts of code during soot analysis
 * 
 * @author 79940
 *
 */
public class Context {
	private List<NestableObj> objs = new ArrayList<NestableObj>();

	public List<NestableObj> getObjs() {
		return objs;
	}

	public void addObj(NestableObj o) {
		objs.add(o);
	}

	public boolean isEmpty() {
		for (NestableObj obj : getObjs()) {
			if (obj.getValues().size() > 0)
				return false;
		}
		return true;
	}
}
