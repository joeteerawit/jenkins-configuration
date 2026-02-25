jenkins_up:
	docker compose up -d

lint_fix:
	npx npm-groovy-lint --fix "**/*.groovy"

lint_format:
	npx npm-groovy-lint --format .