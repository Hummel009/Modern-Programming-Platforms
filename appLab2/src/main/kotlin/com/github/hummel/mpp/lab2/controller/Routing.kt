package com.github.hummel.mpp.lab2.controller

import com.github.hummel.mpp.lab2.dto.EditTaskRequest
import com.github.hummel.mpp.lab2.dto.FilterRequest
import com.github.hummel.mpp.lab2.entity.Task
import com.google.gson.Gson
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receiveMultipart
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import java.io.File

val tasks = mutableMapOf<Int, Task>()
val gson = Gson()

fun Application.configureRouting() {
	routing {
		get("/get-tasks") {
			val jsonResponse = gson.toJson(tasks)

			call.respond(jsonResponse)
		}

		delete("/clear-tasks") {
			tasks.clear()

			val jsonResponse = gson.toJson(tasks)

			call.respond(jsonResponse)
		}

		post("/filter-tasks") {
			val jsonRequest = call.receiveText()

			val filterRequest = gson.fromJson(jsonRequest, FilterRequest::class.java)
			val filter = filterRequest.filter

			val filteredTasks = tasks.asSequence().filter {
				it.value.status == filter || filter == "all"
			}.associate { it.key to it.value }

			val jsonResponse = gson.toJson(filteredTasks)

			call.respond(jsonResponse)
		}

		put("/edit-task") {
			val jsonRequest = call.receiveText()

			val editTaskRequest = gson.fromJson(jsonRequest, EditTaskRequest::class.java)
			val index = editTaskRequest.index
			val title = editTaskRequest.title

			tasks[index]!!.title = title

			val jsonResponse = gson.toJson(tasks)

			call.respond(jsonResponse)
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

			tasks.put(getNextAvailableId(), Task(title, status, dueDate, fileName))

			call.respond(HttpStatusCode.OK)
		}

		get("{...}") {
			call.respond(HttpStatusCode.NotFound)
		}
	}
}

fun getNextAvailableId(): Int = if (tasks.isEmpty()) 0 else tasks.keys.max() + 1