#!groovy

pipeline {
    agent {
        label 'linux'
    }
    parameters {
        string(name: 'RELEASE_VERSION', defaultValue: '9.0.0', description: '')
        string(name: 'POST_RELEASE_SNAPSHOT_VERSION', defaultValue: '9.0.1-SNAPSHOT', description: '')
        string(name: 'TEST_ONLY', defaultValue: 'true', description: '')
        string(name: 'DRY_RUN', defaultValue: 'true', description: '')
    }
    tools {
        maven 'M3'
        jdk 'jdk8'
    }
    options {
        timestamps()
        skipDefaultCheckout()
        disableConcurrentBuilds()
    }
    stages {
        stage('Cleanup') {
            steps {
                cleanWs()
            }
        }
        stage('Clone') {
            steps {
                sshagent(['github-creds']) {
                    git credentialsId: 'github-creds', url: 'git@github.com:deliverymind/wiremock-maven-plugin.git'
                }
            }
        }
        stage('Purge') {
            steps {
                sh 'rm -rf ~/.m2/repository/uk/co/deliverymind/wiremock-maven-plugin/'
            }
        }
        stage('Set release version number') {
            when {
                expression {
                    "${params.TEST_ONLY}" == "false"
                }
            }
            steps {
                sh "mvn versions:set -DnewVersion=${params.RELEASE_VERSION}"
                sh "git add -A; git commit -m 'Release version bump'"
            }
        }
        stage('Integration test') {
            steps {
                sh 'mvn clean verify'
            }
            post {
                always {
                    jacoco execPattern: '**/jacoco-*.exec'
                }
            }
        }
        stage('Tag release') {
            when {
                expression {
                    "${params.TEST_ONLY}" == "false"
                }
            }
            steps {
                sh "git tag ${params.RELEASE_VERSION}"
            }
        }
        stage('Release artefacts') {
            when {
                expression {
                    "${params.TEST_ONLY}" == "false" && "${params.DRY_RUN}" == "false" && "${env.BRANCH_NAME}" == "master"
                }
            }
            steps {
                withCredentials([string(credentialsId: 'gpg-passphrase', variable: 'GPGPP')]) {
                    sh "mvn clean deploy -P release -Dgpg.passphrase=${GPGPP} -Dskip.integration.tests=true"
                }
            }
        }
        stage('Set snapshot version number') {
            when {
                expression {
                    "${params.TEST_ONLY}" == "false"
                }
            }
            steps {
                sh "mvn versions:set -DnewVersion=${POST_RELEASE_SNAPSHOT_VERSION}"
                sh "git add -A; git commit -m 'Post-release version bump'"
            }
        }
        stage('Push release to origin') {
            when {
                expression {
                    "${params.TEST_ONLY}" == "false" && "${params.DRY_RUN}" == "false" && "${env.BRANCH_NAME}" == "master"
                }
            }
            steps {
                sshagent(['github-creds']) {
                    sh "git push --set-upstream origin master; git push --tags"
                }
            }
        }
    }
}