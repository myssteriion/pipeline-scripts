package com.es2i.pipeline.job;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import com.es2i.pipeline.job.entities.Environment;
import com.es2i.pipeline.job.entities.Tool;
import com.es2i.pipeline.job.entities.enums.ProjectKeyEnum;
import com.es2i.pipeline.job.entities.parameter.Parameter;
import com.es2i.pipeline.job.helper.ConfReader;
import com.es2i.pipeline.job.helper.ConstructHelper;
import com.es2i.pipeline.job.script.abstracts.Script;
import com.es2i.pipeline.job.script.abstracts.WithProjectsStages;
import com.es2i.pipeline.job.script.impl.BuildAll;
import com.es2i.pipeline.job.script.impl.BuildOne;
import com.es2i.pipeline.job.script.impl.Dashboard;
import com.es2i.pipeline.job.script.impl.Runner;
import com.es2i.pipeline.tools.ConstantTools;
import com.es2i.pipeline.tools.Tools;

public class Pipeline {
	
	private ConstructHelper constrcuctHelper;
	
	private ConfReader confReader;
	

	
	public Pipeline() throws IOException {
		
		constrcuctHelper = ConstructHelper.getInstance();
		confReader = ConfReader.getInstance();
	}

	
	
	public void run() throws IOException {

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
	
	
	
	
	private void generateRunner() throws IOException {
		
		// création du fichier physique
		File runnerDirecrory = Tools.createDirectoryIfNeedIt( Paths.get(ConstantTools.BUILD_ALL_DIRECTORY, ConstantTools.RUNNER_DIRECTORY) ).toFile();
		File jenkinsfile = Paths.get(runnerDirecrory.getAbsolutePath(), ConstantTools.JENKINS_FILE).toFile();
		jenkinsfile.createNewFile();
		
		try ( OutputStream os = new FileOutputStream(jenkinsfile) ) {
			try ( Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8) ) {
				
				Runner runner = confReader.getRunner();
				
				// pipeline - agent - param - env - tools - stages - stage - steps
				writer.write(constrcuctHelper.beginPipeline() + constrcuctHelper.addCRLF());
				writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.agent() + constrcuctHelper.addCRLF());
				addParamEnvTools(writer, runner);
				writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.beginStages() + constrcuctHelper.addCRLF());
				writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.beginStage("runner") + constrcuctHelper.addCRLF());
				writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.beginSteps() + constrcuctHelper.addCRLF());
				
				
				// création des wget pour l'IC
				List<String> revisions = runner.getRevisions();
				List<String> mavenProfiles = runner.getMavenProfiles();
				
				if ( revisions.isEmpty() || mavenProfiles.isEmpty() ) {
					writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.callBuildAll(null, null) + constrcuctHelper.addCRLF());
				}
				else {
					for (String revision : revisions)
						for (String mavenProfile : mavenProfiles)
							writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.callBuildAll(revision.trim(), mavenProfile.trim()) + constrcuctHelper.addCRLF());
				}
				
				// steps - stage - stages - pipeline
				writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.endSteps() + constrcuctHelper.addCRLF());
				writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.endStage() + constrcuctHelper.addCRLF());
				writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.endStages() + constrcuctHelper.addCRLF());
				writer.write(constrcuctHelper.endPipeline());
			}
		}
	}

	private void generateBuildAll() throws IOException {
		
		// création du fichier physique
		File buildAllDirectory = Tools.createDirectoryIfNeedIt( Paths.get(ConstantTools.BUILD_ALL_DIRECTORY) ).toFile();
		File jenkinsfile = Paths.get(buildAllDirectory.getAbsolutePath(), ConstantTools.JENKINS_FILE).toFile();
		jenkinsfile.createNewFile();
		
		try ( OutputStream os = new FileOutputStream(jenkinsfile) ) {
			try ( Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8) ) {
					
				BuildAll buildAll = confReader.getBuildAll();
				
				// pipeline - agent - param - env - tools - stages
				writer.write(constrcuctHelper.beginPipeline() + constrcuctHelper.addCRLF());
				writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.agent() + constrcuctHelper.addCRLF());
				addParamEnvTools(writer, buildAll);
				writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.beginStages() + constrcuctHelper.addCRLF());
				
				addInitializeStage(writer, buildAll);
				
				addCreateDataFolderStage(writer);
				
				// all build
				Map<Integer, List<String>> projectsBuildAll = buildAll.getProjects();
				int nbGroup = projectsBuildAll.size();
				for (int currentGroup = 1; currentGroup <= nbGroup; currentGroup++) {
					
					// pour tous les groupes : 
					//	- si un seul élément => créer stage bloc
					//	- sinon, créer parallel bloc
					List<String> groupe = projectsBuildAll.get(currentGroup);
					if (groupe.size() == 1)
						addStageForProject(writer, buildAll, groupe.get(0), false);
					else
						addParallelStageForProject(writer, buildAll, currentGroup, groupe);
				}
				
				addDeployToSecondaryRemoteStage(writer, buildAll);
				
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
	
	private void generateAllBuildOne() throws IOException {
		
		File buildOneDirecrory = Tools.createDirectoryIfNeedIt( Paths.get(ConstantTools.BUILD_ONE_DIRECTORY) ).toFile();
		
		BuildOne buildOne = confReader.getBuildOne();
		
		// all buildOne	
		Map<Integer, String> projects = buildOne.getProjects();
		for (int index = 1; index <= projects.size(); index++) {
				
			String project = projects.get(index);
			
			// création du fichier physique
			File projectDirecrory = Tools.createDirectoryIfNeedIt( Paths.get(buildOneDirecrory.getAbsolutePath(), project) ).toFile();
			File jenkinsfile = Paths.get(projectDirecrory.getAbsolutePath(), ConstantTools.JENKINS_FILE).toFile();
			jenkinsfile.createNewFile();
			
			try ( OutputStream os = new FileOutputStream(jenkinsfile) ) {
				try ( Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8) ) {
				
					// pipeline - agent - param - env - tools - stages
					writer.write(constrcuctHelper.beginPipeline() + constrcuctHelper.addCRLF());
					writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.agent() + constrcuctHelper.addCRLF());
					addParamEnvTools(writer, buildOne);
					writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.beginStages() + constrcuctHelper.addCRLF());
					
					addInitializeStage(writer, buildOne);
					
					addStageForProject(writer, buildOne, project, false);
					
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
	
	private void generateDashboard() throws IOException {
		
		// création du fichier physique
		File dashboardDirectory = Tools.createDirectoryIfNeedIt( Paths.get(ConstantTools.DASHBOARD_DIRECTORY) ).toFile();
		File jenkinsfile = Paths.get(dashboardDirectory.getAbsolutePath(), ConstantTools.JENKINS_FILE).toFile();
		jenkinsfile.createNewFile();
		
		try ( OutputStream os = new FileOutputStream(jenkinsfile) ) {
			try ( Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8) ) {
					
				Dashboard dashboard = confReader.getDashboard();
				
				// pipeline - agent - param - env - tools - stages
				writer.write(constrcuctHelper.beginPipeline() + constrcuctHelper.addCRLF());
				writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.agent() + constrcuctHelper.addCRLF());
				addParamEnvTools(writer, dashboard);
				writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.beginStages() + constrcuctHelper.addCRLF());
				
				addInitializeStage(writer, dashboard);
				
				// add all stage back and front
				dashboard.setProjectsEnvironements( dashboard.getFrontEnvironments() );
				for ( String project :  dashboard.getProjectsFront() )
					addStageForProject(writer, dashboard, project, false);
				
				dashboard.setProjectsEnvironements( dashboard.getBackEnvironments() );
				for ( String project :  dashboard.getProjectsBack() )
					addStageForProject(writer, dashboard, project, false);
				
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
	
	private void addParamEnvTools(Writer writer, Script script) throws IOException {
		
		// parameters
		if ( script.hadParameters() ) {
			writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.beginParameters() + constrcuctHelper.addCRLF());
			for ( Parameter param : script.getParameters() )
				writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.contentParameters(param) + constrcuctHelper.addCRLF());
			writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.endParameters() + constrcuctHelper.addCRLF());
		}		

		// global env
		if ( script.hadEnvironments() ) {
			writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.beginEnv() + constrcuctHelper.addCRLF());
			for ( Environment env : script.getEnvironements() )
				writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.contentEnv(env) + constrcuctHelper.addCRLF());
			writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.endEnv() + constrcuctHelper.addCRLF());
		}
		
		// tools
		if ( script.hadTools() ) {
			writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.beginTools() + constrcuctHelper.addCRLF());
			for ( Tool tool : script.getTools() )
				writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.contentTools(tool) + constrcuctHelper.addCRLF());
			writer.write(constrcuctHelper.addTab(1) + constrcuctHelper.endTools() + constrcuctHelper.addCRLF());
		}
	}
	
	
	private void addInitializeStage(Writer writer, Script script) throws IOException {
		
		// stage - steps
		writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.beginStage("Initialize") + constrcuctHelper.addCRLF());
		writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.beginSteps() + constrcuctHelper.addCRLF());

		// echo all parameters
		if ( script.hadParameters() ) {
			writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.beginWrap() + constrcuctHelper.addCRLF());
			for (Parameter param : script.getParameters()) {
				String echoStr = constrcuctHelper.infoColor(param.getName() + " : " + constrcuctHelper.dollarParams(param.getName()));
				writer.write(constrcuctHelper.addTab(5) + constrcuctHelper.echo(echoStr) + constrcuctHelper.addCRLF());
			}
			writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.endWrap() + constrcuctHelper.addCRLF());	
		}
		
		// clean jenkins workspace
		writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.cleanWs() + constrcuctHelper.addCRLF());
		
		// clean primary remote
		if (script instanceof BuildAll)
			writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.cleanPrimaryRemote() + constrcuctHelper.addCRLF());
		
		// steps - stage
		writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.endSteps() + constrcuctHelper.addCRLF());	
		writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.endStage() + constrcuctHelper.addCRLF());
	}
	
	private void addCreateDataFolderStage(Writer writer) throws IOException {
		
		// stage - stpes
		writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.beginStage("Create data folder") + constrcuctHelper.addCRLF());
		writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.beginSteps() + constrcuctHelper.addCRLF());
		
		writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.createDataFolderOnPrimaryRemote() + constrcuctHelper.addCRLF());
		
		// steps - stage
		writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.endSteps() + constrcuctHelper.addCRLF());	
		writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.endStage() + constrcuctHelper.addCRLF());
	}
	
	private void addDeployToSecondaryRemoteStage(Writer writer, BuildAll buildAll) throws IOException {
		
		// au moins un remote secondaire
		if ( buildAll.hadSecondaryRemotes() ) {
			
			// stage - steps - script - if()
			writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.beginStage("secondary deploy") + constrcuctHelper.addCRLF());
			writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.beginSteps() + constrcuctHelper.addCRLF());
			writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.beginScript() + constrcuctHelper.addCRLF());
			writer.write(constrcuctHelper.addTab(5) + constrcuctHelper.beginIfSecondaryDeploy() + constrcuctHelper.addCRLF());
			
			// creation tar
			writer.write(constrcuctHelper.addTab(6) + constrcuctHelper.createTarOnPrimaryRemote() + constrcuctHelper.addCRLF());
			
			// les deploy
			for ( String secondaryRemote : buildAll.getSecondaryRemotes() )
				writer.write(constrcuctHelper.addTab(6) + constrcuctHelper.runDeployToSecondaryRemote(secondaryRemote) + constrcuctHelper.addCRLF());
			
			// supression tar
			writer.write(constrcuctHelper.addTab(6) + constrcuctHelper.removeTarOnPrimaryRemote() + constrcuctHelper.addCRLF());
			
			// if() - script - steps - stage
			writer.write(constrcuctHelper.addTab(5) + constrcuctHelper.endIfSecondaryDeploy() + constrcuctHelper.addCRLF());
			writer.write(constrcuctHelper.addTab(4) + constrcuctHelper.endScript() + constrcuctHelper.addCRLF());
			writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.endSteps() + constrcuctHelper.addCRLF());
			writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.endStage() + constrcuctHelper.addCRLF());
		}
	}
	
	
	private void addStageForProject(Writer writer, WithProjectsStages script, String project, boolean insideParallelBloc) throws IOException {
		
		int identToAdd = insideParallelBloc ? 2 : 0;
		
		// stage
		writer.write(constrcuctHelper.addTab(2 + identToAdd) + constrcuctHelper.beginStage("build " + project) + constrcuctHelper.addCRLF());
		
		// nombre d'App et de Conf à déployer (car la value peut être un tableau)
		int nbAppDeploy = 1;
		int nbConfDeploy = 1;
		
		// local env
		writer.write(constrcuctHelper.addTab(3 + identToAdd) + constrcuctHelper.beginEnv() + constrcuctHelper.addCRLF());
		for ( Environment environment : script.getProjectsEnvironements().get(project) ) {

			if ( environment.isList() ) {
				
				List<Environment> envSplittedList = environment.getValuesSplitted();
				for (Environment envSplit : envSplittedList)
					writer.write(constrcuctHelper.addTab(4 + identToAdd) + constrcuctHelper.contentEnv(envSplit) + constrcuctHelper.addCRLF());
				
				if ( environment.getName().equalsIgnoreCase( ProjectKeyEnum.SOURCE_APP_DIRECTORY.getName() ) )
					nbAppDeploy = envSplittedList.size();
				if ( environment.getName().equalsIgnoreCase( ProjectKeyEnum.SOURCE_CONF_DIRECTORY.getName() ) )
					nbConfDeploy = envSplittedList.size();
			}
			else
				writer.write(constrcuctHelper.addTab(4 + identToAdd) + constrcuctHelper.contentEnv(environment) + constrcuctHelper.addCRLF());
		}
		writer.write(constrcuctHelper.addTab(3 + identToAdd) + constrcuctHelper.endEnv() + constrcuctHelper.addCRLF());
		
		// steps
		writer.write(constrcuctHelper.addTab(3 + identToAdd) + constrcuctHelper.beginSteps() + constrcuctHelper.addCRLF());
		
		
		// ajout d'un script if deployBack ou deployConf dans le cas du Dashboard
		if (script instanceof Dashboard) {
			// script
			writer.write(constrcuctHelper.addTab(4 + identToAdd) + constrcuctHelper.beginScript() + constrcuctHelper.addCRLF());
			
			if ( ((Dashboard) script).isProjectIsBack(project) )
				writer.write(constrcuctHelper.addTab(5 + identToAdd) + constrcuctHelper.beginIfBackDeploy() + constrcuctHelper.addCRLF());
			else if ( ((Dashboard) script).isProjectIsFront(project) )
				writer.write(constrcuctHelper.addTab(5 + identToAdd) + constrcuctHelper.beginIfFrontDeploy() + constrcuctHelper.addCRLF());
			else
				throw new IllegalArgumentException("TECH : vérifier en débug que l'objet Dashboard est bien alimenté");
			
			identToAdd += 2;
		}
		
		
		// mkdir - dir gitRoot
		writer.write(constrcuctHelper.addTab(4 + identToAdd) + constrcuctHelper.mkdirGitRoot() + constrcuctHelper.addCRLF());
		writer.write(constrcuctHelper.addTab(4 + identToAdd) + constrcuctHelper.beginDirGitRoot() + constrcuctHelper.addCRLF());
		
		// clean du dossier sur le primary remote
		if (script instanceof BuildOne)
			writer.write(constrcuctHelper.addTab(5 + identToAdd) + constrcuctHelper.cleanProjectPrimaryRemote() + constrcuctHelper.addCRLF());
		
		// runBuild
		writer.write(constrcuctHelper.addTab(5 + identToAdd) + constrcuctHelper.runBuild() + constrcuctHelper.addCRLF());
		
		// deploy app
		writer.write(constrcuctHelper.addTab(5 + identToAdd) + constrcuctHelper.addCRLF());
		writer.write(constrcuctHelper.addTab(5 + identToAdd) + constrcuctHelper.mkdirRemoteAppTargetDirectory() + constrcuctHelper.addCRLF());
		for (int i = 0; i < nbAppDeploy; i++)
			writer.write(constrcuctHelper.addTab(5 + identToAdd) + constrcuctHelper.deployApp(i) + constrcuctHelper.addCRLF());
		
		// deploy conf
		writer.write(constrcuctHelper.addTab(5 + identToAdd) + constrcuctHelper.addCRLF());
		writer.write(constrcuctHelper.addTab(5 + identToAdd) + constrcuctHelper.mkdirRemoteConfTargetDirectory() + constrcuctHelper.addCRLF());
		for (int i = 0; i < nbConfDeploy; i++)
			writer.write(constrcuctHelper.addTab(5 + identToAdd) + constrcuctHelper.deployConf(i) + constrcuctHelper.addCRLF());
		
		// dir gitRoot - steps - stage
		writer.write(constrcuctHelper.addTab(4 + identToAdd) + constrcuctHelper.endDirGitRoot() + constrcuctHelper.addCRLF());
		
		
		if (script instanceof Dashboard) {
			identToAdd -= 2;
			
			if ( ((Dashboard) script).isProjectIsBack(project) )
				writer.write(constrcuctHelper.addTab(5 + identToAdd) + constrcuctHelper.endIfBackDeploy() + constrcuctHelper.addCRLF());
			else if ( ((Dashboard) script).isProjectIsFront(project) )
				writer.write(constrcuctHelper.addTab(5 + identToAdd) + constrcuctHelper.endIfFrontDeploy() + constrcuctHelper.addCRLF());
			
			// script
			writer.write(constrcuctHelper.addTab(4 + identToAdd) + constrcuctHelper.endScript() + constrcuctHelper.addCRLF());
		}
		
		
		writer.write(constrcuctHelper.addTab(3 + identToAdd) + constrcuctHelper.endSteps() + constrcuctHelper.addCRLF());
		writer.write(constrcuctHelper.addTab(2 + identToAdd) + constrcuctHelper.endStage() + constrcuctHelper.addCRLF());
	}
	
	private void addParallelStageForProject(Writer writer, WithProjectsStages script, int currentGroup, List<String> groupe) throws IOException {
		
		// stage - parallel
		writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.beginStage("build groupe " + currentGroup) + constrcuctHelper.addCRLF());
		writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.beginParallel() + constrcuctHelper.addCRLF());
		
		for (String project : groupe)
			addStageForProject(writer, script, project, true);
		
		// parallel - stage
		writer.write(constrcuctHelper.addTab(3) + constrcuctHelper.endParallel() + constrcuctHelper.addCRLF());
		writer.write(constrcuctHelper.addTab(2) + constrcuctHelper.endStage() + constrcuctHelper.addCRLF());
	}
	
}
