package main.java.client.statistic.model;

import java.util.ArrayList;
import java.util.List;
import org.dom4j.Element;

public class XmlStatistic {

	// statistic
	private List<Element> allMethodSummaryEleList;
	private List<Element> entryMethodSummaryEleList;

	private List<Element> allPathSummaryEleList;
	private List<Element> entryPathSummaryEleList;

	private List<Element> allIntentSummaryEleList;
	private List<Element> entryIntentSummaryEleList;

	public XmlStatistic() {
		setAllMethodSummaryEleList(new ArrayList<Element>());
		setEntryMethodSummaryEleList(new ArrayList<Element>());
		setAllPathSummaryEleList(new ArrayList<Element>());
		setEntryPathSummaryEleList(new ArrayList<Element>());
		setAllIntentSummaryEleList(new ArrayList<Element>());
		setEntryIntentSummaryEleList(new ArrayList<Element>());
	}

	public List<Element> getEntryMethodSummaryEleList() {
		return entryMethodSummaryEleList;
	}

	public void setEntryMethodSummaryEleList(List<Element> entryNodeEleList) {
		this.entryMethodSummaryEleList = entryNodeEleList;
	}

	public void addEntryMethodSummaryEleList(Element element) {
		this.entryMethodSummaryEleList.add(element);
	}

	public List<Element> getAllMethodSummaryEleList() {
		return allMethodSummaryEleList;
	}

	public void addAllMethodSummaryEleList(Element element) {
		this.allMethodSummaryEleList.add(element);
	}

	public void setAllMethodSummaryEleList(List<Element> elementList) {
		this.allMethodSummaryEleList = elementList;
	}

	public List<Element> getEntryPathSummaryEleList() {
		return entryPathSummaryEleList;
	}

	public void setEntryPathSummaryEleList(List<Element> entryNodeEleList) {
		this.entryPathSummaryEleList = entryNodeEleList;
	}

	public void addEntryPathSummaryEleList(Element element) {
		this.entryPathSummaryEleList.add(element);
	}

	public List<Element> getAllPathSummaryEleList() {
		return allPathSummaryEleList;
	}

	public void addAllPathSummaryEleList(Element element) {
		this.allPathSummaryEleList.add(element);
	}

	public void setAllPathSummaryEleList(List<Element> elementList) {
		this.allPathSummaryEleList = elementList;
	}

	public List<Element> getAllIntentSummaryEleList() {
		return allIntentSummaryEleList;
	}

	public void setAllIntentSummaryEleList(List<Element> allIntentSummaryEleList) {
		this.allIntentSummaryEleList = allIntentSummaryEleList;
	}

	public void addAllIntentSummaryEleList(Element element) {
		this.allIntentSummaryEleList.add(element);
	}

	public List<Element> getEntryIntentSummaryEleList() {
		return entryIntentSummaryEleList;
	}

	public void setEntryIntentSummaryEleList(List<Element> entryIntentSummaryEleList) {
		this.entryIntentSummaryEleList = entryIntentSummaryEleList;
	}

	public void addEntryIntentSummaryEleList(Element element) {
		this.entryIntentSummaryEleList.add(element);
	}

}
