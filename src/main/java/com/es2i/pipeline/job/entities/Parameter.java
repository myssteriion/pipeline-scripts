package com.es2i.pipeline.job.entities;

import com.es2i.pipeline.job.Pipeline;
import com.es2i.pipeline.job.Pipeline.BuildType;
import com.es2i.pipeline.tools.Tools;

/**
 * Représente une variable paramètre d'un point de vue Pipeline.
 * @see conf/parameters.json
 */
public abstract class Parameter {

	/**
	 * boolean => case à cocher
	 * string => saisie libre
	 * choice => liste déroulante
	 */
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
				throw new IllegalArgumentException("Le type '" + name + "' n'est pas reconnu pour définir le type d'un paramètre.");
		}
	}; 
	
	/**
	 * Les paramètres sont définit dans le même fichier.
	 * Permet de définir si le paramètre est présent pour les monoBuild (mono), pour le buildAll (all), pour tous (every).
	 */
	public enum ScopeParamater { MONO, ALL, DASH, EVERY }; 
	
	/**
	 * Les propriété obligatoires et facultatives permettant de définir une variable paramètre.
	 */
	public enum ParameterKey { NAME("name"), TYPE("type"), SCOPE("scope"), DEFAULT_VALUE("defaultValue"), CHOICES ("choices"), DESCRIPTION("description");
	
		private String name;
	
		private ParameterKey(String name) {
			this.name = name;
		}
		
		public String getName() {
			return this.name;
		}
		
		/**
		 * Retourne le tableau des propriétés obligatoires dans le json (dépend du type).
		 */
		public static ParameterKey[] getKeys(TypeParameter type) {
			
			if (type == TypeParameter.BOOLEAN || type == TypeParameter.STRING) {
				ParameterKey[] tab = { NAME, TYPE, DEFAULT_VALUE, DESCRIPTION };
				return tab;
			}
			else if (type == TypeParameter.CHOICE) {
				ParameterKey[] tab = { NAME, TYPE, CHOICES, DESCRIPTION };
				return tab;
			}
			else
				throw new IllegalArgumentException("il manque un DEV : définir les champs obligatoires pour le type '" + type + "'.");
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
			this.scope = ScopeParamater.EVERY;
		}
		else if ( scope.equalsIgnoreCase(ScopeParamater.MONO.toString()) ) {
			this.scope = ScopeParamater.MONO;
		}
		else if ( scope.equalsIgnoreCase(ScopeParamater.ALL.toString()) ) {
			this.scope = ScopeParamater.ALL;
		}
		else if ( scope.equalsIgnoreCase(ScopeParamater.DASH.toString()) ) {
			this.scope = ScopeParamater.DASH;
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
	
	public boolean isInScope(Pipeline.BuildType buildType) {
		return this.scope == ScopeParamater.EVERY 
				|| (this.scope == ScopeParamater.ALL && buildType == BuildType.ALL_BUILD)
				|| (this.scope == ScopeParamater.MONO && buildType == BuildType.MONO_BUILD) 
				|| (this.scope == ScopeParamater.DASH && buildType == BuildType.DASHBOARD) ;
	}
}
