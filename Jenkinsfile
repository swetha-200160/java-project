pipeline {
  agent any

  environment {
    SONARQUBE_SERVER  = 'SonarQube'         // the SonarQube server name in Jenkins Configure System
    SONAR_PROJECT_KEY = 'my-java-project'   // change to your Sonar project key
    GIT_CREDENTIALS   = 'gitrepo'           // your git credential id
  }

  stages {
    stage('Checkout') {
      steps {
        git branch: 'main', credentialsId: "${GIT_CREDENTIALS}", url: 'https://github.com/swetha-200160/java-project.git'
      }
    }

    stage('Build') {
      steps {
        // assumes mvn on agent PATH
        bat 'mvn -B -DskipTests clean package'
      }
      post { always { junit 'C:\\User\\swethasuresh\\data\\pom.xml'} }
    }

    stage('SonarQube Analysis') {
      environment {
        // bind the Sonar token (Secret Text) into SONAR_AUTH_TOKEN
        SONAR_AUTH_TOKEN = credentials('sonarqube-token')
      }
      steps {
        withSonarQubeEnv("${SONARQUBE_SERVER}") {
          // use %VAR% inside bat so the agent resolves it at runtime
          bat "mvn -B sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY} -Dsonar.login=%SONAR_AUTH_TOKEN%"
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
    failure { echo '❌ Pipeline failed or Quality Gate failed.' }
  }
}
