package com.github.hummel.mpp.lab4

import com.github.hummel.mpp.lab4.controller.configureRouting
import com.github.hummel.mpp.lab4.controller.configureWebSocket
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.websocket.WebSockets
import java.io.File

fun main() {
	val uploadsDir = File("uploads")
	if (!uploadsDir.exists()) {
		uploadsDir.mkdirs()
	}

	embeddedServer(Netty, port = 2999, module = Application::module).start(wait = true)
}

fun Application.module() {
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