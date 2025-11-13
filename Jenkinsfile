pipeline {
  agent any

  environment {
    SONARQUBE_SERVER  = 'SonarQube'
    SONAR_PROJECT_KEY = 'my-java-project'
    SONAR_CRED_ID     = 'sonarqube'    // Secret Text id in Jenkins
    GIT_REPO_URL      = 'https://github.com/swetha-200160/java-project.git'
    GIT_BRANCH        = 'master'
    DEBUG_LOG         = 'mvn-debug.txt'
  }

  stages {
    stage('Checkout') {
      steps {
        echo "Checking out ${GIT_REPO_URL} (${GIT_BRANCH})"
        git branch: "${GIT_BRANCH}", url: "${GIT_REPO_URL}"
      }
    }

    stage('Agent check') {
      steps {
        echo "Workspace: ${env.WORKSPACE}"
        bat 'java -version || echo java_not_found'
        bat 'mvn -v || echo maven_not_found'
      }
    }

    stage('Find POM & Build') {
  steps {
    script {
      echo "Locate pom.xml and run mvn clean package (skip tests)"
      // run mvn, capture status
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
echo Found pom at %POM_DIR%/pom.xml
cd /d "%POM_DIR%"
mvn -B -DskipTests clean package
''')

      if (rc == 0) {
        echo "Maven build succeeded."
        // archive produced jar(s)
        archiveArtifacts artifacts: '**\\target\\*.jar', allowEmptyArchive: false
      } else {
        echo "Maven build FAILED (exit ${rc}). Running debug mvn -X and archiving ${DEBUG_LOG}..."
        // run debug and save to workspace root
        bat """
@echo off
set "POM="
for /f "delims=" %%F in ('dir /s /b pom.xml 2^>nul') do (
  set "POM=%%~fF"
  goto FOUND2
)
echo ERROR: pom.xml not found
exit /b 2
:FOUND2
set "POM_DIR=%%~dpF"
set "POM_DIR=%POM_DIR:~0,-1%"
cd /d "%POM_DIR%"
mvn -X clean package > "${env.WORKSPACE}\\${DEBUG_LOG}" 2>&1
"""
        archiveArtifacts artifacts: "${DEBUG_LOG}", allowEmptyArchive: false
        error("Maven build failed. Debug log archived: ${DEBUG_LOG}")
      }
    }
  }
  post {
    always {
      // collect test reports (won't fail if none)
      junit testResults: '**\\target\\surefire-reports\\*.xml', allowEmptyResults: true
    }
  }
}


    stage('SonarQube Analysis') {
      when {
        expression { return currentBuild.result == null || currentBuild.result == 'SUCCESS' }
      }
      steps {
        echo "Running Sonar (only if build succeeded)"
        withCredentials([string(credentialsId: "${SONAR_CRED_ID}", variable: 'SONAR_TOKEN')]) {
          bat """
@echo off
set "POM="
for /f "delims=" %%F in ('dir /s /b pom.xml 2^>nul') do (
  set "POM=%%~fF"
  goto FOUND_S
)
echo ERROR: pom.xml not found
exit /b 2
:FOUND_S
set "POM_DIR=%%~dpF"
set "POM_DIR=%POM_DIR:~0,-1%"
cd /d "%POM_DIR%"
mvn -B sonar:sonar -Dsonar.projectKey=%SONAR_PROJECT_KEY% -Dsonar.login=%SONAR_TOKEN%
"""
        }
      }
    }

    stage('Quality Gate') {
      steps {
        timeout(time: 5, unit: 'MINUTES') {
          waitForQualityGate abortPipeline: true
        }
      }
    }
  }

  post {
    success { echo '✅ Pipeline succeeded.' }
    failure { echo '❌ Pipeline failed — check mvn-debug.txt (artifact) and console output.' }
    always { archiveArtifacts artifacts: "${DEBUG_LOG}", allowEmptyArchive: true }
  }
}
