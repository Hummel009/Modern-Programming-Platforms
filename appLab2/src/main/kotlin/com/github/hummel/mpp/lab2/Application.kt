package com.github.hummel.mpp.lab2

import com.github.hummel.mpp.lab2.controller.configureRouting
import com.github.hummel.mpp.lab2.controller.tasks
import com.github.hummel.mpp.lab2.entity.Task
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import kotlin.random.Random

fun main() {
	for (i in 0..9) {
		tasks[i] = Task(
			title = "title$i",
			status = if (Random.nextBoolean()) "completed" else "pending",
			dueDate = "dueDate$i"
		)
	}

	embeddedServer(Netty, port = 2999, module = Application::module).start(wait = true)
}

fun Application.module() {
	install(CORS) {
		anyHost()
		allowCredentials = true
		allowMethod(HttpMethod.Post)
		allowMethod(HttpMethod.Put)
		allowMethod(HttpMethod.Get)
		allowMethod(HttpMethod.Options)
		allowHeader(HttpHeaders.ContentType)
		allowHeader(HttpHeaders.Authorization)
		allowHeader(HttpHeaders.AccessControlAllowOrigin)
	}
	configureRouting()
}