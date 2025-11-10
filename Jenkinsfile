pipeline {
  agent { label 'windows' } // change to your Windows agent label if it's different
  environment {
    BUILD_OUTPUT = "C:\\Users\\swethasuresh\\build"
  }

  stages {
    stage('Verify ENV & Tools') {
      steps {
        echo "=== VERIFY ENV ==="
        bat 'echo Running as: & whoami || echo whoami not available'
        bat 'java -version || echo "java not found"'
        bat 'mvn -v || echo "mvn not found or not on PATH"'
        bat 'echo Workspace: %WORKSPACE%'
        bat 'dir "%WORKSPACE%\\pom.xml" /B || echo "pom.xml not found in workspace root"'
      }
    }

    stage('Build (capture log, fail on mvn error)') {
      steps {
        bat '''
          pushd "%WORKSPACE%"
          echo Running mvn in %CD%
          rem run maven and capture full log
          mvn -B clean package -DskipTests > mvn-build.log 2>&1
          set RC=%ERRORLEVEL%
          echo MAVEN RETURN CODE: %RC%
          if %RC% NEQ 0 (
            echo ===== MAVEN BUILD FAILED - printing tail of mvn-build.log =====
            powershell -Command "if (Test-Path '.\\mvn-build.log') { Get-Content '.\\mvn-build.log' -Tail 500 | Out-String | Write-Host } else { Write-Host 'mvn-build.log not present' }"
            echo ===== END MAVEN LOG =====
            exit /b %RC%
          )
          echo MAVEN build succeeded
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
            powershell -Command "Get-Content '.\\mvn-build.log' -Tail 200 | Out-String | Write-Host"
          ) else (
            echo "mvn-build.log not present"
          )
        '''
      }
    }

    stage('Safe Debug Dump (non-failing)') {
      steps {
        bat '''
          echo ==== SAFE DEBUG DUMP ====
          echo whoami:
          whoami || echo whoami failed
          echo.
          echo Workspace:
          echo %WORKSPACE%pipeline {
  agent { label 'windows' } // change to your Windows agent label if needed
  environment {
    BUILD_OUTPUT = "C:\\Users\\swethasuresh\\build"
    MVN_CMD = 'mvn'
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Verify') {
      steps {
        bat '''
          echo whoami:
          whoami || echo whoami failed
          echo java version:
          java -version || echo java not found
          %MVN_CMD% -v || echo mvn not found
        '''
      }
    }

    stage('Build (compile only — no package)') {
      steps {
        // compile sources but do NOT run package phase, so no .jar/.war created
        bat '''
          pushd "%WORKSPACE%"
          echo Running mvn compile (no package)
          %MVN_CMD% -B clean compile > mvn-compile.log 2>&1
          set RC=%ERRORLEVEL%
          echo MAVEN RETURN CODE: %RC%
          if %RC% NEQ 0 (
            echo ===== MAVEN COMPILE FAILED - printing tail of mvn-compile.log =====
            powershell -Command "if (Test-Path '.\\mvn-compile.log') { Get-Content '.\\mvn-compile.log' -Tail 200 | Out-String | Write-Host } else { Write-Host 'mvn-compile.log not present' }"
            exit /b %RC%
          )
          echo MAVEN compile succeeded
          popd
        '''
      }
    }

    stage('Safe Debug Dump (non-failing)') {
      steps {
        bat '''
          echo ==== SAFE DEBUG DUMP ====pipeline {
  agent { label 'windows' } // change to your Windows agent label if different
  environment {
    BUILD_OUTPUT = "C:\\Users\\swethasuresh\\build"
    MVN_CMD = 'mvn'
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Verify Tools') {
      steps {
        bat '''
          echo ==== VERIFY ENV ====
          echo whoami:
          whoami || echo whoami failed
          echo java version:
          java -version || echo java not found
          %MVN_CMD% -v || echo mvn not found
          echo Workspace: %WORKSPACE%
        '''
      }
    }

    stage('Build (compile only — no package)') {
      steps {
        bat '''
          pushd "%WORKSPACE%"
          echo Running mvn compile (no package)
          %MVN_CMD% -B clean compile > mvn-compile.log 2>&1
          set RC=%ERRORLEVEL%
          echo MAVEN RETURN CODE: %RC%
          if %RC% NEQ 0 (
            echo ===== MAVEN COMPILE FAILED - printing tail of mvn-compile.log =====
            powershell -Command "if (Test-Path '.\\mvn-compile.log') { Get-Content '.\\mvn-compile.log' -Tail 200 | Out-String | Write-Host } else { Write-Host 'mvn-compile.log not present' }"
            exit /b %RC%
          )
          echo MAVEN compile succeeded
          popd
        '''
      }
    }

    stage('Safe Debug (non-failing)') {
      steps {
        bat '''
          echo ==== SAFE DEBUG DUMP ====
          whoami || echo whoami failed
          echo Workspace: %WORKSPACE%
          if exist "%BUILD_OUTPUT%" ( dir "%BUILD_OUTPUT%" /B ) else ( echo BUILD_OUTPUT not present )
          echo ==== END SAFE DEBUG DUMP ====
          exit /b 0
        '''
      }
    }

    stage('Verify BUILD_OUTPUT write access') {
      steps {
        bat """
          if not exist "%BUILD_OUTPUT%" mkdir "%BUILD_OUTPUT%" || echo mkdir returned %ERRORLEVEL%
          echo TEST > "%BUILD_OUTPUT%\\\\jenkins-write-test.txt" || echo write failed %ERRORLEVEL%
          dir "%BUILD_OUTPUT%" /B || echo cannot list BUILD_OUTPUT
        """
      }
    }

    stage('Copy Workspace (exclude target)') {
      steps {
        // Exclude "target" so no compiled artifacts are copied
        bat """
          robocopy "%WORKSPACE%" "%BUILD_OUTPUT%" /E /COPY:DAT /R:2 /W:2 /XD ".git" "target"
          set RC=%ERRORLEVEL%
          if %RC% GEQ 8 (
            echo ROBocopy workspace FAILED with exit code %RC%
            exit /b %RC%
          ) else (
            echo ROBocopy workspace completed (exit code %RC%)
          )
        """
      }
    }
  }

  post {
    success { echo "Pipeline succeeded. No artifacts were packaged or archived. Check ${env.BUILD_OUTPUT}" }
    failure { echo "Pipeline failed; inspect logs." }
  }
}

          whoami || echo whoami failed
          echo Workspace: %WORKSPACE%
          echo Build output dir listing:
          if exist "%BUILD_OUTPUT%" ( dir "%BUILD_OUTPUT%" /B ) else ( echo BUILD_OUTPUT not present )
          echo ==== END SAFE DEBUG DUMP ====
          exit /b 0
        '''
      }
    }

    stage('Verify BUILD_OUTPUT') {
      steps {
        bat """
          if not exist "%BUILD_OUTPUT%" mkdir "%BUILD_OUTPUT%" || echo mkdir returned %ERRORLEVEL%
          echo TEST > "%BUILD_OUTPUT%\\\\jenkins-write-test.txt" || echo write failed %ERRORLEVEL%
          dir "%BUILD_OUTPUT%" /B || echo cannot list BUILD_OUTPUT
        """
      }
    }

    stage('Copy Workspace (exclude target/artifacts)') {
      steps {
        // Exclude target so no built artifacts (if any) are copied
        bat """
          robocopy "%WORKSPACE%" "%BUILD_OUTPUT%" /E /COPY:DAT /R:2 /W:2 /XD ".git" "target"
          set RC=%ERRORLEVEL%
          if %RC% GEQ 8 (
            echo ROBocopy workspace FAILED with exit code %RC%
            exit /b %RC%
          ) else (
            echo ROBocopy workspace completed (exit code %RC%)
          )
        """
      }
    }
  }

  post {
    success { echo "Pipeline succeeded. No artifacts were packaged or archived. Check ${env.BUILD_OUTPUT}" }
    failure { echo "Pipeline failed; inspect logs." }
  }
}

          echo.
          echo mvn version:
          mvn -v || echo mvn not available
          echo.
          echo Tail mvn-build.log (if present):
          powershell -Command "if (Test-Path '.\\mvn-build.log') { Get-Content '.\\mvn-build.log' -Tail 200 | Out-String | Write-Host } else { Write-Host 'mvn-build.log not present' }"
          echo.
          echo BUILD_OUTPUT listing:
          if exist "%BUILD_OUTPUT%" ( dir "%BUILD_OUTPUT%" /B ) else ( echo BUILD_OUTPUT not present )
          echo ==== END SAFE DEBUG DUMP ====
          exit /b 0
        '''
      }
    }

    stage('Verify BUILD_OUTPUT write access') {
      steps {
        bat """
          echo Creating BUILD_OUTPUT and write-test file...
          if not exist "%BUILD_OUTPUT%" mkdir "%BUILD_OUTPUT%" || echo mkdir returned %ERRORLEVEL%
          echo TEST > "%BUILD_OUTPUT%\\\\jenkins-write-test.txt" || echo write failed %ERRORLEVEL%
          dir "%BUILD_OUTPUT%" /B || echo "Cannot list BUILD_OUTPUT"
        """
      }
    }

    stage('Copy workspace -> BUILD_OUTPUT (robocopy)') {
      steps {
        bat """
          robocopy "%WORKSPACE%" "%BUILD_OUTPUT%" /E /COPY:DAT /R:2 /W:2 /XD ".git"
          set RC=%ERRORLEVEL%
          if %RC% GEQ 8 (
            echo ROBocopy workspace FAILED with exit code %RC%
            exit /b %RC%
          ) else (
            echo ROBocopy workspace completed (exit code %RC%)
          )
        """
      }
    }

    stage('Copy target artifacts -> BUILD_OUTPUT\\\\artifacts') {
      steps {
        bat """
          if not exist "%BUILD_OUTPUT%\\\\artifacts" mkdir "%BUILD_OUTPUT%\\\\artifacts"
          if exist "%WORKSPACE%\\target" (
            robocopy "%WORKSPACE%\\target" "%BUILD_OUTPUT%\\\\artifacts" *.jar *.war *.zip /E /COPY:DAT /R:2 /W:2
            set RC=%ERRORLEVEL%
            if %RC% GEQ 8 (
              echo ROBocopy artifacts FAILED with exit code %RC% & exit /b %RC%
            ) else (
              echo ROBocopy artifacts completed (exit code %RC%)
            )
          ) else (
            echo "No target found; nothing to copy"
          )
        """
      }
    }

    stage('Archive artifacts to Jenkins') {
      steps {
        archiveArtifacts artifacts: 'target/**/*.{jar,war,zip}', allowEmptyArchive: true, fingerprint: true
      }
    }
  }

  post {
    success { echo "Pipeline SUCCEEDED — check ${env.BUILD_OUTPUT}" }
    failure { echo "Pipeline FAILED — inspect console output above." }
  }
}
