pipeline {
  agent any
  environment {
    BUILD_OUTPUT = "C:\\Users\\swethasuresh\\build"
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Verify mvn') {
      steps { bat 'mvn -v' }
    }

    stage('Build') {
      steps {
        bat '''
          pushd "%WORKSPACE%"
          mvn -B clean package -DskipTests > mvn-build.log 2>&1
          type mvn-build.log | findstr /R /C:"Building .*jar:" /C:"BUILD SUCCESS" || echo "mvn messages not found in log"
          popd
        '''
      }
    }

    stage('Show build outputs') {
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
          echo ==== tail of mvn-build.log (BUILD lines) ====
          if exist "%WORKSPACE%\\mvn-build.log" (
            more +0 "%WORKSPACE%\\mvn-build.log" | findstr /R /C:"BUILD SUCCESS" /C:"BUILD FAILURE" /C:"ERROR" || echo "no mvn build markers found"
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

    stage('Copy workspace -> BUILD_OUTPUT') {
      steps {
        script {
          bat """
            REM ensure destination exists
            if not exist "${BUILD_OUTPUT}" mkdir "${BUILD_OUTPUT}"

            REM copy entire workspace except .git
            robocopy "%WORKSPACE%" "${BUILD_OUTPUT}" /E /COPY:DAT /R:2 /W:2 /XD ".git" ".git\\" 

            REM print robocopy exit code for workspace copy
            set RC=%ERRORLEVEL%
            echo Robocopy (workspace) exit code: %RC%
            if %RC% GTR 7 (
              echo "Workspace robocopy returned error %RC%"
              exit /b %RC%
            )
          """
        }
      }
    }

    stage('Copy target artifacts -> BUILD_OUTPUT\\artifacts') {
      steps {
        script {
          bat """
            if not exist "${BUILD_OUTPUT}\\\\artifacts" mkdir "${BUILD_OUTPUT}\\\\artifacts"
            if exist "%WORKSPACE%\\target" (
              robocopy "%WORKSPACE%\\target" "${BUILD_OUTPUT}\\\\artifacts" *.jar *.war *.zip /E /COPY:DAT /R:2 /W:2
            ) else (
              echo "No target found to copy artifacts from."
            )
            set RC=%ERRORLEVEL%
            echo Robocopy (artifacts) exit code: %RC%
            if %RC% GTR 7 (
              echo "Artifacts robocopy returned error %RC%"
              exit /b %RC%
            )
          """
        }
      }
    }
  } // end stages

  post {
    success {
      echo "Pipeline succeeded — workspace and artifacts copied to ${env.BUILD_OUTPUT}"
    }
    failure {
      echo "Pipeline failed — check console output for robocopy/mvn errors"
    }
  }
}
