#!/usr/bin/env groovy

[
    [
        name: 'example'
    ]
].each { item ->
    def entity = item['name']

    folder(entity)
    job("${entity}/seed_job") {
        description "Seed Job for ${entity}"
        disabled(false)
        concurrentBuild(false)
        logRotator(-1, 5)

        scm {
            git {
                remote {
                    url("${GIT_HOST_NAME}/${JENKINS_CONFIGURATION_REPO}")
                    credentials('git_credential')
                }
                branch('master')
            }
        }

        steps {
            jobDsl {
                targets("${entity}/seeds/*.groovy")
                sandbox(false)
                ignoreExisting(false)
            }
        }
    }
}
