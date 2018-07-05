package com.es2i.pipeline.tools;

public class ConstantTools {

	public static final String TAB							= "\t";
	public static final String CRLF							= "\n";
	public static final String UNDERSCORE					= "_";
	public static final String DOT							= ".";
	public static final String COMA							= ",";
	
	public static final String JENKINS_FILE 				= "Jenkinsfile";
	public static final String JENKINS_LINEAR_FILE 			= "Jenkinsfile-linear";
	public static final String RUNNER_DIRECTORY 			= "runner";
	
	public static final String APPLICATION_PROP_FILE		= "application.properties";
	public static final String BUILD_ALL_DIRECTORY_KEY 		= "dest.directory.build.all";
	public static final String BUILD_ONE_DIRECTORY_KEY 		= "dest.directory.build.one";
	public static final String PROJECTS_BUILD_ONE_KEY 		= "projects.build.one";
	public static final String PROJECTS_BUILD_ALL_KEY 		= "projects.build.all";
	public static final String GROUP_KEY 					= "groupe";
	public static final String PROJECTS_GROUPE1_KEY 		= PROJECTS_BUILD_ALL_KEY + DOT + GROUP_KEY + "1";
	
	public static final String PARAM_PROP_FILE 				= "parameters.properties";
	public static final String REVISION_KEY 				= "revision";
	
	public static final String ENV_PROP_FILE 				= "environment.properties";
	public static final String GLOBAL_KEY 					= "global";
	public static final String RUNNER_KEY 					= "runner";
	public static final String GITLAB_URL_KEY 				= GLOBAL_KEY + DOT + "gitLabUrl";
	public static final String REMOTE_CONNEXION_KEY 		= GLOBAL_KEY + DOT + "remoteConnexion";
	public static final String REMOTE_DEPOT_FOLDER_KEY 		= GLOBAL_KEY + DOT + "depotFolder";
	public static final String REMOTE_ESII_APP_FOLDER_KEY 	= GLOBAL_KEY + DOT + "esiiAppFolder";
	public static final String REMOTE_ESII_CONF_FOLDER_KEY 	= GLOBAL_KEY + DOT + "esiiConfFolder";
	
	public static final String TOOLS_PROP_FILE 				= "tools.properties";
	
	public static final String FUNCTIONS_FILE 				= "functions.txt";
	
}
