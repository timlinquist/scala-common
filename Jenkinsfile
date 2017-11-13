#!groovy

pipeline {
  agent {
    dockerfile true
  }
  environment {
    NEXUS = credentials('exchange-nexus')
  }
  stages {
    stage('Test') {
      steps {
        sh 'sbt clean coverage test coverageReport'
      }
    }
    stage('Publish') {
      steps {
        sh 'sbt commonJS/publish'
        sh 'sbt commonJVM/publish'
      }
    }
  }
}
