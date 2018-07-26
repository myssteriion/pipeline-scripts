pipeline {
	agent any
	environment {
		pipelineToken = "760429"
		jenkinsUrl = "http://192.168.25.69:8080"
		jobName = "GIT_eSirius_build_all"
	}
	stages {
		stage ("runner") {
			steps {
				sh "wget \"${env.jenkinsUrl}/view/GIT/job/${env.jobName}/buildWithParameters?token=${env.pipelineToken}&revision=master&mavenProfile=prod_windows\""
				sh "wget \"${env.jenkinsUrl}/view/GIT/job/${env.jobName}/buildWithParameters?token=${env.pipelineToken}&revision=master&mavenProfile=prod_linux\""
				sh "wget \"${env.jenkinsUrl}/view/GIT/job/${env.jobName}/buildWithParameters?token=${env.pipelineToken}&revision=develop&mavenProfile=prod_windows\""
				sh "wget \"${env.jenkinsUrl}/view/GIT/job/${env.jobName}/buildWithParameters?token=${env.pipelineToken}&revision=develop&mavenProfile=prod_linux\""
			}
		}
	}
}