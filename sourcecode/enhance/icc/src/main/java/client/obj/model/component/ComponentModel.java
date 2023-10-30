package main.java.client.obj.model.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import main.java.analyze.model.analyzeModel.AppModel;
import main.java.analyze.utils.output.PrintUtils;
import main.java.client.obj.model.ctg.IntentRecieveModel;
import main.java.client.obj.model.ctg.IntentSendModel;

/**
 * ComponentModel
 * 
 * @author 79940
 *
 */
public class ComponentModel implements Serializable {
	private static final long serialVersionUID = 7L;

	protected AppModel appModel;
	protected String componentName;
	protected String exported;
	protected String permission;
	protected List<IntentFilterModel> intentFilterList;
	protected String type;

	protected IntentRecieveModel receiveModel;
	protected IntentSendModel sendModel;
	
	protected MisExposeModel misEAModel;
	
	public ComponentModel(AppModel appModel) {
		this.appModel = appModel;
		exported = "";
		permission = "";
		intentFilterList = new ArrayList<IntentFilterModel>();
		receiveModel = new IntentRecieveModel();
		sendModel = new IntentSendModel();
		misEAModel = new MisExposeModel(this);
	}

	public Boolean is_mainAct() {
		if (appModel.getMainActivity().equals(componentName))
			return true;
		return false;
	}

	public Boolean is_exported() {
		boolean action = false;
		for (int i = 0; i < intentFilterList.size(); i++) {
			if (intentFilterList.get(i).getAction_list().size() > 0)
				action = true;
		}
		if (exported != null && exported.equals("true"))
			return true;
		if (exported == null || exported.equals(""))
			if (intentFilterList.size() > 0 && action == true)
				return true;
		return false;
	}

	@Override
	public String toString() {

		String res = "";
		res += "componentName: " + componentName + "\n";
		res += "exported: " + exported + "\n";
		res += "permission: " + permission + "\n";
		res += "intentFilterList: " + PrintUtils.printList(intentFilterList) + "\n";
		res += "receiveModel: " + receiveModel.toString() + "\n";
		res += "sendModel: " + sendModel.toString() + "\n";

		return res;
	}

	public AppModel getAppModel() {
		return appModel;
	}

	public String getComponetName() {
		return componentName;
	}

	public void setComponetName(String activityName) {
		this.componentName = activityName;
	}

	public String getExported() {
		return exported;
	}

	public void setExported(String exported) {
		this.exported = exported;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getComponentType() {
		return "unkown";
	}

	public void setComponentType(String type) {
		this.type = type;
	}

	public List<IntentFilterModel> getIntentFilters() {
		return intentFilterList;
	}

	public void addIntentFilter(IntentFilterModel filterModel) {
		this.intentFilterList.add(filterModel);
	}

	public IntentRecieveModel getReceiveModel() {
		return receiveModel;
	}

	public void setReceiveModel(IntentRecieveModel receiveModel) {
		this.receiveModel = receiveModel;
	}

	public IntentSendModel getSendModel() {
		return sendModel;
	}

	public void setSendModel(IntentSendModel sendModel) {
		this.sendModel = sendModel;
	}
	public MisExposeModel getMisEAModel() {
		return misEAModel;
	}

	public void setMisEAModel(MisExposeModel misEAModel) {
		this.misEAModel = misEAModel;
	}
}
