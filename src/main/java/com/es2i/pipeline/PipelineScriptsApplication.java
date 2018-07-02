package com.es2i.pipeline;

import org.apache.log4j.Logger;

import com.es2i.pipeline.job.Pipeline;

public class PipelineScriptsApplication {

	private static final Logger LOGGER = Logger.getLogger(PipelineScriptsApplication.class);
	 
	
	
	public static void main(String[] args) {
		
		try {
			
			LOGGER.info("Start process");
			Pipeline job = new Pipeline();
			job.run();
			LOGGER.info("End process");
		}
		catch(Exception e) {
			LOGGER.error("End process with error", e);
		}
	}
	
}
