pipeline {
  agent any

  environment {
    GIT_CREDENTIALS = 'gitrepo'
    SONARQUBE_SERVER = 'SonarQube'
    SONAR_PROJECT_KEY = 'my-java-project'
    SONAR_AUTH_TOKEN = credentials('sonarqube-token')
    MODULE_DIR = 'jp 1'   // change if different
  }

  stages {
    stage('Checkout') {
      steps {
        git branch: 'main', credentialsId: "${GIT_CREDENTIALS}", url: 'https://github.com/swetha-200160/java-project.git'
      }
    }

    stage('Build') {
      steps {
        dir("${MODULE_DIR}") {
          // remove -DskipTests if you want tests to run and produce reports
          bat 'mvn -B -DskipTests clean package'
        }
      }
      post {
        always {
          // Collect test reports from the module (works with spaces in name)
          junit "${MODULE_DIR}\\**\\target\\surefire-reports\\*.xml"
        }
      }
    }

    stage('SonarQube Analysis') {
      steps {
        dir("${MODULE_DIR}") {
          withSonarQubeEnv("${SONARQUBE_SERVER}") {
            bat "mvn -B sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY} -Dsonar.login=%SONAR_AUTH_TOKEN%"
          }
        }
      }
    }

    stage('Quality Gate') {
      steps {
        timeout(time:5, unit:'MINUTES') {
          waitForQualityGate abortPipeline: true
        }
      }
    }
  }
}
