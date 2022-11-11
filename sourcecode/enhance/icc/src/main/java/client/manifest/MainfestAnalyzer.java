package main.java.client.manifest;

import java.util.HashMap;
import java.util.List;


//import jymbolic.android.resources.controls.ProcessManifest;
//import jymbolic.android.resources.xml.AXmlNode;
import main.java.Analyzer;
import main.java.Global;
import main.java.analyze.model.analyzeModel.AppModel;
import main.java.analyze.utils.SootUtils;
import main.java.client.obj.model.component.ActivityModel;
import main.java.client.obj.model.component.BroadcastReceiverModel;
import main.java.client.obj.model.component.ComponentModel;
import main.java.client.obj.model.component.ContentProviderModel;
import main.java.client.obj.model.component.Data;
import main.java.client.obj.model.component.IntentFilterModel;
import main.java.client.obj.model.component.ServiceModel;
import soot.jimple.infoflow.android.axml.AXmlNode;
import soot.jimple.infoflow.android.manifest.ProcessManifest;

/**
 * This class is used to parse a manifest XML file Extract all the exported
 * components (Activity, Service, and Receiver)
 * 
 * @author hanada
 *
 */
public class MainfestAnalyzer extends Analyzer {
	private ProcessManifest manifestManager;

	public MainfestAnalyzer() {
		super();
	}

	/**
	 * get information from manifest file e.g., activity list, exported
	 * attributes
	 */
	@Override
	public void analyze() {
		try {
			manifestManager = new ProcessManifest(appModel.getAppPath());
			appModel.setManifestString(getManifestString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("no manifest is available.");
		}
		if (manifestManager == null)
			return;
		String pkg = manifestManager.getPackageName();
		// the ".debug" will cause the imprecise of -exlib
		if (pkg.endsWith(".debug"))
			pkg = pkg.replace(".debug", "");
		appModel.setPackageName(pkg);
		appModel.getExtendedPakgs().add(pkg);
		appModel.setVersionCode(manifestManager.getVersionCode());
		AXmlNode appNode = manifestManager.getApplication();
		// get permissions
		if (appNode.getAttribute("permission") != null) {
			appModel.setPermission(appNode.getAttribute("permission").getValue().toString());// which
																								// permission?
		}
		appModel.setUsesPermissionSet(manifestManager.getPermissions());

		parseComponent(manifestManager.getActivities(), "Activity");
		parseComponent(manifestManager.getServices(), "Service");
		parseComponent(manifestManager.getProviders(), "Provider");
		parseComponent(manifestManager.getReceivers(), "Receiver");

		mergeAllComponents();

		List<AXmlNode> alis = appNode.getChildrenWithTag("activity-alias");
		for (AXmlNode actNode : alis) {
			String name = actNode.getAttribute("targetActivity").getValue().toString();
			if (appModel.getActivityMap().containsKey(name)) {
				ActivityModel actModel = (ActivityModel) appModel.getActivityMap().get(name);
				analyzeIntentFilter(actModel, actNode);
			}
		}

		// get EAs
		for (ComponentModel component : appModel.getComponentMap().values()) {
			if (component.is_exported()) {
				appModel.getExportedComponentMap().put(component.getComponetName(), component);
			}
		}

	}

	/**
	 * parse activity + service + contentProvider + broadcastReceiver node in
	 * manifest
	 */
	private void parseComponent(List<AXmlNode> components, String type) {
		// get components
		HashMap<String, ComponentModel> componentMap = getComponentMap(type);
		for (AXmlNode componentNode : components) {
			// ignore the component whose "enabled" attribute is "false"
			if (componentNode.hasAttribute("enabled")) {
				if (componentNode.getAttribute("enabled").getValue().toString().equals("")
				 || componentNode.getAttribute("enabled").getValue().toString().equals("false")) {
					continue;
				}
			}
			
			// new ActivityData instance
			String componentName = componentNode.getAttribute("name").getValue().toString();
			if (!Global.v().getAppModel().getApplicationClassNames().contains(componentName)) {
				if (!componentName.contains(appModel.getPackageName())) {
					if (componentName.startsWith(".")) {
						componentName = appModel.getPackageName() + componentName;
					} else {
						componentName = appModel.getPackageName() + "." + componentName;
					}
				}
			}
			// Exclude the activities not declared in app's package.
			// if (!MyConfig.getInstance().getMySwithch().isLibCodeSwitch()&&
			// !componentName.contains(appModel.getPackageName())) // out of
			// package
			// continue;

			// add external libs according to component decalartion
			if (!componentName.contains(appModel.getPackageName())) {
				String ss[] = componentName.split("\\.");
				if (ss.length >= 2)
					appModel.getExtendedPakgs().add(ss[0] + "." + ss[1]);
			}
			ComponentModel componentModel = getComponentModel(type, componentName);
			if (componentModel == null)
				continue;
			componentModel.setComponetName(componentName);

			synchronized (componentMap) {
				if (!componentMap.containsKey(componentModel.getComponetName())) {
					componentMap.put(componentModel.getComponetName(), componentModel);
				}
			}

			// get the attributes of the activity element
			if (componentNode.getAttribute("exported") != null)
				componentModel.setExported(componentNode.getAttribute("exported").getValue().toString());

			if (componentNode.getAttribute("permission") != null)
				componentModel.setPermission(componentNode.getAttribute("permission").getValue().toString());

			if (componentModel instanceof ActivityModel && componentNode.getAttribute("launchMode") != null)
				((ActivityModel) componentModel).setLaunchMode(componentNode.getAttribute("launchMode").getValue()
						.toString());

			if (componentModel instanceof ActivityModel && componentNode.getAttribute("taskAffinity") != null)
				((ActivityModel) componentModel).setTaskAffinity(componentNode.getAttribute("taskAffinity").getValue()
						.toString());

			else if (componentModel instanceof ActivityModel)
				((ActivityModel) componentModel).setTaskAffinity(appModel.getPackageName());

			analyzeIntentFilter(componentModel, componentNode);
		}

	}

	/**
	 * merge All Components
	 */
	private void mergeAllComponents() {
		appModel.setComponentMap(appModel.getActivityMap());
		appModel.setComponentMap(appModel.getServiceMap());
		// appModel.setComponentMap(appModel.getProviderMap());
		appModel.setComponentMap(appModel.getRecieverMap());
	}

	/**
	 * getComponentMap for type
	 * 
	 * @param type
	 * @return
	 */
	private HashMap<String, ComponentModel> getComponentMap(String type) {
		switch (type) {
		case "Activity":
			return appModel.getActivityMap();
		case "Service":
			return appModel.getServiceMap();
		case "Provider":
			return appModel.getProviderMap();
		case "Receiver":
			return appModel.getRecieverMap();
		}
		return null;
	}

	/**
	 * getComponentModel for type
	 * 
	 * @param type
	 * @param componentName
	 * @return
	 */
	private ComponentModel getComponentModel(String type, String componentName) {
		if (SootUtils.getSootClassByName(componentName) == null)
			return null;
		switch (type) {
		case "Activity":
			if (appModel.getActivityMap().containsKey(componentName))
				return appModel.getActivityMap().get(componentName);
			return new ActivityModel(appModel);
		case "Service":
			if (appModel.getServiceMap().containsKey(componentName))
				return appModel.getServiceMap().get(componentName);
			return new ServiceModel(appModel);
		case "Receiver":
			if (appModel.getRecieverMap().containsKey(componentName))
				return appModel.getRecieverMap().get(componentName);
			return new BroadcastReceiverModel(appModel);
		case "Provider":
			if (appModel.getProviderMap().containsKey(componentName))
				return appModel.getProviderMap().get(componentName);
			return new ContentProviderModel(appModel);
		}
		return null;
	}

	private String getManifestString() {
		return manifestManager.getAXml().toString();
	}

	public AppModel getAppMode() {
		return appModel;
	}

	/**
	 * analcomponent each intent-filter
	 * 
	 * @param componentModel
	 * @param componentNode
	 */
	private void analyzeIntentFilter(ComponentModel componentModel, AXmlNode componentNode) {
		// traverse the child elements of the activity element
		for (AXmlNode ifNode : componentNode.getChildren()) {
			if (ifNode.getTag().equals("intent-filter")) {
				// new IntentFilterData instance
				IntentFilterModel ifd = new IntentFilterModel();
				componentModel.addIntentFilter(ifd);
				if (ifNode.getAttribute("priority") != null)
					ifd.setPriority(ifNode.getAttribute("priority").getValue().toString());

				// traverse child elements of the intent filter element
				for (AXmlNode childNode : ifNode.getChildren()) {
					if (childNode.getTag().equals("action")) {
						String action = childNode.getAttribute("name").getValue().toString();
						ifd.getAction_list().add(action);
					}
					if (childNode.getTag().equals("category")) {
						String category = childNode.getAttribute("name").getValue().toString();
						ifd.getCategory_list().add(category);
					}
					if (ifd.getAction_list().contains("android.intent.action.MAIN")
							&& ifd.getCategory_list().contains("android.intent.category.LAUNCHER")) {
						appModel.setMainActivity(componentModel.getComponetName());
					}

					if (childNode.getTag().equals("data")) {
						Data data = new Data();
						if (childNode.getAttribute("scheme") != null)
							data.setScheme(childNode.getAttribute("scheme").getValue().toString());
						if (childNode.getAttribute("host") != null)
							data.setHost(childNode.getAttribute("host").getValue().toString());
						if (childNode.getAttribute("port") != null)
							data.setPort(childNode.getAttribute("port").getValue().toString());
						if (childNode.getAttribute("path") != null)
							data.setPath(childNode.getAttribute("path").getValue().toString());
						if (childNode.getAttribute("mimeType") != null)
							ifd.getDatatype_list().add(childNode.getAttribute("mimeType").getValue().toString());
						if (data.toString().length() > 0)
							ifd.getData_list().add(data);
					}
				}
			}
		}
	}
}
