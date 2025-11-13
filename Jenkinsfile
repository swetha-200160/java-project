pipeline {
    agent any

    environment {
        SONARQUBE_SERVER = 'SonarQube'       // Name from "Manage Jenkins → Configure System → SonarQube servers"
        SONAR_PROJECT_KEY = 'my-java-project' // Your SonarQube project key
        GIT_CREDENTIALS = 'gitrepo'           // Jenkins Git credentials ID
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
                bat 'mvn clean package -DskipTests'
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

    post {
        success {
            echo "✅ SonarQube analysis completed successfully!"
        }
        failure {
            echo "❌ Build or SonarQube analysis failed!"
        }
    }
}
