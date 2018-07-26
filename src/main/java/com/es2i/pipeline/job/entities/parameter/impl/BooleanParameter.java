package com.es2i.pipeline.job.entities.parameter.impl;

import com.es2i.pipeline.job.entities.parameter.Parameter;

public class BooleanParameter extends Parameter {

	private String defaultValue;
	
	
	
	public BooleanParameter(String name, String defaultValue, String desc) {
		super(name, TypeParameter.BOOLEAN, desc);
		this.defaultValue = defaultValue;
	}

	
	
	public String getDefaultValue() {
		return defaultValue;
	}
	
	@Override
	public String toString() {
		return "BooleanParameter [" + super.toString() + ", defaultValue=" + defaultValue + "]";
	}
	
}
