package com.es2i.pipeline.job;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import com.es2i.pipeline.job.entities.Environment;
import com.es2i.pipeline.job.entities.Parameter;
import com.es2i.pipeline.job.entities.Tool;
import com.es2i.pipeline.job.helper.ConstrcuctHelper;
import com.es2i.pipeline.job.helper.PropToEntitiy;
import com.es2i.pipeline.tools.ConstantTools;
import com.es2i.pipeline.tools.Tools;

public class Pipeline {
	
	private Properties application;
	
	private Map<Integer, List<String>> projects;
	
	private Properties parameters;
	
	private Properties environment;

	private Properties tools;
	
	
	
	public Pipeline() throws IOException {
		init();
	}
	
	
	
	private void init() throws IOException {

		application = new Properties();
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(ConstantTools.APPLICATION_PROP_FILE)) {
			application.load(is);
		}
		Set<String> expectedKeys = new HashSet<String>();
		expectedKeys.add(ConstantTools.BUILD_ALL_DIRECTORY_KEY);
		expectedKeys.add(ConstantTools.BUILD_ONE_DIRECTORY_KEY);
		expectedKeys.add(ConstantTools.PROJECTS_GROUPE1_KEY);
		verifyKeys(expectedKeys, application.stringPropertyNames(), ConstantTools.APPLICATION_PROP_FILE);
		
		projects = new HashMap<Integer, List<String>>();
		int index = 1;
		String key = ConstantTools.PROJECTS_KEY + ConstantTools.DOT + ConstantTools.GROUP_KEY + index;
		while ( application.containsKey(key) ) {
			String[] projectsTab = application.getProperty(key).split(ConstantTools.COMA);
			projects.put(index, Arrays.asList(projectsTab));
			index++;
			key = ConstantTools.PROJECTS_KEY + ConstantTools.DOT + ConstantTools.GROUP_KEY + index;
		}
	
		/* */
		
		parameters = new Properties();
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(ConstantTools.PARAM_PROP_FILE)) {
			parameters.load(is);
		}
		expectedKeys = new HashSet<String>();
		expectedKeys.add(ConstantTools.REVISION_KEY);
		verifyKeys(expectedKeys, parameters.stringPropertyNames(), ConstantTools.TOOLS_PROP_FILE);
		
		/* */
		
		environment = new Properties();
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(ConstantTools.ENV_PROP_FILE)) {
			environment.load(is);
		}
		expectedKeys = new HashSet<String>();
		expectedKeys.add(ConstantTools.GITLAB_URL_KEY);
		expectedKeys.add(ConstantTools.REMOTE_CONNEXION_KEY);
		expectedKeys.add(ConstantTools.REMOTE_DEPOT_KEY);
		expectedKeys.add(ConstantTools.REMOTE_DIRECTORY_KEY);
		verifyKeys(expectedKeys, environment.stringPropertyNames(), ConstantTools.ENV_PROP_FILE);
		
		/* */
		
		tools = new Properties();
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(ConstantTools.TOOLS_PROP_FILE)) {
			tools.load(is);
		}
		expectedKeys = new HashSet<String>();
		verifyKeys(expectedKeys, tools.stringPropertyNames(), ConstantTools.TOOLS_PROP_FILE);
	}
	
	private void verifyKeys(Set<String> expectedKeys, Set<String> actualKeys, String fileName) {
		
		if ( !actualKeys.containsAll(expectedKeys) ) {
			String message = "Au moins une clé est manquante dans " + fileName;
			message += " (" + Tools.concatenateItem(expectedKeys) + ")";
			throw new IllegalArgumentException(message);
		}
	}
	
	
	
	public void run() throws IOException, URISyntaxException {

		clean();
		generateBuildAllLinear();
		generateBuildAll();
		generateRunner();
		genetateAllBuildOne();
	}
	
	private void clean() throws IOException {

		Tools.deleteIfExists( Paths.get( application.getProperty(ConstantTools.BUILD_ALL_DIRECTORY_KEY)) );
		Tools.deleteIfExists( Paths.get( application.getProperty(ConstantTools.BUILD_ONE_DIRECTORY_KEY)) );
	}
	
	private void generateBuildAllLinear() throws IOException, URISyntaxException {
		
		Path path = Paths.get(application.getProperty(ConstantTools.BUILD_ALL_DIRECTORY_KEY));
		File direcrory = Tools.createDirectoryIfNeedIt(path).toFile();
		File jenkinsfile = Paths.get(direcrory.getAbsolutePath(), ConstantTools.JENKINS_LINEAR_FILE).toFile();
		jenkinsfile.createNewFile();
		
		try (Writer writer = new PrintWriter(jenkinsfile)) {
			
			// pipeline
			writer.write(ConstrcuctHelper.beginPipeline() + ConstrcuctHelper.addCRLF());
			
			// agent
			writer.write(ConstrcuctHelper.addTab(1) + ConstrcuctHelper.agent() + ConstrcuctHelper.addCRLF());
			
			addParamEnvTools(writer);
						
			// stages
			writer.write(ConstrcuctHelper.addTab(1) + ConstrcuctHelper.beginStages() + ConstrcuctHelper.addCRLF());
			
			addInitialize(writer);
			
			// all build
			int nbGroup = projects.size();
			for (int index = 1; index <= nbGroup; index++) {
				for ( String project : projects.get(index) )
					addStageForProject(writer, project, 0);
			}
			
			// end stages
			writer.write(ConstrcuctHelper.addTab(1) + ConstrcuctHelper.endStages() + ConstrcuctHelper.addCRLF());
			
			// end pipeline
			writer.write( ConstrcuctHelper.endPipeline() + ConstrcuctHelper.addCRLF());
			
			writer.write( ConstrcuctHelper.addCRLF());
			
			// functions
			writer.write( ConstrcuctHelper.getFunctions() );
		}
	}
	
	private void generateBuildAll() throws IOException, URISyntaxException {
		
		Path path = Paths.get(application.getProperty(ConstantTools.BUILD_ALL_DIRECTORY_KEY));
		File direcrory = Tools.createDirectoryIfNeedIt(path).toFile();
		File jenkinsfile = Paths.get(direcrory.getAbsolutePath(), ConstantTools.JENKINS_FILE).toFile();
		jenkinsfile.createNewFile();
		
		try (Writer writer = new PrintWriter(jenkinsfile)) {
			
			// pipeline
			writer.write(ConstrcuctHelper.beginPipeline() + ConstrcuctHelper.addCRLF());
			
			// agent
			writer.write(ConstrcuctHelper.addTab(1) + ConstrcuctHelper.agent() + ConstrcuctHelper.addCRLF());
			
			addParamEnvTools(writer);
						
			// stages
			writer.write(ConstrcuctHelper.addTab(1) + ConstrcuctHelper.beginStages() + ConstrcuctHelper.addCRLF());
			
			addInitialize(writer);
			
			// all build
			int nbGroup = projects.size();
			for (int index = 1; index <= nbGroup; index++) {
				
				List<String> groupe = projects.get(index);
				if (groupe.size() == 1)
					addStageForProject(writer, groupe.get(0), 0);
				else
					addParallelStageForProject(writer, index, groupe);
			}
			
			// end stages
			writer.write(ConstrcuctHelper.addTab(1) + ConstrcuctHelper.endStages() + ConstrcuctHelper.addCRLF());
			
			// end pipeline
			writer.write( ConstrcuctHelper.endPipeline() + ConstrcuctHelper.addCRLF());
			
			writer.write( ConstrcuctHelper.addCRLF());
			
			// functions
			writer.write( ConstrcuctHelper.getFunctions() );
		}
	}
	
	private void generateRunner() throws IOException, URISyntaxException {
		
		Path path = Paths.get(application.getProperty(ConstantTools.BUILD_ALL_DIRECTORY_KEY), ConstantTools.RUNNER_DIRECTORY);
		File direcrory = Tools.createDirectoryIfNeedIt(path).toFile();
		File jenkinsfile = Paths.get(direcrory.getAbsolutePath(), ConstantTools.JENKINS_FILE).toFile();
		jenkinsfile.createNewFile();
		
		try (Writer writer = new PrintWriter(jenkinsfile)) {
			
			// pipeline
			writer.write(ConstrcuctHelper.beginPipeline() + ConstrcuctHelper.addCRLF());
			
			// agent
			writer.write(ConstrcuctHelper.addTab(1) + ConstrcuctHelper.agent() + ConstrcuctHelper.addCRLF());
			
			// global runner env
			writer.write(ConstrcuctHelper.addTab(1) + ConstrcuctHelper.beginEnv() + ConstrcuctHelper.addCRLF());
			for ( String key : Tools.getKeysFilterByPrefix(environment, ConstantTools.RUNNER_KEY + ConstantTools.DOT) ) {
				Environment env = PropToEntitiy.transformToEnvironment(key, environment.getProperty(key));
				writer.write(ConstrcuctHelper.addTab(2) + ConstrcuctHelper.contentEnv(env) + ConstrcuctHelper.addCRLF());
			}
			writer.write(ConstrcuctHelper.addTab(1) + ConstrcuctHelper.endEnv() + ConstrcuctHelper.addCRLF());
			
			// stages
			writer.write(ConstrcuctHelper.addTab(1) + ConstrcuctHelper.beginStages() + ConstrcuctHelper.addCRLF());
			
			writer.write(ConstrcuctHelper.addTab(2) + ConstrcuctHelper.beginStage("run") + ConstrcuctHelper.addCRLF());
			writer.write(ConstrcuctHelper.addTab(3) + ConstrcuctHelper.beginSteps() + ConstrcuctHelper.addCRLF());
			
			String shCommand = "wget \\\"${env.jenkinsUrl}/view/GIT/job/${env.jobName}/buildWithParameters?token=${env.pipelineToken}&revision=master\\\"";
			writer.write(ConstrcuctHelper.addTab(4) + ConstrcuctHelper.sh(shCommand) + ConstrcuctHelper.addCRLF());
			
			shCommand = "wget \\\"${env.jenkinsUrl}/view/GIT/job/${env.jobName}/buildWithParameters?token=${env.pipelineToken}&revision=develop\\\"";
			writer.write(ConstrcuctHelper.addTab(4) + ConstrcuctHelper.sh(shCommand) + ConstrcuctHelper.addCRLF());
			
			writer.write(ConstrcuctHelper.addTab(3) + ConstrcuctHelper.endSteps() + ConstrcuctHelper.addCRLF());
			writer.write(ConstrcuctHelper.addTab(2) + ConstrcuctHelper.endStage() + ConstrcuctHelper.addCRLF());
			
			// end stages
			writer.write(ConstrcuctHelper.addTab(1) + ConstrcuctHelper.endStages() + ConstrcuctHelper.addCRLF());
			
			// end pipeline
			writer.write( ConstrcuctHelper.endPipeline());
		}
	}
	
	private void genetateAllBuildOne() throws IOException, URISyntaxException {
		
		Path path = Paths.get(application.getProperty(ConstantTools.BUILD_ONE_DIRECTORY_KEY));
		File direcrory = Tools.createDirectoryIfNeedIt(path).toFile();
		
		
		// all build
		int nbGroup = projects.size();
		for (int index = 1; index <= nbGroup; index++) {
			for ( String project : projects.get(index) ) {
				
				path = Paths.get(direcrory.getAbsolutePath(), project);
				File subDirecrory = Tools.createDirectoryIfNeedIt(path).toFile();
				
				File jenkinsfile = Paths.get(subDirecrory.getAbsolutePath(), ConstantTools.JENKINS_FILE).toFile();
				jenkinsfile.createNewFile();
				
				try (Writer writer = new PrintWriter(jenkinsfile)) {
					
					// pipeline
					writer.write(ConstrcuctHelper.beginPipeline() + ConstrcuctHelper.addCRLF());
					
					// agent
					writer.write(ConstrcuctHelper.addTab(1) + ConstrcuctHelper.agent() + ConstrcuctHelper.addCRLF());
					
					addParamEnvTools(writer);
					
					// stages
					writer.write(ConstrcuctHelper.addTab(1) + ConstrcuctHelper.beginStages() + ConstrcuctHelper.addCRLF());
					
					addInitialize(writer);
					
					addStageForProject(writer, project, 0);
					
					// end stages
					writer.write(ConstrcuctHelper.addTab(1) + ConstrcuctHelper.endStages() + ConstrcuctHelper.addCRLF());
					
					// end pipeline
					writer.write( ConstrcuctHelper.endPipeline() + ConstrcuctHelper.addCRLF());
					
					writer.write( ConstrcuctHelper.addCRLF());
					
					// functions
					writer.write( ConstrcuctHelper.getFunctions() );
				}
			}
		}
		
	}
	
	
	
	private void addParamEnvTools(Writer writer) throws IOException, URISyntaxException {
		
		// parameters
		writer.write(ConstrcuctHelper.addTab(1) + ConstrcuctHelper.beginParameters() + ConstrcuctHelper.addCRLF());
		for ( String key : Tools.getKeysFilterByPrefix(parameters, "") ) {
			Parameter param = PropToEntitiy.transformToParameter(key, parameters.getProperty(key));
			writer.write(ConstrcuctHelper.addTab(2) + ConstrcuctHelper.contentParameters(param) + ConstrcuctHelper.addCRLF());
		}
		writer.write(ConstrcuctHelper.addTab(1) + ConstrcuctHelper.endParameters() + ConstrcuctHelper.addCRLF());
		
		// global env
		writer.write(ConstrcuctHelper.addTab(1) + ConstrcuctHelper.beginEnv() + ConstrcuctHelper.addCRLF());
		for ( String key : Tools.getKeysFilterByPrefix(environment, ConstantTools.GLOBAL_KEY + ConstantTools.DOT) ) {
			Environment env = PropToEntitiy.transformToEnvironment(key, environment.getProperty(key));
			writer.write(ConstrcuctHelper.addTab(2) + ConstrcuctHelper.contentEnv(env) + ConstrcuctHelper.addCRLF());
		}
		writer.write(ConstrcuctHelper.addTab(1) + ConstrcuctHelper.endEnv() + ConstrcuctHelper.addCRLF());
		
		// tools
		// laisser décommenter tant qu'il n'y a pas de clé valeur dans le bloc tools
//		writer.write(ConstrcuctHelper.addTab(1) + ConstrcuctHelper.beginTools() + ConstrcuctHelper.addCRLF());
//		for ( String key : Tools.getKeysFilterByPrefix(tools, "") ) {
//			Tool tool = PropToEntitiy.transformToTool(key, tools.getProperty(key));
//			writer.write(ConstrcuctHelper.addTab(2) + ConstrcuctHelper.contentTools(tool) + ConstrcuctHelper.addCRLF());
//		}
//		writer.write(ConstrcuctHelper.addTab(1) + ConstrcuctHelper.endTools() + ConstrcuctHelper.addCRLF());
	}
	
	private void addInitialize(Writer writer) throws IOException, URISyntaxException {
		
		writer.write(ConstrcuctHelper.addTab(2) + ConstrcuctHelper.beginStage("Initialize") + ConstrcuctHelper.addCRLF());
		
		// clean workspace
		writer.write(ConstrcuctHelper.addTab(3) + ConstrcuctHelper.beginSteps() + ConstrcuctHelper.addCRLF());
		writer.write(ConstrcuctHelper.addTab(4) + ConstrcuctHelper.cleanWs() + ConstrcuctHelper.addCRLF());
		writer.write(ConstrcuctHelper.addTab(4) + ConstrcuctHelper.beginWrap() + ConstrcuctHelper.addCRLF());
		
		// echo all parameters
		for ( Entry<Object, Object> entry : parameters.entrySet() ) {
			Parameter param = PropToEntitiy.transformToParameter(entry.getKey().toString(), entry.getValue().toString());
			String echoStr = ConstrcuctHelper.infoColor(param.getName() + " : " + ConstrcuctHelper.dollarParams(param.getName()));
			writer.write(ConstrcuctHelper.addTab(5) + ConstrcuctHelper.echo(echoStr) + ConstrcuctHelper.addCRLF());
		}
		
		writer.write(ConstrcuctHelper.addTab(4) + ConstrcuctHelper.endWrap() + ConstrcuctHelper.addCRLF());	
		writer.write(ConstrcuctHelper.addTab(3) + ConstrcuctHelper.endSteps() + ConstrcuctHelper.addCRLF());	
		
		writer.write(ConstrcuctHelper.addTab(2) + ConstrcuctHelper.endStage() + ConstrcuctHelper.addCRLF());
	}
	
	private void addStageForProject(Writer writer, String project, int identToAdd) throws IOException, URISyntaxException {
		
		writer.write(ConstrcuctHelper.addTab(2 + identToAdd) + ConstrcuctHelper.beginStage("build " + project) + ConstrcuctHelper.addCRLF());
		
		// env
		writer.write(ConstrcuctHelper.addTab(3 + identToAdd) + ConstrcuctHelper.beginEnv() + ConstrcuctHelper.addCRLF());
		for ( String key : Tools.getKeysFilterByPrefix(environment, project + ConstantTools.DOT) ) {
			Environment env = PropToEntitiy.transformToEnvironment(key, environment.getProperty(key));
			writer.write(ConstrcuctHelper.addTab(4 + identToAdd) + ConstrcuctHelper.contentEnv(env) + ConstrcuctHelper.addCRLF());
		}
		writer.write(ConstrcuctHelper.addTab(3 + identToAdd) + ConstrcuctHelper.endEnv() + ConstrcuctHelper.addCRLF());
		
		// step
		writer.write(ConstrcuctHelper.addTab(3 + identToAdd) + ConstrcuctHelper.beginSteps() + ConstrcuctHelper.addCRLF());
		writer.write(ConstrcuctHelper.addTab(4 + identToAdd) + ConstrcuctHelper.runBuild() + ConstrcuctHelper.addCRLF());
		writer.write(ConstrcuctHelper.addTab(3 + identToAdd) + ConstrcuctHelper.endEnv() + ConstrcuctHelper.addCRLF());
		
		writer.write(ConstrcuctHelper.addTab(2 + identToAdd) + ConstrcuctHelper.endStage() + ConstrcuctHelper.addCRLF());
	}
	
	private void addParallelStageForProject(Writer writer, int index, List<String> groupe) throws IOException, URISyntaxException {
		
		writer.write(ConstrcuctHelper.addTab(2) + ConstrcuctHelper.beginStage("build groupe " + index) + ConstrcuctHelper.addCRLF());
		
		// parallel
		writer.write(ConstrcuctHelper.addTab(3) + ConstrcuctHelper.beginParallel() + ConstrcuctHelper.addCRLF());
		
		for (String project : groupe)
			addStageForProject(writer, project, 2);
		
		writer.write(ConstrcuctHelper.addTab(3) + ConstrcuctHelper.endParallel() + ConstrcuctHelper.addCRLF());
		
		writer.write(ConstrcuctHelper.addTab(2) + ConstrcuctHelper.endStage() + ConstrcuctHelper.addCRLF());
	}
	
}
