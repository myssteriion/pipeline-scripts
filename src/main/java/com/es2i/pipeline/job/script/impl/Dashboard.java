package com.es2i.pipeline.job.script.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.es2i.pipeline.job.entities.Environment;
import com.es2i.pipeline.job.script.Script;

public class Dashboard extends Script {

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
		frontEnvironments = new HashMap<String, List<Environment>>();
		backEnvironments = new HashMap<String, List<Environment>>();
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

}
