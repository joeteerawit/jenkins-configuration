#!/usr/bin/env groovy

/* groovylint-disable DuplicateStringLiteral, DuplicateMapLiteral */
/* groovylint-disable-next-line UnusedVariable, VariableName */
@Library('pipeline-library') _

node {
    stage('Refetch Script') {
        checkout([
            changelog: false,
            poll     : false,
            scm      : [
                '\$class'                        : 'GitSCM',
                branches                         : [
                    [name: '*/master']
                ],
                doGenerateSubmoduleConfigurations: false,
                extensions                       : [
                    [
                        '\$class'        : 'RelativeTargetDirectory',
                        relativeTargetDir: 'jenkinsfile'
                    ],
                    ['\$class': 'IgnoreNotifyCommit'],
                    ['\$class': 'WipeWorkspace']
                ],
                submoduleCfg     : [],
                userRemoteConfigs: [
                    [
                        credentialsId: 'github_credential',
                        url          : '$gitHostName/$jenkinsConfigRepo.git'
                    ]
                ]
            ]
        ])
    }

    stage('Checkout Source Code') {
        checkout([
            changelog: true,
            scm      : [
                '\$class'                        : 'GitSCM',
                branches                         : [
                    [name: '*/$branch']
                ],
                doGenerateSubmoduleConfigurations: false,
                extensions                       : [
                    [
                        // set timeout when clone huge repo
                        '\$class': 'CloneOption',
                        timeout : 60
                    ],
                    [
                        '\$class'        : 'RelativeTargetDirectory',
                        relativeTargetDir: 'src'
                    ],
                    ['\$class': 'WipeWorkspace']
                ],
                submoduleCfg     : [],
                userRemoteConfigs: [
                    [
                        credentialsId: 'github_credential',
                        url          : '$gitHostName/$projectRepo.git'
                    ]
                ]
            ]
        ])
    }

    def jf = load('$template')
    jf.defaultPipeline('$jobname')
}
