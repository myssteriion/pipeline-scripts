package com.es2i.pipeline.tools;

public class ConstantTools {

	/*
	 * caractères spéciaux
	 */
	public static final String TAB										= "\t";
	public static final String CRLF										= "\n";
	public static final String COMA										= ",";
	public static final String DOLLAR									= "$";
	public static final String SLASH									= "/";
	
	/*
	 * noms des fichiers et dossiers
	 */
	public static final String JENKINS_FILE 							= "Jenkinsfile";
	public static final String RUNNER_DIRECTORY 						= "runner";
	public static final String BUILD_ALL_DIRECTORY						= "buildAll";
	public static final String BUILD_ONE_DIRECTORY						= "buildOne";
	public static final String DASHBOARD_DIRECTORY						= "dashboard";
	
	/*
	 * arboressence pour le déploiement
	 */
	public static final String ESII_APPLICATION 						= "ESII-Applications";
	public static final String APP 										= "app";
	public static final String CONF 									= "conf";
	public static final String DATA 									= "data";
	public static final String EVENT_STORAGE 							= "event-storage";
	
	/*
	 * Le code java va directement remplacer des valeurs qui seront dans le Jenkinsfile.
	 * Ce sont les éléments statiques qui ne sont pas des variables d'environement (d'un point de vue pipeline).
	 * Ces valeurs sont présentes dans le fichier "functions.txt" et dans "ConstructHelper.java".
	 */
	public static final String ESII_APPLICATION_PARAM 					= DOLLAR + "esiiFolder";
	public static final String APP_PARAM 								= DOLLAR + "appFolder";
	public static final String CONF_PARAM 								= DOLLAR + "confFolder";
	
	/*
	 * application.properties
	 */
	public static final String APPLICATION_PROP_FILE					= "application.properties";
	public static final String RUNNER_REVISIONS_KEY 					= "runner.revisons";
	public static final String RUNNER_MAVEN_PROFILES_KEY 				= "runner.mavenProfiles";
	public static final String PROJECTS_BUILD_ONE_KEY 					= "projects.build.one";
	public static final String PROJECTS_BUILD_ALL_GROUPE_KEY 			= "projects.build.all.groupe";
	public static final String PROJECTS_BUILD_ALL_GROUPE1_KEY 			= PROJECTS_BUILD_ALL_GROUPE_KEY + "1";
	
	/*
	 * parameters.json / tools.json / functions.txt
	 */
	public static final String PARAM_JSON_FILE 							= "parameters.json";
	public static final String TOOLS_JSON_FILE 							= "tools.json";
	public static final String FUNCTIONS_FILE 							= "functions.txt";
	
	/*
	 * environment.json
	 */
	public static final String ENV_PROP_FILE 							= "environment.json";
	public static final String GLOBAL_KEY 								= "global";
	public static final String RUNNER_KEY 								= "runner";
	
	/*
	 * dashboard
	 */
	public static final String DASHBOARD_ENV_PROP_FILE 					= "dashboard/environment.json";
	public static final String BACK 									= "back";
	public static final String FRONT 									= "front";
	
	/*
	 * remoteDescriptor.properties
	 */
	public static final String REMOTE_DESCRIPTOR_FILE					= "remoteDescriptor.properties";
	public static final String SECONDARY_REMOTE_KEY 					= "secondary.remote";

}
