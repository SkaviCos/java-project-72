dev:
	@./gradlew run

start: dev

setup:
	@gradle wrapper --gradle-version 8.3

install:
	@./gradlew installDist

clean:
	@./gradlew clean

build:
	@./gradlew clean build

test:
	@./gradlew test

report:
	@./gradlew jacocoTestReport

lint:
	@./gradlew checkstyleMain

.PHONY: build
