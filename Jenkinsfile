pipeline {
  agent any

  environment {
    SONARQUBE_SERVER  = 'SonarQube'      // name in Manage Jenkins -> Configure System
    SONAR_PROJECT_KEY = 'my-java-project'
    SONAR_CRED_ID     = 'sonarqube'      // Secret Text credential id (Sonar token)
    GIT_REPO_URL      = 'https://github.com/swetha-200160/java-project.git'
    GIT_BRANCH        = 'master'
    DEBUG_LOG         = 'mvn-debug.txt'
  }

  stages {
    stage('Checkout') {
      steps { git branch: "${GIT_BRANCH}", url: "${GIT_REPO_URL}" }
    }

    stage('Agent check') {
      steps {
        bat 'java -version || echo java_not_found'
        bat 'mvn -v || echo maven_not_found'
      }
    }

    stage('Find POM & Build') {
      steps {
        script {
          // find first pom and run build (same as before)
          def rc = bat(returnStatus: true, script: '''
@echo off
set "POM="
for /f "delims=" %%F in ('dir /s /b pom.xml 2^>nul') do (
  set "POM=%%~fF"
  goto FOUND
)
echo ERROR: pom.xml not found
exit /b 2
:FOUND
set "POM_DIR=%%~dpF"
set "POM_DIR=%POM_DIR:~0,-1%"
cd /d "%POM_DIR%"
mvn -B -DskipTests clean package
''')
          if (rc != 0) {
            // run debug and archive (same pattern)
            bat """
@echo off
cd /d "%POM_DIR%"
mvn -X clean package > "${env.WORKSPACE}\\${DEBUG_LOG}" 2>&1
"""
            archiveArtifacts artifacts: "${DEBUG_LOG}", allowEmptyArchive: false
            error("Maven build failed. Debug log archived.")
          }
        }
      }
      post { always { junit testResults: '**\\target\\surefire-reports\\*.xml', allowEmptyResults: true } }
    }

    stage('SonarQube Analysis') {
      steps {
        // **CRITICAL**: use withSonarQubeEnv so the Sonar Jenkins plugin registers the analysis
        withCredentials([string(credentialsId: "${SONAR_CRED_ID}", variable: 'SONAR_TOKEN')]) {
          withSonarQubeEnv("${SONARQUBE_SERVER}") {
            bat "mvn -B sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY} -Dsonar.login=%SONAR_TOKEN%"
          }
        }
      }
    }

    stage('Quality Gate') {
      steps {
        script {
          timeout(time: 10, unit: 'MINUTES') {
            // Now waitForQualityGate can correlate result to this build
            def qg = waitForQualityGate(abortPipeline: true)
            echo "Quality Gate status: ${qg.status}"
          }
        }
      }
    }
  }

  post {
    success { echo '✅ Pipeline succeeded and Quality Gate passed' }
    failure { echo '❌ Pipeline failed or Quality Gate failed' }
    always { archiveArtifacts artifacts: "${DEBUG_LOG}", allowEmptyArchive: true }
  }
}
