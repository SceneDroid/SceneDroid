package main.java.client.obj.model.fragment;

import java.util.ArrayList;
import java.util.List;

import main.java.analyze.model.analyzeModel.ObjectSummaryModel;
import main.java.analyze.model.analyzeModel.PathSummaryModel;
import main.java.analyze.utils.output.PrintUtils;
import soot.Unit;

public class FragmentSummaryModel extends ObjectSummaryModel {
	// unit
	private List<Unit> sendFragment2Start;
	private List<Unit> getFragmentFromOut;

	// value
	private List<String> addValueList;
	private List<String> replaceValueList;
	private List<String> ListDestinationValueList;

	public FragmentSummaryModel(PathSummaryModel pathSummary) {
		super(pathSummary);
		setSendFragment2Start(new ArrayList<Unit>());
		setGetFragmentFromOut(new ArrayList<Unit>());

		setAddList(new ArrayList<String>());
		setReplaceList(new ArrayList<String>());
		setSetDestinationList(new ArrayList<String>());
	}

	public void copy(FragmentSummaryModel temp) {
		super.copy(temp);
		setSendFragment2Start(temp.getSendFragment2Start());
		setGetFragmentFromOut(temp.getGetFragmentFromOut());

		setAddList(temp.getAddList());
		setReplaceList(temp.getReplaceList());
		setSetDestinationList(temp.getSetDestinationList());
	}

	@Override
	public void merge(ObjectSummaryModel temp) {
		super.merge(temp);
		FragmentSummaryModel temp2 = (FragmentSummaryModel) temp;
		getSendFragment2Start().addAll(temp2.getSendFragment2Start());
		getGetFragmentFromOut().addAll(temp2.getGetFragmentFromOut());

		getAddList().addAll(temp2.getAddList());
		getReplaceList().addAll(temp2.getReplaceList());
		getSetDestinationList().addAll(temp2.getSetDestinationList());
	}

	@Override
	public String toHashString() {
		String res = super.toHashString();

		res += sendFragment2Start.size();
		res += addValueList.size();
		res += replaceValueList.size();
		res += PrintUtils.printList(ListDestinationValueList);

		return res;
	}

	@Override
	public String toString() {
		String res = super.toString();

		res += "sendFragment2Start:" + PrintUtils.printList(sendFragment2Start) + "\n";
		res += "addList:" + PrintUtils.printList(addValueList) + "\n";
		res += "replaceList:" + PrintUtils.printList(replaceValueList) + "\n";
		res += "ListDestinationList:" + PrintUtils.printList(ListDestinationValueList) + "\n";
		return res;
	}

	/**
	 * @return the addList
	 */
	public List<String> getAddList() {
		return addValueList;
	}

	/**
	 * @param List
	 *            the addList to List
	 */
	public void setAddList(List<String> List) {
		this.addValueList = List;
	}

	/**
	 * @return the replaceList
	 */
	public List<String> getReplaceList() {
		return replaceValueList;
	}

	/**
	 * @param replaceList
	 *            the replaceList to List
	 */
	public void setReplaceList(List<String> replaceList) {
		this.replaceValueList = replaceList;
	}

	/**
	 * @return the sendFragment2Start
	 */
	public List<Unit> getSendFragment2Start() {
		return sendFragment2Start;
	}

	/**
	 * @param sendFragment2Start
	 *            the sendFragment2Start to List
	 */
	public void setSendFragment2Start(List<Unit> sendFragment2Start) {
		this.sendFragment2Start = sendFragment2Start;
	}

	/**
	 * @return the ListDestinationList
	 */
	public List<String> getSetDestinationList() {
		return ListDestinationValueList;
	}

	/**
	 * @param ListDestinationList
	 *            the ListDestinationList to List
	 */
	public void setSetDestinationList(List<String> ListDestinationList) {
		this.ListDestinationValueList = ListDestinationList;
	}

	/**
	 * @return the getFragmentFromOut
	 */
	public List<Unit> getGetFragmentFromOut() {
		return getFragmentFromOut;
	}

	/**
	 * @param getFragmentFromOut
	 *            the getFragmentFromOut to List
	 */
	public void setGetFragmentFromOut(List<Unit> getFragmentFromOut) {
		this.getFragmentFromOut = getFragmentFromOut;
	}

}
