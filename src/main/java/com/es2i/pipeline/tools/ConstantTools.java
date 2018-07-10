package com.es2i.pipeline.tools;

public class ConstantTools {

	/*
	 * caractères spéciaux
	 */
	public static final String TAB										= "\t";
	public static final String CRLF										= "\n";
	public static final String UNDERSCORE								= "_";
	public static final String DOT										= ".";
	public static final String COMA										= ",";
	public static final String DOLLAR									= "$";
	
	/*
	 * noms des fichiers et dossiers
	 */
	public static final String JENKINS_FILE 							= "Jenkinsfile";
	public static final String RUNNER_DIRECTORY 						= "runner";
	public static final String BUILD_ALL_DIRECTORY						= "buildAll";
	public static final String BUILD_ONE_DIRECTORY						= "buildOne";
	
	/*
	 * arboressence pour le dépoloiement
	 */
	public static final String ESII_APPLICATION 						= "ESII-Applications";
	public static final String APP 										= "app";
	public static final String CONF 									= "conf";
	public static final String DATA 									= "data";
	public static final String EVENT_STORAGE 							= "enventStorage";
	
	/*
	 * Le code java va directement remplacer des valeurs qui seront dans le Jenkinsfile.
	 * Ce sont les éléments statiques qui ne sont pas des variable d'environment (d'un point de vue pipeline).
	 * Ces valeurs sont présentes dans le fichier "funcions.txt".
	 */
	public static final String ESII_APPLICATION_PARAM 					= DOLLAR + "esiiFolder";
	public static final String APP_PARAM 								= DOLLAR + "appFolder";
	public static final String CONF_PARAM 								= DOLLAR + "confFolder";
	
	/*
	 * application.properties
	 */
	public static final String APPLICATION_PROP_FILE					= "application.properties";
	public static final String RUNNER_BRANCHES_KEY 						= "runner.branches";
	public static final String PROJECTS_BUILD_ONE_KEY 					= "projects.build.one";
	public static final String PROJECTS_BUILD_ALL_GROUPE_KEY 			= "projects.build.all.groupe";
	public static final String PROJECTS_BUILD_ALL_GROUPE1_KEY 			= PROJECTS_BUILD_ALL_GROUPE_KEY + "1";
	
	/*
	 * parameters.properties
	 */
	public static final String PARAM_PROP_FILE 							= "parameters.properties";
	public static final String REVISION_KEY 							= "revision";
	
	/*
	 * environment.properties
	 */
	public static final String ENV_PROP_FILE 							= "environment.properties";
	public static final String GLOBAL_KEY 								= "global";
	public static final String GITLAB_URL_KEY 							= GLOBAL_KEY + DOT + "gitLabUrl";
	public static final String PRIMARY_REMOTE_KEY 						= GLOBAL_KEY + DOT + "primaryRemote";
	public static final String REMOTE_DEPOT_FOLDER_KEY 					= GLOBAL_KEY + DOT + "depotFolder";
	public static final String RUNNER_KEY 								= "runner";
	public static final String JENKINS_URL_KEY 							= RUNNER_KEY + DOT + "jenkinsUrl";
	public static final String JOB_NAME_KEY 							= RUNNER_KEY + DOT + "jobName";
	public static final String JOB_TOKEN_KEY 							= RUNNER_KEY + DOT + "pipelineToken";
	
	/*
	 * tools.properties
	 */
	public static final String TOOLS_PROP_FILE 							= "tools.properties";
	
	/*
	 * functions.txt
	 */
	public static final String FUNCTIONS_FILE 							= "functions.txt";
	
	/*
	 * remoteDescriptor.properties
	 */
	public static final String REMOTE_DESCRIPTOR_FILE					= "remoteDescriptor.properties";
	public static final String SECONDARY_REMOTE_KEY 					= "secondary.remote";
	public static final String SECONDARY_REMOTE1_KEY 					= SECONDARY_REMOTE_KEY + "1";

}
