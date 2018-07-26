package com.es2i.pipeline.job.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import com.es2i.pipeline.job.entities.Environment;
import com.es2i.pipeline.job.entities.ProjectKeyEnum;
import com.es2i.pipeline.job.entities.parameter.Parameter;
import com.es2i.pipeline.job.entities.parameter.impl.BooleanParameter;
import com.es2i.pipeline.job.entities.parameter.impl.ChoiceParameter;
import com.es2i.pipeline.job.entities.parameter.impl.StringParameter;
import com.es2i.pipeline.job.script.Script;
import com.es2i.pipeline.job.script.impl.BuildAll;
import com.es2i.pipeline.job.script.impl.BuildOne;
import com.es2i.pipeline.job.script.impl.Dashboard;
import com.es2i.pipeline.job.script.impl.Runner;
import com.es2i.pipeline.tools.ConstantTools;
import com.es2i.pipeline.tools.Tools;

/**
 * Singleton. Accède à la conf et vérifie l'intégrité des éléments et aliment les objets de type Script.
 * Possède un cache.
 */
public class ConfReader {

	private static ConfReader instance;
	
	private Runner runner;

	private BuildAll buildAll;
	
	private BuildOne buildOne;
	
	private Dashboard dashboard;
	
	
	
	private ConfReader() {
		
	}
	
	
	
	public static ConfReader getInstance() throws IOException {
		
		if (instance == null)
			instance = new ConfReader();
		
		return instance;
	}
	
	
	
	/*
	 * Le script Runner.
	 */
	
	public Runner getRunner() throws IOException {
		
		if (runner == null) {
			runner = new Runner();
			runner.setRevisions( getRunnerRevisions() );
			runner.setMavenProfiles( getRunnerMavenProfiles() );
			runner.setEnvironements( getRunnerEnvironment() );
		}
		
		return runner;
	}
	
	private List<Environment> getRunnerEnvironment() throws IOException {
		
		List<Environment> runnerEnvironements = new ArrayList<Environment>();
		
		Properties props = Tools.findPropertyFile(ConstantTools.RUNNER_FOLDER + ConstantTools.ENV_PROP_FILE);
		for ( Map.Entry<Object, Object> entry : props.entrySet() ) 
			runnerEnvironements.add( new Environment( entry.getKey().toString(), entry.getValue().toString() ) );
		
		return runnerEnvironements;
	}

	private List<String> getRunnerRevisions() throws IOException {
		
		List<String> runnerRevisions = new ArrayList<String>();
		
		Properties prop = Tools.findPropertyFile(ConstantTools.RUNNER_FOLDER + ConstantTools.APPLICATION_PROP_FILE);
		String[] revisions = prop.getProperty(ConstantTools.RUNNER_REVISIONS_KEY).split(ConstantTools.COMA);
		if ( revisions != null && revisions.length > 0 ) {
			for (String revision : revisions) {
				if ( !Tools.isNullOrEmpty(revision) ) 
					runnerRevisions.add( revision.trim() );
			}
		}
		
		return runnerRevisions;
	}
	
	private List<String> getRunnerMavenProfiles() throws IOException {
		
		List<String> runnerMavenProfiles = new ArrayList<String>();
		
		Properties prop = Tools.findPropertyFile(ConstantTools.RUNNER_FOLDER + ConstantTools.APPLICATION_PROP_FILE);
		String[] mavenProfiles = prop.getProperty(ConstantTools.RUNNER_MAVEN_PROFILES_KEY).split(ConstantTools.COMA);
		if ( mavenProfiles != null && mavenProfiles.length > 0 ) {
			for (String mavenProfile : mavenProfiles) {
				if ( !Tools.isNullOrEmpty(mavenProfile) ) 
					runnerMavenProfiles.add( mavenProfile.trim() );
			}
		}
		
		return runnerMavenProfiles;
	}


	/*
	 * Le script BuilAll.
	 */
	
	public BuildAll getBuildAll() throws IOException {
		
		if (buildAll == null) {
			buildAll = new BuildAll();
			buildAll.setProjects( getBuildAllProjects() );
			buildAll.setSecondaryRemotes( getSecondaryRemotes() );
			buildAll.setParameters( getBuilAllParameters() );
			buildAll.setEnvironements( getBuilAllEnvironment() );
			buildAll.setProjectsEnvironements( getProjectsEnvironments(buildAll.getProjectsList()) );
		}
		
		return buildAll;
	}
	
	private List<Parameter> getBuilAllParameters() throws IOException {

		List<Parameter> params = new ArrayList<Parameter>();
		params.addAll( getCommumParameters() );
		params.addAll( getCustomParameters(BuildAll.class) );
		return params;
	}

	private List<Environment> getBuilAllEnvironment() throws IOException {
		return getGlobalEnvironment();
	}
	
	private Map<Integer, List<String>> getBuildAllProjects() throws IOException {
		
		Map<Integer, List<String>> projects = new HashMap<Integer, List<String>>();
			
		Properties prop = Tools.findPropertyFile(ConstantTools.BUILD_ALL_FOLDER + ConstantTools.APPLICATION_PROP_FILE);
		
		projects = new HashMap<Integer, List<String>>();
		String key = ConstantTools.BUILD_ALL_PROJECTS_GROUPE1_KEY;
		
		int index = 1;
		while ( prop.containsKey(key) ) {
			String[] projectsTab = prop.getProperty(key).split(ConstantTools.COMA);
			if (projectsTab != null) {
				projects.put( index, Arrays.asList(projectsTab).stream().map(String::trim).collect(Collectors.toList()) );
			}
			index++;
			key = ConstantTools.BUILD_ALL_PROJECTS_GROUPE_KEY + index;
		}
		
		return projects;
	}

	private List<String> getSecondaryRemotes() throws IOException {
		
		List<String> secondaryRemotes = new ArrayList<String>();
		
		Properties prop = Tools.findPropertyFile(ConstantTools.BUILD_ALL_FOLDER + ConstantTools.SECONDARY_REMOTE_FILE);
		String key = ConstantTools.SECONDARY_REMOTE1_KEY;
		
		int index = 1;
		while ( prop.containsKey(key) ) {
			secondaryRemotes.add( prop.getProperty(key) );
			index++;
			key = ConstantTools.SECONDARY_REMOTE_KEY + index;
		}
		
		return secondaryRemotes;
	}
	
	
	/*
	 * Le script BuildOne.
	 */
	
	public BuildOne getBuildOne() throws IOException {
		
		if (buildOne == null) {
			buildOne = new BuildOne();
			buildOne.setProjects( getBuildOneProjects() );
			buildOne.setParameters( getBuilOneParameters() );
			buildOne.setEnvironements( getBuilOneEnvironment() );
			buildOne.setProjectsEnvironements( getProjectsEnvironments(buildOne.getProjectsList()) );
		}
		
		return buildOne;
	}
	
	private List<Parameter> getBuilOneParameters() throws IOException {
		
		List<Parameter> params = new ArrayList<Parameter>();
		params.addAll( getCommumParameters() );
		params.addAll( getCustomParameters(BuildOne.class) );
		return params;
	}
	
	private List<Environment> getBuilOneEnvironment() throws IOException {
		return getGlobalEnvironment();
	}

	private Map<Integer, String> getBuildOneProjects() throws IOException {
		
		Map<Integer, String> projects = new HashMap<Integer, String>();
		
		Properties prop = Tools.findPropertyFile(ConstantTools.BUILD_ONE_FOLDER + ConstantTools.APPLICATION_PROP_FILE);
		
		projects = new HashMap<Integer, String>();
		String[] projectsList = prop.getProperty(ConstantTools.PROJECTS_BUILD_ONE_KEY).split(ConstantTools.COMA);
		if (projectsList != null) {
			for (int index = 0; index < projectsList.length; index++)
				projects.put(index+1, projectsList[index].trim());
		}
		
		return projects;
	}

	
	/*
	 * Le script Dashboard.
	 */
	
	public Dashboard getDashboard() throws IOException {
		
		if (dashboard == null) {
			dashboard = new Dashboard();
			dashboard.setFrontEnvironments( getDashboardFrontEnvironments() );
			dashboard.setBackEnvironments( getDashboardBackEnvironments() );
			dashboard.setParameters( getDashboardParameters() );
			dashboard.setEnvironements( getDashboardEnvironment() );
		}
			
		return dashboard;
	}
	
	private List<Parameter> getDashboardParameters() throws IOException {
		
		List<Parameter> params = new ArrayList<Parameter>();
		params.addAll( getCommumParameters() );
		params.addAll( getCustomParameters(Dashboard.class) );
		return params;
	}
	
	private List<Environment> getDashboardEnvironment() throws IOException {
		return getGlobalEnvironment();
	}
	
	private Map<String, List<Environment>> getDashboardFrontEnvironments() throws IOException {
		return getDashboardEnvironments(ConstantTools.FRONT);
	}
	
	private Map<String, List<Environment>> getDashboardBackEnvironments() throws IOException {
		return getDashboardEnvironments(ConstantTools.BACK);
	}
	
	// TODO : modifier le env.json -> env.prop
	private Map<String, List<Environment>> getDashboardEnvironments(String frontOrBack) throws IOException {
		
		Map<String, List<Environment>> backEnvironments = new HashMap<String, List<Environment>>();
		
		try ( InputStream is = ConfReader.class.getClassLoader().getResourceAsStream(ConstantTools.DASHBOARD_FOLDER + "environment.json") ) {
			try ( Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8) ) {	
				try ( JsonReader jsonReader = Json.createReader(is) ) {
					
					JsonObject racineArray = jsonReader.readObject();
					JsonArray jsonArray = racineArray.getJsonArray(frontOrBack);
					
					for (int i = 0; i < jsonArray.size(); i++) {
						JsonObject object = jsonArray.get(i).asJsonObject();
						for (Map.Entry<String, JsonValue> entry : object.entrySet() ) {
							
							String secondKey = entry.getKey();
							List<Environment> envs = new ArrayList<Environment>();
							for (ProjectKeyEnum keyEnum : ProjectKeyEnum.getKeys())
								envs.add( new Environment(keyEnum.getName(), entry.getValue().asJsonObject().getString(keyEnum.getName(), null)) );
							
							backEnvironments.put(secondKey, envs);
						}
					}
				}
			}
		}
		
		return backEnvironments;
	}
	
	
	/*
	 * Les paramètres.
	 */
	private List<Parameter> getCommumParameters() throws IOException {
		
		List<Parameter> parameters = new ArrayList<Parameter>();
		
		try ( InputStream is = ConfReader.class.getClassLoader().getResourceAsStream(ConstantTools.PARAM_JSON_FILE) ) {
			try ( Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8) ) {	
				try ( JsonReader jsonReader = Json.createReader(is) ) {
					
					JsonArray array = jsonReader.readArray();
					for (int i = 0; i < array.size(); i++)
						parameters.add( transformJsonToParameter(array.getJsonObject(i)) );
				}
			}
		}
	
		return parameters;
	}
	
	private List<Parameter> getCustomParameters(Class<? extends Script> scriptClass) throws IOException {
		
		String folderName = "";
		if (scriptClass == BuildAll.class)				folderName = ConstantTools.BUILD_ALL_FOLDER;
		else if (scriptClass == BuildOne.class)			folderName = ConstantTools.BUILD_ONE_FOLDER;
		else if (scriptClass == Dashboard.class)		folderName = ConstantTools.DASHBOARD_FOLDER;
		else 											throw new IllegalArgumentException("TECH : il manque un if (" + scriptClass + ")");
		
		List<Parameter> parameters = new ArrayList<Parameter>();
		
		try ( InputStream is = ConfReader.class.getClassLoader().getResourceAsStream(folderName + ConstantTools.PARAM_JSON_FILE) ) {
			try ( Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8) ) {	
				try ( JsonReader jsonReader = Json.createReader(is) ) {
					
					JsonArray array = jsonReader.readArray();
					for (int i = 0; i < array.size(); i++)
						parameters.add( transformJsonToParameter(array.getJsonObject(i)) );
				}
			}
		}
	
		return parameters;
	}

	private Parameter transformJsonToParameter(JsonObject jsonObject) throws IOException {
		
		String typeStr = jsonObject.getString(Parameter.ParameterKey.TYPE.getName(), null);
		if ( Tools.isNullOrEmpty(typeStr) )
			throw new IllegalArgumentException("Pour définir un paramètre, la propriétés '" + Parameter.ParameterKey.TYPE.getName() + "' est obligatoire.");
		
		Parameter.TypeParameter type = Parameter.TypeParameter.getTypeParameterByName(typeStr);
		for ( Parameter.ParameterKey paramKey : Parameter.ParameterKey.getKeys(type) ) {
			
			String value = jsonObject.getString(paramKey.getName(), null);
			if ( Tools.isNullOrEmpty(value) )
				throw new IllegalArgumentException("Pour définir un paramètre de type '" + typeStr + "', la propriétés '" + paramKey.getName() + "' est obligatoire.");
		}
		
		String name = jsonObject.getString(Parameter.ParameterKey.NAME.getName(), null);
		String defaultValue = jsonObject.getString(Parameter.ParameterKey.DEFAULT_VALUE.getName(), null);
		String description = jsonObject.getString(Parameter.ParameterKey.DESCRIPTION.getName());
		String choices = jsonObject.getString(Parameter.ParameterKey.CHOICES.getName(), null);
		
		choices = replaceValueByPropertiesIfNeedIt(name, choices);
		
		switch (type) {
		
			case BOOLEAN : return new BooleanParameter(name, defaultValue, description);
			case CHOICE : return new ChoiceParameter(name, choices, description);
			case STRING : return new StringParameter(name, defaultValue, description);
			
			default : throw new IllegalArgumentException("il manque un DEV : définir la classe le type '" + type + "'.");
		}
	}

	private String replaceValueByPropertiesIfNeedIt(String name, String value) throws IOException {
		
		if ( Tools.isNullOrEmpty(value) || !value.startsWith(ConstantTools.DOLLAR) )
			return value;
		
		// surpprimer le caracètre '$'
		String tmp = value.substring(1);
		int lastSlash = tmp.lastIndexOf(ConstantTools.SLASH);
		String propertyFile = tmp.substring(0, lastSlash);
		String propertyKey = tmp.substring(lastSlash+1);
		
		return Tools.findPropertyFile(propertyFile).getProperty(propertyKey);
	}

	
	/*
	 * Les variables d'environements.
	 */
	private List<Environment> getGlobalEnvironment() throws IOException {
			
		List<Environment> globalEnvironements = new ArrayList<Environment>();
		
		Properties props = Tools.findPropertyFile(ConstantTools.ENV_PROP_FILE);
		for ( Map.Entry<Object, Object> entry : props.entrySet() )
			globalEnvironements.add( new Environment(entry.getKey().toString(), entry.getValue().toString()) );
		
		return globalEnvironements;
	}
	
	private Map<String, List<Environment>> getProjectsEnvironments(List<String> projects) throws IOException {
		
		Map<String, List<Environment>> projectsEnvironments = new HashMap<String, List<Environment>>();
		
		Properties props = Tools.findPropertyFile(ConstantTools.PROJECT_ENV_PROP_FILE);
		for (String project : projects) {
			
			List<Environment> envs = new ArrayList<Environment>();
			for (ProjectKeyEnum keyEnum : ProjectKeyEnum.getKeys())
				envs.add( new Environment(keyEnum.getName(), props.getProperty(project + ConstantTools.DOT + keyEnum.getName())) );
			
			projectsEnvironments.put(project, envs);
		}
		
		return projectsEnvironments;
	}

}
