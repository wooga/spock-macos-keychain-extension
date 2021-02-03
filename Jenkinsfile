#!groovy
@Library('github.com/wooga/atlas-jenkins-pipeline@1.x') _

withCredentials([string(credentialsId: 'spock_macos_keychain_extension_coveralls_token', variable: 'coveralls_token')]) {
    buildJavaLibrary plaforms: ['osx'], coverallsToken: coveralls_token
}
