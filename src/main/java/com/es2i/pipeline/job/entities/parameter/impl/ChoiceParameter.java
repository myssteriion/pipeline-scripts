package com.es2i.pipeline.job.entities.parameter.impl;

import com.es2i.pipeline.job.entities.parameter.Parameter;
import com.es2i.pipeline.tools.ConstantTools;

public class ChoiceParameter extends Parameter {

	/**
	 * La liste de valeurs.
	 */
	private String choices;
	
	
	
	public ChoiceParameter(String name, String choices, String desc) {
		
		super(name, TypeParameter.CHOICE, desc);
		this.choices = formatChoices(choices);
		
	}
	
	/**
	 * Formatte la liste de valeur (ajoute le caractère '\n' entre chaque élément).
	 */
	private String formatChoices(String choices) {
		
		String[] elements = choices.split(ConstantTools.COMA);
		
		choices = "";
		if (elements != null && elements.length > 0) {
			for (String element : elements) {
				choices += element.trim() + "\\n";
			}
		}
		
		return choices;
	}

	
	
	public String getChoices() {
		return choices;
	}
	
	@Override
	public String toString() {
		return super.toString() + ", choices=" + choices;
	}
	
}
