pipeline {
  agent any

  environment {
    BUILD_OUTPUT = "C:\\Users\\swethasuresh\\build"
  }

  stages {

    stage('Checkout') {
      steps {
        // Checkout from the repository configured in Jenkins
        checkout scm
      }
    }

    stage('Verify Environment') {
      steps {
        echo '=== VERIFY ENVIRONMENT ==='
        bat '''echo Running as: & whoami || echo whoami not available'''
        bat '''java -version || echo java not found'''
        bat '''where mvn || echo mvn not found on PATH'''
        bat '''echo MAVEN_HOME=%MAVEN_HOME%'''
        bat '''echo Workspace: %WORKSPACE%'''
        bat '''if exist "%WORKSPACE%\\pom.xml" ( echo pom.xml FOUND ) else ( echo pom.xml NOT FOUND )'''
      }
    }

    stage('Build with Maven') {
      steps {
        bat '''
          pushd "%WORKSPACE%"
          echo Running build in %CD%

          if defined MAVEN_HOME (
            echo Using MAVEN_HOME: %MAVEN_HOME%
            "%MAVEN_HOME%\\bin\\mvn" -B -DskipTests=true clean package > mvn-build.log 2>&1
          ) else (
            echo MAVEN_HOME not defined — using mvn from PATH
            mvn -B -DskipTests=true clean package > mvn-build.log 2>&1
          )

          set RC=%ERRORLEVEL%
          echo MAVEN RETURN CODE: %RC%

          if %RC% NEQ 0 (
            echo ===== MAVEN BUILD FAILED (showing last 200 lines) =====
            powershell -Command "if (Test-Path .\\mvn-build.log) { Get-Content .\\mvn-build.log -Tail 200 | Out-String | Write-Host } else { Write-Host 'mvn-build.log not found' }"
            echo ===== END MAVEN LOG =====
            exit /b %RC%
          )

          echo MAVEN build SUCCEEDED
          popd
        '''
      }
    }

    stage('Show Build Outputs') {
      steps {
        bat '''
          echo ==== WORKSPACE FILES ====
          dir "%WORKSPACE%" /B

          echo ==== TARGET DIRECTORY ====
          if exist "%WORKSPACE%\\target" (
            dir "%WORKSPACE%\\target\\*" /S /B
          ) else (
            echo "No target folder found"
          )

          echo ==== LAST 100 LINES OF mvn-build.log ====
          if exist "%WORKSPACE%\\mvn-build.log" (
            powershell -Command "Get-Content .\\mvn-build.log -Tail 100 | Out-String | Write-Host"
          ) else (
            echo "mvn-build.log not present"
          )
        '''
      }
    }

    stage('Archive Artifacts') {
      steps {
        archiveArtifacts artifacts: 'target/**/*.jar, target/**/*.war', allowEmptyArchive: true, fingerprint: true
      }
    }

    stage('Prepare Build Output Folder') {
      steps {
        bat '''
          if not exist "%BUILD_OUTPUT%" mkdir "%BUILD_OUTPUT%"
          echo Jenkins write test > "%BUILD_OUTPUT%\\jenkins-write-test.txt"
          dir "%BUILD_OUTPUT%" /B
        '''
      }
    }

    stage('Copy Workspace to Build Folder') {
      steps {
        bat '''
          robocopy "%WORKSPACE%" "%BUILD_OUTPUT%" /E /COPY:DAT /R:2 /W:2 /XD ".git"
          set RC=%ERRORLEVEL%
          echo ROBOCOPY WORKSPACE EXIT CODE: %RC%

          if %RC% GEQ 8 (
            echo "ROBOCOPY workspace FAILED with exit code %RC%"
            exit /b %RC%
          ) else (
            echo "ROBOCOPY workspace SUCCESS with exit code %RC%"
          )
        '''
      }
    }

    stage('Copy Artifacts to Build Folder') {
      steps {
        bat '''
          if not exist "%BUILD_OUTPUT%\\artifacts" mkdir "%BUILD_OUTPUT%\\artifacts"
          if exist "%WORKSPACE%\\target" (
            robocopy "%WORKSPACE%\\target" "%BUILD_OUTPUT%\\artifacts" *.jar *.war *.zip /E /COPY:DAT /R:2 /W:2
            set RC=%ERRORLEVEL%
            echo ROBOCOPY artifacts EXIT CODE: %RC%
            if %RC% GEQ 8 exit /b %RC%
          ) else (
            echo "No target folder found, skipping artifact copy."
