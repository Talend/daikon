def slackChannel = 'daikon'
def gitCredentials = usernamePassword(
    credentialsId: 'github-credentials',
    passwordVariable: 'GIT_PASSWORD',
    usernameVariable: 'GIT_LOGIN')
def jiraCredentials = usernamePassword(
    credentialsId: 'jira-credentials',
    passwordVariable: 'JIRA_PASSWORD',
    usernameVariable: 'JIRA_LOGIN')
def currentBranch = env.BRANCH_NAME
if (BRANCH_NAME.startsWith("PR-")) {
    currentBranch = env.CHANGE_BRANCH
}

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
      image: jenkinsxio/builder-maven:0.1.211
      command:
      - cat
      tty: true
      volumeMounts:
      - name: docker
        mountPath: /var/run/docker.sock
      - name: m2
        mountPath: /root/daikon/.m2/repository
  volumes:
  - name: docker
    hostPath:
      path: /var/run/docker.sock
  - name: m2
    hostPath:
      path: /tmp/jenkins/daikon/m2
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

  stages {
    stage('Check git connectivity') {
      when {
        expression { params.release }
      }
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

    stage('Build release') {
      when {
        expression { params.release }
      }
      steps {
        container('maven') {
          configFileProvider([configFile(fileId: 'maven-settings-nexus-zl', variable: 'MAVEN_SETTINGS')]) {
            sh 'mvn install -B -s $MAVEN_SETTINGS'
          }
        }
      }
    }

    stage('Build & deploy master') {
      when {
        expression { !params.release && env.BRANCH_NAME == 'master' }
      }
      steps {
        container('maven') {
          configFileProvider([configFile(fileId: 'maven-settings-nexus-zl', variable: 'MAVEN_SETTINGS')]) {
            sh 'mvn deploy -B -s $MAVEN_SETTINGS'
          }
        }
      }
    }

    stage('Build & deploy branch') {
      when {
        expression { !params.release && env.BRANCH_NAME != 'master' }
      }
      environment {
        escaped_branch = currentBranch.toLowerCase().replaceAll('/', '_')
      }
      steps {
        container('maven') {
          configFileProvider([configFile(fileId: 'maven-settings-nexus-zl', variable: 'MAVEN_SETTINGS')]) {
            sh """
              mvn deploy -B -s $MAVEN_SETTINGS -Dtalend_snapshots=https://nexus-smart-branch.datapwn.com/nexus/content/repositories/dev_branch_snapshots/branch_${escaped_branch} -Dtalend_snapshots_deployment=https://artifacts-oss.talend.com/nexus/content/repositories/dev_branch_snapshots/branch_${escaped_branch}
            """
          }
        }
      }
    }

    stage("Release") {
        when {
            expression { params.release }
        }
        steps {
            withCredentials([gitCredentials, jiraCredentials]) {
              container('maven') {
                configFileProvider([configFile(fileId: 'maven-settings-nexus-zl', variable: 'MAVEN_SETTINGS')]) {
                  sh """
                    git config --global push.default current
                    git checkout ${env.BRANCH_NAME}
                    mvn -B -s $MAVEN_SETTINGS -Darguments='-DskipTests' -Dtag=${params.release_version} -DreleaseVersion=${params.release_version} -DdevelopmentVersion=${params.next_version} release:prepare
                    cd releases/
                    mvn install -B -s $MAVEN_SETTINGS -Duser=${JIRA_LOGIN} -Dpassword=${JIRA_PASSWORD} -Dversion=${params.release_version} -Doutput=.
                    git add -A .
                    git commit -m "Add ${params.release_version} release notes"
                    cat ${params.release_version}.adoc
                    git push
                    git push --tags
                    cd ..
                    mvn -B -s $MAVEN_SETTINGS -Darguments='-DskipTests' -DlocalCheckout=true -Dusername=${GIT_LOGIN} -Dpassword=${GIT_PASSWORD} release:perform
                  """
                }
              }
            }
            slackSend(
              color: "GREEN",
              channel: "eng-daikon",
              message: "Daikon version ${params.release_version} released (next version: ${params.next_version}) <https://github.com/Talend/daikon/blob/master/releases/${params.release_version}.adoc|${params.release_version} release notes>"
            )
        }
    }
  }
  post {
    always {
      junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
    }
  }
}
