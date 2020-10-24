pipeline {
  agent any
  stages {
    stage('sfdg') {
      parallel {
        stage('sfdg') {
          steps {
            withGradle() {
              echo 'sdf'
            }

          }
        }

        stage('asdfff') {
          steps {
            echo 'hjhjg'
            waitUntil(quiet: true, initialRecurrencePeriod: 1)
          }
        }

      }
    }

    stage('fadsf') {
      steps {
        sleep 1
      }
    }

    stage('asdfdaf') {
      steps {
        pwd()
      }
    }

  }
}