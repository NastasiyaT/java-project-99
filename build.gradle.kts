import org.siouan.frontendgradleplugin.infrastructure.gradle.InstallFrontendTask
import kotlin.io.path.Path
import java.nio.file.Files

plugins {
	java
	jacoco
	checkstyle
	id("org.springframework.boot") version "3.1.5"
	id("io.spring.dependency-management") version "1.1.3"
	id("org.siouan.frontend-jdk17") version "8.0.0"
	id ("io.sentry.jvm.gradle") version "3.14.0"
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_20
}

jacoco {
	toolVersion = "0.8.9"
	reportsDirectory = layout.buildDirectory.dir("reports/jacoco")
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
	maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter:3.1.0")
	implementation("org.springframework.boot:spring-boot-starter-web:3.1.0")
	implementation("org.springframework.boot:spring-boot-devtools:3.0.4")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")

    compileOnly("org.projectlombok:lombok:1.18.30")
	annotationProcessor("org.projectlombok:lombok:1.18.30")

	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
	implementation("org.openapitools:jackson-databind-nullable:0.2.6")

	implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.0.4")
	implementation("org.springframework.boot:spring-boot-starter-validation:3.1.5")
	runtimeOnly("com.h2database:h2:2.1.214")

	implementation("org.springframework.boot:spring-boot-configuration-processor:3.1.2")
		implementation("org.springframework.boot:spring-boot-starter-security:3.0.4")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server:3.1.0")

	testImplementation(platform("org.junit:junit-bom:5.10.0"))
	testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.0")
	testImplementation("org.springframework.security:spring-security-test:6.0.2")
	testImplementation("net.javacrumbs.json-unit:json-unit-assertj:3.2.2")
	implementation("net.datafaker:datafaker:2.0.1")
	implementation("org.instancio:instancio-junit:3.3.0")

	testCompileOnly("org.projectlombok:lombok:1.18.30")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
}

tasks.test {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required = true
		csv.required = false
		html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
	}
}

frontend {
	nodeVersion.set("18.17.1")
	assembleScript.set("run build")
	cleanScript.set("run clean")
	checkScript.set("run check")
	verboseModeEnabled.set(true)
}

tasks.named<InstallFrontendTask>("installFrontend") {
	val ciPlatformPresent = providers.environmentVariable("CI").isPresent()
	val lockFilePath = "${projectDir}/package-lock.json"
	val retainedMetadataFileNames: Set<String>
	if (ciPlatformPresent) {
		installScript.set("ci")
		retainedMetadataFileNames = setOf(lockFilePath)
	} else {
		val acceptableMetadataFileNames = listOf(lockFilePath, "${projectDir}/yarn.lock")
		retainedMetadataFileNames = mutableSetOf("${projectDir}/package.json")
		for (acceptableMetadataFileName in acceptableMetadataFileNames) {
			if (Files.exists(Path(acceptableMetadataFileName))) {
				retainedMetadataFileNames.add(acceptableMetadataFileName)
				break
			}
		}
		outputs.file(lockFilePath).withPropertyName("lockFile")
	}
	inputs.files(retainedMetadataFileNames).withPropertyName("metadataFiles")
	outputs.dir("${projectDir}/node_modules").withPropertyName("nodeModulesDirectory")
}

buildscript {
	repositories {
		mavenCentral()
	}
}

sentry {
	includeSourceContext = true

	org = "anastasiya-trusova"
	projectName = "java-spring-boot"
	authToken = System.getenv("SENTRY_AUTH_TOKEN")
}
