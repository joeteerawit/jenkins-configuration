#!/usr/bin/env groovy
import com.pipeline.JobConfig

def globals = [configRepo: "${JENKINS_CONFIGURATION_REPO}", gitHost: "${GIT_HOST_NAME}"]
def wrapper = readFileFromWorkspace('upsteam_jobs/default.groovy')

[
    new JobConfig(
        repository: 'joecomscience/spring-boot-demo',
        jobFolder: 'example',
        jobName: 'spring-boot-demo',
        template: 'java-maven',
    ),
].each { cfg ->
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
                script(cfg.pipelineScript(wrapper, globals))
            }
        }
    }
}
