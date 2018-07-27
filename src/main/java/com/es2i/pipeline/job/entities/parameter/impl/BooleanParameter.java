package com.es2i.pipeline.job.entities.parameter.impl;

import com.es2i.pipeline.job.entities.parameter.Parameter;

public class BooleanParameter extends Parameter {

	/**
	 * La valeur par défaut.
	 */
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
		return super.toString() + ", defaultValue=" + defaultValue;
	}
	
}
