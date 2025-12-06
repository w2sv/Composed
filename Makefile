include scripts/Makefile

test-with-coverage:
	@./gradlew koverHtmlReportDebug
