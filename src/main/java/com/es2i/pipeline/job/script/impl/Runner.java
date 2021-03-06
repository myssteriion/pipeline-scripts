package com.es2i.pipeline.job.script.impl;

import java.util.ArrayList;
import java.util.List;

import com.es2i.pipeline.job.script.abstracts.Script;

public class Runner extends Script {

	/**
	 * La liste des revisions de l'IC.
	 */
	private List<String> revisions;
	
	/**
	 * La liste des profiles maven de l'IC.
	 */
	private List<String> mavenProfiles;

	
	
	public Runner() {
		super();
		revisions = new ArrayList<String>();
		mavenProfiles = new ArrayList<String>();
	}

	
	
	public List<String> getRevisions() {
		return revisions;
	}

	public void setRevisions(List<String> revisions) {
		this.revisions = revisions;
	}

	
	public List<String> getMavenProfiles() {
		return mavenProfiles;
	}

	public void setMavenProfiles(List<String> mavenProfiles) {
		this.mavenProfiles = mavenProfiles;
	}
	
}
