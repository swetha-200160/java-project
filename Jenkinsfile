pipeline {
    agent any

    environment {
        SONARQUBE_SERVER = 'SonarQube'    // SonarQube server name from Manage Jenkins -> Configure System
        SONAR_PROJECT_KEY = 'data-project'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master',
                    credentialsId: 'gitrepo',
                    url: 'https://github.com/swetha-200160/java-project.git'
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean verify'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${SONARQUBE_SERVER}") {
                    // If you store a Sonar token in Jenkins as a secret text you can add -Dsonar.login=... here
                    bat "mvn sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY}"
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 2, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }
}
