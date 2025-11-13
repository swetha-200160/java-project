pipeline {
  agent any

  environment {
    SONARQUBE_SERVER  = 'SonarQube'      // name in Manage Jenkins -> Configure System
    SONAR_PROJECT_KEY = 'my-java-project'
    SONAR_CRED_ID     = 'sonarqube'      // Secret Text credential id (Sonar token)
    GIT_REPO_URL      = 'https://github.com/swetha-200160/java-project.git'
    GIT_BRANCH        = 'master'         // change to main if needed
    DEBUG_LOG         = 'mvn-debug.txt'
  }

  stages {
    stage('Checkout') {
      steps {
        git branch: "${GIT_BRANCH}", url: "${GIT_REPO_URL}"
      }
    }

    stage('Agent tools / quick check') {
      steps {
        echo "Workspace: ${env.WORKSPACE}"
        bat 'java -version || echo java_not_found'
        bat 'mvn -v || echo maven_not_found'
      }
    }

    stage('Build (find POM, run mvn, capture debug on failure)') {
      steps {
        script {
          // Try regular build first; if it fails run debug (-X) and archive log.
          // Both attempts run in the same module directory (first found pom.xml).
          echo "Running mvn -B -DskipTests clean package in the module that contains pom.xml..."

          // Run normal build and capture exit code
          def rc = bat(
            returnStatus: true,
            script: '''
@echo off
rem find first pom.xml
set "POM="
for /f "delims=" %%F in ('dir /s /b pom.xml 2^>nul') do (
  set "POM=%%~fF"
  goto :FOUND
)
echo ERROR: pom.xml not found
exit /b 2

:FOUND
set "POM_DIR=%%~dpF"
set "POM_DIR=%POM_DIR:~0,-1%"
cd /d "%POM_DIR%"
echo Building in "%CD%"
mvn -B -DskipTests clean package
'''
          )

          if (rc == 0) {
            echo "Maven build succeeded."
          } else {
            echo "Maven build FAILED (exit ${rc}). Capturing full debug output with mvn -X ..."

            // Run debug build (mvn -X) and redirect output into DEBUG_LOG
            def rc2 = bat(
              returnStatus: true,
              script: """
@echo off
set "POM="
for /f "delims=" %%F in ('dir /s /b pom.xml 2^>nul') do (
  set "POM=%%~fF"
  goto :FOUND
)
echo ERROR: pom.xml not found
exit /b 2

:FOUND
set "POM_DIR=%%~dpF"
set "POM_DIR=%POM_DIR:~0,-1%"
cd /d "%POM_DIR%"
echo Running mvn -X in "%CD%" (writing debug to ${env.DEBUG_LOG})
mvn -X clean package > "${env.WORKSPACE}\\${env.DEBUG_LOG}" 2>&1
if %ERRORLEVEL% NEQ 0 ( exit /b 1 ) else ( exit /b 0 )
"""
            )

            // Archive the debug log for inspection
            archiveArtifacts artifacts: "${env.DEBUG_LOG}", allowEmptyArchive: false

            // Fail the pipeline with a helpful message and point to the archived debug log
            error("Maven build failed. Full debug log archived: ${env.DEBUG_LOG}")
          }
        }
      }
     stage('Build') {
  steps {
    bat 'mvn -B -DskipTests clean package'
  }
  post {
    always {
      // Collect test reports if any (won’t fail if none exist)
      junit allowEmptyResults: true, testResults: '**\\target\\surefire-reports\\*.xml'
    }
  }
}

        }
      }
    }

    stage('SonarQube analysis') {
      steps {
        // Bind Sonar token at runtime (safer than putting credential in environment)
        withCredentials([string(credentialsId: 'sonarqube', variable: 'sonar')]) {
          bat """
@echo off
rem find POM dir and run sonar:sonar from that module (handles spaces)
set "POM="
for /f "delims=" %%F in ('dir /s /b pom.xml 2^>nul') do (
  set "POM=%%~fF"
  goto :FOUND_S
)
echo ERROR: pom.xml not found
exit /b 2

:FOUND_S
set "POM_DIR=%%~dpF"
set "POM_DIR=%POM_DIR:~0,-1%"
cd /d "%POM_DIR%"
echo Running Sonar in "%CD%"
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
    success { echo '✅ Pipeline succeeded and SonarQube Quality Gate passed.' }
    failure {
      echo '❌ Pipeline failed. Check the console and archived debug log (mvn-debug.txt) for details.'
    }
    always {
      // keep the mvn-debug.txt if created
      archiveArtifacts artifacts: "${env.DEBUG_LOG}", allowEmptyArchive: true
    }
  }
}
