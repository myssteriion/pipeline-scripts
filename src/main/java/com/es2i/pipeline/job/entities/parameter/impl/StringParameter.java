package com.es2i.pipeline.job.entities.parameter.impl;

import com.es2i.pipeline.job.entities.parameter.Parameter;

public class StringParameter extends Parameter {

	private String defaultValue;
	
	
	
	public StringParameter(String name, String defaultValue, String desc) {
		super(name, TypeParameter.STRING, desc);
		this.defaultValue = defaultValue;
	}

	
	
	public String getDefaultValue() {
		return defaultValue;
	}

	@Override
	public String toString() {
		return "StringParameter [" + super.toString() + ", defaultValue=" + defaultValue + "]";
	}
	
}
