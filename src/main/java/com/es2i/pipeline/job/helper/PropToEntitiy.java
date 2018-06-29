package com.es2i.pipeline.job.helper;

import com.es2i.pipeline.job.entities.Environment;
import com.es2i.pipeline.job.entities.Parameter;
import com.es2i.pipeline.job.entities.Tool;
import com.es2i.pipeline.tools.ConstantTools;
import com.es2i.pipeline.tools.Tools;

public class PropToEntitiy {

	public static Parameter transformToParameter(String propName, String propValue) {
		
		if ( Tools.isNullOrEmpty(propValue) )
			throw new IllegalArgumentException("Le propriété " + propName + " est vide");
		
		String[] tab = propValue.split(ConstantTools.UNDERSCORE);
		if (tab == null || tab.length != 4)
			throw new IllegalArgumentException("Le propriété " + propName + " n'est pas correct (" + propValue + ")");
		
		return new Parameter(tab[0], tab[1], tab[2], tab[3]);
	}
	
	public static Environment transformToEnvironment(String propName, String propValue) {
		
		if ( Tools.isNullOrEmpty(propValue) )
			throw new IllegalArgumentException("Le propriété " + propName + " est vide");
		
		String[] tab = propName.split("\\" + ConstantTools.DOT);
		if (tab == null || tab.length != 2)
			throw new IllegalArgumentException("Le propriété " + propName + " n'est pas correct (" + propValue + ")");
		
		return new Environment(tab[1], propValue);
	}
	
	public static Tool transformToTool(String propName, String propValue) {
		
		if ( Tools.isNullOrEmpty(propValue) )
			throw new IllegalArgumentException("Le propriété " + propName + " est vide");
		
		return new Tool(propName, propValue);
	}
	
}
