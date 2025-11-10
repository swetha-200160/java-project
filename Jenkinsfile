pipeline {
  agent any

  environment {
    BUILD_OUTPUT = "C:\\Users\\swethasuresh\\build"
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
        echo 'Building Java project...'
        // capture mvn console to a small logfile for debugging if needed
        bat '''
          mvn -B clean package -DskipTests > mvn-build.log 2>&1
          type mvn-build.log | findstr /R /C:"Building .*jar:" /C:"BUILD SUCCESS" || echo "mvn messages not found in log"
        '''
      }
    }

    stage('Archive artifacts') {
      steps {
        archiveArtifacts artifacts: 'target/**/*.jar, target/**/*.war', fingerprint: true
      }
    }

    stage('Copy Build Artifacts to BUILD_OUTPUT') {
      steps {
        script {
          echo "Workspace: ${env.WORKSPACE}"
          bat """
            REM --- show current directory and workspace listing ---
            echo Current dir:
            cd
            echo.
            echo Listing workspace root (first 200 lines):
            dir "%WORKSPACE%\\*" /S /B | more

            REM --- show contents of target if present ---
            if exist "%WORKSPACE%\\target" (
              echo ===== target exists. Listing target contents =====
              dir "%WORKSPACE%\\target\\*" /S /B
            ) else (
              echo ===== target does NOT exist =====
            )

            REM --- ensure destination exists ---
            if not exist "${env.BUILD_OUTPUT}" mkdir "${env.BUILD_OUTPUT}"

            REM --- copy jars/wars from target to destination (preserve subfolders) ---
            if exist "%WORKSPACE%\\target" (
              robocopy "%WORKSPACE%\\target" "${env.BUILD_OUTPUT}" *.jar *.war *.zip /E /COPY:DAT /R:2 /W:2
            ) else (
              echo "No target folder to copy from. Skipping robocopy."
            )

            REM --- capture robocopy exit code ---
            set RC=%ERRORLEVEL%
            echo Robocopy exit code: %RC%

            REM treat 0-7 as success (robocopy behavior)
            if %RC% LEQ 7 (
              echo "Artifacts copy finished with acceptable code %RC%."
              exit /b 0
            ) else (
              echo "Robocopy reported error code %RC%."
              exit /b %RC%
            )
          """
        }
      }
    }
  } // end stages

  post {
    success {
      echo 'Build completed successfully and artifacts archived / copied.'
    }
    failure {
      echo 'Build or artifact copy failed — check console output.'
    }
  }
}
