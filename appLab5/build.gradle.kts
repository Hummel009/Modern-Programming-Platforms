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
	implementation("io.ktor:ktor-server-core")
	implementation("io.ktor:ktor-server-netty")
	implementation("io.ktor:ktor-server-cors")

	implementation("com.google.code.gson:gson:latest.release")

	implementation("com.auth0:java-jwt:latest.release")
	implementation("at.favre.lib:bcrypt:latest.release")

	implementation("com.apurebase:kgraphql:latest.release")
	implementation("com.apurebase:kgraphql-ktor:latest.release")
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(11)
	}
}

application {
	mainClass = "com.github.hummel.mpp.lab5.ApplicationKt"

	val isDevelopment = project.ext.has("development")
	applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}