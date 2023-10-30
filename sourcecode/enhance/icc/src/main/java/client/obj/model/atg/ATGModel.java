package main.java.client.obj.model.atg;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.security.auth.x500.X500Principal;

import main.java.Global;
import main.java.MyConfig;
import main.java.analyze.model.labeledOracleModel.IccTag;
import main.java.analyze.model.labeledOracleModel.IccTagCount;
import main.java.analyze.model.labeledOracleModel.LabeledOracleModel;
import main.java.analyze.utils.SootUtils;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.obj.model.component.ComponentModel;

public class ATGModel {
	private Map<String, Set<AtgEdge>> atgEdges;
	private String ATGFilePath;
	
	
	
	private int enhancedNum;
	private int filteredNum;
	private int filteredServiceNum;
	private int filteredReceiverNum;
	private boolean exist = true;
	
	private int Comp2CompSize;
	private int Act2ActSize;
	
	private int totalCompNum = 0;
	private int separatedCompNum = 0;
	private int mainNotReachableCompNum = 0;
	private int exportNotReachableCompNum = 0;
	
	private int oracleEdgeSize = 0;
	private int fnEdgeSize = 0;
	private double falsenegativeScore = 0.0;
	private Set<String> FNSet ;
	private Set<String> TPSet ;
	private Set<String> edgeSet ;
	
	public ATGModel() {
		atgEdges = new HashMap<String, Set<AtgEdge>>();
		edgeSet = new HashSet<String>();
	}
	

	public void evaluateGraphCount(String tag) {
//		System.out.println(tag + " totalCompNum: " + getTotalCompNum());
//		System.out.println(tag + " separatedCompNum: " + getSeparatedCompNum());
//		System.out.println(tag + " mainNotReachableCompNum: " + getMainNotReachableCompNum());
//		System.out.println(tag + " exportNotReachableCompNum: " + getExportNotReachableCompNum());
	}

	/**
	 * evaluate the false negetive info compare with an oracle
	 * 
	 */
	public double evaluateFalseNegative(String tag) {
		IccTagCount counter = new IccTagCount();
		
		computeFalseNegative(counter);
//
//		System.out.println(tag + " false negative number: " + getFnEdgeSize());
//		System.out.println(tag + " oracle number: " + getOracleEdgeSize());
//		System.out.println(tag + " false negative ratio: " + getFalsenegativeScore());
//
//		for (String fn : FNSet) {
//			System.out.println("false negative: "+fn);
//		}
//		System.out.println(counter);
		
		File f = new File(MyConfig.getInstance().getResultWarpperFolder()  + File.separator + "tagResult.txt");
		if (!f.exists())
			FileUtils.writeText2File(MyConfig.getInstance().getResultWarpperFolder() + File.separator + "tagResult.txt", "\t\t" + counter.getTitle() + "\n",
					true);
		if (isExist())
			FileUtils.writeText2File(MyConfig.getInstance().getResultWarpperFolder() + File.separator + "tagResult.txt", Global.v().getAppModel()
					.getAppName()
					+ "\t" + tag + "\t" + counter.toSimpleString() + "\n", true);
		else {
			FileUtils.writeText2File(MyConfig.getInstance().getResultWarpperFolder() + File.separator + "tagResult.txt", Global.v().getAppModel()
					.getAppName()
					+ "\t" + tag + "\n", true);
		}
		return falsenegativeScore;
	}
	
	/**
	 * compute FN
	 * @param counter
	 */
	private void computeFalseNegative(IccTagCount counter) {
		FNSet = new HashSet<>();
		TPSet = new HashSet<>();
		ATGModel oracle = Global.v().getiCTGModel().getOracleModel();
		Set<String> myEdges = new HashSet<>();
		for (Entry<String, Set<AtgEdge>> entry : atgEdges.entrySet()) {
			for (AtgEdge myEdge : entry.getValue()) {
				myEdges.add(myEdge.getDescribtion());
			}
		}
		for (Entry<String, Set<AtgEdge>> entry : oracle.getAtgEdges().entrySet()) {
			for (AtgEdge oracleEdge : entry.getValue()) {
				if (!myEdges.contains(oracleEdge.getDescribtion()) && !FNSet.contains(oracleEdge.getDescribtion())) {
					FNSet.add(oracleEdge.getDescribtion());
					modifyTagCounter(counter, oracleEdge.getDescribtion());
				} else if (myEdges.contains(oracleEdge.getDescribtion()) && !TPSet.contains(oracleEdge.getDescribtion())){
					TPSet.add(oracleEdge.getDescribtion());
				}
			}
		}
		
		fnEdgeSize = FNSet.size();
		falsenegativeScore = 1.0 * fnEdgeSize / getOracleEdgeSize();
	}

	public Set<String> getFNSet() {
		if(FNSet==null){
			computeFalseNegative(new IccTagCount());
		}
		return FNSet;
	}

	public Set<String> getTPSet() {
		if(TPSet==null){
			computeFalseNegative(new IccTagCount());
		}
		return TPSet;
	}
	public Set<String> getEdgeSet() {
		if(edgeSet==null){
			getComp2CompSize();
		}
		return edgeSet;
	}
	
	/**
	 * get the number of ICCs from comp to comp
	 * @return
	 */
	public int getComp2CompSize() {
		if (Comp2CompSize > 0)
			return Comp2CompSize;
		for (Set<AtgEdge> edges : atgEdges.values()) {
			for (AtgEdge edge : edges) {
				if (!edgeSet.contains(edge.getDescribtion())) {
					edgeSet.add(edge.getDescribtion());
					Comp2CompSize += 1;
				}
			}
		}
		return Comp2CompSize;
	}

	/**
	 * get the number of ICCs from act to act
	 * @return the act2ActSize
	 */
	public int getAct2ActSize() {
		if (Act2ActSize > 0)
			return Act2ActSize;
		Set<String> history = new HashSet<String>();
		for (Set<AtgEdge> edges : atgEdges.values()) {
			for (AtgEdge edge : edges) {
				if (!history.contains(edge.getDescribtion())) {
					history.add(edge.getDescribtion());
					if(edge.getType().equals(AtgType.Act2Act))
						Act2ActSize += 1;
				}
			}
		}
		return Act2ActSize;
	}

	/**
	 * @return the totalCompNum
	 */
	public int getTotalCompNum() {
		if (totalCompNum > 0)
			return totalCompNum;
		totalCompNum = Global.v().getAppModel().getComponentMap().size();
		return totalCompNum;
	}

	/**
	 * @return the separatedCompNum
	 */
	public int getSeparatedCompNum() {
		if (separatedCompNum > 0)
			return separatedCompNum;
		Set<String> notSeparatdSet = new HashSet<String>();
		for (Entry<String, Set<AtgEdge>> entry : atgEdges.entrySet()) {
			for (AtgEdge myEdge : entry.getValue()) {
				if (Global.v().getAppModel().getComponentMap().keySet().contains(myEdge.getSource().getClassName()))
					notSeparatdSet.add(myEdge.getSource().getClassName());
				if (Global.v().getAppModel().getComponentMap().keySet().contains(myEdge.getDestnation().getClassName()))
					notSeparatdSet.add(myEdge.getDestnation().getClassName());
			}
		}
		separatedCompNum = getTotalCompNum() - notSeparatdSet.size();
		return separatedCompNum;
	}
	
	/**
	 * @return the mainNotReachableCompNum
	 */
	public int getMainNotReachableCompNum() {
		if (mainNotReachableCompNum > 0)
			return mainNotReachableCompNum;
		Set<String> mainSet = new HashSet<String>();
		for (ComponentModel component : Global.v().getAppModel().getComponentMap().values()) {
			if (component.is_mainAct()) {
				mainSet.add(component.getComponetName());
			}
		}
		mainNotReachableCompNum = getTotalCompNum() - getnumberofCompFromEntrySet(mainSet);
		return mainNotReachableCompNum;
	}

	
	/**
	 * @return the exportNotReachableCompNum
	 */
	public int getExportNotReachableCompNum() {
		if (exportNotReachableCompNum > 0)
			return exportNotReachableCompNum;
		Set<String> exportSet = new HashSet<String>();
		for (ComponentModel component : Global.v().getAppModel().getComponentMap().values()) {
			if (component.is_exported()) {
				exportSet.add(component.getComponetName());
			}
		}
		exportNotReachableCompNum = getTotalCompNum() - getnumberofCompFromEntrySet(exportSet);
		return exportNotReachableCompNum;
	}

	/**
	 * how many components could be reached form an entry
	 * @param reachable
	 * @return
	 */
	private int getnumberofCompFromEntrySet(Set<String> reachable) {
		int sizeBefore = 0, sizeAfter = sizeBefore - 1;
		while (sizeBefore != sizeAfter) {
			sizeBefore = reachable.size();
			for (String exported : new HashSet<String>(reachable)) {
				if (atgEdges.containsKey(exported)) {
					for (AtgEdge edge : atgEdges.get(exported)) {
						if (!reachable.contains(edge.getDestnation().getName())) {
							reachable.add(edge.getDestnation().getName());
						}
					}
				}
			}
			sizeAfter = reachable.size();
		}
		return sizeAfter;
	}







	/**
	 * edge information count
	 */
	public void countTagForOracle() {
		ATGModel oracleModel = Global.v().getiCTGModel().getOracleModel();
		IccTagCount counter = new IccTagCount();
		Set<String> oracleEdges = new HashSet<String>();
		for (Entry<String, Set<AtgEdge>> entry : oracleModel.getAtgEdges().entrySet()) {
			for (AtgEdge oracleEdge : entry.getValue()) {
				oracleEdges.add(oracleEdge.getDescribtion());
				modifyTagCounter(counter, oracleEdge.getDescribtion());
			}
		}

		File f = new File(MyConfig.getInstance().getResultWarpperFolder() + File.separator + "tagResult.txt");
		if (!f.exists())
			FileUtils.writeText2File(MyConfig.getInstance().getResultWarpperFolder()+ File.separator + "tagResult.txt", "\t\t" + counter.getTitle() + "\n",true);
		if (isExist())
			FileUtils.writeText2File(MyConfig.getInstance().getResultWarpperFolder() + File.separator + "tagResult.txt", Global.v().getAppModel().getAppName()
					+ "\t" + "oracle       " + "\t" + counter.toSimpleString() + "\n", true);
		else {
			FileUtils.writeText2File(MyConfig.getInstance().getResultWarpperFolder() + File.separator + "tagResult.txt", Global.v().getAppModel().getAppName()
					+ "\t" + "oracle       " + "\n", true);
		}
		return;
	}

	/**
	 * modifyTagCounter statistic
	 * @param counter
	 * @param key
	 */
	private void modifyTagCounter(IccTagCount counter, String key) {
		LabeledOracleModel oracleModel = Global.v().getLabeledOracleModel();
		IccTag iccTag = oracleModel.getLabeledOracle().get(key);
		if (iccTag == null)
			return;
		if (iccTag.isLifeCycle())
			counter.isLifeCycle += 1;
		if (iccTag.isStaticCallBack())
			counter.isStaticCallBack += 1;
		if (iccTag.isDynamicCallBack())
			counter.isDynamicCallBack += 1;
		if (iccTag.isImpliciyCallBack())
			counter.isImpliciyCallBack += 1;
		if (iccTag.isLifeCycleOnly())
			counter.isLifeCycleOnly += 1;
		if (iccTag.isCallBackInclude())
			counter.isCallBackInclude += 1;

		if (iccTag.isNormalSendICC())
			counter.isNormalSendICC += 1;
		if (iccTag.isWarpperSendICC())
			counter.isWarpperSendICC += 1;

		if (iccTag.isExplicit())
			counter.isExplicit += 1;
		if (iccTag.isImplicit())
			counter.isImplicit += 1;

		if (iccTag.isActivity())
			counter.isActivity += 1;
		if (iccTag.isService())
			counter.isService += 1;
		if (iccTag.isBroadCast())
			counter.isBroadCast += 1;
		if (iccTag.isDynamicBroadCast())
			counter.isDynamicBroadCast += 1;
		if (iccTag.isNonActivity())
			counter.isNonActivity += 1;

		if (iccTag.isFragment())
			counter.isFragment += 1;
		if (iccTag.isAdapter())
			counter.isAdapter += 1;
		if (iccTag.isWidget())
			counter.isWidget += 1;
		if (iccTag.isOtherClass())
			counter.isOtherClass += 1;
		if (iccTag.isNonComponent())
			counter.isNonComponent += 1;

		if (iccTag.isLibraryInvocation())
			counter.isLibraryInvocation += 1;
		if (iccTag.isAsyncInvocation())
			counter.isAsyncInvocation += 1;
		if (iccTag.isPolymorphic())
			counter.isPolymorphic += 1;

		if (iccTag.isStaticVal())
			counter.isStaticVal += 1;
		if (iccTag.isStringOp())
			counter.isStringOp += 1;

		if (iccTag.isFlowSensitive())
			counter.isFlowSensitive += 1;
		if (iccTag.isPathSensitive())
			counter.isPathSensitive += 1;
		if (iccTag.isContextSensitive())
			counter.isContextSensitive += 1;
		if (iccTag.isFieldSensitive())
			counter.isFieldSensitive += 1;
		if (iccTag.isObjectSensitive())
			counter.isObjectSensitive += 1;

		if (iccTag.isStaticCallBackonly())
			counter.isStaticCallBackonly += 1;
		if (iccTag.isDynamicCallBackonly())
			counter.isDynamicCallBackonly += 1;
		if (iccTag.isImplicitCallBackonly())
			counter.isImplicitCallBackonly += 1;

		if (iccTag.isWarrperonly())
			counter.isWarrperonly += 1;
		if (iccTag.isImplicitICConly())
			counter.isImplicitICConly += 1;

		if (iccTag.isNonComponentonly())
			counter.isNonComponentonly += 1;
		if (iccTag.isFragmentonly())
			counter.isFragmentonly += 1;
		if (iccTag.isAdapteronly())
			counter.isAdapteronly += 1;
		if (iccTag.isWidgetonly())
			counter.isWidgetonly += 1;
		if (iccTag.isOtherClassonly())
			counter.isOtherClassonly += 1;
		if (iccTag.isContextSensionly())
			counter.isContextSensionly += 1;
		if (iccTag.isLibonly())
			counter.isLibonly += 1;
		if (iccTag.isNonActonly())
			counter.isNonActonly += 1;
		if (iccTag.isAsynconly())
			counter.isAsynconly += 1;
		if (iccTag.isPolymonly())
			counter.isPolymonly += 1;
	}


	
	public Map<String, Set<AtgEdge>> getAtgEdges() {
		return atgEdges;
	}

	public void setAtgEdges(Map<String, Set<AtgEdge>> atgEdges) {
		this.atgEdges = atgEdges;
	}

	/**
	 * ATG Construction
	 * 
	 * @param source
	 * @param destination
	 */
	public boolean addAtgEdges(String source, AtgEdge edge) {
		source = SootUtils.getNameofClass(source);
		if (atgEdges.get(source) == null) {
			atgEdges.put(source, new HashSet<AtgEdge>());
		}
		Set<AtgEdge> edges = atgEdges.get(source);
		for(AtgEdge exist: edges){
			if(exist.getDescribtion().equals(edge.getDescribtion()))
				return false;
		}
		edges.add(edge);
		return true;
	}

	public static void mergeNodels2newOne(ATGModel m1, ATGModel m2, ATGModel m3) {
		if(m1!=null){
			for (Entry<String, Set<AtgEdge>> entry : m1.getAtgEdges().entrySet()){
				Set<AtgEdge> edges = entry.getValue();
				Iterator<AtgEdge> it = edges.iterator();
				while(it.hasNext()){
					m3.addAtgEdges(entry.getKey(), it.next());
				}
			}
		}
		if(m2!=null){
			for (Entry<String, Set<AtgEdge>> entry : m2.getAtgEdges().entrySet()){
				Set<AtgEdge> edges = entry.getValue();
				Iterator<AtgEdge> it = edges.iterator();
				while(it.hasNext()){
					m3.addAtgEdges(entry.getKey(), it.next());
				}
			}
		}

	}

	/**
	 * @return the aTGFilePath
	 */
	public String getATGFilePath() {
		return ATGFilePath;
	}

	/**
	 * @param aTGFilePath
	 *            the aTGFilePath to set
	 */
	public void setATGFilePath(String aTGFilePath) {
		ATGFilePath = aTGFilePath;
	}



	/**
	 * @return the falsenegativeScore
	 */
	public double getFalsenegativeScore() {
		if (isExist())
			return falsenegativeScore;
		else
			return -1;
	}

	/**
	 * @param falsenegativeScore
	 *            the falsenegativeScore to set
	 */
	public void setFalsenegativeScore(double falsenegativeScore) {
		this.falsenegativeScore = falsenegativeScore;
	}

	/**
	 * @return the oracleEdgeSize
	 */
	public int getOracleEdgeSize() {
		if (oracleEdgeSize > 0)
			return oracleEdgeSize;
		
		Set<String> oracleEdges = new HashSet<String>();
		for (Entry<String, Set<AtgEdge>> entry : Global.v().getiCTGModel().getOracleModel().getAtgEdges().entrySet()) {
			for (AtgEdge oracleEdge : entry.getValue()) {
				if (!oracleEdges.contains(oracleEdge.getDescribtion())) {
					oracleEdges.add(oracleEdge.getDescribtion());
				}
			}
		}
		oracleEdgeSize = oracleEdges.size();
		return oracleEdgeSize;
	}

	/**
	 * @param oracleEdgeSize
	 *            the oracleEdgeSize to set
	 */
	public void setOracleEdgeSize(int oracleEdgeSize) {
		this.oracleEdgeSize = oracleEdgeSize;
	}

	/**
	 * @return the fnEdgeSize
	 */
	public int getFnEdgeSize() {
		if (isExist())
			return fnEdgeSize;
		else
			return -1;
	}

	/**
	 * @param fnEdgeSize
	 *            the fnEdgeSize to set
	 */
	public void setFnEdgeSize(int fnEdgeSize) {
		this.fnEdgeSize = fnEdgeSize;
	}

	/**
	 * @return the enhancedNum
	 */
	public int getEnhancedNum() {
		return enhancedNum;
	}

	/**
	 * @param enhancedNum
	 *            the enhancedNum to set
	 */
	public void setEnhancedNum(int enhancedNum) {
		this.enhancedNum = enhancedNum;
	}

	/**
	 * @return the filteredNum
	 */
	public int getFilteredNum() {
		return filteredNum;
	}

	/**
	 * @param filteredNum
	 *            the filteredNum to set
	 */
	public void setFilteredNum(int filteredNum) {
		this.filteredNum = filteredNum;
	}

	/**
	 * @return the exist
	 */
	public boolean isExist() {
		return exist;
	}

	/**
	 * @param exist
	 *            the exist to set
	 */
	public void setExist(boolean exist) {
		this.exist = exist;
	}

	/**
	 * @return the filteredServiceNum
	 */
	public int getFilteredServiceNum() {
		return filteredServiceNum;
	}

	/**
	 * @param filteredServiceNum
	 *            the filteredServiceNum to set
	 */
	public void setFilteredServiceNum(int filteredServiceNum) {
		this.filteredServiceNum = filteredServiceNum;
	}

	/**
	 * @return the filteredReceiverNum
	 */
	public int getFilteredReceiverNum() {
		return filteredReceiverNum;
	}

	/**
	 * @param filteredReceiverNum
	 *            the filteredReceiverNum to set
	 */
	public void setFilteredReceiverNum(int filteredReceiverNum) {
		this.filteredReceiverNum = filteredReceiverNum;
	}


	
	
}
