package main.java.client.statistic.model;

public class StatisticResult {
	private TraceStatistic entryTraceStatistic;
	private TraceStatistic allTraceStatistic;
	private ICCStatistic entryICCStatistic;
	private ICCStatistic allICCStatistic;
	private SummaryStatistic summaryStatistic;
	// private XmlStatistic entryXmlStatistic;
	// private XmlStatistic allXmlStatistic;
	private XmlStatistic xmlStatistic;

	public StatisticResult() {
		setEntryTraceStatistic(new TraceStatistic());
		setAllTraceStatistic(new TraceStatistic());
		setEntryICCStatistic(new ICCStatistic());
		setAllICCStatistic(new ICCStatistic());
		setSummaryStatistic(new SummaryStatistic());
		// setEntryXmlStatistic(new XmlStatistic());
		// setAllXmlStatistic(new XmlStatistic());
		setXmlStatistic(new XmlStatistic());

	}

	public TraceStatistic getEntryTraceStatistic() {
		return entryTraceStatistic;
	}

	public void setEntryTraceStatistic(TraceStatistic entryTraceStatistic) {
		this.entryTraceStatistic = entryTraceStatistic;
	}

	public TraceStatistic getAllTraceStatistic() {
		return allTraceStatistic;
	}

	public void setAllTraceStatistic(TraceStatistic allTraceStatistic) {
		this.allTraceStatistic = allTraceStatistic;
	}

	public SummaryStatistic getSummaryStatistic() {
		return summaryStatistic;
	}

	public void setSummaryStatistic(SummaryStatistic summaryStatistic) {
		this.summaryStatistic = summaryStatistic;
	}

	public ICCStatistic getEntryICCStatistic() {
		return entryICCStatistic;
	}

	public void setEntryICCStatistic(ICCStatistic entryICCStatistic) {
		this.entryICCStatistic = entryICCStatistic;
	}

	public ICCStatistic getAllICCStatistic() {
		return allICCStatistic;
	}

	public void setAllICCStatistic(ICCStatistic allICCStatistic) {
		this.allICCStatistic = allICCStatistic;
	}

	// public XmlStatistic getEntryXmlStatistic() {
	// return entryXmlStatistic;
	// }
	// public void setEntryXmlStatistic(XmlStatistic xmlStatistic) {
	// this.entryXmlStatistic = xmlStatistic;
	// }
	// public XmlStatistic getAllXmlStatistic() {
	// return allXmlStatistic;
	// }
	// public void setAllXmlStatistic(XmlStatistic allXmlStatistic) {
	// this.allXmlStatistic = allXmlStatistic;
	// }
	public XmlStatistic getXmlStatistic() {
		return xmlStatistic;
	}

	public void setXmlStatistic(XmlStatistic xmlStatistic) {
		this.xmlStatistic = xmlStatistic;
	}
}
