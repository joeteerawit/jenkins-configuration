/** Java projects built with maven. Stage bodies live in the pipeline-library. */
def defaultPipeline(Map args) {
    dir('src') {
        stage('Install Dependencies') {
            maven.InstallDependency()
        }

        stage('SonarScanner') {
            sonar.Scan(
                    args.sonarProjectKey,
                    args.jobname,
            )
        }

        stage('DependencyCheck') {
            owasp.DependencyCheck(
                    args.jobname,
                    pwd(),
            )
        }
    }
}

return this
