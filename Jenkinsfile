pipeline {
  agent any

  environment {
    SONARQUBE_SERVER  = 'SonarQube'
    SONAR_PROJECT_KEY = 'my-java-project'
    SONAR_TOKEN_ID    = 'sonarqube'  // change to your credential id
  }

  stages {
    stage('Checkout') {
      steps {
        git branch: 'main', url: 'https://github.com/swetha-200160/java-project.git'
      }
    }

    stage('Find module & Build (safe)') {
      steps {
        bat '''
@echo off
rem find the first pom.xml and determine its directory
set "FOUND_DIR="
for /f "delims=" %%F in ('dir /s /b pom.xml 2^>nul') do (
  set "FOUND=%%~fF"
  rem get directory of the found pom
  set "FOUND_DIR=%%~dpF"
  goto :FOUND_POM
)
echo ERROR: pom.xml not found in workspace
exit /b 1

:FOUND_POM
rem remove trailing backslash
set "FOUND_DIR=%FOUND_DIR:~0,-1%"
echo Found pom in "%FOUND_DIR%"
rem validate folder exists (defensive)
if not exist "%FOUND_DIR%" (
  echo ERROR: found directory does not exist: "%FOUND_DIR%"
  exit /b 1
)
cd /d "%FOUND_DIR%"
echo Running mvn in %CD%
mvn -B -DskipTests clean package
'''
      }
      post {
        always {
          junit '**\\target\\surefire-reports\\*.xml'
        }
      }
    }

    stage('Sonar') {
      steps {
        withCredentials([string(credentialsId: "${SONAR_TOKEN_ID}", variable: 'SONAR_AUTH_TOKEN')]) {
          bat '''
@echo off
for /f "delims=" %%F in ('dir /s /b pom.xml 2^>nul') do (
  set "FOUND_DIR=%%~dpF"
  goto :FOUND_POM
)
echo ERROR: pom.xml not found
exit /b 1

:FOUND_POM
set "FOUND_DIR=%FOUND_DIR:~0,-1%"
cd /d "%FOUND_DIR%"
echo Running Sonar in %CD%
mvn -B sonar:sonar -Dsonar.projectKey=%SONAR_PROJECT_KEY% -Dsonar.login=%SONAR_AUTH_TOKEN%
'''
        }
      }
    }
  }
}
