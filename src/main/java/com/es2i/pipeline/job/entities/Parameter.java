package com.es2i.pipeline.job.entities;

public class Parameter {

	private String name;
	
	private String type;
	
	private String defaultValue;
	
	private String desc;

	
	
	public Parameter(String name, String type, String defaultValue, String desc) {
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
		this.desc = desc;
	}



	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public String getDesc() {
		return desc;
	}
	
}
