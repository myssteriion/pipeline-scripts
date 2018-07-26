package com.es2i.pipeline.job.entities.parameter.impl;

import com.es2i.pipeline.job.entities.parameter.Parameter;
import com.es2i.pipeline.tools.ConstantTools;

public class ChoiceParameter extends Parameter {

	private String choices;
	
	
	
	public ChoiceParameter(String name, String choices, String desc) {
		super(name, TypeParameter.CHOICE, desc);
		
		String[] elements = choices.split(ConstantTools.COMA);
		
		choices = "";
		if (elements != null && elements.length > 0) {
			for (String element : elements) {
				choices += element.trim() + "\\n";
			}
		}
		
		this.choices = choices;
	}

	
	
	public String getChoices() {
		return choices;
	}
	
	@Override
	public String toString() {
		return "ChoiceParameter [" + super.toString() + ", choices=" + choices + "]";
	}
	
}
