pipeline {
    agent any
	parameters {
		string(name: "revision", defaultValue: "master", description: "branche ou TAG")
	}
	environment  {
		gitLabUrl = "http://192.168.25.68/eSirius/"
	}
    tools { 
        maven "Maven 3.0.4" 
        jdk "JDK 1.6.0_20" 
    }
    stages {
    	stage ("Initialize") {
            steps {
				sh "java -version"
				sh "mvn -v"
				wrap([$class: 'AnsiColorBuildWrapper']) {
					echo "\u001B[34m gitlab url : ${env.gitLabUrl} \u001B[m"
					echo "\u001B[34m revision : ${params.revision} \u001B[m"
				}
            }
        }
		stage("build 1/6") {
			parallel {
				stage("build es2i") {
					environment  {
						repo = "es2i"
						subFolder = "es2i"
					}
					steps {
						runBuild(env.repo, env.subFolder)
					}
				}
				stage("build eCustomer") {
					environment  {
						repo = "eCustomer"
						subFolder = "eCustomer"
					}
					steps {
						runBuild(env.repo, env.subFolder)
					}
				}
				stage("build smsSender") {
					environment  {
						repo = "smsSender"
						subFolder = "smsSender"
					}
					steps {
						runBuild(env.repo, env.subFolder)
					}
				}
				stage("build procyon") {
					environment  {
						repo = "eStat"
						subFolder = "procyon"
					}
					steps {
						runBuild(env.repo, env.subFolder)
					}
				}
			}
		}
			
		stage("build eSecurity") {
			environment  {
				repo = "eSecurity"
				subFolder = "esecurity"
			}
			steps {
				runBuild(env.repo, env.subFolder)
			}
		}
		
		stage("build 3/6") {
			parallel {
				stage("build eSirius-shared") {
					environment  {
						repo = "eSirius"
						subFolder = "eSirius-shared"
					}
					steps {
						runBuild(env.repo, env.subFolder)
					}
				}
				stage("build es2i-rsi-connector") {
					environment  {
						repo = "es2i-rsi-connector"
						subFolder = "es2i-rsi-connector"
					}
					steps {
						runBuild(env.repo, env.subFolder)
					}
				}
				stage("build eStat") {
					environment  {
						repo = "eStat"
						subFolder = "eSirius-estat"
					}
					steps {
						runBuild(env.repo, env.subFolder)
					}
				}
			}
		}
		
		stage("build 4/6") {
			parallel {
				stage("build ePlanning") {
					environment  {
						repo = "ePlanning"
						subFolder = "ePlanning"
					}
					steps {
						runBuild(env.repo, env.subFolder)
					}
				}
				stage("build History") {
					environment  {
						repo = "History"
						subFolder = "eSirius-history"
					}
					steps {
						runBuild(env.repo, env.subFolder)
					}
				}
			}
		}
	
		stage("build 5/6") {
			parallel {
				stage("build eSirius") {
					environment  {
						repo = "eSirius"
						subFolder = "eSirius"
					}
					steps {
						runBuild(env.repo, env.subFolder)
					}
				}
				stage("build Appointment") {
					environment  {
						repo = "Appointment"
						subFolder = "appointment"
					}
					steps {
						runBuild(env.repo, env.subFolder)
					}
				}
			}
		}
		
		stage("build 6/6") {
			parallel {
				stage("build eAdmin") {
					environment  {
						repo = "eAdmin"
						subFolder = "eSirius-admin"
					}
					steps {
						runBuild(env.repo, env.subFolder)
					}
				}
				stage("build eVision") {
					environment  {
						repo = "eVision"
						subFolder = "eSirius-vision"
					}
					steps {
						runBuild(env.repo, env.subFolder)
					}
				}	
			}
		}

    	stage("clean workspace") {
            steps {
                cleanWs()
            }
        }
    }
}

void runBuild(String repo, String subFolder) {
	cleanWs()
	
	sh "mkdir ${subFolder}Dir"

	dir ("${subFolder}Dir") {
		checkoutRevisionOnRepo(repo)
		mvnCleanInstall(subFolder)
	}
}

void checkoutRevisionOnRepo(String repo) {
	script {
		try {
			checkout scm: [ $class: "GitSCM", 
							userRemoteConfigs: [ [url: env.gitLabUrl+repo, credentialsId: "root"] ],
							branches: [ [name: params.revision] ]
						  ], poll: false
		}
		catch(Exception e) {
			wrap([$class: 'AnsiColorBuildWrapper']) {
				echo "\u001B[31mLa branche ou le tag '${params.revision}' n'existe pas sur le repository '${repo}' => build de la branche 'master'\u001B[m"
			}
			checkout scm: [ $class: "GitSCM", 
							userRemoteConfigs: [ [url: env.gitLabUrl+repo, credentialsId: "root"] ],
							branches: [ [name: 'master'] ]
						  ], poll: false
		}
	}
}

void mvnCleanInstall(String subFolder) {
	withMaven {
		sh "mvn -f ./${subFolder}/pom.xml clean install"
	}
}