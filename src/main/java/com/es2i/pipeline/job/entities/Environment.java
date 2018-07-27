package com.es2i.pipeline.job.entities;

import java.util.HashMap;
import java.util.Map;

import com.es2i.pipeline.tools.ConstantTools;

/**
 * Représente une variable environement d'un point de vue Pipeline.
 */
public class Environment {
	
	/**
	 * Le nom de la varaible d'env.
	 */
	private String name;
	
	/**
	 * Peut contenir plusieur valeur est sera splité sur le caractère ','.
	 */
	private String value;
	
	/**
	 * Est vraie si la variable est un tableau dans le fichier properties. Le champ value sera splité par le caractère ','.
	 */
	private boolean isList;
	
	
	
	public Environment(String name, String value, boolean isList) {
		this.name = name;
		this.value = value;
		this.isList = isList;
	}



	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
	
	public boolean isList() {
		return isList;
	}

	/**
	 * Split la value avec le caractère ','.
	 * Le 1er élément a pour clé "<name>", les autres éléments ont pour clé "<name>1", "<name>2", "<name>3", etc
	 */
	public Map<String, String> getValuesSplitted() {

		String[] values = value.split(ConstantTools.COMA);
		
		Map<String, String> valuesMap = new HashMap<String, String>();
		for (int i = 0; i < values.length; i++)
			valuesMap.put(name + (i == 0 ? "" : i), values[i].trim() );
		
		return valuesMap;
	}
	
	@Override
	public String toString() {
		return "name=" + name + ", value=" + value + ", isList =" + isList + "]";
	}

}
