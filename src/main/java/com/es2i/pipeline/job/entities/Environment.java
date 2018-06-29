package com.es2i.pipeline.job.entities;

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
	
}
