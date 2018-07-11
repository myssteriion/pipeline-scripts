package com.es2i.pipeline.job.entities.parameter_impl;

import com.es2i.pipeline.job.entities.Parameter;

public class BooleanParameter extends Parameter {

	private String defaultValue;
	
	
	
	public BooleanParameter(String name, String defaultValue, String desc, String scope) {
		
		super(name, TypeParameter.BOOLEAN, desc, scope);
		this.defaultValue = defaultValue;
	}

	
	
	public String getDefaultValue() {
		return defaultValue;
	}
	
}
