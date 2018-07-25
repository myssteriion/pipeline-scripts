package com.es2i.pipeline.job.script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.es2i.pipeline.job.entities.Environment;
import com.es2i.pipeline.job.entities.Tool;
import com.es2i.pipeline.job.entities.parameter.Parameter;

/**
 * Correspond au Job dans Jenkins.
 * Un job doit posséde (même si la liste est vide), des paramètres, des variables d'environements, des variables tools.
 * Cette architecture permet de mutualiser le code dans la classe Pipeline.
 */
public abstract class Script {
	
	/**
	 * La listes des paramètres.
	 */
	protected List<Parameter> parameters;
	
	/**
	 * La liste des variables d'environements.
	 */
	protected List<Environment> environements;

	/**
	 * La liste des variables tools.
	 */
	protected List<Tool> tools;
	
	/**
	 * La liste des projets et de ses variables d'environements.
	 * 	key : 		le nom du projet (identique à l'attribut projects ci dessus)
	 *  value : 	la liste des variables d'environements.	
	 */
	protected Map<String, List<Environment>> projectsEnvironements;
	
	
	
	public Script() {
		parameters = new ArrayList<Parameter>();
		environements = new ArrayList<Environment>();
		tools = new ArrayList<Tool>();
	}
	
	
	
	/*
	 * Le bloc parameter.
	 */
	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public boolean hadParameters() {
		return parameters != null && !parameters.isEmpty();
	}

	
	/*
	 * Le bloc env.
	 */
	public List<Environment> getEnvironements() {
		return environements;
	}

	public void setEnvironements(List<Environment> environements) {
		this.environements = environements;
	}

	public boolean hadEnvironments() {
		return environements != null && !environements.isEmpty();
	}
	
	
	/*
	 * Le bloc tools.
	 */
	public List<Tool> getTools() {
		return tools;
	}

	public void setTools(List<Tool> tools) {
		this.tools = tools;
	}
	
	public boolean hadTools() {
		return tools != null && !tools.isEmpty();
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
