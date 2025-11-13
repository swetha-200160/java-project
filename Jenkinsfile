pipeline {
  agent { label 'windows' }    // use a Windows labeled agent

  tools {
    maven 'maven'                 // configure this in Jenkins global tools (name = M3)
    jdk 'AdoptOpenJDK-11'      // or your JDK tool name
  }

  environment {
    SONARQUBE_INSTALLATION = 'MySonarQube'   // SonarQube server name from Manage Jenkins -> Configure System
    SONAR_TOKEN_CREDENTIAL = 'sonarqube'   // Jenkins credential ID (Secret Text) that stores Sonar token
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Clean & Build') {
      steps {
        // Use double quotes to expand environment variables in bat
        bat "mvn -B -DskipTests clean package"
      }
    }

    stage('Run Unit Tests') {
      steps {
        bat "mvn -B test"
      }
      post {
        always {
          junit '**\\target\\surefire-reports\\*.xml'
        }
      }
    }

    stage('SonarQube Analysis') {
      environment {
        SONAR_AUTH_TOKEN = credentials(SONAR_TOKEN_CREDENTIAL)
      }
      steps {
        // withSonarQubeEnv will set SONAR_HOST_URL and other env vars
        withSonarQubeEnv(SONARQUBE_INSTALLATION) {
          // Use mvn sonar:sonar and pass the token via -Dsonar.login
          bat "mvn -B sonar:sonar -Dsonar.login=%SONAR_AUTH_TOKEN%"
        }
      }
    }

    stage('Quality Gate') {
      steps {
        // requires Sonar webhook configured to Jenkins (sonarqube-webhook) and 'Pipeline: SonarQube' plugin
        timeout(time: 5, unit: 'MINUTES') {
          waitForQualityGate abortPipeline: true
        }
      }
    }
  }

  post {
    success { echo "Build + SonarQube quality gate passed." }
    failure { echo "Pipeline failed (or quality gate failed)." }
  }
}
