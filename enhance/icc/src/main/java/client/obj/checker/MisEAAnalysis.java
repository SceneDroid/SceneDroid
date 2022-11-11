package main.java.client.obj.checker;

import java.util.List;
import java.util.Map;

import main.java.Analyzer;
import main.java.Global;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.obj.model.component.ComponentModel;
import main.java.client.obj.model.component.Data;
import main.java.client.obj.model.component.EAStatus;
import main.java.client.obj.model.component.IntentFilterModel;
import main.java.client.obj.model.component.Launchable;

public class MisEAAnalysis  extends Analyzer {
	int[] patternArray;
	double[] patternArrayRatio;
	ComponentModel exportedModel;
	List<String> systemActCateList;
	Map<String, Integer> classInvokedMap;
	Map<String, Integer> classDeclareddMap;
	Map<String, Integer> actInvokedMap;
	
	public MisEAAnalysis() {
		this.systemActCateList = FileUtils.getListFromFile("data/systemActionAndCategory.txt");
		//the following three values are based on the results of a set of app, may not suit for new app
		this.classInvokedMap = FileUtils.getMapFromFile("data/invoked_class_result.txt");
		this.classDeclareddMap = FileUtils.getMapFromFile("data/class_result.txt");
		this.actInvokedMap = FileUtils.getMapFromFile("data/action_result.txt");
		patternArray = getPatternNum();
		patternArrayRatio = getPatternRatio(patternArray);
	}
	
	@Override
	public void analyze() {
		for(ComponentModel exportedModel: Global.v().getAppModel().getExportedComponentMap().values()){
			this.exportedModel = exportedModel;
			EAFeature feature = new EAFeature();
			
			//analyze manifest file
			analyzeManifestOther(exportedModel, feature);
			analyzeDebug(exportedModel, feature);
			analyzeclass_Declaration(exportedModel, feature); // not precise
			analyzeLaunchMode(exportedModel, feature);
			analyzePattern(patternArray, patternArrayRatio , feature);
			misexposeCheck(exportedModel, feature);
			
			System.out.println(exportedModel.getMisEAModel().toString());
		}
	}
	

	
	/**
	 * checking whether a component is misexported or not
	 * @param exportedModel 
	 * @param feature
	 */
	private void misexposeCheck(ComponentModel exportedModel, EAFeature feature) {
		feature.parseFeature();
		int patternId = getPatternIndex(exportedModel);
		//rule 1: sysActNoData and ifTrue and not exTrue -- MustIA
		if(feature.sysActNoData && feature.ifTrue && !feature.exTrue){
			exportedModel.getMisEAModel().setStatus(EAStatus.MustIA);
			exportedModel.getMisEAModel().setMisInfo("rule 1: sysActNoData and ifTrue and not exTrue -- MustIA"); 
		}
		//rule 2: noDefault and ifTrue and not exTrue -- MustIA
		else if(feature.noDefault && feature.ifTrue && !feature.exTrue){
			exportedModel.getMisEAModel().setStatus(EAStatus.MustIA);
			exportedModel.getMisEAModel().setMisInfo("rule 2: noDefault and ifTrue and not exTrue -- MustIA"); 
		}
		//rule 3: debug -- MustIA 
		else if(feature.debug){
			exportedModel.getMisEAModel().setStatus(EAStatus.MustIA);
			exportedModel.getMisEAModel().setMisInfo("rule 3: debug -- MustIA"); 
		}
		//rule 4: clsInvoke or actInvoke -- MustEA
		else if(feature.clsInvoke || feature.actInvoke ){
			exportedModel.getMisEAModel().setStatus(EAStatus.MustEA);
			exportedModel.getMisEAModel().setMisInfo("rule 4: clsInvoke or actInvoke -- MustEA"); 
		}
		//rule 5: clsDeclare -- MustEA
		else if(feature.clsDeclare){
			exportedModel.getMisEAModel().setStatus(EAStatus.MustEA);
			exportedModel.getMisEAModel().setMisInfo("rule 5: clsDeclare -- MustEA"); 
		}
		//rule 6: priority or permission -- MustEA
		if(feature.priority || feature.permission){
			exportedModel.getMisEAModel().setStatus(EAStatus.MustEA);
			exportedModel.getMisEAModel().setMisInfo("rule 6: priority or permission -- MustEA"); 
		}
		//rule 7: similar -- MustIA
		else if(patternId >=0 && patternArrayRatio[patternId]>0.4){
			exportedModel.getMisEAModel().setStatus(EAStatus.MustIA);
			exportedModel.getMisEAModel().setMisInfo("rule 7: similar -- MustIA"); 
		}
		//rule 8: highRatio -- MustIA
		else if(feature.highRatio){
			exportedModel.getMisEAModel().setStatus(EAStatus.MustIA);
			exportedModel.getMisEAModel().setMisInfo("rule 8: highRatio -- MustIA"); 
		}
		//rule 9: exTrue -- MayEA
		else if(feature.exTrue){
			exportedModel.getMisEAModel().setStatus(EAStatus.MayEA);
			exportedModel.getMisEAModel().setMisInfo("rule 9: exTrue -- MayEA"); 
		}
		//rule 10: ifTrue and not exTrue -- MustIA
		else if(feature.ifTrue && !feature.exTrue){
			exportedModel.getMisEAModel().setStatus(EAStatus.MustIA);
			exportedModel.getMisEAModel().setMisInfo("rule 10: ifTrue and not exTrue -- MustIA"); 
		}
	}

	
	/**
	 * analyzeManifestOther
	 * d1-d20
	 * @param mEA
	 * @param feature
	 */
	private void analyzeManifestOther(ComponentModel mEA, EAFeature feature) {
		String eaName = mEA.getComponetName();
		boolean hasData= false;
		feature.d1_EAnumber = appModel.getExportedComponentMap().size();
		feature.d2_EARatio = 1.0 * appModel.getExportedComponentMap().size()/appModel.getComponentMap().size();
		feature.d3_exTrue = exportedModel.getExported().equals("true");
		if(mEA.getIntentFilters()!=null && mEA.getIntentFilters().size()>0) {
			feature.d4_ifNum = mEA.getIntentFilters().size();
		}else{
			feature.d12_noDefault = false;
		}
		
		for (IntentFilterModel ifd : mEA.getIntentFilters()){
			int mainActFlag= 0; //2 is true 	
			if(ifd.getAction_list()==null || ifd.getAction_list().size()==0){
				continue;
			}
			for(String ac: ifd.getAction_list()){
				if(ac.equals("android.intent.action.MAIN"))	mainActFlag++;
				if(!systemActCateList.contains(ac)){
					feature.d6_nonsysActNum++;
					if(actInvokedMap.containsKey(eaName)) {
						feature.d34_actInvoke += actInvokedMap.get(eaName);
					}
				}else{
					feature.d5_sysActNum++;
				}
				if(ac.contains("test") ||ac.contains("debug") || ac.contains("Test") ||ac.contains("Debug"))
					feature.d20_action_debug = true;
			}
			for(String ca: ifd.getCategory_list()){
				if(ca.equals("android.intent.category.LAUNCHER"))	mainActFlag++;
				if(!systemActCateList.contains(ca)) {
					feature.d8_nonsysCateNum++;
				}else{
					feature.d7_sysCateNum++;
				}
				if(ca.equals("android.intent.category.DEFAULT"))	feature.d12_noDefault=false;
				if(ca.contains("test") ||ca.contains("debug") || ca.contains("Test") ||ca.contains("Debug"))
					feature.d20_cate_debug = true;
			}
			if(ifd.getData_list().size()>0 || ifd.getDatatype_list().size()>0){
				hasData=true;
				feature.d10_dataNum++;
				for(Data data: ifd.getData_list()){
					if(data.toString().contains("*"))
						feature.d11_dataRE = true;
				}
			}
			
			if(mainActFlag==2) feature.d9_mainAct =true;
			if(ifd.getPriority()!=null && !ifd.getPriority().equals("0")) feature.d18_priority =true;
		}
		
		if(feature.d4_ifNum>0 && !feature.d21_nonSysAct && !hasData ) 	feature.d13_sysActNoData =true;
		if( mEA.getPermission()!=null &&  !mEA.getPermission().equals(""))	feature.d19_permission = true;
	}
	
	/**
	 * analyzeLaunchMode
	 * d14-17
	 * @param mEA
	 * @param feature
	 */
	private void analyzeLaunchMode(ComponentModel mEA, EAFeature feature) {
		if(mEA instanceof Launchable){
			if(((Launchable)mEA).getLaunchMode().equals("singleTop"))
				feature.d14_singleTop = true;
			else if(((Launchable)mEA).getLaunchMode().equals("singleTask"))
				feature.d15_singleTask = true;
			else if(((Launchable)mEA).getLaunchMode().equals("singleInstance"))
				feature.d16_singleInstance = true;
			else
				feature.d17_standard = true;
		}
	}
	
	/**
	 * analyze whether is for Debugging
	 * d20
	 * @param mEA
	 * @param feature
	 */
	private void analyzeDebug(ComponentModel mEA, EAFeature feature) {
		String str = mEA.getComponetName();
		if(str.contains("test") ||str.contains("debug") || str.contains("Test") ||str.contains("Debug"))
			feature.d20_name_debug = true;	
	}

	/**
	 * analyzePattern
	 * d22-d31
	 * @param p_arry
	 * @param p_ratio_arry
	 * @param feature
	 */
	private void analyzePattern(int[] p_arry, double[] p_ratio_arry,
			EAFeature feature) {
		feature.d22_ex_only = p_arry[0];
		feature.d23_ex_if = p_arry[1];
		feature.d24_if_only_nonsys = p_arry[2];
		feature.d25_if_only_sys_data = p_arry[3];
		feature.d26_if_only_sys_nodata = p_arry[4];

		feature.d27_ex_only_ratio = p_ratio_arry[0];
		feature.d28_ex_if_ratio = p_ratio_arry[1];
		feature.d29_if_only_nonsys_ratio = p_ratio_arry[2];
		feature.d30_if_only_sys_data_ratio = p_ratio_arry[3];
		feature.d31_if_only_sys_nodata_ratio = p_ratio_arry[4];
	}
	/**
	 * 
	 * @param mEA
	 * @param feature
	 */
	private void analyzeclass_Declaration(ComponentModel mEA, EAFeature feature) {
		if(classDeclareddMap.containsKey(mEA.getComponetName())) {
//			System.out.println("classDeclareddMap " +eaName);
			feature.d32_clsDeclare += classDeclareddMap.get(mEA.getComponetName());
		}
		if(classInvokedMap.containsKey(mEA.getComponetName())){
//			System.out.println("clsInvclassdMap" +eaName);
			feature.d33_clsInvoke += classInvokedMap.get(mEA.getComponetName());
		}
		
	}
	
	/**
	 * the pattern of the component declaration
	 * @return
	 */
	private int[] getPatternNum() {
		int[] p_arr = {0,0,0,0,0};
		int p0_export_only = 0;
		int p1_export_intentFilter = 0;
		int p2_intentFilter_only_nonsys = 0;
		int p3_intentFilter_only_sys_data = 0;
		int p4_intentFilter_only_sys_nodata = 0;
		for(ComponentModel exportedModel: Global.v().getAppModel().getExportedComponentMap().values()){
			boolean p3=false, p4=false,p5=false;
			for (IntentFilterModel ifd :exportedModel.getIntentFilters()){
				boolean hasNonSysAct_Cate = false, hasData = false;
				
				for(String ac: ifd.getAction_list())
					if(!systemActCateList.contains(ac)) hasNonSysAct_Cate=true;
				if(ifd.getData_list().size()>0)
					hasData=true;
				if(hasNonSysAct_Cate ) p3 = true;
				if(!hasNonSysAct_Cate && hasData) p4= true;
				if(!hasNonSysAct_Cate && !hasData) p5= true;;
			}
			if(exportedModel.getExported()!=null && exportedModel.getExported().equals("true") ){
				if(exportedModel.getIntentFilters()==null) p0_export_only++;
				else if(exportedModel.getIntentFilters()!=null) p1_export_intentFilter++;
			}
			else if(p3 ) p2_intentFilter_only_nonsys++;
			else if(p4) p3_intentFilter_only_sys_data++;
			else if(p5) p4_intentFilter_only_sys_nodata++;
		} 
		p_arr[0] = p0_export_only;
		p_arr[1] = p1_export_intentFilter;
		p_arr[2] = p2_intentFilter_only_nonsys;
		p_arr[3] = p3_intentFilter_only_sys_data;
		p_arr[4] = p4_intentFilter_only_sys_nodata;
		return p_arr;
	}
	
	/**
	 * the pattern of the component declaration
	 * @return
	 */
	public int getPatternIndex(ComponentModel exportedModel) {
		boolean p2=false, p3=false,p4=false;
		for (IntentFilterModel ifd :exportedModel.getIntentFilters()){
			boolean hasNonSysAct_Cate = false, hasData = false;
			
			for(String ac: ifd.getAction_list())
				if(!systemActCateList.contains(ac)) hasNonSysAct_Cate=true;
			if(ifd.getData_list().size()>0)
				hasData=true;
			if(hasNonSysAct_Cate ) p2 = true;
			if(!hasNonSysAct_Cate && hasData) p3= true;
			if(!hasNonSysAct_Cate && !hasData) p4= true;;
		}
		if(exportedModel.getExported()!=null && exportedModel.getExported().equals("true") ){
			if(exportedModel.getIntentFilters()==null) return 0;
			else if(exportedModel.getIntentFilters()!=null) return 1;
		}
		else if(p2 ) return 2;
		else if(p3) return 3;
		else if(p4) return 4;
		return -1;
	}
	
	/**
	 * the ratio of each pattern of the component declaration
	 * @return
	 */
	private double[] getPatternRatio(int[] p_arry) {
		double[] p_ratio_arry = {0,0,0,0,0};
		int sum = 0;
		for(int x: p_arry) sum+=x;
		for(int i=0; i<p_ratio_arry.length;i++) {
			if(sum ==0 ) return null;
			else{
				p_ratio_arry[i] = 1.0* p_arry[i] / sum;
			}
		}
		return p_ratio_arry;
	}
}
