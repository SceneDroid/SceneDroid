package main.java.client.related.story.model;

import main.java.client.obj.model.atg.ATGModel;

public class StoryModel {
	private ATGModel storyAtgModelwithoutFrag;
	private ATGModel storyAtgModel;
	private String storyFilePath;

	public StoryModel() {
		setStoryAtgModelWithoutFrag(new ATGModel());
		setStoryAtgModel(new ATGModel());
	}

	/**
	 * @return the storyAtgModelwithoutFrag
	 */
	public ATGModel getStoryAtgModelWithoutFrag() {
		return storyAtgModelwithoutFrag;
	}

	/**
	 * @param storyAtgModelwithoutFrag
	 *            the storyAtgModelwithoutFrag to set
	 */
	public void setStoryAtgModelWithoutFrag(ATGModel storyAtgModel) {
		this.storyAtgModelwithoutFrag = storyAtgModel;
	}

	/**
	 * @return the storyFilePath
	 */
	public String getStoryFilePath() {
		return storyFilePath;
	}

	/**
	 * @param storyFilePath
	 *            the storyFilePath to set
	 */
	public void setStoryFilePath(String storyFilePath) {
		this.storyFilePath = storyFilePath;
	}

	/**
	 * @return the storyAtgModel
	 */
	public ATGModel getStoryAtgModel() {
		return storyAtgModel;
	}

	/**
	 * @param storyAtgModel the storyAtgModel to set
	 */
	public void setStoryAtgModel(ATGModel storyAtgModel) {
		this.storyAtgModel = storyAtgModel;
	}
}
