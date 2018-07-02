package com.es2i.pipeline.job.helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;

import com.es2i.pipeline.job.Pipeline;
import com.es2i.pipeline.job.entities.Environment;
import com.es2i.pipeline.job.entities.Parameter;
import com.es2i.pipeline.job.entities.Tool;
import com.es2i.pipeline.tools.ConstantTools;

public class ConstrcuctHelper {

	public static String beginPipeline() {
		return "pipeline {";
	}
	
	public static String endPipeline() {
		return "}";
	}
	
	
	public static String agent() {
		return "agent any";
	}
	
	
	public static String beginParameters() {
		return "parameters {";
	}
	
	public static String contentParameters(Parameter param) {
		
		if (param.getType().equalsIgnoreCase("String")) {
			return "string(name: \"" + param.getName() + "\", defaultValue: \"" + param.getDefaultValue() + "\", description: \"" + param.getDesc() + "\")";
		}
		else {
			return "";
		}
	}
	
	public static String endParameters() {
		return "}";
	}
	
	
	public static String beginEnv() {
		return "environment {";
	}
	
	public static String contentEnv(Environment env) {
		
		return env.getName() + " = \"" + env.getValue() + "\"";
	}
	
	public static String endEnv() {
		return "}";
	}

	
	public static String beginTools() {
		return "tools {";
	}
	
	public static String contentTools(Tool tool) {
		
		return tool.getName() + " \"" + tool.getValue() + "\"";
	}
	
	public static String endTools() {
		return "}";
	}

	
	public static String beginStages() {
		return "stages {";
	}
	
	public static String endStages() {
		return "}";
	}
	
	
	public static String beginStage(String name) {
		return "stage (\"" + name + "\") {";
	}
	
	public static String endStage() {
		return "}";
	}
	
	
	public static String beginSteps() {
		return "steps {";
	}
	
	public static String endSteps() {
		return "}";
	}
	
	
	public static String beginWrap() {
		return "wrap([$class: 'AnsiColorBuildWrapper']) {";
	}
	
	public static String endWrap() {
		return "}";
	}
	
	
	public static String beginParallel() {
		return "parallel {";
	}
	
	public static String endParallel() {
		return "}";
	}
	
	
	public static String getFunctions() throws IOException, URISyntaxException {
		
		InputStream is = Pipeline.class.getResourceAsStream(ConstantTools.FUNCTIONS_FILE);
		byte[] bytes = IOUtils.toByteArray(is);
		return new String(bytes);
	}

	public static String runBuild() {
		return "runBuild(env.gitRoot, env.projectRoot, env.jdkCompilation)";
	}
	
	public static String addTab(int nb) {
		
		String str = "";
		for (int i = 0; i < nb; i++)
			str += ConstantTools.TAB;
		
		return str;
	}
	
	public static String addCRLF() {
		return ConstantTools.CRLF;
	}
	
	public static String echo(String str) {
		return "echo \"" + str + "\"";
	}
	
	public static String sh(String str) {
		return "sh \"" + str + "\"";
	}
	
	public static String cleanWs() {
		return "cleanWs()";
	}
	
	public static String infoColor(String str) {
		return "\\u001B[34m " + str + " \\u001B[m";
	}
	
	public static String errorColor(String str) {
		return "\\u001B[31m " + str + " \\u001B[m";
	}
	
	public static String dollarParams(String prop) {
		return "${params." + prop + "}";
	}
	
	public static String dollarEnv(String prop) {
		return "${env." + prop + "}";
	}

}