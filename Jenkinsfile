pipeline {
    agent any

    tools {
        maven 'Maven 3.9.9'    // or whatever Maven name you configured in Jenkins
        jdk 'jdk17'            // make sure JDK 17 is configured in Jenkins
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    credentialsId: 'gitrepo',
                    url: 'https://github.com/swetha-200160/data.git'
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean verify'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    bat 'mvn sonar:sonar -Dsonar.projectKey=data-project'
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
