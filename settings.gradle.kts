pluginManagement {
	repositories {
		maven { url = uri("https://repo.spring.io/milestone") }
		maven { url = uri("https://repo.spring.io/snapshot") }
		gradlePluginPortal()
	}

	plugins {
		id("org.siouan.frontend-jdk17") version "8.0.0"
	}
}
rootProject.name = "app"
