pipeline {
  agent any

  environment {
    // change these to match your Jenkins/Sonar config if different
    SONARQUBE_SERVER   = 'SonarQube'    // name configured in Manage Jenkins -> Configure System
    SONAR_PROJECT_KEY  = 'my-java-project'
    SONAR_TOKEN_ID     = 'sonarqube'    // Jenkins Secret Text credential id (Sonar token)
    GIT_REPO_URL       = 'https://github.com/swetha-200160/java-project.git'
    GIT_BRANCH         = 'master'       // change to 'main' if your repo uses main
  }

  stages {
    stage('Checkout') {
      steps {
        // if repo private, add credentialsId: 'gitrepo' to git() call
        git branch: "${GIT_BRANCH}", url: "${GIT_REPO_URL}"
      }
    }

    stage('Agent / Tools check') {
      steps {
        echo "Workspace: ${env.WORKSPACE}"
        // show java/maven on agent to help debugging if they are missing
        bat 'java -version || echo "java_not_found"'
        bat 'mvn -v || echo "mvn_not_found"'
      }
    }

    stage('Find POM and Build') {
      steps {
        // find the first pom.xml and run mvn in that folder (handles spaces)
        bat '''
@echo off
setlocal enabledelayedexpansion

set "POM_FILE="
for /f "delims=" %%F in ('dir /s /b pom.xml 2^>nul') do (
  set "POM_FILE=%%~fF"
  goto :FOUND
)

echo ERROR: pom.xml not found in workspace
endlocal
exit /b 1

:FOUND
set "POM_DIR=%%~dpF"
set "POM_DIR=!POM_DIR:~0,-1!"
echo Found pom: "!POM_FILE!"
echo Found dir: "!POM_DIR!"
cd /d "!POM_DIR!"
echo Running mvn in "%CD%"
mvn -B -DskipTests clean package
endlocal
'''
      }
      post {
        always {
          // collect any test reports produced by the module
          junit '**\\target\\surefire-reports\\*.xml'
        }
      }
    }

    stage('SonarQube Analysis') {
      environment {
        // bind Sonar token (Secret Text). Must be a literal id.
        SONAR_AUTH_TOKEN = credentials("${SONAR_TOKEN_ID}")
      }
      steps {
        // run sonar from the same module folder (find POM dir again and execute)
        bat '''
@echo off
setlocal enabledelayedexpansion

set "POM_DIR="
for /f "delims=" %%F in ('dir /s /b pom.xml 2^>nul') do (
  set "POM_DIR=%%~dpF"
  goto :FOUND_POM
)

echo ERROR: pom.xml not found
endlocal
exit /b 1

:FOUND_POM
set "POM_DIR=%POM_DIR:~0,-1%"
cd /d "%POM_DIR%"
echo Running Sonar in "%CD%"
mvn -B sonar:sonar -Dsonar.projectKey=%SONAR_PROJECT_KEY% -Dsonar.login=%SONAR_AUTH_TOKEN%
endlocal
'''
      }
    }

    stage('Quality Gate') {
      steps {
        // requires Sonar webhook configured to point at Jenkins /sonarqube-webhook/
        timeout(time: 5, unit: 'MINUTES') {
          waitForQualityGate abortPipeline: true
        }
      }
    }
  }

  post {
    success { echo '✅ Pipeline finished and SonarQube Quality Gate passed.' }
    failure { echo '❌ Pipeline failed — check console output for details.' }
  }
}
