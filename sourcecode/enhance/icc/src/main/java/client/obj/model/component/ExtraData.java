package main.java.client.obj.model.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.annotation.JSONField;

import main.java.analyze.utils.IntUtil;
import main.java.analyze.utils.SootUtils;
import main.java.analyze.utils.output.PrintUtils;
import main.java.client.obj.model.component.BundleType;
import main.java.client.obj.model.component.ExtraData;

public class ExtraData implements Serializable, Cloneable {
	private static final long serialVersionUID = 4L;
	@JSONField
	private Object type;
	private ExtraData parent;
	@JSONField
	private String name;
	private String eaName;
	private String objName;
	@JSONField
	private final int id = ++IntUtil.extraDataId;
	@JSONField
	private int parentId;
	@JSONField
	private List<String> values;
	
	public ExtraData() {
		this.setName("");
		this.eaName = "";
		this.setObjName("");
		this.setValues(new ArrayList<String>());

	}
	public ExtraData(String name) {
		super();
		this.setName(name);
	}

	public ExtraData(BundleType bundleType, String string, String eaName2, String string2, List<String> list, ExtraData parent2) {
		this.setName("");
		this.eaName = "";
		this.setParent(parent2);
//		this.setValue(new ArrayList<String>());

	}

	public ExtraData(Object type2, String name2, String eaName2, String objName2, List<String> value2, ExtraData parent2) {
		this.setType(type2);
		this.setName(name2);
		this.eaName = eaName2;
		this.setParent(parent2);
//		this.setValue(new ArrayList<String>());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object obtainExtraDataType() {
		return type;
	}
	

	
	public Object getType() {
		if(type instanceof BundleType)
			return "bundle";
		else
			return type;
	}
	public void setType(Object type) {
		this.type = type;
	}

	public String getObjName() {
		return objName;
	}

	public void setObjName(String objName) {
		this.objName = objName;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public Object getBody() {
		if(type instanceof BundleType)
			return ((BundleType)type).getExtraDatas();
		return null; 
	}
	@Override
	public String toString() {
		String res = "";
		String n = "what";
		if (getName() == null)
			n = "null";
		else
			n = getName();
		String valueStr = "";
		// extra value, only send icc has value, receive icc has candidate
		if (getValues()!=null && getValues().size() > 0)
			valueStr = ":" + PrintUtils.printList(getValues());

		if (obtainExtraDataType() == null) {
			res = "null-" + n + valueStr;
		} else if (obtainExtraDataType() instanceof String) {
			if (SootUtils.isParOrSerExtra((String) obtainExtraDataType()))
				res = obtainExtraDataType() + "@" + getObjName() + "-" + n + valueStr + ",";
			else
				res = obtainExtraDataType() + "-" + n + valueStr + ",";
		} else {
			BundleType bt = (BundleType) obtainExtraDataType();
			res = bt.getType() + "-" + n + ",(," + bt.toString() + "),";
		}
		return res;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		ExtraData ed;
		if(obtainExtraDataType() instanceof BundleType){
			BundleType bt = (BundleType) obtainExtraDataType();
			ed = new ExtraData(bt.clone(), getName(), eaName, getObjName(), getValues(), parent);
		}
		else{
			ed = new ExtraData(obtainExtraDataType(), getName(), eaName, getObjName(), getValues(),parent);
		}
		return ed;
	}
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj){
            return true;
        }
        if(obj == null){
            return false;
        }

        if(obj instanceof ExtraData){
        	ExtraData other = (ExtraData) obj;
            if(this.toString().equals(other.toString())){
                return true;
            }
        }

        return false;
	}
	
	public boolean covers(Object obj) {
		if(this == obj){
            return true;
        }
        if(obj == null){
            return false;
        }
        if(obj instanceof ExtraData){
        	ExtraData extraDataObj = (ExtraData) obj;
        	String thisStr = this.toString().replace(",", "");
        	String objStr = extraDataObj.toString().replace(",", "");
            if(thisStr.contains(objStr)){
                return true;
            }else if(this.obtainExtraDataType() instanceof BundleType && extraDataObj.obtainExtraDataType() instanceof BundleType){
            	Set<ExtraData> thisExtraDatas = ((BundleType) this.obtainExtraDataType()).getExtraDatas();
            	Set<ExtraData> extraDataObjExtraDatas = ((BundleType) extraDataObj.obtainExtraDataType()).getExtraDatas();
            	int coverdNum = 0;
            	for(ExtraData newExtraData : extraDataObjExtraDatas){
        			for(ExtraData oldExtraData : thisExtraDatas){
        				if(oldExtraData.covers(newExtraData)){
        					coverdNum++;
        					break;
        				}
        					
        			}
            	}
            	if(coverdNum == extraDataObjExtraDatas.size())
            		return true;
            }
        }

        return false;
	}
	/**
	 * merge two set of extra data
	 * @param oldSet
	 * @param newSet
	 */
	public static void merge(Set<ExtraData> oldSet, Set<ExtraData> newSet) {
		for(ExtraData newExtraData : newSet){
			addNewData2OldSet(oldSet,newExtraData);
		}
	}

	private static void addNewData2OldSet(Set<ExtraData> oldSet, ExtraData newExtraData) {
		boolean addEle = true;
		for(ExtraData oldExtraData : oldSet){
			if(oldExtraData.covers(newExtraData)){//do not add new one into oldSet, terminate add
				return; 
			}else if(newExtraData.covers(oldExtraData)){//replace the old one with the new, continue loop
				oldSet.remove(oldExtraData);
				oldSet.add(newExtraData);
				addEle = false; // do not add twice
			}else if(oldExtraData.obtainExtraDataType() instanceof BundleType 
						&& newExtraData.obtainExtraDataType() instanceof BundleType){
					try {
						ExtraData mixExtraData = (ExtraData) oldExtraData.clone();
						BundleType newBt = (BundleType)newExtraData.obtainExtraDataType();
						BundleType mixBt = (BundleType)mixExtraData.obtainExtraDataType();
						if(newBt!=null && mixBt!=null){
							merge( mixBt.getExtraDatas(), newBt.getExtraDatas());
							oldSet.remove(oldExtraData);
							oldSet.add(mixExtraData);
						}
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
					return;
				}
		}
		if(addEle) {
			oldSet.add(newExtraData);
		}
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @param parent the parent to set
	 */
	public void setParent(ExtraData parent) {
		this.parent = parent;
		if(parent==null)
			this.setParentId(0);
		else {
			this.setParentId(parent.getId());
		}
	}

	/**
	 * @return the parentId
	 */
	public int getParentId() {
		return parentId;
	}

	/**
	 * @param parentId the parentId to set
	 */
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

}
