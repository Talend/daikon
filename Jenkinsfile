def slackChannel = 'daikon'
def version = 'will be replaced'
def image = 'will be replaced'

pipeline {

  parameters {
    booleanParam(
      name: "release",
      description: "Build a release from current commit",
      defaultValue: false)
    string(
      name: "release_version",
      description: "Release version",
      defaultValue: "0.0.0")
    string(
      name: "next_version",
      description: "Next version",
      defaultValue: "0.0.0-SNAPSHOT")
  }

  agent {
    kubernetes {
      label 'all_daikon'
      yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
    - name: maven
      image: jenkinsxio/builder-maven:0.0.319
      command:
      - cat
      tty: true
      volumeMounts:
      - name: docker
        mountPath: /var/run/docker.sock
      - name: m2
        mountPath: /root/.m2/repository
  volumes:
  - name: docker
    hostPath:
      path: /var/run/docker.sock
  - name: m2
    hostPath:
      path: /tmp/jenkins/all/m2
"""
    }
  }

  environment {
    MAVEN_OPTS = '-Dmaven.artifact.threads=128 -Dorg.slf4j.simpleLogger.showThreadName=true -Dorg.slf4j.simpleLogger.showDateTime=true -Dorg.slf4j.simpleLogger.dateTimeFormat=HH:mm:ss'
  }

  options {
    buildDiscarder(logRotator(artifactNumToKeepStr: '5', numToKeepStr: env.BRANCH_NAME == 'master' ? '10' : '2'))
    timeout(time: 60, unit: 'MINUTES')
    skipStagesAfterUnstable()
  }

  triggers {
    pollSCM "* * * * *"
  }

  stages {
    stage('Build') {
      steps {
        container('maven') {
          configFileProvider([configFile(fileId: 'maven-settings-nexus-zl', variable: 'MAVEN_SETTINGS')]) {
            sh 'mvn package -B -s $MAVEN_SETTINGS'
          }
        }
      }
    }

    stage('Deploy') {
      steps {
        container('maven') {
          configFileProvider([configFile(fileId: 'maven-settings-nexus-zl', variable: 'MAVEN_SETTINGS')]) {
            sh 'mvn deploy -B -s $MAVEN_SETTINGS'
          }
        }
      }
    }

    stage("Release") {
        when {
            expression { params.release }
        }
        steps {
            container('maven') {
              configFileProvider([configFile(fileId: 'maven-settings-nexus-zl', variable: 'MAVEN_SETTINGS')]) {
                sh "mvn -B -s $MAVEN_SETTINGS -Darguments='-DskipTests' -Dtag=${params.release_version} -DreleaseVersion=${params.release_version} -DpreparationGoals='deploy' -DdevelopmentVersion=${params.next_version} release:prepare"
                sh "mvn -B -s $MAVEN_SETTINGS -Darguments='-DskipTests' release:perform"
              }
            }
        }
    }
  }
  post {
    always {
      junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
    }
  }
}
