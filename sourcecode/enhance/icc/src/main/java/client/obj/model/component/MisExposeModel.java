package main.java.client.obj.model.component;

public class MisExposeModel {
	private ComponentModel componentModel;
	private String misInfo ="";
	private EAStatus status = EAStatus.None;
	
	public MisExposeModel(ComponentModel componentModel) {
		this.componentModel = componentModel;
	}
	/**
	 * @return the misInfo
	 */
	public String getMisInfo() {
		return misInfo;
	}
	/**
	 * @param misInfo the misInfo to set
	 */
	public void setMisInfo(String misInfo) {
		this.misInfo = misInfo;
	}
	/**
	 * @return the status
	 */
	public EAStatus getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(EAStatus status) {
		this.status = status;
	}
	
	
	@Override
	public String toString() {
		return componentModel.getComponetName() +", status: "+status +", info: "+misInfo;
	}
}
