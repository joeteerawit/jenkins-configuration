package com.pipeline

import groovy.text.SimpleTemplateEngine

/**
 * One generated pipeline job. Defaults here are the defaults for every seed file.
 */
class JobConfig {

    String repository
    String jobFolder
    String jobName
    String template = 'Jenkinsfile'
    String branch = 'master'

    String getFullName() {
        return "${this.jobFolder}/${this.jobName}"
    }

    /* groovylint-disable JavaIoPackageAccess */
    String pipelineScript(Map globals) {
        String scriptPath =
            "${globals.jenkinsHome}/workspace/${this.jobFolder}/seed_job/upsteam_jobs/default.groovy"
        String upSteamJobScript = new File(scriptPath).text.stripIndent().trim()
        /* groovylint-enable JavaIoPackageAccess */
        Map dataBindingToTemplate = [
            'jenkinsConfigRepo': globals.configRepo,
            'gitHostName'      : globals.gitHost,
            'branch'           : this.branch.toLowerCase(),
            'projectRepo'      : this.repository,
            'jobname'          : this.jobName,
            'template'         : "jenkinsfile/${this.jobFolder}/templates/${this.template}.groovy",
        ]
        return new SimpleTemplateEngine()
            .createTemplate(upSteamJobScript)
            .make(dataBindingToTemplate)
            .toString()
    }

}
