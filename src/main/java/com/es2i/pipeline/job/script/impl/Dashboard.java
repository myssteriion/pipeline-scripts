package com.es2i.pipeline.job.script.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.es2i.pipeline.job.entities.Environment;
import com.es2i.pipeline.job.script.abstracts.WithProjectsStages;

public class Dashboard extends WithProjectsStages {

	/**
	 * La liste des projets front.
	 */
	private List<String> projectsFront;
	
	/**
	 * La liste des projets back.
	 */
	private List<String> projectsBack;
	
	/**
	 * La liste des projets FRONT et de ses variables d'environements.
	 * 	key : 		le nom du projet
	 *  value : 	la liste des variables d'environements.	
	 */
	private Map<String, List<Environment>> frontEnvironments;
	
	/**
	 * La liste des projets BACK et de ses variables d'environements.
	 * 	key : 		le nom du projet
	 *  value : 	la liste des variables d'environements.	
	 */
	private Map<String, List<Environment>> backEnvironments;

		
	
	public Dashboard() {
		super();
		projectsFront = new ArrayList<String>();
		projectsBack = new ArrayList<String>();
		frontEnvironments = new HashMap<String, List<Environment>>();
		backEnvironments = new HashMap<String, List<Environment>>();
	}


	
	public List<String> getProjectsFront() {
		return projectsFront;
	}

	public void setProjectsFront(List<String> projectsFront) {
		this.projectsFront = projectsFront;
	}
	
	public List<String> getProjectsBack() {
		return projectsBack;
	}

	public void setProjectsBack(List<String> projectsBack) {
		this.projectsBack = projectsBack;
	}

	public Map<String, List<Environment>> getBackEnvironments() {
		return backEnvironments;
	}

	public void setBackEnvironments(Map<String, List<Environment>> backEnvironments) {
		this.backEnvironments = backEnvironments;
	}
	
	public Map<String, List<Environment>> getFrontEnvironments() {
		return frontEnvironments;
	}
	
	public void setFrontEnvironments(Map<String, List<Environment>> frontEnvironments) {
		this.frontEnvironments = frontEnvironments;
	}

	public boolean isProjectIsBack(String project) {
		return projectsBack.contains(project);
	}
	
	public boolean isProjectIsFront(String project) {
		return projectsFront.contains(project);
	}
	
	public boolean isFirstProjectFront(String project) {
		return projectsFront.get(0).equals(project);
	}
	
	public boolean isFirstProjectBack(String project) {
		return projectsBack.get(0).equals(project);
	}
	
}
