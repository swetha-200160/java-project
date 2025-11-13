pipeline {
  agent any

  environment {
    // keep these as literals (strings)
    SONARQUBE_SERVER       = 'SonarQube'        // Name in Manage Jenkins -> Configure System -> SonarQube servers
    SONAR_PROJECT_KEY      = 'data-project'     // your Sonar project key
    GIT_CREDENTIALS_ID     = 'gitrepo'          // your Git credentials ID
    // bind the secret text into SONAR_AUTH_TOKEN using a literal credential id
    SONAR_AUTH_TOKEN       = credentials('sonarqube')
  }

  stages {
    stage('Checkout') {
      steps {
        script {
          // try master, then main
          def branchToUse = 'master'
          try {
            checkout([$class: 'GitSCM',
              branches: [[name: "refs/heads/${branchToUse}"]],
              userRemoteConfigs: [[url: 'https://github.com/swetha-200160/java-project.git', credentialsId: env.GIT_CREDENTIALS_ID]]
            ])
          } catch (err) {
            echo "master not found or checkout failed, trying main..."
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
          // adjust if your pom is in a subdirectory: bat 'cd subdir && mvn -B -DskipTests clean package'
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
      steps {
        // withSonarQubeEnv injects SONAR_HOST_URL or SONAR_HOST_URL (var name varies by plugin version)
        withSonarQubeEnv(env.SONARQUBE_SERVER) {
          // Use Groovy expansion for project key, but use Windows %VAR% for runtime-injected env vars inside bat
          bat """
            mvn -B sonar:sonar ^
              -Dsonar.projectKey=${env.data_project} ^
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
