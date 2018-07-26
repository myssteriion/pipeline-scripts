package com.es2i.pipeline.job.entities;

/**
 * Représente une variable environement d'un point de vue Pipeline.
 */
public class Environment {
	
	private String name;
	
	private String value;
	
	
	
	public Environment(String name, String value) {
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
		return "Environment [name=" + name + ", value=" + value + "]";
	}

}
