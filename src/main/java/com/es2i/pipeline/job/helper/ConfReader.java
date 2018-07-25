package com.es2i.pipeline.job.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import com.es2i.pipeline.job.entities.Environment;
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
	
	
	
	private ConfReader() throws IOException {
		init();
	}
	
	private void init() throws IOException {

		Properties application = Tools.findPropertyFile(ConstantTools.APPLICATION_PROP_FILE);
		Set<String> expectedKeys = new HashSet<String>();
		expectedKeys.add(ConstantTools.RUNNER_REVISIONS_KEY);
		expectedKeys.add(ConstantTools.PROJECTS_BUILD_ONE_KEY);
		expectedKeys.add(ConstantTools.PROJECTS_BUILD_ALL_GROUPE1_KEY);
		Tools.checkKeys(expectedKeys, application.stringPropertyNames(), ConstantTools.APPLICATION_PROP_FILE);
		
		/* */
		
		Properties remoteDescriptor = Tools.findPropertyFile(ConstantTools.REMOTE_DESCRIPTOR_FILE);
		expectedKeys = new HashSet<String>();
		Tools.checkKeys(expectedKeys, remoteDescriptor.stringPropertyNames(), ConstantTools.REMOTE_DESCRIPTOR_FILE);
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
		
		try ( InputStream is = ConfReader.class.getClassLoader().getResourceAsStream(ConstantTools.ENV_PROP_FILE) ) {
			try ( Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8) ) {	
				try ( JsonReader jsonReader = Json.createReader(is) ) {
					
					JsonObject objects = jsonReader.readObject();
					JsonObject runnerObject = objects.getJsonObject(ConstantTools.RUNNER_KEY);
					runnerEnvironements = checkEnvironment(ConstantTools.RUNNER_DIRECTORY, runnerObject);
				}
			}
		}
		
		return runnerEnvironements;
	}

	private List<String> getRunnerRevisions() throws IOException {
		
		List<String> runnerRevisions = new ArrayList<String>();
		
		Properties prop = Tools.findPropertyFile(ConstantTools.APPLICATION_PROP_FILE);
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
		
		Properties prop = Tools.findPropertyFile(ConstantTools.APPLICATION_PROP_FILE);
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
			buildAll.setProjectsEnvironements( getProjectsEnvironmentsByPrefix(buildAll.getProjectsList()) );
		}
		
		return buildAll;
	}
	
	private List<Parameter> getBuilAllParameters() throws IOException {
		return getParameters(BuildAll.class);
	}

	private List<Environment> getBuilAllEnvironment() throws IOException {
		return getGlobalEnvironment();
	}
	
	private Map<Integer, List<String>> getBuildAllProjects() throws IOException {
		
		Map<Integer, List<String>> projects = new HashMap<Integer, List<String>>();
			
		Properties prop = Tools.findPropertyFile(ConstantTools.APPLICATION_PROP_FILE);
		
		projects = new HashMap<Integer, List<String>>();
		int index = 1;
		String key = ConstantTools.PROJECTS_BUILD_ALL_GROUPE_KEY + index;
		while ( prop.containsKey(key) ) {
			String[] projectsTab = prop.getProperty(key).split(ConstantTools.COMA);
			if (projectsTab != null) {
				projects.put( index, Arrays.asList(projectsTab).stream().map(String::trim).collect(Collectors.toList()) );
			}
			index++;
			key = ConstantTools.PROJECTS_BUILD_ALL_GROUPE_KEY + index;
		}
		
		return projects;
	}

	private List<String> getSecondaryRemotes() throws IOException {
		
		List<String> secondaryRemotes = new ArrayList<String>();
		
		Properties prop = Tools.findPropertyFile(ConstantTools.REMOTE_DESCRIPTOR_FILE);
		int index = 1;
		String key = ConstantTools.SECONDARY_REMOTE_KEY + index;
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
			buildOne.setProjectsEnvironements( getProjectsEnvironmentsByPrefix(buildOne.getProjectsList()) );
		}
		
		return buildOne;
	}
	
	private List<Parameter> getBuilOneParameters() throws IOException {
		return getParameters(BuildOne.class);
	}
	
	private List<Environment> getBuilOneEnvironment() throws IOException {
		return getGlobalEnvironment();
	}

	private Map<Integer, String> getBuildOneProjects() throws IOException {
		
		Map<Integer, String> projects = new HashMap<Integer, String>();
		
		Properties prop = Tools.findPropertyFile(ConstantTools.APPLICATION_PROP_FILE);
		
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
		return getParameters(Dashboard.class);
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
	
	private Map<String, List<Environment>> getDashboardEnvironments(String frontOrBack) throws IOException {
		
		Map<String, List<Environment>> backEnvironments = new HashMap<String, List<Environment>>();
		
		try ( InputStream is = ConfReader.class.getClassLoader().getResourceAsStream(ConstantTools.DASHBOARD_ENV_PROP_FILE) ) {
			try ( Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8) ) {	
				try ( JsonReader jsonReader = Json.createReader(is) ) {
					
					// back
					JsonObject racineArray = jsonReader.readObject();
					JsonArray jsonArray = racineArray.getJsonArray(frontOrBack);
					
					for (int i = 0; i < jsonArray.size(); i++) {
						JsonObject object = jsonArray.get(i).asJsonObject();
						for (Map.Entry<String, JsonValue> entry : object.entrySet() ) {
							String secondKey = entry.getKey();
							backEnvironments.put(secondKey, checkEnvironment(secondKey, entry.getValue().asJsonObject()));
						}
					}
				}
			}
		}
		
		return backEnvironments;
	}
	
	
	
	/*
	 * Le code mutualisé.
	 */
	
	private Map<String, List<Environment>> getProjectsEnvironmentsByPrefix(List<String> projects) throws IOException {
		
		Map<String, List<Environment>> projectsEnvironments = new HashMap<String, List<Environment>>();
			
		try ( InputStream is = ConfReader.class.getClassLoader().getResourceAsStream(ConstantTools.ENV_PROP_FILE) ) {
			try ( Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8) ) {	
				try ( JsonReader jsonReader = Json.createReader(is) ) {
					
					JsonObject racineArray = jsonReader.readObject();
					// global / runner / project
					for (Map.Entry<String, JsonValue> firstBlockEntry : racineArray.entrySet()) {

						String firstBlockKey = firstBlockEntry.getKey();
						JsonObject firstBlockObject = firstBlockEntry.getValue().asJsonObject();
						if ( projects.contains(firstBlockKey) )
							projectsEnvironments.put(firstBlockKey, checkEnvironment(firstBlockKey, firstBlockObject));
					}
				}
			}
		}
		
		return projectsEnvironments;
	}
	
	private List<Parameter> getParameters(Class<? extends Script> scriptClass) throws IOException {
		
		Parameter.ScopeParamater current;
		
		if ( scriptClass.equals(BuildAll.class) )
			current = Parameter.ScopeParamater.BUILDALL;
		
		else if ( scriptClass.equals(BuildOne.class) )
			current = Parameter.ScopeParamater.BUILDONE;
		
		else if ( scriptClass.equals(Dashboard.class) )
			current = Parameter.ScopeParamater.DASHBOARD;
		
		else
			throw new IllegalArgumentException("TECH : il faut définir la classe '" + scriptClass.toString() + "'.");
		
		
		List<Parameter> parameters = new ArrayList<Parameter>();
		
		try ( InputStream is = ConfReader.class.getClassLoader().getResourceAsStream(ConstantTools.PARAM_JSON_FILE) ) {
			try ( Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8) ) {	
				try ( JsonReader jsonReader = Json.createReader(is) ) {
					
					JsonArray objects = jsonReader.readArray();

					for (int i = 0; i < objects.size(); i++) {
						Parameter param = checkAndGetParameter( objects.getJsonObject(i) );
						if (param.getScope() == current || param.getScope() == Parameter.ScopeParamater.EVERY)
							parameters.add(param);
					}
				}
			}
		}
	
		return parameters;
	}

	private Parameter checkAndGetParameter(JsonObject object) throws IOException {
		
		String typeStr = object.getString(Parameter.ParameterKey.TYPE.getName(), null);
		if ( Tools.isNullOrEmpty(typeStr) )
			throw new IllegalArgumentException("Pour définir un paramètre, la propriétés '" + Parameter.ParameterKey.TYPE.getName() + "' est obligatoire.");
		
		Parameter.TypeParameter type = Parameter.TypeParameter.getTypeParameterByName(typeStr);
		for ( Parameter.ParameterKey paramKey : Parameter.ParameterKey.getKeys(type) ) {
			
			String value = object.getString(paramKey.getName(), null);
			if ( Tools.isNullOrEmpty(value) )
				throw new IllegalArgumentException("Pour définir un paramètre de type '" + typeStr + "', la propriétés '" + paramKey.getName() + "' est obligatoire.");
		}
		
		String name = object.getString(Parameter.ParameterKey.NAME.getName(), null);
		String scope = object.getString(Parameter.ParameterKey.SCOPE.getName(), null);
		String defaultValue = object.getString(Parameter.ParameterKey.DEFAULT_VALUE.getName(), null);
		String description = object.getString(Parameter.ParameterKey.DESCRIPTION.getName());
		String choices = object.getString(Parameter.ParameterKey.CHOICES.getName(), null);
		
		choices = replaceValueByPropertiesIfNeedIt(name, choices);
		
		switch (type) {
		
			case BOOLEAN : return new BooleanParameter(name, defaultValue, description, scope);
			case CHOICE : return new ChoiceParameter(name, choices, description, scope);
			case STRING : return new StringParameter(name, defaultValue, description, scope);
			
			default : throw new IllegalArgumentException("il manque un DEV : définir la classe le type '" + type + "'.");
		}
	}

	private String replaceValueByPropertiesIfNeedIt(String name, String value) throws IOException {
		
		if ( Tools.isNullOrEmpty(value) || !value.startsWith(ConstantTools.DOLLAR) )
			return value;
		
		// surpprimer le caracètre '$'
		String tmp = value.substring(1);
		String[] valuesTab = tmp.split(ConstantTools.SLASH);
		if (valuesTab == null || valuesTab.length != 2)
			throw new IllegalArgumentException("La valeur '" + value + "' de la prorpiété '" + name + "' n'est pas correcte");
		
		String propertyFile = valuesTab[0];
		String propertyKey = valuesTab[1];
		
		return Tools.findPropertyFile(propertyFile).getProperty(propertyKey);
	}

	private List<Environment> getGlobalEnvironment() throws IOException {
			
		List<Environment> globalEnvironements = new ArrayList<Environment>();
		
		try ( InputStream is = ConfReader.class.getClassLoader().getResourceAsStream(ConstantTools.ENV_PROP_FILE) ) {
			try ( Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8) ) {	
				try ( JsonReader jsonReader = Json.createReader(is) ) {
					
					JsonObject racineArray = jsonReader.readObject();
					JsonObject jsonObject = racineArray.getJsonObject(ConstantTools.GLOBAL_KEY);
					globalEnvironements = checkEnvironment(ConstantTools.GLOBAL_KEY, jsonObject);
				}
			}
		}
		
		return globalEnvironements;
	}

	private List<Environment> checkEnvironment(String scope, JsonObject object) {
		
		List<Environment> list = new ArrayList<Environment>();
		
		for (Environment.EnvironmentKey envKey : Environment.EnvironmentKey.getKeys(scope)) {
			String value = object.getString(envKey.getName(), null);
			if ( envKey.isMandatory() && Tools.isNullOrEmpty(value) )
				throw new IllegalArgumentException("Pour la variable '" + scope + "', la propriété '" + envKey.getName() + "' est obligatoire.");
			else
				list.add( new Environment(envKey.getName(), value) );
		}
		
		return list;
	}

}
