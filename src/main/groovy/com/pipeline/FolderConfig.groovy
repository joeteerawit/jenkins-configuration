package com.pipeline

/**
 * One folder of generated jobs, plus the seed job that generates them. The name
 * nests freely: 'acme', 'acme/platform', 'acme/platform/tooling'.
 */
class FolderConfig {

    String name
    String branch = 'master'
    String credentialsId = 'github_credential'

    /** job dsl will not create acme/platform before acme, so hand back the whole chain */
    List<String> getAncestors() {
        return this.name.tokenize('/').inject([]) { chain, part ->
            chain << (chain ? "${chain.last()}/${part}".toString() : part)
        }
    }

    String getSeedJobName() {
        return "${this.name}/seed_job"
    }

    String getSeedTargets() {
        return "teams/${this.name}/*.groovy"
    }

}
