package com.es2i.pipeline.job.entities;

/**
 * Représente une variable tools d'un point de vue Pipeline.
 */
public class Tool {

	/**
	 * Le nom de la variable tools.
	 */
	private String name;
	
	/**
	 * La valeur de la variable tools.
	 */
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

	@Override
	public String toString() {
		return "name=" + name + ", value=" + value;
	}
	
}
