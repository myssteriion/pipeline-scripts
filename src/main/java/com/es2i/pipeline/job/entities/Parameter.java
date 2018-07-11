package com.es2i.pipeline.job.entities;

import com.es2i.pipeline.tools.Tools;

public abstract class Parameter {

	public enum TypeParameter { BOOLEAN("boolean"), STRING("string"), CHOICE("choice");
		
		private String name;
		
		private TypeParameter(String name) {
			this.name = name;
		}
		
		public String getName() {
			return this.name;
		}
		
		public static TypeParameter getTypeParameterByName(String name) {
			
			if ( BOOLEAN.getName().equalsIgnoreCase(name) )
				return BOOLEAN;
			else if ( STRING.getName().equalsIgnoreCase(name) )
				return STRING;
			else if ( CHOICE.getName().equalsIgnoreCase(name) )
				return CHOICE;
			else
				return null;
		}
		
	}; 
	
	public enum ScopeParamater { MONO, ALL, BOTH }; 
	
	public enum ParameterKey { NAME("name"), TYPE("type"), SCOPE("scope"), DEFAULT_VALUE("defaultValue"), CHOICES ("choices"), DESCRIPTION("description");
	
		private String name;
	
		private ParameterKey(String name) {
			this.name = name;
		}
		
		public String getName() {
			return this.name;
		}
	};
	
	private String name;
	
	private TypeParameter type;
	
	private String desc;
	
	private ScopeParamater scope;
	
	

	public Parameter(String name, TypeParameter type, String desc, String scope) {
		
		this.name = name;
		this.type = type;
		this.desc = desc;
		
		if ( Tools.isNullOrEmpty(scope) ) {
			this.scope = ScopeParamater.BOTH;
		}
		else if ( scope.equalsIgnoreCase(ScopeParamater.MONO.toString()) ) {
			this.scope = ScopeParamater.MONO;
		}
		else if ( scope.equalsIgnoreCase(ScopeParamater.ALL.toString()) ) {
			this.scope = ScopeParamater.ALL;
		}
	}



	public String getName() {
		return name;
	}

	public TypeParameter getType() {
		return type;
	}

	public String getDesc() {
		return desc;
	}
	
	public boolean isMonoScope() {
		return scope == ScopeParamater.BOTH || scope == ScopeParamater.MONO;
	}
	
	public boolean isAllScope() {
		return scope == ScopeParamater.BOTH || scope == ScopeParamater.ALL;
	}
	
}
