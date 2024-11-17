package com.github.hummel.mpp.lab2.controller

import com.github.hummel.mpp.lab2.dto.EditTaskRequest
import com.github.hummel.mpp.lab2.dto.FilterRequest
import com.github.hummel.mpp.lab2.entity.Task
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.jvm.javaio.*
import java.io.File

val tasks: MutableMap<Int, Task> = mutableMapOf<Int, Task>()

private val gson: Gson = Gson()

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

			call.respond(HttpStatusCode.OK)
		}

		get("{...}") {
			call.respond(HttpStatusCode.NotFound)
		}
	}
}

fun getNextAvailableId(): Int = if (tasks.isEmpty()) 0 else tasks.keys.max() + 1