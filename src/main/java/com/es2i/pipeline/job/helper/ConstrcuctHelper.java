package com.es2i.pipeline.job.helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;

import com.es2i.pipeline.job.entities.Environment;
import com.es2i.pipeline.job.entities.Parameter;
import com.es2i.pipeline.job.entities.Tool;
import com.es2i.pipeline.tools.ConstantTools;

/*
 * Singleton
 */
public class ConstrcuctHelper {

	private static ConstrcuctHelper instance;
	
	
	
	private ConstrcuctHelper() {
		
	}

	
	
	public static ConstrcuctHelper getInstance() {
		
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
		else if (param.getType().equalsIgnoreCase("boolean")) {
			return "booleanParam(name: \"" + param.getName() + "\", defaultValue: " + param.getDefaultValue() + ", description: \"" + param.getDesc() + "\")";
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
	
	
	public String beginScript() {
		return "script {";
	}
	
	public String endScript() {
		return "}";
	}
	
	
	public String getFunctions() throws IOException, URISyntaxException {
		
		InputStream is = ConstrcuctHelper.class.getClassLoader().getResourceAsStream(ConstantTools.FUNCTIONS_FILE);
		byte[] bytes = IOUtils.toByteArray(is);
		String str = new String(bytes);
		
		str = str.replace(ConstantTools.ESII_APPLICATION_PARAM, ConstantTools.ESII_APPLICATION);
		str = str.replace(ConstantTools.APP_PARAM, ConstantTools.APP);
		str = str.replace(ConstantTools.CONF_PARAM, ConstantTools.CONF);
		
		return str;
	}

	
	public String beginIfSecondaryDeploy() {
		return "if (params.secondaryDeploy) {";
	}
	
	public String endIfSecondaryDeploy() {
		return "}";
	}

	
	public String cleanPrimaryRemote() {
		return sh("ssh ${env.primaryRemote} rm -rf ${env.depotFolder}/${params.revision}");
	}
	
	public String createDataFolderOnPrimaryRemote() {
		
		String str = "ssh ${env.primaryRemote} mkdir -p ${env.depotFolder}/${params.revision}/";
		str += ConstantTools.ESII_APPLICATION + "/" + ConstantTools.DATA + "/" + ConstantTools.EVENT_STORAGE + "/";
		
		return sh(str);
	}
	
	public String createTarOnPrimaryRemote() {
		
		String str = "ssh ${env.primaryRemote} \\\"cd ${env.depotFolder}/${params.revision} && tar -cf ";
		str += ConstantTools.ESII_APPLICATION + ".gz " + ConstantTools.ESII_APPLICATION + "\\\"";
		
		return sh(str);
	}
	
	public String removeTarOnPrimaryRemote() {
		return sh("ssh ${env.primaryRemote} rm -rf ${env.depotFolder}/${params.revision}/" + ConstantTools.ESII_APPLICATION + ".gz");
	}
	
	public String callBuildAll(String revision) {
		return sh("wget \\\"${env.jenkinsUrl}/view/GIT/job/${env.jobName}/buildWithParameters?token=${env.pipelineToken}&revision=" + revision + "\\\"");
	}
	
	public String runBuild() {
		return "runBuild(env.gitRoot, env.projectRoot, env.jdkCompilation, env.mvnVersion, env.targetDirectory, env.sourceAppDirectory, env.sourceConfDirectory)";
	}
	
	public String runDeployToSecondaryRemote(String remote) {
		return "deployToSecondaryRemote(\"" + remote + "\")";
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