pipeline {
  agent any

  environment {
    SONARQUBE_SERVER  = 'SonarQube'        // SonarQube server name as configured in Jenkins
    SONAR_PROJECT_KEY = 'my-java-project'  // change to your Sonar project key
    SONAR_TOKEN_ID    = 'sonarqube'        // Jenkins credential ID (Secret Text) that stores Sonar token
  }

  stages {
    stage('Checkout') {
      steps {
        // change branch if your repo uses 'master' instead of 'main'
        git branch: 'main', url: 'https://github.com/swetha-200160/java-project.git'
      }
    }

    stage('Build') {
      steps {
        // assumes mvn is on agent PATH; change to 'mvn -B -DskipTests clean package' if you prefer packaging
        bat 'mvn -B -DskipTests clean verify'
      }
    }

    stage('SonarQube Analysis') {
      environment {
        // bind the secret text credential into SONAR_AUTH_TOKEN
        SONAR_AUTH_TOKEN = credentials('sonarqube')
      }
      steps {
        withSonarQubeEnv("${SONARQUBE_SERVER}") {
          // Use %VAR% for runtime env resolution inside Windows bat
          bat "mvn -B sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY} -Dsonar.login=%SONAR_AUTH_TOKEN%"
        }
      }
    }

    stage('Quality Gate') {
      steps {
        // requires Sonar webhook configured so Jenkins receives the result
        timeout(time: 5, unit: 'MINUTES') {
          waitForQualityGate abortPipeline: true
        }
      }
    }
  }

  post {
    success { echo '✅ Build + SonarQube Quality Gate passed.' }
    failure { echo '❌ Pipeline failed or Quality Gate failed.' }
  }
}
