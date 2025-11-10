pipeline {
  agent any
  environment {
    BUILD_OUTPUT = "C:\\Users\\swethasuresh\\build"
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Verify env') {
      steps {
        bat 'echo Running as: & whoami || echo whoami not available'
        bat 'java -version || echo "java not found"'
        bat 'mvn -v || echo "mvn not found or not on PATH"'
        bat 'dir "%WORKSPACE%\\pom.xml" /B || echo "pom.xml not found in workspace root"'
      }
    }

    stage('Build (fail fast)') {
      steps {
        // run mvn and *fail pipeline* if exitcode != 0, capture log
        bat '''
          pushd "%WORKSPACE%"
          echo Running mvn in %CD%
          "%MAVEN_HOME%\\bin\\mvn" -v > mvn-tool-check.log 2>&1
          rem attempt plain mvn if MAVEN_HOME not set
          if exist "%MAVEN_HOME%\\bin\\mvn" (
            "%MAVEN_HOME%\\bin\\mvn" -B clean package -DskipTests > mvn-build.log 2>&1
            set RC=%ERRORLEVEL%
          ) else (
            mvn -B clean package -DskipTests > mvn-build.log 2>&1
            set RC=%ERRORLEVEL%
          )
          echo Maven exit code: %RC%
          if %RC% NEQ 0 (
            echo "Maven build FAILED, printing last 200 lines of log"
            more +0 mvn-build.log | tail -n 200 || type mvn-build.log
            exit /b %RC%
          )
          echo "Maven build SUCCEEDED"
          popd
        '''
      }
    }

    stage('Show build outputs (debug)') {
      steps {
        bat '''
          echo ==== workspace top-level ====
          dir "%WORKSPACE%\\*" /B
          echo ==== target listing (if exists) ====
          if exist "%WORKSPACE%\\target" (
            dir "%WORKSPACE%\\target\\*" /S /B
          ) else (
            echo "No target folder found"
          )
          echo ==== tail of mvn-build.log (last 200 lines) ====
          if exist "%WORKSPACE%\\mvn-build.log" (
            more +0 "%WORKSPACE%\\mvn-build.log" | findstr /N "^" || type "%WORKSPACE%\\mvn-build.log"
          ) else (
            echo "mvn-build.log not present"
          )
        '''
      }
    }

    stage('Archive artifacts') {
      steps {
        archiveArtifacts artifacts: 'target/**/*.jar, target/**/*.war', fingerprint: true, onlyIfSuccessful: true
      }
    }

    stage('Test & create BUILD_OUTPUT') {
      steps {
        bat """
          echo Agent write test to BUILD_OUTPUT...
          if not exist "${BUILD_OUTPUT}" mkdir "${BUILD_OUTPUT}" || echo "mkdir returned %ERRORLEVEL%"
          echo TEST > "${BUILD_OUTPUT}\\\\jenkins-write-test.txt" || echo "write failed %ERRORLEVEL%"
          dir "${BUILD_OUTPUT}" /B
        """
      }
    }

    stage('Copy workspace -> BUILD_OUTPUT') {
      steps {
        bat """
          robocopy "%WORKSPACE%" "${BUILD_OUTPUT}" /E /COPY:DAT /R:2 /W:2 /XD ".git" ".git\\\\"
          echo Workspace robocopy exit code: %ERRORLEVEL%
          if %ERRORLEVEL% GTR 7 exit /b %ERRORLEVEL%
        """
      }
    }

    stage('Copy target artifacts -> BUILD_OUTPUT\\\\artifacts') {
      steps {
        bat """
          if not exist "${BUILD_OUTPUT}\\\\artifacts" mkdir "${BUILD_OUTPUT}\\\\artifacts"
          if exist "%WORKSPACE%\\target" (
            robocopy "%WORKSPACE%\\target" "${BUILD_OUTPUT}\\\\artifacts" *.jar *.war *.zip /E /COPY:DAT /R:2 /W:2
            echo Artifacts robocopy exit code: %ERRORLEVEL%
            if %ERRORLEVEL% GTR 7 exit /b %ERRORLEVEL%
          ) else (
            echo "No target found; nothing to copy"
          )
        """
      }
    }
  }

  post {
    success { echo "Pipeline SUCCEEDED — check ${env.BUILD_OUTPUT}" }
    failure { echo "Pipeline FAILED — inspect the console output" }
  }
}
