package com.github.hummel.mpp.lab4

import com.github.hummel.mpp.lab4.controller.configureRouting
import com.github.hummel.mpp.lab4.controller.configureWebSocket
import com.github.hummel.mpp.lab4.controller.tasks
import com.github.hummel.mpp.lab4.entity.Task
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.websocket.*
import java.io.File
import kotlin.random.Random

fun main() {
	val uploadsDir = File("uploads")
	if (!uploadsDir.exists()) {
		uploadsDir.mkdirs()
	}

	embeddedServer(Netty, port = 2999, module = Application::module).start(wait = true)
}

fun Application.module() {
	for (i in 0..9) {
		tasks[i] = Task(
			title = "title$i",
			status = if (Random.nextBoolean()) "completed" else "pending",
			dueDate = "dueDate$i",
			file = null
		)
	}
	install(CORS) {
		anyHost()
		allowCredentials = true
		allowMethod(HttpMethod.Delete)
		allowMethod(HttpMethod.Post)
		allowMethod(HttpMethod.Put)
		allowMethod(HttpMethod.Get)
		allowHeader(HttpHeaders.ContentType)
	}
	install(WebSockets)
	configureRouting()
	configureWebSocket()
}