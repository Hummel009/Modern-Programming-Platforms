import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
	id("org.jetbrains.kotlin.jvm")
	id("org.jetbrains.kotlin.plugin.serialization")
	id("io.ktor.plugin")
}

group = "com.github.hummel"
version = LocalDate.now().format(DateTimeFormatter.ofPattern("yy.MM.dd"))

dependencies {
	implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
	implementation("io.ktor:ktor-server-content-negotiation-jvm")

	implementation("io.ktor:ktor-server-core-jvm")
	implementation("io.ktor:ktor-server-netty-jvm")
	implementation("io.ktor:ktor-server-cors")

	implementation("io.insert-koin:koin-ktor:latest.release")
	implementation("io.insert-koin:koin-logger-slf4j:latest.release")
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(11)
	}
}

application {
	mainClass = "com.github.hummel.mpp.lab2.ApplicationKt"

	val isDevelopment = project.ext.has("development")
	applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}