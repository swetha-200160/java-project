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

    stage('Verify env') {
      steps {
        echo '=== VERIFY ENV ==='
        bat '''echo Running as: & whoami || echo whoami not available'''
        bat '''java -version || echo java not found'''
        bat '''where mvn || echo mvn not found on PATH'''
        bat '''echo MAVEN_HOME=%MAVEN_HOME%'''
        bat '''echo Workspace: %WORKSPACE%'''
        bat '''if exist "%WORKSPACE%\\pom.xml" ( echo pom.xml FOUND ) else ( echo pom.xml NOT FOUND )'''
      }
    }

    stage('Build') {
      steps {
        bat '''
          pushd "%WORKSPACE%"
          echo Running build in %CD%

          if defined MAVEN_HOME (
            echo Using MAVEN_HOME: %MAVEN_HOME%
            "%MAVEN_HOME%\\bin\\mvn" -B -DskipTests=true clean package > mvn-build.log 2>&1
          ) else (
            echo MAVEN_HOME not defined — trying mvn from PATH
            mvn -B -DskipTests=true clean package > mvn-build.log 2>&1
          )

          set RC=%ERRORLEVEL%
          echo MAVEN RETURN CODE: %RC%

          if %RC% NEQ 0 (
            echo ===== MAVEN BUILD FAILED - TAIL (200 lines) =====
            powershell -Command "if (Test-Path .\\mvn-build.log) { Get-Content .\\mvn-build.log -Tail 200 | Out-String | Write-Host } else { Write-Host 'mvn-build.log NOT FOUND' }"
            echo ===== END MAVEN LOG =====
            exit /b %RC%
          )
          echo MAVEN build SUCCEEDED
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
          if exist "%WORKSPACE%\\target" ( dir "%WORKSPACE%\\target\\*" /S /B ) else ( echo "No target folder found" )
          echo ==== mvn-build.log tail ====
          if exist "%WORKSPACE%\\mvn-build.log" ( powershell -Command "Get-Content .\\mvn-build.log -Tail 100 | Out-String | Write-Host" ) else ( echo "mvn-build.log not present" )
        '''
      }
    }

    stage('Prepare BUILD_OUTPUT') {
      steps {
        bat '''
          if not exist "%BUILD_OUTPUT%" mkdir "%BUILD_OUTPUT%" || echo mkdir returned %ERRORLEVEL%
          echo Jenkins write test > "%BUILD_OUTPUT%\\jenkins-write-test.txt" || echo write failed %ERRORLEVEL%
          dir "%BUILD_OUTPUT%" /B || echo "Cannot list BUILD_OUTPUT"
        '''
      }
    }

    stage('Copy workspace -> BUILD_OUTPUT') {
      steps {
        bat '''
          robocopy "%WORKSPACE%" "%BUILD_OUTPUT%" /E /COPY:DAT /R:2 /W:2 /XD ".git"
          set RC=%ERRORLEVEL%
          echo ROBocopy workspace exit code: %RC%
          if %RC% GEQ 8 (
            echo "ROBOCOPY FAILED with exit code %RC%"
            exit /b %RC%
          )
        '''
      }
    }

    stage('Copy artifacts -> BUILD_OUTPUT\\artifacts') {
      steps {
        bat '''
          if not exist "%BUILD_OUTPUT%\\artifacts" mkdir "%BUILD_OUTPUT%\\artifacts"
          if exist "%WORKSPACE%\\target" (
            robocopy "%WORKSPACE%\\target" "%BUILD_OUTPUT%\\artifacts" *.jar *.war *.zip /E /COPY:DAT /R:2 /W:2
            set RC=%ERRORLEVEL%
            echo ROBocopy artifacts exit code: %RC%
            if %RC% GEQ 8 (
              echo "ROBOCOPY (artifacts) FAILED with exit code %RC%"
              exit /b %RC%
            )
          ) else (
            echo "No target found; nothing to copy"
          )
        '''
      }
    }
  }

  post {
    success {
      echo "Pipeline SUCCEEDED — workspace and artifacts copied to ${env.BUILD_OUTPUT}"
    }
    failure {
      echo "Pipeline FAILED — inspect console output for mvn/robocopy errors"
    }
  }
}
