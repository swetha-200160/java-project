pipeline {
  agent any

  environment {
    SONARQUBE_SERVER  = 'SonarQube'      // name in Manage Jenkins -> Configure System
    SONAR_PROJECT_KEY = 'my-java-project'
    SONAR_CRED_ID     = 'sonarqube'      // Jenkins Secret Text credential id
    GIT_REPO_URL      = 'https://github.com/swetha-200160/java-project.git'
    GIT_BRANCH        = 'master'
    DEBUG_LOG         = 'mvn-debug.txt'
  }

  stages {
    stage('Checkout') {
      steps {
        // Pull code from GitHub
        git branch: "${GIT_BRANCH}", url: "${GIT_REPO_URL}"
      }
    }

    stage('Agent tools / quick check') {
      steps {
        // Print Java & Maven version (helps debug missing tools)
        echo "Workspace: ${env.WORKSPACE}"
        bat 'java -version || echo java_not_found'
        bat 'mvn -v || echo maven_not_found'
      }
    }

    stage('Build (find POM, run mvn, capture debug on failure)') {
      steps {
        script {
          // Try a normal Maven build first
          def rc = bat(returnStatus: true, script: '''
@echo off
for /f "delims=" %%F in ('dir /s /b pom.xml 2^>nul') do (
  set "POM_DIR=%%~dpF"
  goto :FOUND
)
echo ERROR: pom.xml not found
exit /b 2

:FOUND
cd /d "%POM_DIR%"
echo Building in "%CD%"
mvn -B -DskipTests clean package
''')

          if (rc != 0) {
            // If build fails, rerun in debug (-X) mode and archive output
            bat """
@echo off
cd /d "%POM_DIR%"
mvn -X clean package > "${env.WORKSPACE}\\${env.DEBUG_LOG}" 2>&1
"""
            archiveArtifacts artifacts: "${env.DEBUG_LOG}", allowEmptyArchive: false
            error("❌ Maven build failed. Debug log archived: ${env.DEBUG_LOG}")
          }
        }
      }
      post {
        always {
          // Publish test reports if available
          junit testResults: '**\\target\\surefire-reports\\*.xml', allowEmptyResults: true
        }
      }
    }

    stage('SonarQube analysis') {
      steps {
        // Run static analysis with SonarQube
        withCredentials([string(credentialsId: "${SONAR_CRED_ID}", variable: 'SONAR_TOKEN')]) {
          bat """
@echo off
for /f "delims=" %%F in ('dir /s /b pom.xml 2^>nul') do (
  set "POM_DIR=%%~dpF"
  goto :FOUND
)
echo ERROR: pom.xml not found
exit /b 2

:FOUND
cd /d "%POM_DIR%"
mvn -B sonar:sonar -Dsonar.projectKey=%SONAR_PROJECT_KEY% -Dsonar.login=%SONAR_TOKEN%
"""
        }
      }
    }

    stage('Quality Gate') {
      steps {
        // Wait up to 5 mins for SonarQube Quality Gate result
        timeout(time: 5, unit: 'MINUTES') {
          waitForQualityGate abortPipeline: true
        }
      }
    }
  }

  post {
    success { echo '✅ Pipeline succeeded and SonarQube Quality Gate passed.' }
    failure { echo '❌ Pipeline failed. Check console and mvn-debug.txt for details.' }
    always {
      // Save the debug log (if exists)
      archiveArtifacts artifacts: "${env.DEBUG_LOG}", allowEmptyArchive: true
    }
  }
}
