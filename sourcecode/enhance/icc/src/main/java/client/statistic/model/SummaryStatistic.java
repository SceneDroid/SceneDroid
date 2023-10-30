package main.java.client.statistic.model;

import java.util.HashSet;
import java.util.Set;

public class SummaryStatistic {

	// statistic
	private Set<String> summariedMethods;
	private Set<String> summariedEntryMethods;
	private Set<String> summariedEntryLifeCycleMethods;
	private Set<String> summariedEntryListenerMethods;

	public SummaryStatistic() {
		summariedMethods = new HashSet<String>();
		summariedEntryMethods = new HashSet<String>();
		summariedEntryLifeCycleMethods = new HashSet<String>();
		summariedEntryListenerMethods = new HashSet<String>();
	}

	public Set<String> getSummariedEntryMethods() {
		return summariedEntryMethods;
	}

	public void setSummariedEntryMethods(Set<String> summariedEntryMethods) {
		this.summariedEntryMethods.addAll(summariedEntryMethods);
	}

	public Set<String> getSummariedEntryLifeCycleMethods() {
		return summariedEntryLifeCycleMethods;
	}

	public void addSummariedEntryLifeCycleMethods(String str) {
		this.summariedEntryLifeCycleMethods.add(str);
	}

	public Set<String> getSummariedEntryListenerMethods() {
		return summariedEntryListenerMethods;
	}

	public void addSummariedEntryListenerMethods(String str) {
		this.summariedEntryListenerMethods.add(str);
	}

	public Set<String> getSummariedMethods() {
		return summariedMethods;
	}

	public void setSummariedMethods(Set<String> summariedMethods) {
		this.summariedMethods.addAll(summariedMethods);
	}

}
