#!/usr/bin/env groovy
import com.pipeline.DefaultTemplateHelper

[
    [
        repository: 'joecomscience/spring-boot-demo',
        jobfolder : 'example',
        jobname   : 'spring-boot-demo',
        template  : 'Jenkinsfile',
    ]
].each { item ->
    def projectRepo = item['repository']
    def jobfolder = item['jobfolder']
    def jobname = item['jobname']
    def branch = item.containsKey('branch') ? item['branch'].toLowerCase() : 'master'
    def jobTemplateFile = item.containsKey('template') ? item['template'] : 'Jenkinsfile'

    def pipeline = new DefaultTemplateHelper(
        jobFolder: jobfolder,
        jobName: jobname,
        repository: projectRepo,
        templateFileName: jobTemplateFile,
    )
    def pipelineScript = pipeline.getPipelineScript()

    folder(jobfolder)
    pipelineJob("${jobfolder}/${jobname}") {
        description "Pipeline for ${jobname}"
        disabled(false)
        logRotator(-1, 5)

        properties {
            disableConcurrentBuilds()
            pipelineTriggers {
                triggers {
                    gitlab {
                        branchFilterType('NameBasedFilter')
                        includeBranchesSpec(branch)
                        secretToken("${GITLAB_TOKEN}")
                        triggerOnAcceptedMergeRequest(false)
                        triggerOnApprovedMergeRequest(false)
                        triggerOnMergeRequest(false)
                        triggerOnPipelineEvent(false)
                        triggerOpenMergeRequestOnPush('never')
                    }
                }
            }
        }

        definition {
            cps {
                sandbox(true)
                script(pipelineScript.toString())
            }
        }
    }
}
