pipeline {
  agent any

  environment {
    BUILD_OUTPUT = "C:\\Users\\swethasuresh\\build"
  }

  stages {
    stage('Checkout') {
      steps {
        // Checkout repository to the workspace on the agent
        git branch: 'master',
            credentialsId: 'gitrepo',
            url: 'https://github.com/swetha-200160/java-project.git'
      }
    }

    stage('Build') {
      steps {
        echo 'Running mvn package to produce artifacts...'
        // Run mvn and keep console output in mvn-build.log for debugging
        bat '''
          mvn -B clean package -DskipTests > mvn-build.log 2>&1
          type mvn-build.log | findstr /R /C:"Building .*jar:" /C:"BUILD SUCCESS" || echo "mvn messages not found in log"
        '''
      }
    }

    stage('Copy: entire workspace -> BUILD_OUTPUT') {
      steps {
        script {
          echo "Copying workspace to ${env.BUILD_OUTPUT} (preserve folder structure)"
          bat """
            REM show current workspace and check content
            echo Workspace is: %WORKSPACE%
            dir "%WORKSPACE%\\*" /S /B | more

            REM ensure destination exists
            if not exist "${env.BUILD_OUTPUT}" mkdir "${env.BUILD_OUTPUT}"

            REM copy EVERYTHING from workspace to BUILD_OUTPUT except .git
            REM /E = copy subdirs, including empty ones; /COPY:DAT preserve data/attributes/timestamps
            robocopy "%WORKSPACE%" "${env.BUILD_OUTPUT}" /E /COPY:DAT /R:2 /W:2 /XD ".git" ".git\\"

            set RC=%ERRORLEVEL%
            echo Robocopy (workspace) exit code: %RC%
            if %RC% LEQ 7 (
              echo "Workspace copy ok (code %RC%)."
            ) else (
              echo "Workspace copy FAILED (code %RC%)."
              exit /b %RC%
            )
          """
        }
      }
    }

    stage('Copy: target artifacts -> BUILD_OUTPUT\\artifacts') {
      steps {
        script {
          echo "Copying built artifacts from target to ${env.BUILD_OUTPUT}\\artifacts"
          bat """
            REM ensure artifacts subfolder exists
            if not exist "${env.BUILD_OUTPUT}\\artifacts" mkdir "${env.BUILD_OUTPUT}\\artifacts"

            REM list target (debug)
            if exist "%WORKSPACE%\\target" (
              echo Listing target contents:
              dir "%WORKSPACE%\\target\\*" /S /B
              REM copy only jar/war/zip from target into artifacts folder (preserve subdirs)
              robocopy "%WORKSPACE%\\target" "${env.BUILD_OUTPUT}\\artifacts" *.jar *.war *.zip /E /COPY:DAT /R:2 /W:2
            ) else (
              echo "No target folder found; skipping artifact copy."
            )

            set RC=%ERRORLEVEL%
            echo Robocopy (artifacts) exit code: %RC%
            if %RC% LEQ 7 (
              echo "Artifact copy ok (code %RC%)."
            ) else (
              echo "Artifact copy FAILED (code %RC%)."
              exit /b %RC%
            )
          """
        }
      }
    }

    stage('Archive (Jenkins)') {
      steps {
        // Always archive jars so you can download from Jenkins UI
        archiveArtifacts artifacts: 'target/**/*.jar, target/**/*.war', fingerprint: true
      }
    }
  } // end stages

  post {
    success {
      echo "Pipeline finished: workspace and artifacts copied to ${env.BUILD_OUTPUT}"
    }
    failure {
      echo "Pipeline failed — check console for robocopy or mvn errors."
    }
  }
}
