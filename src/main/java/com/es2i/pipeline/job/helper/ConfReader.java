package com.es2i.pipeline.job.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.es2i.pipeline.job.entities.Parameter;
import com.es2i.pipeline.job.entities.Parameter.TypeParameter;
import com.es2i.pipeline.job.entities.parameter_impl.BooleanParameter;
import com.es2i.pipeline.job.entities.parameter_impl.ChoiceParameter;
import com.es2i.pipeline.job.entities.parameter_impl.StringParameter;
import com.es2i.pipeline.tools.ConstantTools;

/*
 * Singleton
 */
public class ConfReader {

	private static ConfReader instance;
	
	private List<Parameter> parameters;

	
	
	private ConfReader() {
		
	}
	
	
	
	public static ConfReader getInstance() {
		
		if (instance == null)
			instance = new ConfReader();
		
		return instance;
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
							String name = object.getString(Parameter.ParameterKey.NAME.getName());
							TypeParameter type = Parameter.TypeParameter.getTypeParameterByName( object.getString(Parameter.ParameterKey.TYPE.getName()) );
							String scope = object.getString(Parameter.ParameterKey.SCOPE.getName(), null);
							String defaultValue = object.getString(Parameter.ParameterKey.DEFAULT_VALUE.getName(), null);
							String description = object.getString(Parameter.ParameterKey.DESCRIPTION.getName());
							String choices = object.getString(Parameter.ParameterKey.CHOICES.getName(), null);
							
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
	
}
