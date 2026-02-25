def defaultPipeline(jobname = null) {
    dir('src') {
        stage('Install Dependencies') {
            maven.InstallDependency()
        }

        stage('SonarScanner') {
            def projectKey = jobname
            def projectName = jobname
            sonar.Scan(
                    projectKey,
                    projectName,
            )
        }

        stage('DependencyCheck') {
            def project = jobname
            def projectPath = sh(script: 'pwd', returnStdout: true).trim()
            owasp.DependencyCheck(
                    project,
                    projectPath,
            )
        }
    }
}

return this
