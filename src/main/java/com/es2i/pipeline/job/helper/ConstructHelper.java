package com.es2i.pipeline.job.helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;

import com.es2i.pipeline.job.entities.Environment;
import com.es2i.pipeline.job.entities.Parameter;
import com.es2i.pipeline.job.entities.Tool;
import com.es2i.pipeline.job.entities.parameter_impl.BooleanParameter;
import com.es2i.pipeline.job.entities.parameter_impl.ChoiceParameter;
import com.es2i.pipeline.job.entities.parameter_impl.StringParameter;
import com.es2i.pipeline.tools.ConstantTools;

/**
 * Singleton. Permet de construire ligne par ligne les fichier Jenkinsfile.
 */
public class ConstructHelper {

	private static ConstructHelper instance;
	
	
	
	private ConstructHelper() {
		
	}

	
	
	public static ConstructHelper getInstance() {
		
		if (instance == null)
			instance = new ConstructHelper();
		
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
		
		switch ( param.getType() ) {

			case BOOLEAN:
				return "booleanParam(name: \"" + param.getName() + "\", defaultValue: " + ((BooleanParameter) param).getDefaultValue() + ", description: \"" + param.getDesc() + "\")";
			
			case CHOICE:
				return "choice(name: \"" + param.getName() + "\", choices: \"" + ((ChoiceParameter) param).getChoices() + "\", description: \"" + param.getDesc() + "\")";
				
			case STRING:
				return "string(name: \"" + param.getName() + "\", defaultValue: \"" + ((StringParameter) param).getDefaultValue() + "\", description: \"" + param.getDesc() + "\")";
		
			// dead code
			default:
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
		
		InputStream is = ConstructHelper.class.getClassLoader().getResourceAsStream(ConstantTools.FUNCTIONS_FILE);
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
		return "cleanPrimaryRemote()";
	}
	
	public String cleanProjectPrimaryRemote() { 
		return "cleanProjectPrimaryRemote(env.targetDirectory)"; 
	}
	
	public String createDataFolderOnPrimaryRemote() {
		
		String str = "ssh ${env.primaryRemote} mkdir -p ${env.depotFolder}/${params.revision}/${params.mavenProfile}/";
		str += ConstantTools.ESII_APPLICATION + "/" + ConstantTools.DATA + "/" + ConstantTools.EVENT_STORAGE + "/";
		
		return sh(str);
	}
	
	public String createTarOnPrimaryRemote() {
		
		String str = "ssh ${env.primaryRemote} \\\"cd ${env.depotFolder}/${params.revision}/${params.mavenProfile} && tar -cf ";
		str += ConstantTools.ESII_APPLICATION + ".gz " + ConstantTools.ESII_APPLICATION + "\\\"";
		
		return sh(str);
	}
	
	public String removeTarOnPrimaryRemote() {
		return sh("ssh ${env.primaryRemote} rm -rf ${env.depotFolder}/${params.revision}/${params.mavenProfile}/" + ConstantTools.ESII_APPLICATION + ".gz");
	}
	
	public String callBuildAll(String revision, String mavenProfile) {
		
		String param = "";
		if (revision != null && mavenProfile != null) 
			param = "&revision=" + revision + "&mavenProfile=" + mavenProfile + "\\\"";

		return sh("wget \\\"${env.jenkinsUrl}/view/GIT/job/${env.jobName}/buildWithParameters?token=${env.pipelineToken}" + param);
	}
	
	public String runBuild() {
		return "runBuild(env.gitRoot, env.projectRoot, env.jdkCompilation, env.mvnVersion)";
	}
	
	public String deploy() {
		return "deploy(env.gitRoot, env.projectRoot, env.targetDirectory, env.sourceAppDirectory, env.sourceExtension, env.sourceConfDirectory)";
	}
	
	public String runDeployToSecondaryRemote(String remote) {
		return "deployToSecondaryRemote(\"" + remote + "\")";
	}
	
	
	public String ifBackDeploy() {
		return "if (params.backDeploy) {";
	}
	
	public String endIfBackDeploy() {
		return "}";
	}
	
	public String ifFrontDeploy() {
		return "if (params.frontDeploy) {";
	}
	
	public String endIfFrontDeploy() {
		return "}";
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