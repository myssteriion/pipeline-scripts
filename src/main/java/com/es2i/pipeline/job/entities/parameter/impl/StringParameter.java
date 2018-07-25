package com.es2i.pipeline.job.entities.parameter.impl;

import com.es2i.pipeline.job.entities.parameter.Parameter;

public class StringParameter extends Parameter {

	private String defaultValue;
	
	
	
	public StringParameter(String name, String defaultValue, String desc, String scope) {
		
		super(name, TypeParameter.STRING, desc, scope);
		this.defaultValue = defaultValue;
	}

	
	
	public String getDefaultValue() {
		return defaultValue;
	}
	
}
