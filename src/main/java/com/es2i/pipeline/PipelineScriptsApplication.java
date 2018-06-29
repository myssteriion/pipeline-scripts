package com.es2i.pipeline;

import com.es2i.pipeline.job.Pipeline;

public class PipelineScriptsApplication {

	public static void main(String[] args) throws Exception {
		
		System.out.println("Debut");
		Pipeline job = new Pipeline();
		job.run();
		System.out.println("Fin");
	}
}
