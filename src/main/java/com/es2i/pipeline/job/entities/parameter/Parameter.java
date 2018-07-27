package com.es2i.pipeline.job.entities.parameter;

/**
 * Représente une variable paramètre d'un point de vue Pipeline.
 */
public abstract class Parameter {

	/**
	 * boolean => case à cocher
	 * string => saisie libre
	 * choice => liste déroulante
	 */
	public enum TypeParameter { BOOLEAN, STRING, CHOICE;
		
		public static TypeParameter getTypeParameterByName(String name) {
			
			if ( BOOLEAN.toString().equalsIgnoreCase(name) ) 			return BOOLEAN;
			else if ( STRING.toString().equalsIgnoreCase(name) ) 		return STRING;
			else if ( CHOICE.toString().equalsIgnoreCase(name) ) 		return CHOICE;
			else
				throw new IllegalArgumentException("Le type '" + name + "' n'est pas reconnu pour définir le type d'un paramètre.");
		}
	}; 
	
	/**
	 * Les propriété permettant de définir une variable paramètre.
	 */
	public enum ParameterKey { NAME("name"), TYPE("type"), DEFAULT_VALUE("defaultValue"), CHOICES ("choices"), DESCRIPTION("description");
	
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
				throw new IllegalArgumentException("TECH : définir les champs obligatoires pour le type '" + type + "'.");
		}
	};

	
	/**
	 * Le nom du paramètre.
	 */
	private String name;
	
	/**
	 * Le type du paramètre.
	 */
	private TypeParameter type;
	
	/**
	 * La description du paramètre.
	 */
	private String desc;
	
	

	public Parameter(String name, TypeParameter type, String desc) {
		
		this.name = name;
		this.type = type;
		this.desc = desc;
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

	@Override
	public String toString() {
		return "name=" + name + ", type=" + type + ", desc=" + desc;
	}

}
