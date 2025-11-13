pipeline {
    agent any

    environment {
        SONARQUBE_SERVER = 'SonarQube'
        SONAR_PROJECT_KEY = 'data-project'
    }

    stages {
        stage('Checkout') {
            steps {
                // Use the correct .git URL (fixed .gitt -> .git)
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
