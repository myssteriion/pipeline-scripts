package com.es2i.pipeline.job.script.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.es2i.pipeline.job.script.abstracts.WithProjectsStages;

public class BuildOne extends WithProjectsStages {

	/**
	 * La liste des projets (l'odre compte).
	 */
	private Map<Integer, String> projects;
	
	
	
	public BuildOne() {
		super();
		this.projects = new HashMap<Integer, String>();
	}

	
	
	public Map<Integer, String> getProjects() {
		return projects;
	}

	public List<String> getProjectsList() {
		
		List<String> projectsList = new ArrayList<String>();
		for (Map.Entry<Integer, String> entry : projects.entrySet())
			projectsList.add( entry.getValue() );
		
		return projectsList;
	}

	public void setProjects(Map<Integer, String> projects) {
		this.projects = projects;
	}

}
