package com.es2i.pipeline.job.entities;

import java.util.ArrayList;
import java.util.List;

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
	public List<Environment> getValuesSplitted() {

		List<Environment> envList = new ArrayList<Environment>();
		
		String[] values = value.split(ConstantTools.COMA);
		for (int i = 0; i < values.length; i++)
			envList.add( new Environment(name + (i == 0 ? "" : i), values[i].trim(), false) );
		
		return envList;
	}
	
	@Override
	public String toString() {
		return "name=" + name + ", value=" + value + ", isList=" + isList;
	}

}
