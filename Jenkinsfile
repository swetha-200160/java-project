pipeline {
  agent any

  environment {
    SONARQUBE_SERVER = 'SonarQube'
    SONAR_PROJECT_KEY = 'my-java-project'
    GIT_CREDENTIALS = 'gitrepo'
    SONAR_TOKEN_CREDENTIAL = 'sonarqube' // Secret Text credential id
  }

  stages {
    stage('Checkout') {
      steps {
        git branch: 'main', credentialsId: "${GIT_CREDENTIALS}", url: 'https://github.com/swetha-200160/java-project.git'
      }
    }

    stage('Locate pom.xml & Show workspace') {
      steps {
        echo "Searching for pom.xml (will fail if none found)..."
        // show top-level files
        bat 'dir /b'
        // find pom.xml anywhere in the workspace
        bat '''
          powershell -NoProfile -Command ^
            "$p = Get-ChildItem -Path . -Filter pom.xml -Recurse -ErrorAction SilentlyContinue | Select-Object -First 1; ^
             if ($p) { Write-Host 'POM_FOUND:' $p.FullName } else { Write-Host 'POM_NOT_FOUND'; exit 1 }"
        '''
      }
    }

    stage('Build (run mvn where pom is)') {
      steps {
        echo "Running mvn in the directory that contains pom.xml"
        // find pom, cd into its directory and run mvn
        bat '''
          powershell -NoProfile -Command ^
            "$p = Get-ChildItem -Path . -Filter pom.xml -Recurse -ErrorAction SilentlyContinue | Select-Object -First 1; ^
             if (-not $p) { Write-Host 'ERROR: pom.xml not found'; exit 1 } ^
             ; $dir = $p.DirectoryName; Write-Host 'Building in:' $dir; ^
             Set-Location $dir; ^
             & mvn -B -DskipTests clean package"
        '''
      }
      post {
        always {
          junit '**\\target\\surefire-reports\\*.xml' // will be present only if that module produced tests
        }
      }
    }

    stage('SonarQube Analysis') {
      steps {
        // bind Sonar token for mvn command
        withCredentials([string(credentialsId: "${SONAR_TOKEN_CREDENTIAL}", variable: 'SONAR_AUTH_TOKEN')]) {
          // run sonar in the same module (find pom again and run sonar:sonar there)
          bat '''
            powershell -NoProfile -Command ^
              "$p = Get-ChildItem -Path . -Filter pom.xml -Recurse -ErrorAction SilentlyContinue | Select-Object -First 1; ^
               if (-not $p) { Write-Host 'ERROR: pom.xml not found'; exit 1 } ; ^
               $dir = $p.DirectoryName; Set-Location $dir; ^
               Write-Host 'Running Sonar in:' $dir; ^
               & mvn -B sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY} -Dsonar.login=%SONAR_AUTH_TOKEN%"
          '''
        }
      }
    }

    stage('Quality Gate') {
      steps {
        timeout(time: 5, unit: 'MINUTES') {
          waitForQualityGate abortPipeline: true
        }
      }
    }
  }

  post {
    success { echo '✅ Pipeline finished and passed SonarQube Quality Gate.' }
    failure { echo '❌ Pipeline failed — check the console output.' }
  }
}
