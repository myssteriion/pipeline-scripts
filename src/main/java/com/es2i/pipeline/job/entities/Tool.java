package com.es2i.pipeline.job.entities;

/**
 * Représente une variable tools d'un point de vue Pipeline.
 * @see conf/tools.json
 */
public class Tool {

	private String name;
	
	private String value;
	
	
	
	public Tool(String name, String value) {
		this.name = name;
		this.value = value;
	}



	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
	
}
