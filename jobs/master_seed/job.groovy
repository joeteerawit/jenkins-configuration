#!/usr/bin/env groovy
import com.pipeline.FolderConfig

[
    new FolderConfig(name: 'example'),
].each { cfg ->
    folder(cfg.name)
    job(cfg.seedJobName) {
        description "Seed Job for ${cfg.name}"
        disabled(false)
        concurrentBuild(false)
        logRotator(-1, 5)

        scm {
            git {
                remote {
                    url("${GIT_HOST_NAME}/${JENKINS_CONFIGURATION_REPO}")
                    credentials(cfg.credentialsId)
                }
                branch(cfg.branch)
            }
        }

        steps {
            jobDsl {
                targets(cfg.seedTargets)
                additionalClasspath('src/main/groovy')
                sandbox(false)
                ignoreExisting(false)
            }
        }
    }

    // generate the folder's own jobs without waiting for someone to click build
    queue(cfg.seedJobName)
}
