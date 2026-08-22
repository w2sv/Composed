include scripts/Makefile

test-with-coverage:
	@./gradlew koverHtmlReport

playground-usage:
	@./gradlew :playground:usage

run-playground:
	@./gradlew :playground:hotRunJvm --auto
