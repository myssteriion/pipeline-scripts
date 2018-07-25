package com.es2i.pipeline.job.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dashboard {

	private Map<String, List<Environment>> frontEnvironements;
	
	private Map<String, List<Environment>> backEnvironements;

		
	
	public Dashboard() {
		frontEnvironements = new HashMap<String, List<Environment>>();
		backEnvironements = new HashMap<String, List<Environment>>();
	}



	public Map<String, List<Environment>> getFrontEnvironements() {
		return frontEnvironements;
	}
	
	public void addFrontEnvironements(String key, List<Environment> environment) {
		
		if ( !frontEnvironements.containsKey(key) )
			frontEnvironements.put(key, environment);
	}

	public Map<String, List<Environment>> getBackEnvironements() {
		return backEnvironements;
	}

	public void addBackEnvironements(String key, List<Environment> environment) {
		
		if ( !backEnvironements.containsKey(key) )
			backEnvironements.put(key, environment);
	}
	
}
