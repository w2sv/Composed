include scripts/Makefile

test-with-coverage:
	@./gradlew koverHtmlReport

show-playground-usage:
	@./gradlew :playground:usage

run-playground:
	@./gradlew :playground:hotRunJvm --auto

run-playground-web-app:
	@./gradlew :playground:wasmJsBrowserDevelopmentRun
