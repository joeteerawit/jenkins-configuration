package com.pipeline

import groovy.text.SimpleTemplateEngine

/**
 * One generated pipeline job. Defaults here are the defaults for every team,
 * a project overrides only what it actually differs on.
 */
class JobConfig {

    String repository
    String jobFolder
    String jobName
    /** file under templates/ without the extension, ie. the project's tech stack */
    String template
    String branch = 'master'
    /** pin to a tag so a shared library change cannot break this job unannounced */
    String libraryVersion = 'master'

    String getFullName() {
        return "${this.jobFolder}/${this.jobName}"
    }

    /** unique across teams, so two spring-boot-demos do not share a sonar dashboard */
    String getSonarProjectKey() {
        return this.fullName.replace('/', '-')
    }

    /**
     * @param wrapper upsteam_jobs/default.groovy, read with readFileFromWorkspace
     * @param globals gitHost and configRepo, exported to every build by the base image
     */
    String pipelineScript(String wrapper, Map globals) {
        assert this.template: "${this.fullName}: template is required, name a file under templates/"
        Map dataBindingToTemplate = [
            'jenkinsConfigRepo': globals.configRepo,
            'gitHostName'      : globals.gitHost,
            'libraryVersion'   : this.libraryVersion,
            'branch'           : this.branch.toLowerCase(),
            'projectRepo'      : this.repository,
            'jobname'          : this.jobName,
            'sonarProjectKey'  : this.sonarProjectKey,
            'template'         : "jenkinsfile/templates/${this.template}.groovy",
        ]
        return new SimpleTemplateEngine()
            .createTemplate(wrapper.stripIndent().trim())
            .make(dataBindingToTemplate)
            .toString()
    }

}
