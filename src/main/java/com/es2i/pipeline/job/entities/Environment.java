package com.es2i.pipeline.job.entities;

import com.es2i.pipeline.tools.ConstantTools;


/**
 * Représente une variable environement d'un point de vue Pipeline.
 * @see conf/environment.json
 */
public class Environment {

	/**
	 * Les variables d'environements sont définit dans le même fichier (sauf pour dashboard).
	 * Permet de déterminer les différentes clés.
	 */
	public enum ScopeEnvironment { GLOBAL, RUNNER, PROJECT;
	
		public static ScopeEnvironment findScopeEnvironmentByName(String name) {
			
			if ( GLOBAL.toString().equalsIgnoreCase(name) )
				return GLOBAL;
			else if ( RUNNER.toString().equalsIgnoreCase(name) )
				return RUNNER;
			else 
				return PROJECT;
		}
	};
	
	/**
	 * Les propriété obligatoires et facultatives permettant de définir une variable environement.
	 */
	public enum EnvironmentKey {
		// global
		GIT_LAB_URL("gitLabUrl", true), PRIMARY_REMOTE("primaryRemote", true), DEPOT_FOLDER("depotFolder", true),
		
		// runner
		JENKINS_URL("jenkinsUrl", true), JOB_NAME_REMOTE("jobName", true), PIPELINE_TOKEN("pipelineToken", true),
		
		// project
		GIT_ROOT("gitRoot", true), PROJECT_ROOT("projectRoot", true), JDK_COMPILATION("jdkCompilation", true), MVN_VERSION("mvnVersion", true),
		TARGET_DIRECTORY("targetDirectory", false), SOURCE_APP_DIRECTORY("sourceAppDirectory", false), SOURCE_EXTENSION("sourceExtension", false),
		SOURCE_CON_DIRECTORY("sourceConfDirectory", false);
		
		private String name;
		
		private boolean isMandatory;
	
		private EnvironmentKey(String name, boolean isMandatory) {
			this.name = name;
			this.isMandatory = isMandatory;
		}
		
		public String getName() {
			return this.name;
		}
		
		public boolean isMandatory() {
			return this.isMandatory;
		}
		
		public static EnvironmentKey[] getKeys(String scope) {
			
			if ( scope.equalsIgnoreCase(ConstantTools.GLOBAL_KEY) ) {
				EnvironmentKey[] tab = { GIT_LAB_URL, PRIMARY_REMOTE, DEPOT_FOLDER };
				return tab;
			}
			else if ( scope.equalsIgnoreCase(ConstantTools.RUNNER_KEY) ) {
				EnvironmentKey[] tab = { JENKINS_URL, JOB_NAME_REMOTE, PIPELINE_TOKEN };
				return tab;
			}
			else {
				EnvironmentKey[] tab = { GIT_ROOT, PROJECT_ROOT, JDK_COMPILATION, MVN_VERSION, TARGET_DIRECTORY, SOURCE_APP_DIRECTORY, SOURCE_EXTENSION, SOURCE_CON_DIRECTORY };
				return tab;
			}
		}
	};
	
	private String name;
	
	private String value;
	
	
	
	public Environment(String name, String value) {
		this.name = name;
		this.value = value;
	}



	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
	
}
