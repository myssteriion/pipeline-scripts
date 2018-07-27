package com.es2i.pipeline.job.script.abstracts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.es2i.pipeline.job.entities.Environment;

/**
 * Contient les stages pour les projets à builder.
 */
public abstract class WithProjectsStages extends Script {

	
	/**
	 * La liste des projets et de ses variables d'environements.
	 *	key : 		le nom du projet (identique à l'attribut projects ci dessus)
	 *	value : 	la liste des variables d'environements.	
	 */
	protected Map<String, List<Environment>> projectsEnvironements;
	
	
	
	public WithProjectsStages() {
		super();
		projectsEnvironements = new HashMap<String, List<Environment>>();
	}
	
	

	/*
	 * La liste de chacun des stages (nom projet) avec la liste des variables d'env.
	 */
	public Map<String, List<Environment>> getProjectsEnvironements() {
		return projectsEnvironements;
	}
	
	public void setProjectsEnvironements(Map<String, List<Environment>> projectsEnvironements) {
		this.projectsEnvironements = projectsEnvironements;
	}

	public boolean hadProjectsEnvironements() {
		return projectsEnvironements != null && !projectsEnvironements.isEmpty();
	}

}
