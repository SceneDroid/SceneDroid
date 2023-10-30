package main.java.client.obj.model.component;

import main.java.analyze.model.analyzeModel.AppModel;
import main.java.analyze.utils.ConstantUtils;

public class ContentProviderModel extends ComponentModel {
	private static final long serialVersionUID = 3L;

	public ContentProviderModel(AppModel appModel) {
		super(appModel);
		type = "p";
	}

	@Override
	public String getComponentType() {
		return ConstantUtils.PROVIDER;
	}
}
