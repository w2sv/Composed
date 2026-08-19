include scripts/Makefile

test-with-coverage:
	@./gradlew koverHtmlReport

playground-usage:
	@./gradlew :playground:usage
