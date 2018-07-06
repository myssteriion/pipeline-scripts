package com.es2i.pipeline.tools;

public class ConstantTools {

	public static final String TAB								= "\t";
	public static final String CRLF								= "\n";
	public static final String UNDERSCORE						= "_";
	public static final String DOT								= ".";
	public static final String COMA								= ",";
		
	public static final String JENKINS_FILE 					= "Jenkinsfile";
	public static final String JENKINS_LINEAR_FILE 				= "Jenkinsfile-linear";
	public static final String RUNNER_DIRECTORY 				= "runner";
		
	public static final String APPLICATION_PROP_FILE			= "application.properties";
	public static final String BUILD_ALL_DIRECTORY_KEY 			= "dest.directory.build.all";
	public static final String BUILD_ONE_DIRECTORY_KEY 			= "dest.directory.build.one";
	public static final String PROJECTS_BUILD_ONE_KEY 			= "projects.build.one";
	public static final String PROJECTS_BUILD_ALL_GROUPE_KEY 	= "projects.build.all.groupe";
	public static final String PROJECTS_BUILD_ALL_GROUPE1_KEY 	= PROJECTS_BUILD_ALL_GROUPE_KEY + "1";
	
	public static final String PARAM_PROP_FILE 					= "parameters.properties";
	public static final String REVISION_KEY 					= "revision";
	
	public static final String ENV_PROP_FILE 					= "environment.properties";
	public static final String GLOBAL_KEY 						= "global";
	public static final String GITLAB_URL_KEY 					= GLOBAL_KEY + DOT + "gitLabUrl";
	public static final String REMOTE_CONNEXION_KEY 			= GLOBAL_KEY + DOT + "remoteConnexion";
	public static final String REMOTE_DEPOT_FOLDER_KEY 			= GLOBAL_KEY + DOT + "depotFolder";
	public static final String REMOTE_ESII_APP_FOLDER_KEY 		= GLOBAL_KEY + DOT + "esiiAppFolder";
	public static final String REMOTE_ESII_CONF_FOLDER_KEY 		= GLOBAL_KEY + DOT + "esiiConfFolder";
	public static final String RUNNER_KEY 						= "runner";
	public static final String JENKINS_URL_KEY 					= RUNNER_KEY + DOT + "jenkinsUrl";
	public static final String JOB_NAME_KEY 					= RUNNER_KEY + DOT + "jobName";
	public static final String JOB_TOKEN_KEY 					= RUNNER_KEY + DOT + "pipelineToken";
	public static final String INITIALIZE_KEY 					= "initialize";
	public static final String ENVENT_STORAGE_KEY 				= INITIALIZE_KEY + DOT + "enventStorage";
	
	public static final String TOOLS_PROP_FILE 					= "tools.properties";
	
	public static final String FUNCTIONS_FILE 					= "functions.txt";
	
	public static final String CALL_FUNCTIONS_PROP_FILE			= "callFunctions.properties";
	public static final String CALL_RUN_BUILD_KEY 				= "runBuild";
	
}
