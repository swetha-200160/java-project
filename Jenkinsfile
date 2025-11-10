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
        bat 'mvn clean package -DskipTests'
      }
    }

    stage('Copy Build Artifacts (target -> BUILD_OUTPUT)') {
  steps {
    script {
      echo "Workspace: ${env.WORKSPACE}"
      bat """
        REM show current dir and java/mvn version for debug
        echo Current dir:
        cd
        echo.

        REM show whether target exists and its contents
        echo ==== Does target exist? ====
        if exist "%WORKSPACE%\\target" (
          echo target exists
          echo ==== Listing target (recursive) ====
          dir "%WORKSPACE%\\target\\*" /S /B
        ) else (
          echo target directory does NOT exist
        )

        REM show last few lines of mvn log if present
        if exist "%WORKSPACE%\\mvn-build.log" (
          echo ===== recent mvn log =====
          more +0 "%WORKSPACE%\\mvn-build.log" | findstr /R /C:"Building jar:" /C:"BUILD SUCCESS" || echo "no mvn messages found"
        )

        REM ensure destination exists
        if not exist "${env.BUILD_OUTPUT}" mkdir "${env.BUILD_OUTPUT}"

        REM copy only jars and zips from target (preserve directory structure)
        REM Using robocopy from the target folder to the destination
        if exist "%WORKSPACE%\\target" (
          robocopy "%WORKSPACE%\\target" "${env.BUILD_OUTPUT}" *.jar *.war *.zip /E /COPY:DAT /R:2 /W:2
        ) else (
          echo "Skipping robocopy: target not present"
        )

        REM capture robocopy exit code
        set RC=%ERRORLEVEL%
        echo Robocopy exit code: %RC%

        REM treat 0-7 as success
        if %RC% LEQ 7 (
          echo "Artifacts copy finished with acceptable code %RC%"
          exit /b 0
        )
        echo "Robocopy FAILED with code %RC%"
        exit /b %RC%
      """
    }
  }
}


  post {
    success {
      echo 'Build completed successfully and entire workspace copied to target folder'
    }
    failure {
      echo 'Build or copy failed!'
    }
  }
} // end pipeline
