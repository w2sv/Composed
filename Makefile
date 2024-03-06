SHELL=/bin/bash

VERSION := $(shell grep -Po '^version=\K.*' gradle.properties)

publish:
	@echo "###### Assembling & running checks ######"
	@./gradlew build
	@echo "###### Pushing latest changes ######"
	@git status;git add .;git commit -m '$(VERSION)';git push
	@echo "###### Creating release ######"
	@gh release create $(VERSION) --generate-notes