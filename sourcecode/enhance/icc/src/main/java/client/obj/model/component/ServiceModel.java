package main.java.client.obj.model.component;

import main.java.analyze.model.analyzeModel.AppModel;
import main.java.analyze.utils.ConstantUtils;

public class ServiceModel extends ComponentModel  {
	private static final long serialVersionUID = 3L;

	public ServiceModel(AppModel appModel) {
		super(appModel);
		type = "s";
	}

	@Override
	public String getComponentType() {
		return ConstantUtils.SERVICE;
	}
}
