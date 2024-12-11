import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
	id("org.jetbrains.kotlin.jvm")
	id("org.jetbrains.kotlin.plugin.serialization")
	id("io.ktor.plugin") version "2+"
	id("com.google.protobuf") version "latest.release"
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

	runtimeOnly("io.grpc:grpc-netty-shaded:latest.release")
	implementation("io.grpc:grpc-protobuf:latest.release")
	implementation("io.grpc:grpc-stub:latest.release")
	compileOnly("org.apache.tomcat:annotations-api:latest.release")
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(11)
	}
}

application {
	mainClass = "com.github.hummel.mpp.lab6.ApplicationKt"

	val isDevelopment = project.ext.has("development")
	applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

protobuf {
	protoc {
		artifact = "com.google.protobuf:protoc:3.25.3"
	}
	plugins {
		create("grpc") {
			artifact = "io.grpc:protoc-gen-grpc-java:latest.release"
		}
	}
	generateProtoTasks {
		all().forEach {
			it.plugins {
				create("grpc")
			}
		}
	}
}