package com.es2i.pipeline.job;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import com.es2i.pipeline.job.entities.Dashboard;
import com.es2i.pipeline.job.entities.Environment;
import com.es2i.pipeline.job.entities.Parameter;
import com.es2i.pipeline.job.helper.ConfReader;
import com.es2i.pipeline.job.helper.ConstructHelper;
import com.es2i.pipeline.tools.ConstantTools;
import com.es2i.pipeline.tools.Tools;

public class Pipeline {
	
	private ConstructHelper constrcuctHelper;
	
	private ConfReader confReader;
	
	public enum BuildType { MONO_BUILD, ALL_BUILD, DASHBOARD }
	
	
	public Pipeline() throws IOException {
		
		constrcuctHelper = ConstructHelper.getInstance();
		confReader = ConfReader.getInstance();
	}

	
	
	public void run() throws IOException, URISyntaxException {

		clean();
		generateBuildAll();
		generateRunner();
		generateAllBuildOne();
		generateDashboard();
	}
	
	private void clean() throws IOException {

		Tools.deleteIfExists( Paths.get(ConstantTools.BUILD_ALL_DIRECTORY) );
		Tools.deleteIfExists( Paths.get(ConstantTools.BUILD_ONE_DIRECTORY) );
		Tools.deleteIfExists( Paths.get(ConstantTools.DASHBOARD_DIRECTORY) );
	}
	
	private void generateBuildAll() throws IOException, URISyntaxException {
		
		File buildAllDirectory = Tools.createDirectoryIfNeedIt( Paths.get(ConstantTools.BUILD_ALL_DIRECTORY) ).toFile();
		File jenkinsfile = Paths.get(buildAllDirectory.getAbsolutePath(), ConstantTools.JENKINS_FILE).toFile();
		jenkinsfile.createNewFile();
		
		try ( OutputStream os = new FileOutputStream(jenkinsfile) ) {
			try ( Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8) ) {
					
				// pipeline - agent - param - env - tools - stages
				writer.write(constrcuctHelper.beginPipeline() + constrcuctHelper.addCRLF());
				writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.agent() + constrcuctHelper.addCRLF());
				addParamEnvTools(writer, BuildType.ALL_BUILD);
				writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.beginStages() + constrcuctHelper.addCRLF());
				
				addInitializeStage(writer, BuildType.ALL_BUILD);
				
				addCreateDataFolderStage(writer);
				
				// all build
				Map<Integer, List<String>> projectsBuildAll = confReader.getProjectsBuildAll();
				int nbGroup = projectsBuildAll.size();
				for (int index = 1; index <= nbGroup; index++) {
					
					List<String> groupe = projectsBuildAll.get(index);
					if (groupe.size() == 1)
						addStageForProject(writer, groupe.get(0), false, BuildType.ALL_BUILD);
					else
						addParallelStageForProject(writer, index, groupe, BuildType.ALL_BUILD);
				}
				
				addDeployToSecondaryRemoteStage(writer);
				
				// stages - pipeline
				writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.endStages() + constrcuctHelper.addCRLF());
				writer.write(constrcuctHelper.endPipeline() + constrcuctHelper.addCRLF());
				
				// saut de ligne
				writer.write(constrcuctHelper.addCRLF());
				
				// functions.txt
				writer.write(constrcuctHelper.getFunctions() );
			}
		}
	}
	
	private void generateRunner() throws IOException, URISyntaxException {
		
		File runnerDirecrory = Tools.createDirectoryIfNeedIt( Paths.get(ConstantTools.BUILD_ALL_DIRECTORY, ConstantTools.RUNNER_DIRECTORY) ).toFile();
		File jenkinsfile = Paths.get(runnerDirecrory.getAbsolutePath(), ConstantTools.JENKINS_FILE).toFile();
		jenkinsfile.createNewFile();
		
		try ( OutputStream os = new FileOutputStream(jenkinsfile) ) {
			try ( Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8) ) {
				
				// pipeline - agent
				writer.write(constrcuctHelper.beginPipeline() + constrcuctHelper.addCRLF());
				writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.agent() + constrcuctHelper.addCRLF());
				
				// global runner env
				writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.beginEnv() + constrcuctHelper.addCRLF());
				for ( Environment env : confReader.getEnvironmentByPrefix(ConstantTools.RUNNER_KEY) )
					writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.contentEnv(env) + constrcuctHelper.addCRLF());
				writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.endEnv() + constrcuctHelper.addCRLF());
				
				// stages - stage - steps
				writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.beginStages() + constrcuctHelper.addCRLF());
				writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.beginStage("run") + constrcuctHelper.addCRLF());
				writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.beginSteps() + constrcuctHelper.addCRLF());
				
				
				List<String> revisions = confReader.getRunnerRevisions();
				List<String> mavenProfiles = confReader.getRunnerMavenProfiles();
				
				if ( revisions.isEmpty() || mavenProfiles.isEmpty() ) {
					writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.callBuildAll(null, null) + constrcuctHelper.addCRLF());
				}
				else {
					for (String revision : revisions)
						for (String mavenProfile : mavenProfiles)
							writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.callBuildAll(revision.trim(), mavenProfile.trim()) + constrcuctHelper.addCRLF());
				}
				
				// steps - stage - stages
				writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.endSteps() + constrcuctHelper.addCRLF());
				writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.endStage() + constrcuctHelper.addCRLF());
				writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.endStages() + constrcuctHelper.addCRLF());
				
				// pipeline
				writer.write(constrcuctHelper.endPipeline());
			}
		}
	}
	
	private void generateAllBuildOne() throws IOException, URISyntaxException {
		
		File monoBuildDirecrory = Tools.createDirectoryIfNeedIt( Paths.get(ConstantTools.BUILD_ONE_DIRECTORY) ).toFile();
		
		// all buildOne	
		Map<Integer, String> projectsBuilOne = confReader.getProjectsBuildOne();
		for (int index = 1; index <= projectsBuilOne.size(); index++) {
				
			String project = projectsBuilOne.get(index);
			
			File projectDirecrory = Tools.createDirectoryIfNeedIt( Paths.get(monoBuildDirecrory.getAbsolutePath(), project) ).toFile();
			File jenkinsfile = Paths.get(projectDirecrory.getAbsolutePath(), ConstantTools.JENKINS_FILE).toFile();
			jenkinsfile.createNewFile();
			
			try ( OutputStream os = new FileOutputStream(jenkinsfile) ) {
				try ( Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8) ) {
				
					// pipeline - agent - param - env - tools - stages
					writer.write(constrcuctHelper.beginPipeline() + constrcuctHelper.addCRLF());
					writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.agent() + constrcuctHelper.addCRLF());
					addParamEnvTools(writer, BuildType.MONO_BUILD);
					writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.beginStages() + constrcuctHelper.addCRLF());
					
					addInitializeStage(writer, BuildType.MONO_BUILD);
					
					addStageForProject(writer, project, false, BuildType.MONO_BUILD);
					
					// stages - pipeline
					writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.endStages() + constrcuctHelper.addCRLF());
					writer.write(constrcuctHelper.endPipeline() + constrcuctHelper.addCRLF());
					
					// saut de ligne
					writer.write(constrcuctHelper.addCRLF());
					
					// functions.txt
					writer.write(constrcuctHelper.getFunctions());
				}
			}
		}
		
	}
	
	private void generateDashboard() throws IOException, URISyntaxException {
		
		File dashboardDirectory = Tools.createDirectoryIfNeedIt( Paths.get(ConstantTools.DASHBOARD_DIRECTORY) ).toFile();
		File jenkinsfile = Paths.get(dashboardDirectory.getAbsolutePath(), ConstantTools.JENKINS_FILE).toFile();
		jenkinsfile.createNewFile();
		
		try ( OutputStream os = new FileOutputStream(jenkinsfile) ) {
			try ( Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8) ) {
					
				// pipeline - agent - param - env - tools - stages
				writer.write(constrcuctHelper.beginPipeline() + constrcuctHelper.addCRLF());
				writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.agent() + constrcuctHelper.addCRLF());
				addParamEnvTools(writer, BuildType.DASHBOARD);
				writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.beginStages() + constrcuctHelper.addCRLF());
				
				addInitializeStage(writer, BuildType.DASHBOARD);
				
				
				Dashboard dashboard = confReader.getDashboard();
				
				// afin de clean une seul fois le target
				boolean isFirst = true;
				
				// back
				for (Map.Entry<String, List<Environment>> entry : dashboard.getBackEnvironements().entrySet()) {

					// stage
					writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.beginStage("build " + entry.getKey()) + constrcuctHelper.addCRLF());
					
					// env
					writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.beginEnv() + constrcuctHelper.addCRLF());
					for (Environment environment : entry.getValue()) 
						writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.contentEnv(environment) + constrcuctHelper.addCRLF());
					writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.endEnv() + constrcuctHelper.addCRLF());
					
					// steps - script
					writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.beginSteps() + constrcuctHelper.addCRLF());
					writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.beginScript() + constrcuctHelper.addCRLF());
					
					// if
					writer.write(constrcuctHelper.addTab(5) + constrcuctHelper.ifBackDeploy() + constrcuctHelper.addCRLF());
					if (isFirst) {
						writer.write(constrcuctHelper.addTab(6) + constrcuctHelper.cleanProjectPrimaryRemote() + constrcuctHelper.addCRLF());
						isFirst = false;
					}
					writer.write(constrcuctHelper.addTab(6) + constrcuctHelper.runBuild() + constrcuctHelper.addCRLF());
					writer.write(constrcuctHelper.addTab(6) + constrcuctHelper.deploy() + constrcuctHelper.addCRLF());
					writer.write(constrcuctHelper.addTab(5) + constrcuctHelper.endIfBackDeploy() + constrcuctHelper.addCRLF());
					
					// script - steps
					writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.endScript() + constrcuctHelper.addCRLF());
					writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.endSteps() + constrcuctHelper.addCRLF());
					
					// stage
					writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.endStage() + constrcuctHelper.addCRLF());
				}
				
				
				isFirst = true;
				
				// front
				for (Map.Entry<String, List<Environment>> entry : dashboard.getFrontEnvironements().entrySet()) {
					
					// stage
					writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.beginStage("build " + entry.getKey()) + constrcuctHelper.addCRLF());
					
					// env
					writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.beginEnv() + constrcuctHelper.addCRLF());
					for (Environment environment : entry.getValue()) 
						writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.contentEnv(environment) + constrcuctHelper.addCRLF());
					writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.endEnv() + constrcuctHelper.addCRLF());
					
					// steps - script
					writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.beginSteps() + constrcuctHelper.addCRLF());
					writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.beginScript() + constrcuctHelper.addCRLF());
					
					// if
					writer.write(constrcuctHelper.addTab(5) + constrcuctHelper.ifFrontDeploy() + constrcuctHelper.addCRLF());
					if (isFirst) {
						writer.write(constrcuctHelper.addTab(6) + constrcuctHelper.cleanProjectPrimaryRemote() + constrcuctHelper.addCRLF());
						isFirst = false;
					}
					writer.write(constrcuctHelper.addTab(6) + constrcuctHelper.runBuild() + constrcuctHelper.addCRLF());
					writer.write(constrcuctHelper.addTab(6) + constrcuctHelper.deploy() + constrcuctHelper.addCRLF());
					writer.write(constrcuctHelper.addTab(5) + constrcuctHelper.endIfFrontDeploy() + constrcuctHelper.addCRLF());
					
					// script - steps
					writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.endScript() + constrcuctHelper.addCRLF());
					writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.endSteps() + constrcuctHelper.addCRLF());
					
					// stage
					writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.endStage() + constrcuctHelper.addCRLF());
				}
				
				
				// stages - pipeline
				writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.endStages() + constrcuctHelper.addCRLF());
				writer.write(constrcuctHelper.endPipeline() + constrcuctHelper.addCRLF());
				
				// saut de ligne
				writer.write(constrcuctHelper.addCRLF());
				
				// functions.txt
				writer.write(constrcuctHelper.getFunctions() );
			}
		}
	}
	
	
	
	private void addParamEnvTools(Writer writer, BuildType buildType) throws IOException, URISyntaxException {
		
		// parameters
		writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.beginParameters() + constrcuctHelper.addCRLF());
		for (Parameter param : confReader.getParameters()) {
			if ( param.isInScope(buildType) )
				writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.contentParameters(param) + constrcuctHelper.addCRLF());
		}
		writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.endParameters() + constrcuctHelper.addCRLF());
		
		// global env
		writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.beginEnv() + constrcuctHelper.addCRLF());
		for ( Environment env : confReader.getEnvironmentByPrefix(ConstantTools.GLOBAL_KEY) )
			writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.contentEnv(env) + constrcuctHelper.addCRLF());
		writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.endEnv() + constrcuctHelper.addCRLF());
		
		// tools
//		writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.beginTools() + constrcuctHelper.addCRLF());
//		for ( String key : Tools.getKeysFilterByPrefix(tools, "") ) {
//			Tool tool = PropToEntitiy.transformToTool(key, tools.getProperty(key));
//			writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.contentTools(tool) + constrcuctHelper.addCRLF());
//		}
//		writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.endTools() + constrcuctHelper.addCRLF());
	}
	
	private void addInitializeStage(Writer writer, BuildType buildType) throws IOException, URISyntaxException {
		
		// stage - steps
		writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.beginStage("Initialize") + constrcuctHelper.addCRLF());
		writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.beginSteps() + constrcuctHelper.addCRLF());

		// echo all parameters
		writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.beginWrap() + constrcuctHelper.addCRLF());
		for (Parameter param : confReader.getParameters()) {
			if ( param.isInScope(buildType) ) {
				String echoStr = constrcuctHelper.infoColor(param.getName() + " : " + constrcuctHelper.dollarParams(param.getName()));
				writer.write(constrcuctHelper.addTab(5) + constrcuctHelper.echo(echoStr) + constrcuctHelper.addCRLF());
			}
		}
		writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.endWrap() + constrcuctHelper.addCRLF());	
		
		// clean jenkins workspace
		writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.cleanWs() + constrcuctHelper.addCRLF());
		
		// clean remote
		if (buildType == BuildType.ALL_BUILD)
			writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.cleanPrimaryRemote() + constrcuctHelper.addCRLF());
		
		// steps - stage
		writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.endSteps() + constrcuctHelper.addCRLF());	
		writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.endStage() + constrcuctHelper.addCRLF());
	}
	
	private void addCreateDataFolderStage(Writer writer) throws IOException, URISyntaxException {
		
		// stage - stpes
		writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.beginStage("Create data folder") + constrcuctHelper.addCRLF());
		writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.beginSteps() + constrcuctHelper.addCRLF());
		
		writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.createDataFolderOnPrimaryRemote() + constrcuctHelper.addCRLF());
		
		// steps - stage
		writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.endSteps() + constrcuctHelper.addCRLF());	
		writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.endStage() + constrcuctHelper.addCRLF());
	}
	
	private void addDeployToSecondaryRemoteStage(Writer writer) throws IOException, URISyntaxException {
		
		// au moins un remote secondaire
		if ( !confReader.getSecondaryRemotes().isEmpty() ) {
			
			// stage - steps - script - if()
			writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.beginStage("secondary deploy") + constrcuctHelper.addCRLF());
			writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.beginSteps() + constrcuctHelper.addCRLF());
			writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.beginScript() + constrcuctHelper.addCRLF());
			writer.write(constrcuctHelper.addTab(5) + constrcuctHelper.beginIfSecondaryDeploy() + constrcuctHelper.addCRLF());
			
			// creation tar
			writer.write(constrcuctHelper.addTab(6) + constrcuctHelper.createTarOnPrimaryRemote() + constrcuctHelper.addCRLF());
			
			for ( String remote : confReader.getSecondaryRemotes() )
				writer.write(constrcuctHelper.addTab(6) + constrcuctHelper.runDeployToSecondaryRemote(remote) + constrcuctHelper.addCRLF());
			
			// supression tar
			writer.write(constrcuctHelper.addTab(6) + constrcuctHelper.removeTarOnPrimaryRemote() + constrcuctHelper.addCRLF());
			
			// if() - script - steps - stage
			writer.write(constrcuctHelper.addTab(5) + constrcuctHelper.endIfSecondaryDeploy() + constrcuctHelper.addCRLF());
			writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.endScript() + constrcuctHelper.addCRLF());
			writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.endSteps() + constrcuctHelper.addCRLF());
			writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.endStage() + constrcuctHelper.addCRLF());
		}
	}
	
	private void addStageForProject(Writer writer, String project, boolean insideParallelBloc, BuildType buildType) throws IOException, URISyntaxException {
		
		int identToAdd = insideParallelBloc ? 2 : 0;
		
		// stage
		writer.write(constrcuctHelper.addTab(2 + identToAdd) + constrcuctHelper.beginStage("build " + project) + constrcuctHelper.addCRLF());
		
		// local env
		writer.write(constrcuctHelper.addTab(3 + identToAdd) + constrcuctHelper.beginEnv() + constrcuctHelper.addCRLF());
		for ( Environment env : confReader.getEnvironmentByPrefix(project) )
			writer.write(constrcuctHelper.addTab(4 + identToAdd) + constrcuctHelper.contentEnv(env) + constrcuctHelper.addCRLF());
		writer.write(constrcuctHelper.addTab(3 + identToAdd) + constrcuctHelper.endEnv() + constrcuctHelper.addCRLF());
		
		// steps
		writer.write(constrcuctHelper.addTab(3 + identToAdd) + constrcuctHelper.beginSteps() + constrcuctHelper.addCRLF());
		if (buildType == BuildType.MONO_BUILD)
			writer.write(constrcuctHelper.addTab(4 + identToAdd) + constrcuctHelper.cleanProjectPrimaryRemote() + constrcuctHelper.addCRLF());
		writer.write(constrcuctHelper.addTab(4 + identToAdd) + constrcuctHelper.runBuild() + constrcuctHelper.addCRLF());
		writer.write(constrcuctHelper.addTab(4 + identToAdd) + constrcuctHelper.deploy() + constrcuctHelper.addCRLF());
		writer.write(constrcuctHelper.addTab(3 + identToAdd) + constrcuctHelper.endSteps() + constrcuctHelper.addCRLF());
		
		// stage
		writer.write(constrcuctHelper.addTab(2 + identToAdd) + constrcuctHelper.endStage() + constrcuctHelper.addCRLF());
	}
	
	private void addParallelStageForProject(Writer writer, int index, List<String> groupe, BuildType buildType) throws IOException, URISyntaxException {
		
		// stage - parallel
		writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.beginStage("build groupe " + index) + constrcuctHelper.addCRLF());
		writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.beginParallel() + constrcuctHelper.addCRLF());
		
		for (String project : groupe)
			addStageForProject(writer, project, true, buildType);
		
		// parallel - stage
		writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.endParallel() + constrcuctHelper.addCRLF());
		writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.endStage() + constrcuctHelper.addCRLF());
	}
	
}
