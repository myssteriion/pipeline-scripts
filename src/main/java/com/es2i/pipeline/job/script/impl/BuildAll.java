package com.es2i.pipeline.job.script.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.es2i.pipeline.job.script.Script;

public class BuildAll extends Script {

	/**
	 * La liste des projets trier par groupe afin d'exécuter en parallèle (l'ordre des groupes comptes ; l'ordre dans un groupe est arbitraire).
	 */
	private Map<Integer, List<String>> projects;
	
	/**
	 * La liste des serveurs de déploiement secondaires.
	 */
	private List<String> secondaryRemotes;
	
	
	
	public BuildAll() {
		super();
		projects = new HashMap<Integer, List<String>>();
		secondaryRemotes = new ArrayList<String>();
	}

	
	
	public Map<Integer, List<String>> getProjects() {
		return projects;
	}
	
	public List<String> getProjectsList() {
		
		List<String> projectsList = new ArrayList<String>();
		for (Map.Entry<Integer, List<String>> entry : projects.entrySet())
			projectsList.addAll( entry.getValue() );
		
		return projectsList;
	}
	
	public void setProjects(Map<Integer, List<String>> projects) {
		this.projects = projects;
	}

	
	public List<String> getSecondaryRemotes() {
		return secondaryRemotes;
	}

	public void setSecondaryRemotes(List<String> secondaryRemotes) {
		this.secondaryRemotes = secondaryRemotes;
	}
	
	public boolean hadSecondaryRemotes() {
		return secondaryRemotes != null && !secondaryRemotes.isEmpty();
	}

}
