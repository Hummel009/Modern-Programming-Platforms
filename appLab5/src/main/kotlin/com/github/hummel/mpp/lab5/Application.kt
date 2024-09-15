package com.github.hummel.mpp.lab5

import com.apurebase.kgraphql.GraphQL
import com.github.hummel.mpp.lab5.bean.Task
import com.github.hummel.mpp.lab5.controller.configureGraphRouting
import com.github.hummel.mpp.lab5.controller.configureRouting
import com.github.hummel.mpp.lab5.controller.tasks
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.CORS
import java.io.File

fun main() {
	val uploadsDir = File("uploads")
	if (!uploadsDir.exists()) {
		uploadsDir.mkdirs()
	}

	embeddedServer(Netty, port = 3000, module = Application::module).start(wait = true)
}

fun Application.module() {
	tasks[0] = Task("Sus", "Sus", "Sus", null)
	install(ContentNegotiation) {
		json()
	}
	install(GraphQL) {
		configureGraphRouting()
		playground = true
		schema {
			mutation("edit_task") {
				resolver { index: Int, name: String ->
					tasks[index]!!.title = name
				}
			}
		}
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
	configureRouting()
}