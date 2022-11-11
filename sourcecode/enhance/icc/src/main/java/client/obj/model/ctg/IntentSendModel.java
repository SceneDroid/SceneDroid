package main.java.client.obj.model.ctg;

import java.util.HashSet;
import java.util.Set;

import main.java.client.obj.model.component.BundleType;

public class IntentSendModel {

	private Set<String> sendActionSet;
	private Set<String> sendCategorySet;
	private Set<String> sendDataSet;
	private Set<String> sendTypeSet;
	private BundleType sendExtraData;

	private Set<String> sendFlagSet;
	private Set<String> iccTargetSet;

	public IntentSendModel() {
		setSendActionSet(new HashSet<String>());
		setSendCategorySet(new HashSet<String>());
		setSendDataSet(new HashSet<String>());
		setSendTypeSet(new HashSet<String>());
		setSendFlagSet(new HashSet<String>());
		setIccTargetSet(new HashSet<String>());
		setSendExtraData(new BundleType());
	}

	public Set<String> getSendFlagSet() {
		return sendFlagSet;
	}

	public void setSendFlagSet(Set<String> sendFlagSet) {
		this.sendFlagSet = sendFlagSet;
	}

	/**
	 * @return the iccTargetSet
	 */
	public Set<String> getIccTargetSet() {
		return iccTargetSet;
	}

	/**
	 * @param iccTargetSet
	 *            the iccTargetSet to set
	 */
	public void setIccTargetSet(Set<String> iccTargetSet) {
		this.iccTargetSet = iccTargetSet;
	}

	/**
	 * @return the sendActionSet
	 */
	public Set<String> getSendActionSet() {
		return sendActionSet;
	}

	/**
	 * @param sendActionSet
	 *            the sendActionSet to set
	 */
	public void setSendActionSet(Set<String> sendActionSet) {
		this.sendActionSet = sendActionSet;
	}

	/**
	 * @return the sendCategorySet
	 */
	public Set<String> getSendCategorySet() {
		return sendCategorySet;
	}

	/**
	 * @param sendCategorySet
	 *            the sendCategorySet to set
	 */
	public void setSendCategorySet(Set<String> sendCategorySet) {
		this.sendCategorySet = sendCategorySet;
	}

	/**
	 * @return the sendExtraData
	 */
	public BundleType getSendExtraData() {
		return sendExtraData;
	}

	/**
	 * @param sendExtraData
	 *            the sendExtraData to set
	 */
	public void setSendExtraData(BundleType sendExtraData) {
		this.sendExtraData = sendExtraData;
	}

	/**
	 * @return the sendDataSet
	 */
	public Set<String> getSendDataSet() {
		return sendDataSet;
	}

	/**
	 * @param sendDataSet
	 *            the sendDataSet to set
	 */
	public void setSendDataSet(Set<String> sendDataSet) {
		this.sendDataSet = sendDataSet;
	}

	/**
	 * @return the sendTypeSet
	 */
	public Set<String> getSendTypeSet() {
		return sendTypeSet;
	}

	/**
	 * @param sendTypeSet
	 *            the sendTypeSet to set
	 */
	public void setSendTypeSet(Set<String> sendTypeSet) {
		this.sendTypeSet = sendTypeSet;
	}
}
