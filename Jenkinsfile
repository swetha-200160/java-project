pipeline {
  agent any

  environment {
    SONARQUBE_SERVER  = 'SonarQube'        // Must match the name in Manage Jenkins -> Configure System
    SONAR_PROJECT_KEY = 'my-java-project'  // change to your Sonar project key
    SONAR_TOKEN_ID    = 'sonarqube'        // Secret Text credential id in Jenkins
    GIT_REPO_URL      = 'https://github.com/swetha-200160/java-project.git'
    GIT_BRANCH        = 'master'           // change to 'main' if your repo uses main
  }

  stages {
    stage('Checkout') {
      steps {
        // Use credentialsId if your repo is private (set credentials in Jenkins)
        git branch: "master", url: "https://github.com/swetha-200160/java-project.git"
      }
    }

    stage('Show environment') {
      steps {
        // diagnostics to ensure agent has correct java/mvn
        bat 'echo WORKSPACE=%WORKSPACE%'
        bat 'java -version'
        bat 'mvn -v'
      }
    }

    stage('Build') {
      steps {
        // run maven build (remove -DskipTests if you want tests to run)
        bat 'mvn -B -DskipTests clean package'
      }
      post {
        always {
          // collect test reports (will be no files if tests were skipped)
          junit '**\\target\\surefire-reports\\*.xml'
        }
      }
    }

    stage('SonarQube Analysis') {
      environment {
        // bind secret text into SONAR_AUTH_TOKEN (literal id required)
        SONAR_AUTH_TOKEN = credentials("sonarqube")
      }
      steps {
        withSonarQubeEnv("${SONARQUBE_SERVER}") {
          // use %SONAR_AUTH_TOKEN% inside bat so agent resolves the secret at runtime
          bat "mvn -B sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY} -Dsonar.login=%SONAR_AUTH_TOKEN%"
        }
      }
    }

    stage('Quality Gate') {
      steps {
        // requires Sonar webhook configured to http(s)://<jenkins-host>/sonarqube-webhook/
        timeout(time: 5, unit: 'MINUTES') {
          waitForQualityGate abortPipeline: true
        }
      }
    }
  }

  post {
    success { echo '✅ Pipeline succeeded and SonarQube Quality Gate passed.' }
    failure { echo '❌ Pipeline failed — check console output for details.' }
  }
}
