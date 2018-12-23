def slackChannel = 'daikon'
def gitCredentials = usernamePassword(
    credentialsId: 'github-credentials',
    passwordVariable: 'GIT_PASSWORD',
    usernameVariable: 'GIT_LOGIN')

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
    stage('Check git connectivity') {
      steps {
        container('maven') {
          withCredentials([gitCredentials]) {
            sh """
                ./jenkins/configure_git_credentials.sh '${GIT_LOGIN}' '${GIT_PASSWORD}'
                git tag ci-kuke-test && git push --tags
                git push --delete origin ci-kuke-test && git tag --delete ci-kuke-test
            """
          }
        }
      }
    }

    stage('Configure branch deployment') {
      when {
        expression { env.BRANCH_NAME != 'master' }
      }
      steps {
        container('maven') {
          withCredentials([gitCredentials]) {
            sh """
                escaped_branch=$(echo ${env.BRANCH_NAME} | tr '[:upper:]' '[:lower:]')
                sed -i "/<\/settings>/ i <mirrors>\n<mirror>\n<id>talend_nexus_smart_proxy</id>\n<mirrorOf>talend_nexus</mirrorOf>\n<name>branch repos</name>\n<url>https://nexus-smart-branch.datapwn.com/nexus/content/repositories/branch_${escaped_branch}</url>\n</mirror>\n</mirrors>" $MAVEN_SETTINGS
                cat $MAVEN_SETTINGS
            """
          }
        }
      }
    }

    stage('Build & deploy') {
      when {
        expression { false }
      }
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
            withCredentials([gitCredentials]) {
              container('maven') {
                configFileProvider([configFile(fileId: 'maven-settings-nexus-zl', variable: 'MAVEN_SETTINGS')]) {
                  sh """
                    git config --global push.default current"
                    mvn -B -s $MAVEN_SETTINGS -Darguments='-DskipTests' -Dtag=${params.release_version} -DreleaseVersion=${params.release_version} -DdevelopmentVersion=${params.next_version} release:prepare
                    mvn -B -s $MAVEN_SETTINGS -Darguments='-DskipTests' -DlocalCheckout=true -Dusername=${GIT_LOGIN} -Dpassword=${GIT_PASSWORD} release:perform
                    git push
                  """
                }
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
