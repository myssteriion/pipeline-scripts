package com.es2i.pipeline.job.entities;

import com.es2i.pipeline.tools.Tools;

public class Parameter {

	private String name;
	
	private String type;
	
	private String defaultValue;
	
	private String desc;
	
	private ParamaterScope scope;
	
	public enum ParamaterScope { MONO, ALL, BOTH }; 
	
	

	public Parameter(String name, String type, String defaultValue, String desc, String scope) {
		
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
		this.desc = desc;
		
		if ( Tools.isNullOrEmpty(scope) ) {
			this.scope = ParamaterScope.BOTH;
		}
		else if ( scope.equalsIgnoreCase(ParamaterScope.MONO.toString()) ) {
			this.scope = ParamaterScope.MONO;
		}
		else if ( scope.equalsIgnoreCase(ParamaterScope.ALL.toString()) ) {
			this.scope = ParamaterScope.ALL;
		}
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
	
	public boolean isMonoScope() {
		return scope == ParamaterScope.BOTH || scope == ParamaterScope.MONO;
	}
	
	public boolean isAllScope() {
		return scope == ParamaterScope.BOTH || scope == ParamaterScope.ALL;
	}
	
}
