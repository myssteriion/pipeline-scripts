package com.es2i.pipeline.job.helper;

import com.es2i.pipeline.job.entities.Tool;
import com.es2i.pipeline.tools.Tools;

public class PropToEntitiy {

	public static Tool transformToTool(String propName, String propValue) {
		
		if ( Tools.isNullOrEmpty(propValue) )
			throw new IllegalArgumentException("Le propriété " + propName + " est vide");
		
		return new Tool(propName, propValue);
	}
	
}
