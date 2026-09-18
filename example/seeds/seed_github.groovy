#!/usr/bin/env groovy
import com.pipeline.JobConfig

def globals = [
    jenkinsHome: "${JENKINS_HOME}",
    configRepo : "${JENKINS_CONFIGURATION_REPO}",
    gitHost    : "${GIT_HOST_NAME}",
]

[
    new JobConfig(
        repository: 'joecomscience/spring-boot-demo',
        jobFolder: 'example',
        jobName: 'spring-boot-demo',
    ),
].each { cfg ->
    folder(cfg.jobFolder)
    pipelineJob(cfg.fullName) {
        description "Pipeline for ${cfg.jobName}"
        disabled(false)
        logRotator(-1, 5)

        properties {
            disableConcurrentBuilds()
            githubProjectUrl("${GIT_HOST_NAME}/${cfg.repository}.git")
            pipelineTriggers {
                triggers {
                    githubPush()
                }
            }
        }

        definition {
            cps {
                sandbox(true)
                script(cfg.pipelineScript(globals))
            }
        }
    }
}
