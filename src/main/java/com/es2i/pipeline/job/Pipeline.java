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
import com.es2i.pipeline.job.helper.ConstrcuctHelper;
import com.es2i.pipeline.job.helper.PropToEntitiy;
import com.es2i.pipeline.tools.ConstantTools;
import com.es2i.pipeline.tools.Tools;

public class Pipeline {
	
	private ConstrcuctHelper constrcuctHelper;
	
	private Properties application;
	
	private Map<Integer, String> projectsBuilOne;
	
	private Map<Integer, List<String>> projectsBuildAll;
	
	private Properties parameters;
	
	private Properties environment;

	private Properties tools;
	
	
	
	public Pipeline() throws IOException {
		
		init();
		constrcuctHelper = ConstrcuctHelper.getInstance();
	}
	
	
	
	private void init() throws IOException {

		application = new Properties();
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(ConstantTools.APPLICATION_PROP_FILE)) {
			application.load(is);
		}
		Set<String> expectedKeys = new HashSet<String>();
		expectedKeys.add(ConstantTools.BUILD_ALL_DIRECTORY_KEY);
		expectedKeys.add(ConstantTools.BUILD_ONE_DIRECTORY_KEY);
		expectedKeys.add(ConstantTools.RUNNER_BRANCHES_KEY);
		expectedKeys.add(ConstantTools.PROJECTS_BUILD_ONE_KEY);
		expectedKeys.add(ConstantTools.PROJECTS_BUILD_ALL_GROUPE1_KEY);
		Tools.verifyKeys(expectedKeys, application.stringPropertyNames(), ConstantTools.APPLICATION_PROP_FILE);
		
		fillProjectsList();
	
		/* */
		
		parameters = new Properties();
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(ConstantTools.PARAM_PROP_FILE)) {
			parameters.load(is);
		}
		expectedKeys = new HashSet<String>();
		expectedKeys.add(ConstantTools.REVISION_KEY);
		Tools.verifyKeys(expectedKeys, parameters.stringPropertyNames(), ConstantTools.TOOLS_PROP_FILE);
		
		/* */
		
		environment = new Properties();
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(ConstantTools.ENV_PROP_FILE)) {
			environment.load(is);
		}
		expectedKeys = new HashSet<String>();
		expectedKeys.add(ConstantTools.GITLAB_URL_KEY);
		expectedKeys.add(ConstantTools.REMOTE_CONNEXION_KEY);
		expectedKeys.add(ConstantTools.REMOTE_DEPOT_FOLDER_KEY);
		expectedKeys.add(ConstantTools.REMOTE_ESII_APP_FOLDER_KEY);
		expectedKeys.add(ConstantTools.JENKINS_URL_KEY);
		expectedKeys.add(ConstantTools.JOB_NAME_KEY);
		expectedKeys.add(ConstantTools.JOB_TOKEN_KEY);
		expectedKeys.add(ConstantTools.ENVENT_STORAGE_KEY);
		Tools.verifyKeys(expectedKeys, environment.stringPropertyNames(), ConstantTools.ENV_PROP_FILE);
		
		/* */
		
		tools = new Properties();
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(ConstantTools.TOOLS_PROP_FILE)) {
			tools.load(is);
		}
		expectedKeys = new HashSet<String>();
		Tools.verifyKeys(expectedKeys, tools.stringPropertyNames(), ConstantTools.TOOLS_PROP_FILE);
	}
	
	private void fillProjectsList() {
		
		// les buildOne
		projectsBuilOne = new HashMap<Integer, String>();
		String[] projectsList = application.getProperty(ConstantTools.PROJECTS_BUILD_ONE_KEY).split(ConstantTools.COMA);
		for (int index = 0; index < projectsList.length; index++)
			projectsBuilOne.put(index+1, projectsList[index]);
		
		// les buildAll
		projectsBuildAll = new HashMap<Integer, List<String>>();
		int index = 1;
		String key = ConstantTools.PROJECTS_BUILD_ALL_GROUPE_KEY + index;
		while ( application.containsKey(key) ) {
			String[] projectsTab = application.getProperty(key).split(ConstantTools.COMA);
			projectsBuildAll.put(index, Arrays.asList(projectsTab));
			index++;
			key = ConstantTools.PROJECTS_BUILD_ALL_GROUPE_KEY + index;
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
			writer.write(constrcuctHelper.beginPipeline() + constrcuctHelper.addCRLF());
			
			// agent
			writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.agent() + constrcuctHelper.addCRLF());
			
			addParamEnvTools(writer);
						
			// stages
			writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.beginStages() + constrcuctHelper.addCRLF());
			
			addInitialize(writer, true);
			
			// all build
			int nbGroup = projectsBuildAll.size();
			for (int index = 1; index <= nbGroup; index++) {
				for ( String project : projectsBuildAll.get(index) )
					addStageForProject(writer, project, 0);
			}
			
			addRemoteDataFolder(writer);
			
			// end stages
			writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.endStages() + constrcuctHelper.addCRLF());
			
			// end pipeline
			writer.write(constrcuctHelper.endPipeline() + constrcuctHelper.addCRLF());
			
			writer.write(constrcuctHelper.addCRLF());
			
			// functions
			writer.write(constrcuctHelper.getFunctions() );
		}
	}
	
	private void generateBuildAll() throws IOException, URISyntaxException {
		
		Path path = Paths.get(application.getProperty(ConstantTools.BUILD_ALL_DIRECTORY_KEY));
		File direcrory = Tools.createDirectoryIfNeedIt(path).toFile();
		File jenkinsfile = Paths.get(direcrory.getAbsolutePath(), ConstantTools.JENKINS_FILE).toFile();
		jenkinsfile.createNewFile();
		
		try (Writer writer = new PrintWriter(jenkinsfile)) {
			
			// pipeline
			writer.write(constrcuctHelper.beginPipeline() + constrcuctHelper.addCRLF());
			
			// agent
			writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.agent() + constrcuctHelper.addCRLF());
			
			addParamEnvTools(writer);
						
			// stages
			writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.beginStages() + constrcuctHelper.addCRLF());
			
			addInitialize(writer, true);
			
			// all build
			int nbGroup = projectsBuildAll.size();
			for (int index = 1; index <= nbGroup; index++) {
				
				List<String> groupe = projectsBuildAll.get(index);
				if (groupe.size() == 1)
					addStageForProject(writer, groupe.get(0), 0);
				else
					addParallelStageForProject(writer, index, groupe);
			}
			
			addRemoteDataFolder(writer);
			
			// end stages
			writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.endStages() + constrcuctHelper.addCRLF());
			
			// end pipeline
			writer.write(constrcuctHelper.endPipeline() + constrcuctHelper.addCRLF());
			
			writer.write(constrcuctHelper.addCRLF());
			
			// functions
			writer.write(constrcuctHelper.getFunctions() );
		}
	}
	
	private void generateRunner() throws IOException, URISyntaxException {
		
		Path path = Paths.get(application.getProperty(ConstantTools.BUILD_ALL_DIRECTORY_KEY), ConstantTools.RUNNER_DIRECTORY);
		File direcrory = Tools.createDirectoryIfNeedIt(path).toFile();
		File jenkinsfile = Paths.get(direcrory.getAbsolutePath(), ConstantTools.JENKINS_FILE).toFile();
		jenkinsfile.createNewFile();
		
		try (Writer writer = new PrintWriter(jenkinsfile)) {
			
			// pipeline
			writer.write(constrcuctHelper.beginPipeline() + constrcuctHelper.addCRLF());
			
			// agent
			writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.agent() + constrcuctHelper.addCRLF());
			
			// global runner env
			writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.beginEnv() + constrcuctHelper.addCRLF());
			for ( String key : Tools.getKeysFilterByPrefix(environment, ConstantTools.RUNNER_KEY + ConstantTools.DOT) ) {
				Environment env = PropToEntitiy.transformToEnvironment(key, environment.getProperty(key));
				writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.contentEnv(env) + constrcuctHelper.addCRLF());
			}
			writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.endEnv() + constrcuctHelper.addCRLF());
			
			// stages
			writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.beginStages() + constrcuctHelper.addCRLF());
			
			writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.beginStage("run") + constrcuctHelper.addCRLF());
			writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.beginSteps() + constrcuctHelper.addCRLF());
			
			String[] branches = application.getProperty(ConstantTools.RUNNER_BRANCHES_KEY).split(ConstantTools.COMA);
			// master par défaut
			if ( branches == null || branches.length == 0 || (branches.length == 1 && branches[0].equals("")) ) {
				String shCommand = "wget \\\"${env.jenkinsUrl}/view/GIT/job/${env.jobName}/buildWithParameters?token=${env.pipelineToken}&revision=master\\\"";
				writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.sh(shCommand) + constrcuctHelper.addCRLF());
			}
			else {
				for (String branche : branches) {
					String shCommand = "wget \\\"${env.jenkinsUrl}/view/GIT/job/${env.jobName}/buildWithParameters?token=${env.pipelineToken}&revision=" + branche + "\\\"";
					writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.sh(shCommand) + constrcuctHelper.addCRLF());
				}
			}
			
			writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.endSteps() + constrcuctHelper.addCRLF());
			writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.endStage() + constrcuctHelper.addCRLF());
			
			// end stages
			writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.endStages() + constrcuctHelper.addCRLF());
			
			// end pipeline
			writer.write(constrcuctHelper.endPipeline());
		}
	}
	
	private void genetateAllBuildOne() throws IOException, URISyntaxException {
		
		Path path = Paths.get(application.getProperty(ConstantTools.BUILD_ONE_DIRECTORY_KEY));
		File direcrory = Tools.createDirectoryIfNeedIt(path).toFile();
		
		
		// all buildOne	
		for (int index = 1; index <= projectsBuilOne.size(); index++) {
				
			String project = projectsBuilOne.get(index);
			
			path = Paths.get(direcrory.getAbsolutePath(), project);
			File subDirecrory = Tools.createDirectoryIfNeedIt(path).toFile();
			
			File jenkinsfile = Paths.get(subDirecrory.getAbsolutePath(), ConstantTools.JENKINS_FILE).toFile();
			jenkinsfile.createNewFile();
			
			try (Writer writer = new PrintWriter(jenkinsfile)) {
				
				// pipeline
				writer.write(constrcuctHelper.beginPipeline() + constrcuctHelper.addCRLF());
				
				// agent
				writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.agent() + constrcuctHelper.addCRLF());
				
				addParamEnvTools(writer);
				
				// stages
				writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.beginStages() + constrcuctHelper.addCRLF());
				
				addInitialize(writer, false);
				
				addStageForProject(writer, project, 0);
				
				// end stages
				writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.endStages() + constrcuctHelper.addCRLF());
				
				// end pipeline
				writer.write(constrcuctHelper.endPipeline() + constrcuctHelper.addCRLF());
				
				writer.write(constrcuctHelper.addCRLF());
				
				// functions
				writer.write(constrcuctHelper.getFunctions());
			}
		}
		
	}
	
	
	
	private void addParamEnvTools(Writer writer) throws IOException, URISyntaxException {
		
		// parameters
		writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.beginParameters() + constrcuctHelper.addCRLF());
		for ( String key : Tools.getKeysFilterByPrefix(parameters, "") ) {
			Parameter param = PropToEntitiy.transformToParameter(key, parameters.getProperty(key));
			writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.contentParameters(param) + constrcuctHelper.addCRLF());
		}
		writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.endParameters() + constrcuctHelper.addCRLF());
		
		// global env
		writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.beginEnv() + constrcuctHelper.addCRLF());
		for ( String key : Tools.getKeysFilterByPrefix(environment, ConstantTools.GLOBAL_KEY + ConstantTools.DOT) ) {
			Environment env = PropToEntitiy.transformToEnvironment(key, environment.getProperty(key));
			writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.contentEnv(env) + constrcuctHelper.addCRLF());
		}
		writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.endEnv() + constrcuctHelper.addCRLF());
		
		// tools
		// laisser décommenter tant qu'il n'y a pas de clé valeur dans le bloc tools
//		writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.beginTools() + constrcuctHelper.addCRLF());
//		for ( String key : Tools.getKeysFilterByPrefix(tools, "") ) {
//			Tool tool = PropToEntitiy.transformToTool(key, tools.getProperty(key));
//			writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.contentTools(tool) + constrcuctHelper.addCRLF());
//		}
//		writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.endTools() + constrcuctHelper.addCRLF());
	}
	
	private void addInitialize(Writer writer, boolean isBuildAll) throws IOException, URISyntaxException {
		
		writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.beginStage("Initialize") + constrcuctHelper.addCRLF());
		writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.beginSteps() + constrcuctHelper.addCRLF());

		// echo all parameters
		writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.beginWrap() + constrcuctHelper.addCRLF());
		for ( Entry<Object, Object> entry : parameters.entrySet() ) {
			Parameter param = PropToEntitiy.transformToParameter(entry.getKey().toString(), entry.getValue().toString());
			String echoStr = constrcuctHelper.infoColor(param.getName() + " : " + constrcuctHelper.dollarParams(param.getName()));
			writer.write(constrcuctHelper.addTab(5) + constrcuctHelper.echo(echoStr) + constrcuctHelper.addCRLF());
		}
		writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.endWrap() + constrcuctHelper.addCRLF());	
		
		// clean jenkins workspace
		writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.cleanWs() + constrcuctHelper.addCRLF());
		
		// clean remote
		if (isBuildAll) {
			String shCommand = "ssh ${env.remoteConnexion} rm -rf ${env.depotFolder}/${params.revision}";
			writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.sh(shCommand) + constrcuctHelper.addCRLF());
		}
		
		writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.endSteps() + constrcuctHelper.addCRLF());	
		writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.endStage() + constrcuctHelper.addCRLF());
	}
	
	private void addRemoteDataFolder(Writer writer) throws IOException, URISyntaxException {
		
		writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.beginStage("Create data folder") + constrcuctHelper.addCRLF());
		
		// env
		writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.beginEnv() + constrcuctHelper.addCRLF());
		for ( String key : Tools.getKeysFilterByPrefix(environment, ConstantTools.INITIALIZE_KEY + ConstantTools.DOT) ) {
			Environment env = PropToEntitiy.transformToEnvironment(key, environment.getProperty(key));
			writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.contentEnv(env) + constrcuctHelper.addCRLF());
		}
		writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.endEnv() + constrcuctHelper.addCRLF());
		
		writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.beginSteps() + constrcuctHelper.addCRLF());

		String shCommand = "ssh ${env.remoteConnexion} mkdir -p ${env.depotFolder}/${params.revision}/${env.esiiDataFolder}/${enventStorage}";
		writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.sh(shCommand) + constrcuctHelper.addCRLF());
		
		writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.endSteps() + constrcuctHelper.addCRLF());	
		
		writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.endStage() + constrcuctHelper.addCRLF());
	}
	
	private void addStageForProject(Writer writer, String project, int identToAdd) throws IOException, URISyntaxException {
		
		writer.write(constrcuctHelper.addTab(2 + identToAdd) + constrcuctHelper.beginStage("build " + project) + constrcuctHelper.addCRLF());
		
		// env
		writer.write(constrcuctHelper.addTab(3 + identToAdd) + constrcuctHelper.beginEnv() + constrcuctHelper.addCRLF());
		for ( String key : Tools.getKeysFilterByPrefix(environment, project + ConstantTools.DOT) ) {
			Environment env = PropToEntitiy.transformToEnvironment(key, environment.getProperty(key));
			writer.write(constrcuctHelper.addTab(4 + identToAdd) + constrcuctHelper.contentEnv(env) + constrcuctHelper.addCRLF());
		}
		writer.write(constrcuctHelper.addTab(3 + identToAdd) + constrcuctHelper.endEnv() + constrcuctHelper.addCRLF());
		
		// step
		writer.write(constrcuctHelper.addTab(3 + identToAdd) + constrcuctHelper.beginSteps() + constrcuctHelper.addCRLF());
		writer.write(constrcuctHelper.addTab(4 + identToAdd) + constrcuctHelper.runBuild() + constrcuctHelper.addCRLF());
		writer.write(constrcuctHelper.addTab(3 + identToAdd) + constrcuctHelper.endEnv() + constrcuctHelper.addCRLF());
		
		writer.write(constrcuctHelper.addTab(2 + identToAdd) + constrcuctHelper.endStage() + constrcuctHelper.addCRLF());
	}
	
	private void addParallelStageForProject(Writer writer, int index, List<String> groupe) throws IOException, URISyntaxException {
		
		writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.beginStage("build groupe " + index) + constrcuctHelper.addCRLF());
		
		// parallel
		writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.beginParallel() + constrcuctHelper.addCRLF());
		
		for (String project : groupe)
			addStageForProject(writer, project, 2);
		
		writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.endParallel() + constrcuctHelper.addCRLF());
		
		writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.endStage() + constrcuctHelper.addCRLF());
	}
	
}
