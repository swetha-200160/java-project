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
        echo "=== VERIFY ENV ==="
        bat 'echo Running as: & whoami || echo whoami not available'
        bat 'java -version || echo "java not found"'
        bat 'where mvn || echo "mvn not found on PATH"'
        bat 'echo Workspace: %WORKSPACE%'
        bat 'if exist "%WORKSPACE%\\pom.xml" ( echo "pom.xml FOUND" ) else ( echo "pom.xml NOT FOUND in workspace root" )'
      }
    }

    stage('Build (fail loudly)') {
      steps {
        bat """
          pushd "%WORKSPACE%"
          echo Running mvn in %CD%

          rem run mvn and capture exit code and full log
          mvn -B -DskipTests=true clean package > mvn-build.log 2>&1
          set RC=%ERRORLEVEL%
          echo MAVEN RETURN CODE: %RC%

          if %RC% NEQ 0 (
            echo ===== MAVEN BUILD FAILED - PRINTING TAIL (200 lines) =====
            powershell -Command "if (Test-Path .\\mvn-build.log) { Get-Content .\\mvn-build.log -Tail 200 | Out-String | Write-Host } else { Write-Host 'mvn-build.log not present' }"
            echo ===== END MAVEN LOG =====
            exit /b %RC%
          )

          echo MAVEN build SUCCEEDED
          popd
        """
      }
    }

    stage('Show build outputs') {
      steps {
        bat '''
          echo ==== workspace top-level ====
          dir "%WORKSPACE%\\*" /B
          echo ==== target listing (if exists) ====
          if exist "%WORKSPACE%\\target" ( dir "%WORKSPACE%\\target\\*" /S /B ) else ( echo "No target folder found" )
          echo ==== tail of mvn-build.log (last 100 lines) ====
          if exist "%WORKSPACE%\\mvn-build.log" ( powershell -Command "Get-Content .\\mvn-build.log -Tail 100 | Out-String | Write-Host" ) else ( echo "mvn-build.log not present" )
        '''
      }
    }

    stage('Test BUILD_OUTPUT write') {
      steps {
        bat """
          if not exist "${BUILD_OUTPUT}" mkdir "${BUILD_OUTPUT}" || echo mkdir returned %ERRORLEVEL%
          echo Jenkins write test > "${BUILD_OUTPUT}\\\\jenkins-write-test.txt" || echo write failed %ERRORLEVEL%
          dir "${BUILD_OUTPUT}" /B || echo "Cannot list BUILD_OUTPUT"
        """
      }
    }

    stage('Copy workspace -> BUILD_OUTPUT') {
      steps {
        bat """
          robocopy "%WORKSPACE%" "${BUILD_OUTPUT}" /E /COPY:DAT /R:2 /W:2 /XD ".git"
          set RC=%ERRORLEVEL%
          echo ROBocopy workspace exit code: %RC%
          if %RC% GEQ 8 (
            echo "ROBOCOPY FAILED with exit code %RC%"
            exit /b %RC%
          )
        """
      }
    }

    stage('Copy artifacts -> BUILD_OUTPUT\\\\artifacts') {
      steps {
        bat """
          if not exist "${BUILD_OUTPUT}\\\\artifacts" mkdir "${BUILD_OUTPUT}\\\\artifacts"
          if exist "%WORKSPACE%\\target" (
            robocopy "%WORKSPACE%\\target" "${BUILD_OUTPUT}\\\\artifacts" *.jar *.war *.zip /E /COPY:DAT /R:2 /W:2
            echo ROBocopy artifacts exit code: %ERRORLEVEL%
            if %ERRORLEVEL% GEQ 8 exit /b %ERRORLEVEL%
          ) else (
            echo "No target found; nothing to copy"
          )
        """
      }
    }
  }

  post {
    success { echo "Pipeline SUCCEEDED — check ${env.BUILD_OUTPUT}" }
    failure { echo "Pipeline FAILED — see console log above for details." }
  }
}
