package com.github.hummel.mpp.lab2.controller

import com.github.hummel.mpp.lab2.bean.EditTaskRequest
import com.github.hummel.mpp.lab2.bean.FilterRequest
import com.github.hummel.mpp.lab2.bean.Task
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import io.ktor.utils.io.jvm.javaio.toInputStream
import java.io.File

val tasks = mutableMapOf<Int, Task>()

fun Application.configureRouting() {
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
						part.provider().toInputStream().use { input ->
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

			tasks.put(getNextAvailableId(), Task(title, status, dueDate, fileName))

			call.respond(HttpStatusCode.Created)
		}

		post("/filter-tasks") {
			val request = call.receive<FilterRequest>()
			val filterStatus = request.filterStatus

			val filteredTasks = tasks.asSequence().filter {
				it.value.status == filterStatus || filterStatus == "all"
			}.associate { it.key to it.value }

			call.respond(filteredTasks)
		}

		delete("/clear-tasks") {
			tasks.clear()

			call.respond(tasks)
		}

		put("/edit-task/{index}") {
			val index = call.parameters["index"]!!.toInt()
			val request = call.receive<EditTaskRequest>()

			tasks[index]!!.title = request.title

			call.respond(tasks)
		}
	}
}

fun getNextAvailableId(): Int = if (tasks.isEmpty()) 0 else tasks.keys.max() + 1