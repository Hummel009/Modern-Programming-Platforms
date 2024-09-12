package com.github.hummel.mpp.lab1

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.request.receiveMultipart
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class Task(val title: String, val status: String, val dueDate: String, val file: String?)

val tasks = mutableListOf<Task>()

fun main() {
	val uploadsDir = File("uploads")
	if (!uploadsDir.exists()) {
		uploadsDir.mkdirs()
	}

	embeddedServer(Netty, port = 3000, module = Application::module).start(wait = true)
}

fun Application.module() {
	install(ContentNegotiation) {
		json()
	}

	install(CORS) {
		anyHost()
		allowMethod(HttpMethod.Post)
		allowHeader(HttpHeaders.ContentType)
	}

	routing {
		get("/") {
			call.respond(tasks)
		}

		post("/add-task") {
			val multipart = call.receiveMultipart()
			var title = ""
			var status = ""
			var dueDate = ""
			var fileName: String? = null

			multipart.forEachPart { part ->
				when (part) {
					is PartData.FormItem -> {
						when (part.name) {
							"title" -> title = part.value
							"status" -> status = part.value
							"dueDate" -> dueDate = part.value
						}
					}

					is PartData.FileItem -> {
						fileName = part.originalFileName
						val file = File("uploads/${System.currentTimeMillis()}-$fileName")
						part.streamProvider().use { input ->
							file.outputStream().buffered().use { output ->
								input.copyTo(output)
							}
						}
					}

					is PartData.BinaryChannelItem -> {}
					is PartData.BinaryItem -> {}
				}
				part.dispose()
			}

			tasks.add(Task(title, status, dueDate, fileName))
			call.respond(HttpStatusCode.Created)
		}

		post("/filter-tasks") {
			val parameters = call.receiveParameters()
			val filterStatus = parameters["filterStatus"]

			if (filterStatus == null) {
				call.respond(HttpStatusCode.BadRequest, "Отсутствует параметр filterStatus")
				return@post
			}

			val filteredTasks = tasks.filter { it.status == filterStatus || filterStatus == "all" }
			call.respond(filteredTasks)
		}
	}
}