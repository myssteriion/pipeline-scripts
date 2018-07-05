package com.es2i.pipeline.job.helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.es2i.pipeline.job.entities.Environment;
import com.es2i.pipeline.job.entities.Parameter;
import com.es2i.pipeline.job.entities.Tool;
import com.es2i.pipeline.tools.ConstantTools;
import com.es2i.pipeline.tools.Tools;

/*
 * Singleton
 */
public class ConstrcuctHelper {

	private static ConstrcuctHelper instance;
	
	private Properties callFunctions;
	
	
	
	private ConstrcuctHelper() throws IOException {
		init();
	}
	
	private void init() throws IOException {
		
		callFunctions = new Properties();
		try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(ConstantTools.CALL_FUNCTIONS_PROP_FILE)) {
			callFunctions.load(is);
		}
		
		Set<String> expectedKeys = new HashSet<String>();
		expectedKeys.add(ConstantTools.CALL_RUN_BUILD_KEY);
		Tools.verifyKeys(expectedKeys, callFunctions.stringPropertyNames(), ConstantTools.CALL_FUNCTIONS_PROP_FILE);
	}

	
	
	public static ConstrcuctHelper getInstance() throws IOException {
		
		if (instance == null)
			instance = new ConstrcuctHelper();
		
		return instance;
	}
	
	
	
	public String beginPipeline() {
		return "pipeline {";
	}
	
	public String endPipeline() {
		return "}";
	}
	
	
	public String agent() {
		return "agent any";
	}
	
	
	public String beginParameters() {
		return "parameters {";
	}
	
	public String contentParameters(Parameter param) {
		
		if (param.getType().equalsIgnoreCase("String")) {
			return "string(name: \"" + param.getName() + "\", defaultValue: \"" + param.getDefaultValue() + "\", description: \"" + param.getDesc() + "\")";
		}
		else {
			return "";
		}
	}
	
	public String endParameters() {
		return "}";
	}
	
	
	public String beginEnv() {
		return "environment {";
	}
	
	public String contentEnv(Environment env) {
		
		return env.getName() + " = \"" + env.getValue() + "\"";
	}
	
	public String endEnv() {
		return "}";
	}

	
	public String beginTools() {
		return "tools {";
	}
	
	public String contentTools(Tool tool) {
		
		return tool.getName() + " \"" + tool.getValue() + "\"";
	}
	
	public String endTools() {
		return "}";
	}

	
	public String beginStages() {
		return "stages {";
	}
	
	public String endStages() {
		return "}";
	}
	
	
	public String beginStage(String name) {
		return "stage (\"" + name + "\") {";
	}
	
	public String endStage() {
		return "}";
	}
	
	
	public String beginSteps() {
		return "steps {";
	}
	
	public String endSteps() {
		return "}";
	}
	
	
	public String beginWrap() {
		return "wrap([$class: 'AnsiColorBuildWrapper']) {";
	}
	
	public String endWrap() {
		return "}";
	}
	
	
	public String beginParallel() {
		return "parallel {";
	}
	
	public String endParallel() {
		return "}";
	}
	
	
	public String getFunctions() throws IOException, URISyntaxException {
		
		InputStream is = ConstrcuctHelper.class.getClassLoader().getResourceAsStream(ConstantTools.FUNCTIONS_FILE);
		byte[] bytes = IOUtils.toByteArray(is);
		return new String(bytes);
	}

	public String runBuild() {
		return callFunctions.getProperty(ConstantTools.CALL_RUN_BUILD_KEY);
	}
	
	public String addTab(int nb) {
		
		String str = "";
		for (int i = 0; i < nb; i++)
			str += ConstantTools.TAB;
		
		return str;
	}
	
	public String addCRLF() {
		return ConstantTools.CRLF;
	}
	
	public String echo(String str) {
		return "echo \"" + str + "\"";
	}
	
	public String sh(String str) {
		return "sh \"" + str + "\"";
	}
	
	public String cleanWs() {
		return "cleanWs()";
	}
	
	/**
	 * Doit être utilisé à l'intérieur d'un bloc 'wrap'
	 * 
	 * @see #beginWrap()
	 * @see #endWrap()
	 */
	public String infoColor(String str) {
		return "\\u001B[34m " + str + " \\u001B[m";
	}
	
	/**
	 * Doit être utilisé à l'intérieur d'un bloc 'wrap'
	 * 
	 * @see #beginWrap()
	 * @see #endWrap()
	 */
	public String errorColor(String str) {
		return "\\u001B[31m " + str + " \\u001B[m";
	}
	
	public String dollarParams(String prop) {
		return "${params." + prop + "}";
	}
	
	public String dollarEnv(String prop) {
		return "${env." + prop + "}";
	}

}