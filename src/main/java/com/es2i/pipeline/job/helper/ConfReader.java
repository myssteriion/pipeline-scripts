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
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.swing.text.AbstractDocument.BranchElement;

import com.es2i.pipeline.job.entities.Environment;
import com.es2i.pipeline.job.entities.Parameter;
import com.es2i.pipeline.job.entities.Parameter.TypeParameter;
import com.es2i.pipeline.job.entities.parameter_impl.BooleanParameter;
import com.es2i.pipeline.job.entities.parameter_impl.ChoiceParameter;
import com.es2i.pipeline.job.entities.parameter_impl.StringParameter;
import com.es2i.pipeline.tools.ConstantTools;
import com.es2i.pipeline.tools.Tools;

/*
 * Singleton
 */
public class ConfReader {

	private static ConfReader instance;

	private List<Parameter> parameters;
	
	private List<String> runnerRevisions;
	
	private List<String> runnerMavenProfiles;
	
	private List<String> secondaryRemotes;
	
	private Map<String, List<Environment>> environements;

	private Map<Integer, String> projectsBuilOne;
	
	private Map<Integer, List<String>> projectsBuildAll;
	
	
	
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
		
		Properties tools = Tools.findPropertyFile(ConstantTools.TOOLS_PROP_FILE);
		expectedKeys = new HashSet<String>();
		Tools.checkKeys(expectedKeys, tools.stringPropertyNames(), ConstantTools.TOOLS_PROP_FILE);
		
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
	
	
	
	public List<String> getRunnerRevisions() throws IOException {
		
		if (runnerRevisions == null) {
			
			runnerRevisions = new ArrayList<String>();
			
			Properties prop = Tools.findPropertyFile(ConstantTools.APPLICATION_PROP_FILE);
			String[] revisions = prop.getProperty(ConstantTools.RUNNER_REVISIONS_KEY).split(ConstantTools.COMA);
			if ( revisions != null && revisions.length > 0 ) {
				for (String revision : revisions) {
					if ( !Tools.isNullOrEmpty(revision) ) 
						runnerRevisions.add( revision.trim() );
				}
			}
		}
		
		return runnerRevisions;
	}
	
	public List<String> getRunnerMavenProfiles() throws IOException {
		
		if (runnerMavenProfiles == null) {
			
			runnerMavenProfiles = new ArrayList<String>();
			
			Properties prop = Tools.findPropertyFile(ConstantTools.APPLICATION_PROP_FILE);
			String[] mavenProfiles = prop.getProperty(ConstantTools.RUNNER_MAVEN_PROFILES_KEY).split(ConstantTools.COMA);
			if ( mavenProfiles != null && mavenProfiles.length > 0 ) {
				for (String mavenProfile : mavenProfiles) {
					if ( !Tools.isNullOrEmpty(mavenProfile) ) 
						runnerMavenProfiles.add( mavenProfile.trim() );
				}
			}
		}
		
		return runnerMavenProfiles;
	}
	
	public List<String> getSecondaryRemotes() throws IOException {
		
		if (secondaryRemotes == null) {
			
			secondaryRemotes = new ArrayList<String>();
			
			Properties prop = Tools.findPropertyFile(ConstantTools.REMOTE_DESCRIPTOR_FILE);
			int index = 1;
			String key = ConstantTools.SECONDARY_REMOTE_KEY + index;
			while ( prop.containsKey(key) ) {
				secondaryRemotes.add( prop.getProperty(key) );
				index++;
				key = ConstantTools.SECONDARY_REMOTE_KEY + index;
			}
		}
		
		return secondaryRemotes;
	}

	public List<Parameter> getParameters() throws IOException {
		
		if (parameters == null) {
		
			parameters = new ArrayList<Parameter>();
			try ( InputStream is = ConfReader.class.getClassLoader().getResourceAsStream(ConstantTools.PARAM_JSON_FILE) ) {
				try ( Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8) ) {	
					try ( JsonReader jsonReader = Json.createReader(is) ) {
						
						JsonArray array = jsonReader.readArray();
						for (int i = 0; i < array.size(); i++) {
							
							JsonObject object = array.getJsonObject(i);
							checkParameter(object);
							
							String name = object.getString(Parameter.ParameterKey.NAME.getName(), null);
							String typeStr = object.getString(Parameter.ParameterKey.TYPE.getName(), null);
							String scope = object.getString(Parameter.ParameterKey.SCOPE.getName(), null);
							String defaultValue = object.getString(Parameter.ParameterKey.DEFAULT_VALUE.getName(), null);
							String description = object.getString(Parameter.ParameterKey.DESCRIPTION.getName());
							String choices = object.getString(Parameter.ParameterKey.CHOICES.getName(), null);
							
							TypeParameter type = Parameter.TypeParameter.getTypeParameterByName(typeStr);
							choices = replaceValueByPropertiesIfNeedIt(name, choices);
							
							switch (type) {
								
								case BOOLEAN:
									parameters.add( new BooleanParameter(name, defaultValue, description, scope) );
									break;
								
								case CHOICE:
									parameters.add( new ChoiceParameter(name, choices, description, scope) );
									break;
									
								case STRING:
									parameters.add( new StringParameter(name, defaultValue, description, scope) );
									break;
							}
						}
					}
				}
			}
		}
		
		return parameters;
	}
	
	private void checkParameter(JsonObject object) {
		
		String name = object.getString(Parameter.ParameterKey.NAME.getName(), null);
		String typeStr = object.getString(Parameter.ParameterKey.TYPE.getName(), null);
		String defaultValue = object.getString(Parameter.ParameterKey.DEFAULT_VALUE.getName(), null);
		String description = object.getString(Parameter.ParameterKey.DESCRIPTION.getName());
		String choices = object.getString(Parameter.ParameterKey.CHOICES.getName(), null);
		
		if ( Tools.isNullOrEmpty(name) || Tools.isNullOrEmpty(typeStr) || Tools.isNullOrEmpty(description) ) {
			String message = "Pour définir un paramètre, les propriétés '" + Parameter.ParameterKey.NAME.getName() + "', "
							+ Parameter.ParameterKey.TYPE.getName() + "', '" + Parameter.ParameterKey.DESCRIPTION.getName() + "' sont obligatoires.";
			throw new IllegalArgumentException(message);
		}
		
		switch (Parameter.TypeParameter.getTypeParameterByName(typeStr)) {
		
			case BOOLEAN:
			case STRING:
				if ( Tools.isNullOrEmpty(defaultValue) )
					throw new IllegalArgumentException("Pour définir un paramètre de type '" + typeStr + "', la propriétés '" + Parameter.ParameterKey.DEFAULT_VALUE.getName() + "' est obligatoire.");
				break;
				
			case CHOICE:
				if ( Tools.isNullOrEmpty(choices) )
					throw new IllegalArgumentException("Pour définir un paramètre de type '" + typeStr + "', la propriétés '" + Parameter.ParameterKey.CHOICES.getName() + "' est obligatoire.");
				break;
		}
	}
	
	public List<Environment> getEnvironmentByPrefix(String prefix) throws IOException {
		
		if ( environements == null)
			environements = new HashMap<String, List<Environment>>();
			
		if ( !environements.containsKey(prefix) ) {
			
			try ( InputStream is = ConfReader.class.getClassLoader().getResourceAsStream(ConstantTools.ENV_PROP_FILE) ) {
				try ( Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8) ) {	
					try ( JsonReader jsonReader = Json.createReader(is) ) {
						
						JsonObject racineArray = jsonReader.readObject();
						for (Entry<String, JsonValue> firstBlockEntry : racineArray.entrySet()) {

							String firstBlockKey = firstBlockEntry.getKey();
							JsonObject firstBlockObject = firstBlockEntry.getValue().asJsonObject();
							
							List<Environment> list = new ArrayList<Environment>();
							for ( String secondBlockKey : firstBlockObject.keySet() )
								list.add( new Environment(secondBlockKey, firstBlockObject.getString(secondBlockKey)) );
							
							environements.put(firstBlockKey, list);
						}
					}
				}
			}
		}
		
		return environements.get(prefix);
	}
	
	public Map<Integer, String> getProjectsBuildOne() throws IOException {
		
		if (projectsBuilOne == null) {
			
			Properties prop = Tools.findPropertyFile(ConstantTools.APPLICATION_PROP_FILE);
			
			projectsBuilOne = new HashMap<Integer, String>();
			String[] projectsList = prop.getProperty(ConstantTools.PROJECTS_BUILD_ONE_KEY).split(ConstantTools.COMA);
			if (projectsList != null) {
				for (int index = 0; index < projectsList.length; index++)
					projectsBuilOne.put(index+1, projectsList[index].trim());
			}
		}
		
		return projectsBuilOne;
	}
	
	public Map<Integer, List<String>> getProjectsBuildAll() throws IOException {
		
		if (projectsBuildAll == null) {
			
			Properties prop = Tools.findPropertyFile(ConstantTools.APPLICATION_PROP_FILE);
			
			projectsBuildAll = new HashMap<Integer, List<String>>();
			int index = 1;
			String key = ConstantTools.PROJECTS_BUILD_ALL_GROUPE_KEY + index;
			while ( prop.containsKey(key) ) {
				String[] projectsTab = prop.getProperty(key).split(ConstantTools.COMA);
				if (projectsTab != null) {
					projectsBuildAll.put( index, Arrays.asList(projectsTab).stream().map(String::trim).collect(Collectors.toList()) );
				}
				index++;
				key = ConstantTools.PROJECTS_BUILD_ALL_GROUPE_KEY + index;
			}
		}
		
		return projectsBuildAll;
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
	
}
