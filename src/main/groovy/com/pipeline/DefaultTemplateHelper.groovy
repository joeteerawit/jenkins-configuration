package com.pipeline

import groovy.text.SimpleTemplateEngine

/**
 * Helper class for generating Jenkins pipeline job scripts from templates.
 */
class DefaultTemplateHelper {

    String jobFolder
    String jobName
    String jenkinsConfigRepo = this.global.JENKINS_CONFIGURATION_REPO
    String gitHostName = this.global.GIT_HOST_NAME
    String branch = 'master'
    String repository
    String templateFileName
    private final String jenkinsHome = this.global.JENKINS_HOME
    def global

    /* groovylint-disable JavaIoPackageAccess */
    String getPipelineScript() {
        String scriptPath =
            "${this.jenkinsHome}/workspace/${this.jobFolder}/seed_job/upsteam_jobs/default.groovy"
        SimpleTemplateEngine templateEngine = new SimpleTemplateEngine()
        String upSteamJobScript = new File(scriptPath).text.stripIndent().trim()
        /* groovylint-enable JavaIoPackageAccess */
        Map dataBindingToTemplate = [
            'jenkinsConfigRepo': "${this.jenkinsConfigRepo}",
            'gitHostName'      : "${this.gitHostName}",
            'branch'           : "${this.branch}",
            'projectRepo'      : "${this.repository}",
            'jobname'          : "${this.jobName}",
            'template'         : "jenkinsfile/${this.jobName}/templates/${this.templateFileName}.groovy",
        ]
        return templateEngine
            .createTemplate(upSteamJobScript)
            .make(dataBindingToTemplate)
    }

}

