package main.java;

import java.util.ArrayList;
import java.util.List;

import soot.Unit;
import main.java.analyze.model.analyzeModel.AppModel;
import main.java.analyze.model.labeledOracleModel.LabeledOracleModel;
import main.java.client.obj.model.ctg.CTGModel;
import main.java.client.obj.model.fragment.FragmentModel;
import main.java.client.related.a3e.model.A3EModel;
import main.java.client.related.gator.model.GatorModel;
import main.java.client.related.ic3.model.IC3Model;
import main.java.client.related.story.model.StoryModel;

public class Global {
	private static final Global instance = new Global();
	private AppModel appModel;
	private A3EModel a3eModel;
	private StoryModel storyModel;
	private IC3Model iC3Model;
	private IC3Model iC3DialDroidModel;
	private CTGModel iCTGModel;
	private GatorModel gatorModel;
	private LabeledOracleModel labeledOracleModel;
	private FragmentModel fragmentModel;
	private List<Unit> instrumentList;
	
	//counter of analyzed app
	public int id = 0;
	
	/**
	 * get the single instance of Global information
	 * include multiple models
	 * @return
	 */
	public static Global v() {
		return instance;
	}

	/**
	 * initialize the Global instance
	 */
	private Global() {
		appModel = new AppModel();
		a3eModel = new A3EModel();
		storyModel = new StoryModel();
		iC3Model = new IC3Model();
		iC3DialDroidModel = new IC3Model();
		fragmentModel = new FragmentModel();
		iCTGModel = new CTGModel();
		setGatorModel(new GatorModel());
		setInstrumentList(new ArrayList<Unit>());
		labeledOracleModel = new LabeledOracleModel();
	}

	/**
	 * @return get the AppModel
	 */
	public AppModel getAppModel() {
		return appModel;
	}

	/**
	 * @return the iC3Model
	 */
	public IC3Model getiC3Model() {
		return iC3Model;
	}

	/**
	 * @return the iCTGModel
	 */
	public CTGModel getiCTGModel() {
		return iCTGModel;
	}

	/**
	 * @return the fragmentModel
	 */
	public FragmentModel getFragmentModel() {
		return fragmentModel;
	}

	/**
	 * @return the instrumentList
	 */
	public List<Unit> getInstrumentList() {
		return instrumentList;
	}

	/**
	 * @param instrumentList
	 *            the instrumentList to set
	 */
	public void setInstrumentList(List<Unit> instrumentList) {
		this.instrumentList = instrumentList;
	}

	/**
	 * @param instrumentList
	 *            the instrumentList to set
	 */
	public void addInstrumentList(Unit u) {
		this.instrumentList.add(u);
	}


	/**
	 * @return the iC3DialDroidModel
	 */
	public IC3Model getiC3DialDroidModel() {
		return iC3DialDroidModel;
	}

	/**
	 * @param iC3DialDroidModel
	 *            the iC3DialDroidModel to set
	 */
	public void setiC3DialDroidModel(IC3Model iC3DialDroidModel) {
		this.iC3DialDroidModel = iC3DialDroidModel;
	}

	/**
	 * @return the labeledOracleModel
	 */
	public LabeledOracleModel getLabeledOracleModel() {
		return labeledOracleModel;
	}

	/**
	 * @param labeledOracleModel
	 *            the labeledOracleModel to set
	 */
	public void setLabeledOracleModel(LabeledOracleModel labeledOracleModel) {
		this.labeledOracleModel = labeledOracleModel;
	}

	/**
	 * @return the gatorModel
	 */
	public GatorModel getGatorModel() {
		return gatorModel;
	}

	/**
	 * @param gatorModel the gatorModel to set
	 */
	public void setGatorModel(GatorModel gatorModel) {
		this.gatorModel = gatorModel;
	}

	/**
	 * @return the a3eModel
	 */
	public A3EModel getA3eModel() {
		return a3eModel;
	}

	/**
	 * @param a3eModel the a3eModel to set
	 */
	public void setA3eModel(A3EModel a3eModel) {
		this.a3eModel = a3eModel;
	}

	/**
	 * @return the storyModel
	 */
	public StoryModel getStoryModel() {
		return storyModel;
	}

	/**
	 * @param storyModel the storyModel to set
	 */
	public void setStoryModel(StoryModel storyModel) {
		this.storyModel = storyModel;
	}

}
