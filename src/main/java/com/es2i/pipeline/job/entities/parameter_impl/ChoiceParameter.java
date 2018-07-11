package com.es2i.pipeline.job.entities.parameter_impl;

import com.es2i.pipeline.job.entities.Parameter;
import com.es2i.pipeline.tools.ConstantTools;

public class ChoiceParameter extends Parameter {

	private String choices;
	
	
	
	public ChoiceParameter(String name, String choices, String desc, String scope) {
		
		super(name, TypeParameter.CHOICE, desc, scope);
		
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
	
}
