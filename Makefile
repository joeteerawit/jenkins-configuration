IMAGE ?= joewalker/jenkins-master
GROOVY_JAR = .cache/groovy-all.jar

jenkins_up:
	docker compose up -d

# unit tests for the seed classes, using the same groovy the seed job runs
$(GROOVY_JAR):
	@mkdir -p $(dir $@)
	docker run --rm $(IMAGE) sh -c 'unzip -p /usr/share/jenkins/jenkins.war "WEB-INF/lib/groovy-all-*.jar"' > $@

test: $(GROOVY_JAR)
	docker run --rm -v "$(PWD):/work" -w /work $(IMAGE) \
	  java -cp "$(GROOVY_JAR):src/main/groovy" groovy.ui.GroovyMain src/test/groovy/ConfigTest.groovy

lint_fix:
	npx npm-groovy-lint --fix "**/*.groovy"

lint_format:
	npx npm-groovy-lint --format .