package com.buschmais.tf4jenkins.notifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.buschmais.tf4jenkins.BrickletNotifier;
import com.buschmais.tf4jenkins.JobStatus;
import com.buschmais.tf4jenkins.JobSummary;

public abstract class StatefulBrickletNotifier implements BrickletNotifier {

	private SortedMap<String, JobSummary> summaries = new TreeMap<String, JobSummary>();

	private Map<JobStatus, Set<JobSummary>> jobsByStatus = new HashMap<JobStatus, Set<JobSummary>>();

	protected StatefulBrickletNotifier() {
		for (JobStatus status : JobStatus.values()) {
			jobsByStatus.put(status, new HashSet<JobSummary>());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.buschmais.tf4jenkins.BrickletNotifier#setSummary(com.buschmais.tf4jenkins
	 * .JobSummary)
	 */
	@Override
	public void setSummary(JobSummary summary) {
		JobSummary oldSummary = summaries.put(summary.getName(), summary);
		if (oldSummary!= null) {
			jobsByStatus.get(oldSummary.getStatus()).remove(oldSummary);
		}
		jobsByStatus.get(summary.getStatus()).add(summary);
	}

	/**
	 * @return the jobSummaries
	 */
	public SortedMap<String, JobSummary> getJobSummaries() {
		return summaries;
	}

	/**
	 * @return the failedJobs
	 */
	public Set<JobSummary> getJobsByStatus(JobStatus status) {
		return jobsByStatus.get(status);
	}

}
