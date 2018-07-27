package com.es2i.pipeline.job.script.abstracts;

import java.util.ArrayList;
import java.util.List;

import com.es2i.pipeline.job.entities.Environment;
import com.es2i.pipeline.job.entities.Tool;
import com.es2i.pipeline.job.entities.parameter.Parameter;

/**
 * Correspond aux Jobs dans l'applciation Jenkins.
 * Un job possède (même si la liste est vide), des paramètres, des variables d'environements, des variables tools.
 */
/* 
 * Une touche d'abstraction pour mutualiser le code.
 * Le sympbole <- traduit l'héritage.
 * Le symbole <A> traduit l'abstraction
 * 
 * Script <A>	<-	WithProjectsStages <A>	<-	BuildAll
 * 											<-	BuildOne
 * 											<-	Dashboard
 * 				<-	Runner
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
	
}
