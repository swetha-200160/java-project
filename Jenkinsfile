pipeline {
  agent any

  environment {
    SONARQUBE_SERVER      = 'SonarQube'      // SonarQube server name from Manage Jenkins -> Configure System
    SONAR_PROJECT_KEY     = 'data-project'   // change to your project key
    SONAR_TOKEN_CREDENTIAL= 'sonarqube'      // Jenkins Secret Text credential ID containing Sonar token
    GIT_CREDENTIALS_ID    = 'gitrepo'        // your Git credentials ID
  }

  stages {
    stage('Checkout') {
      steps {
        script {
          // try common branches: master then main
          def branchToUse = 'master'
          try {
            checkout([$class: 'GitSCM',
              branches: [[name: "refs/heads/${branchToUse}"]],
              userRemoteConfigs: [[url: 'https://github.com/swetha-200160/java-project.git', credentialsId: env.GIT_CREDENTIALS_ID]]
            ])
          } catch(err) {
            echo "master branch not found or checkout failed, trying main..."
            branchToUse = 'main'
            checkout([$class: 'GitSCM',
              branches: [[name: "refs/heads/${branchToUse}"]],
              userRemoteConfigs: [[url: 'https://github.com/swetha-200160/java-project.git', credentialsId: env.GIT_CREDENTIALS_ID]]
            ])
          }
          echo "Checked out branch: ${branchToUse}"
        }
      }
    }

    stage('Inspect workspace') {
      steps {
        echo "Listing top-level files:"
        bat 'dir /b'
        bat 'if exist pom.xml (echo POM_FOUND) else (echo POM_NOT_FOUND)'
        bat 'mvn -v || echo MVN_NOT_FOUND'
        bat 'java -version || echo JAVA_NOT_FOUND'
      }
    }

    stage('Build') {
      steps {
        script {
          // if pom not in root, adjust path here (e.g. 'cd subdir && mvn ...')
          bat 'mvn -B -DskipTests clean package'
        }
      }
      post {
        always {
          junit '**\\target\\surefire-reports\\*.xml'
        }
      }
    }

    stage('SonarQube Analysis') {
      environment {
        SONAR_AUTH_TOKEN = credentials(sonarqube)
      }
      steps {
        withSonarQubeEnv(env.SONARQUBE_SERVER) {
          bat """
            mvn -B sonar:sonar ^
              -Dsonar.projectKey=%data_PROJECT_KEY% ^
              -Dsonar.host.url=%http://localhost:9000/% ^
              -Dsonar.login=%sonarqube%
          """
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
    success { echo "✅ Pipeline succeeded and Sonar Quality Gate passed." }
    failure { echo "❌ Pipeline failed — check console & Sonar project for details." }
  }
}
