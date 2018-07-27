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
	public static final String DOT										= ".";
	
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
	 * Ce sont les éléments statiques qui ne sont pas des variables d'environements (d'un point de vue pipeline).
	 * Ces valeurs sont présentes dans le fichier "functions.txt" et dans "ConstructHelper.java".
	 */
	public static final String ESII_APPLICATION_PARAM 					= DOLLAR + "esiiFolder";
	public static final String APP_PARAM 								= DOLLAR + "appFolder";
	public static final String CONF_PARAM 								= DOLLAR + "confFolder";
	
	/*
	 * dossiers conf (en fonction des script générés)
	 */
	public static final String RUNNER_FOLDER 							= "runner" + SLASH;
	public static final String BUILD_ALL_FOLDER 						= "buildAll" + SLASH;
	public static final String BUILD_ONE_FOLDER 						= "buildOne" + SLASH;
	public static final String DASHBOARD_FOLDER 						= "dashboard" + SLASH;
	
	/*
	 * fichiers conf
	 */
	public static final String APPLICATION_PROP_FILE					= "application.properties";
	public static final String PARAM_JSON_FILE 							= "parameters.json";
	public static final String TOOLS_JSON_FILE 							= "tools.json";
	public static final String ENV_PROP_FILE 							= "environment.properties";
	public static final String PROJECT_ENV_PROP_FILE 					= "project_environment.properties";
	public static final String FUNCTIONS_TXT_FILE 						= "functions.txt";
	
	public static final String SECONDARY_REMOTE_PROP_FILE				= "secondaryRemote.properties";
	
	/*
	 * runner
	 */
	public static final String RUNNER_REVISIONS_KEY 					= "revisons";
	public static final String RUNNER_MAVEN_PROFILES_KEY 				= "mavenProfiles";
	
	/*
	 * buildAll
	 */
	public static final String BUILD_ALL_PROJECTS_GROUPE_KEY 			= "projects.groupe";
	public static final String BUILD_ALL_PROJECTS_GROUPE1_KEY 			= BUILD_ALL_PROJECTS_GROUPE_KEY + "1";
	public static final String SECONDARY_REMOTE_KEY 					= "secondary.remote";
	public static final String SECONDARY_REMOTE1_KEY 					= SECONDARY_REMOTE_KEY + "1";
	
	/*
	 * buildOne
	 */
	public static final String PROJECTS_BUILD_ONE_KEY 					= "projects";
	
	/*
	 * dashboard
	 */
	public static final String PROJECTS_BACK_DASHBOARD_KEY 				= "projects.back";
	public static final String PROJECTS_FRONT_DASHBOARD_KEY 			= "projects.front";

}
