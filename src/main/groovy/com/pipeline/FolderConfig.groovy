package com.pipeline

/**
 * One folder of generated jobs, plus the seed job that generates them.
 */
class FolderConfig {

    String name
    String branch = 'master'
    String credentialsId = 'github_credential'

    String getSeedJobName() {
        return "${this.name}/seed_job"
    }

    String getSeedTargets() {
        return "${this.name}/seeds/*.groovy"
    }

}
