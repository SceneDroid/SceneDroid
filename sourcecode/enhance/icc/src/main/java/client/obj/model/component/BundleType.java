package main.java.client.obj.model.component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import main.java.client.obj.model.component.ExtraData;

/**
 * Intent attribute bundle
 * 
 * @author 79940
 *
 */
public class BundleType implements Serializable, Cloneable {
	private static final long serialVersionUID = 4L;
	private Map<String, List<ExtraData>> bundle;
	private Set<String> contentSet;
	private String type;
	private Set<ExtraData> extraDatas;
	
	public BundleType() {
		bundle = new HashMap<String, List<ExtraData>>();
		contentSet = new HashSet<String>();
		type = "";
		
	}

	public BundleType(Map<String, List<ExtraData>> bundle2, Set<String> contentSet2, String type2) {
		bundle = new HashMap<String, List<ExtraData>>(bundle2);
		contentSet = new HashSet<String>(contentSet2);
		type = type2;
	}

	public Map<String, List<ExtraData>> obtainBundle() {
		return bundle;
	}
	
	/**
	 * for json output
	 * @return
	 */
	public Set<ExtraData> getExtraDatas() {
		if(extraDatas == null)
			obtainExtraDatas();
		return extraDatas;
	}
	
	private void obtainExtraDatas() {
		extraDatas = new HashSet<ExtraData>();
		for(List<ExtraData> eds: bundle.values()){
			for(ExtraData ed: eds){
				extraDatas.add(ed);
			}
		}
		
	}

	public void setBundle(Map<String, List<ExtraData>> bundle) {
		this.bundle = bundle;
	}

	public void setContentSet(Set<String> contentSet) {
		this.contentSet = contentSet;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

//	public void dump() {
//		for (Entry<String, List<ExtraData>> en : bundle.entrySet()) {
//			for (ExtraData ed : en.getValue()) {
//				System.out.println("key: " + ed.getName());
//				if (ed.getType() instanceof String)
//					System.out.println("type: " + ed.getType());
//				else {
//					System.out.println("type: bundle");
//					((BundleType) ed.getType()).dump();
//				}
//			}
//		}
//	}

	@Override
	public String toString() {
		String res = "";
		for (Entry<String, List<ExtraData>> en : bundle.entrySet()) {
			for (ExtraData ed : en.getValue()) {
				if (!res.contains(ed.toString()))
					res += ed.toString() + ",";
			}
		}
		if (res.endsWith(","))
			res = res.substring(0, res.length() - 1);
		return res;
	}

	public void dump(BufferedWriter bw) {
		for (Entry<String, List<ExtraData>> en : bundle.entrySet()) {
			for (ExtraData ed : en.getValue()) {
				try {
					bw.write("key: " + ed.getName() + " ");
					if (ed.getType() instanceof String)
						bw.write("type: " + ed.getType() + System.getProperty("line.separator"));
					else {
						bw.write("type: bundle, key: " + ed.getType() + System.getProperty("line.separator"));
						((BundleType) ed.getType()).dump(bw);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public void put(String u, List<ExtraData> eds) {
		bundle.put(u, eds);
		for (ExtraData ed : eds)
			if (ed.getType() instanceof String)
				contentSet.add(ed.getType() + ed.getName());
			else
				contentSet.add("Bundle" + ed.getName());
	}

	// public ExtraData get(String u) {
	// return bundle.get(u);
	// }

	public void write_param_file(BufferedWriter bw) {
		Set<String> set = new HashSet<String>();
		for (Entry<String, List<ExtraData>> en : bundle.entrySet()) {
			for (ExtraData ed : en.getValue()) {
				try {
					if (ed.getType() instanceof String) {
						String info = ed.toString();
						if (!set.contains(info)) {
							bw.write(info + System.getProperty("line.separator"));
							set.add(info);
						}
					} else {
						String info = ed.toString();
						if (!set.contains(info)) {
							bw.write("Bundle " + ed.getName() + " " + ((BundleType) ed.getType()).bundle.size()
									+ System.getProperty("line.separator"));
							((BundleType) ed.getType()).write_param_file(bw);
							set.add(info);
						}
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private String getKeyValPairNormal() {
		String res = "";
		for (Entry<String, List<ExtraData>> en : bundle.entrySet()) {
			for (ExtraData ed : en.getValue()) {
				if (ed.getType() instanceof String) {
					switch ((String) ed.getType()) {
					case "String":
						res += "--es " + ed.getName() + " abcd ";
						break;
					case "Integer":
						res += "--ei " + ed.getName() + " 0 ";
						break;
					case "Boolean":
						res += "--ez " + ed.getName() + " true ";
						break;
					}
				} else {
					res += ((BundleType) ed.getType()).getKeyValPairNormal();
				}
			}
		}
		return res;
	}

	@Override
	public BundleType clone() throws CloneNotSupportedException {
		BundleType bt = new BundleType(bundle, contentSet, type);
		return bt;
	}
}
