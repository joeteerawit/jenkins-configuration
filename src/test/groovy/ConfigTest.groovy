import com.pipeline.FolderConfig
import com.pipeline.JobConfig

/**
 * Unit tests for the seed classes. Plain asserts, no framework: run with
 * `make test`. The job dsl itself needs a real controller, scripts/verify.sh
 * in the base image repo covers that.
 */

def wrapper = '''
    @Library('pipeline-library@$libraryVersion') _
    url = '$gitHostName/${jenkinsConfigRepo}.git'
    repo = '$projectRepo', branch = '$branch'
    load('$template').defaultPipeline(jobname: '$jobname', sonarProjectKey: '$sonarProjectKey')
'''
def globals = [configRepo: 'acme/jenkins-configuration', gitHost: 'https://github.com']
def job = { Map overrides = [:] ->
    new JobConfig([
        repository: 'acme/demo', jobFolder: 'acme/platform',
        jobName: 'demo', template: 'java-maven',
    ] + overrides)
}

// a folder cannot be created before its parent
assert new FolderConfig(name: 'acme').ancestors == ['acme']
assert new FolderConfig(name: 'acme/platform/tooling').ancestors ==
    ['acme', 'acme/platform', 'acme/platform/tooling']
assert new FolderConfig(name: 'acme/platform').seedTargets == 'teams/acme/platform/*.groovy'
assert new FolderConfig(name: 'acme/platform').seedJobName == 'acme/platform/seed_job'

// two teams' demo must not land on the same sonar dashboard
assert job().sonarProjectKey == 'acme-platform-demo'
assert job(jobFolder: 'other').sonarProjectKey == 'other-demo'

def script = job().pipelineScript(wrapper, globals)
assert script.contains("@Library('pipeline-library@master')")
assert script.contains("load('jenkinsfile/templates/java-maven.groovy')")
assert script.contains("sonarProjectKey: 'acme-platform-demo'")
assert script.contains("url = 'https://github.com/acme/jenkins-configuration.git'")
assert script.contains("repo = 'acme/demo', branch = 'master'")

// a pinned library version and a non-default branch reach the wrapper
def pinned = job(libraryVersion: 'v1.2.0', branch: 'RELEASE').pipelineScript(wrapper, globals)
assert pinned.contains("@Library('pipeline-library@v1.2.0')")
assert pinned.contains("branch = 'release'")

// the stack is not guessable, so a missing template must fail the seed, not the build
def failed = false
try {
    job(template: null).pipelineScript(wrapper, globals)
} catch (AssertionError expected) {
    failed = expected.message.contains('template is required')
}
assert failed, 'a JobConfig without a template should refuse to render'

// job dsl makes a class out of each script file name and rejects anything else
new File('teams').eachFileRecurse { file ->
    if (file.name.endsWith('.groovy')) {
        assert file.name ==~ /[A-Za-z_][A-Za-z0-9_]*\.groovy/,
            "job dsl will not load '${file.name}': letters, digits and underscores only"
    }
}

println 'PASS: seed config tests'
