pipeline {
  agent any
  environment {
    BUILD_OUTPUT = "C:\\Users\\swethasuresh\\build"
    ARTIFACTS_PATH = "%BUILD_OUTPUT%\\artifacts"
    ROBO_LOG = "%WORKSPACE%\\robocopy-artifacts.log"
  }

  stages {
    stage('Checkout') { steps { checkout scm } }

    stage('Verify') {
      steps {
        echo "WORKSPACE = ${env.WORKSPACE}"
        echo "BUILD_OUTPUT = ${env.BUILD_OUTPUT}"
        bat '''echo whoami: & whoami || echo whoami not available'''
        bat '''echo JAVA: & java -version || echo java not found'''
        bat '''echo MVN: & where mvn || echo mvn not found on PATH'''
        bat '''if exist "%WORKSPACE%\\pom.xml" ( echo pom.xml FOUND ) else ( echo pom.xml NOT FOUND )'''
      }
    }

    stage('Show target') {
      steps {
        bat '''
          echo ==== target listing ====
          if exist "%WORKSPACE%\\target" (
            dir "%WORKSPACE%\\target" /S /B
          ) else (
            echo "No target folder found"
          )
        '''
      }
    }

    stage('Copy only artifacts (jar/war)') {
      steps {
        bat '''
          REM ensure artifacts destination
          if not exist "%BUILD_OUTPUT%\\artifacts" mkdir "%BUILD_OUTPUT%\\artifacts"
          REM remove previous log if present
          if exist "%WORKSPACE%\\robocopy-artifacts.log" del "%WORKSPACE%\\robocopy-artifacts.log"
          REM copy only jars/wars and create a log
          robocopy "%WORKSPACE%\\target" "%BUILD_OUTPUT%\\artifacts" *.jar *.war *.zip /S /COPY:DAT /R:2 /W:2 /LOG:"%WORKSPACE%\\robocopy-artifacts.log"
          set RC=%ERRORLEVEL%
          echo ROBOCOPY ARTIFACTS RC=%RC%
          if %RC% GEQ 8 (
            echo "Robocopy failure (RC=%RC%) — see robocopy-artifacts.log"
            type "%WORKSPACE%\\robocopy-artifacts.log"
            exit /b %RC%
          ) else (
            echo "Robocopy treated as success (RC=%RC%)"
            type "%WORKSPACE%\\robocopy-artifacts.log"
          )
        '''
      }
    }

    stage('List destination') {
      steps {
        bat '''
          echo ==== LIST DESTINATION ARTIFACTS ====
          dir "%BUILD_OUTPUT%\\artifacts" /S /B || echo "No files in %BUILD_OUTPUT%\\artifacts"
          echo ==== LIST BUILD_OUTPUT TOP ====
          dir "%BUILD_OUTPUT%" /B || echo "No files in %BUILD_OUTPUT%"
        '''
      }
    }
  }

  post {
    always {
      echo "Done — check the console and %BUILD_OUTPUT% on agent"
    }
  }
}
