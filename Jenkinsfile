pipeline {
  agent any

  environment {
    BUILD_OUTPUT = "C:\\Users\\swethasuresh\\build"
    MAVEN_TOOL = 'Maven3' // name your Maven install in Jenkins Global Tool Config
  }

  stages {
    stage('Checkout') {
      steps {
        checkout([
          $class: 'GitSCM',
          branches: [[name: '*/master']],
          userRemoteConfigs: [[
            url: 'https://github.com/swetha-200160/java-project.git',
            credentialsId: 'gitrepo'
          ]]
        ])
      }
    }

    stage('Prepare') {
      steps {
        script {
          // resolve MAVEN_HOME via Jenkins tool
          def mvnHome = tool env.MAVEN_TOOL
          env.MVN_HOME = mvnHome
          echo "Using Maven: ${env.MVN_HOME}"
        }
        bat 'java -version || echo "java not found"'
        bat '"%MVN_HOME%\\bin\\mvn" -v || echo "mvn not found or failed"'
      }
    }

    stage('Build') {
      steps {
        script {
          // Use pushd/popd to handle spaces in paths reliably
          bat """
            pushd "%WORKSPACE%"
            echo Running mvn in: %CD%
            "%MVN_HOME%\\bin\\mvn" -B clean package -DskipTests > mvn-build.log 2>&1
            type mvn-build.log | findstr /R /C:"Building .*jar:" /C:"BUILD SUCCESS" || echo "mvn messages not found in log"
            popd
          """
        }
      }
    }

    stage('Show build output') {
      steps {
        bat """
          echo ==== workspace listing (top) ====
          dir "%WORKSPACE%\\*" /B
          echo ==== target listing if exists ====
          if exist "%WORKSPACE%\\target" (
            dir "%WORKSPACE%\\target\\*" /S /B
          ) else (
            echo "No target folder found"
          )
          echo ==== tail of mvn-build.log ====
          if exist "%WORKSPACE%\\mvn-build.log" (
            more +0 "%WORKSPACE%\\mvn-build.log" | findstr /R /C:"BUILD SUCCESS" /C:"BUILD FAILURE" /C:"ERROR"
          ) else (
            echo "mvn-build.log not found"
          )
        """
      }
    }

    stage('Archive artifacts') {
      steps {
        archiveArtifacts artifacts: 'target/**/*.jar, target/**/*.war', fingerprint: true, onlyIfSuccessful: true
      }
    }

    stage('Copy workspace and artifacts') {
      steps {
        script {
          bat """
            REM ensure destination exists
            if not exist "${BUILD_OUTPUT}" mkdir "${BUILD_OUTPUT}"

            REM copy entire workspace (except .git) safely
            robocopy "%WORKSPACE%" "${BUILD_OUTPUT}" /E /COPY:DAT /R:2 /W:2 /XD ".git" ".git\\"

            REM copy only artifacts (jars/wars) into BUILD_OUTPUT\\artifacts
            if not exist "${BUILD_OUTPUT}\\\\artifacts" mkdir "${BUILD_OUTPUT}\\\\artifacts"
            if exist "%WORKSPACE%\\target" (
              robocopy "%WORKSPACE%\\target" "${BUILD_OUTPUT}\\\\artifacts" *.jar *.war *.zip /E /COPY:DAT /R:2 /W:2
            )

            REM print robocopy exitcodes
            echo Robocopy exit code: %ERRORLEVEL%
          """
        }
      }
    }
  }

  post {
    success { echo 'Pipeline succeeded — check BUILD_OUTPUT for copied files.' }
    failure { echo 'Pipeline failed — see console for errors.' }
  }
}
