pipeline {
  agent any
  environment {
    BUILD_OUTPUT = "C:\\Users\\swethasuresh\\build"
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Verify mvn') {
      steps {
        bat 'mvn -v'
      }
    }

    stage('Build') {
      steps {
        bat """
          pushd "%WORKSPACE%"
          mvn -B clean package -DskipTests > mvn-build.log 2>&1
          type mvn-build.log | findstr /R /C:"BUILD SUCCESS" /C:"BUILD FAILURE" || echo "mvn messages not found"
          popd
        """
      }
    }

    // ... (archive & copy stages same as before)
  }

  post {
    success { echo 'Pipeline succeeded' }
    failure { echo 'Pipeline failed' }
  }
}
