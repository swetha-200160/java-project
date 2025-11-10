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

    stage('Copy Build Files (robocopy whole workspace)') {
  steps {
    script {
      echo "Workspace: ${env.WORKSPACE}"
      bat """
        REM --- debug: show current folder and list all files in workspace (recursive) ---
        echo Current dir:
        cd
        echo.
        echo Listing workspace contents (first 200 lines):
        dir "%WORKSPACE%\\*" /S /B | more

        REM --- ensure destination exists ---
        if not exist "${env.BUILD_OUTPUT}" mkdir "${env.BUILD_OUTPUT}"

        REM --- robocopy entire workspace to destination ---
        REM /MIR mirrors (be careful: will delete extra files in destination)
        REM /COPY:DAT copies Data, Attributes, Timestamps
        REM /DCOPY:T preserve directory timestamps
        REM /R:2 /W:2 retry 2 times, wait 2 seconds
        REM /NFL /NDL keep them for now to see output (remove if you want less logs)
        robocopy "%WORKSPACE%" "${env.BUILD_OUTPUT}" /MIR /COPY:DAT /DCOPY:T /R:2 /W:2 /XD ".git" ".git\\" 

        REM capture robocopy exit code
        set RC=%ERRORLEVEL%
        echo Robocopy exit code: %RC%

        REM Robocopy exit codes: 0=no files copied, 1=some files copied, 0-7 are commonly OK
        if %RC% LEQ 7 (
          echo Robocopy finished with acceptable code %RC%. Exiting success.
          exit /b 0
        )

        echo Robocopy FAILED with code %RC%. Exiting with failure.
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
}
