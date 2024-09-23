package com.github.hummel.mpp.lab5.controller

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.github.hummel.mpp.lab5.entity.Task
import com.github.hummel.mpp.lab5.generateToken
import com.github.hummel.mpp.lab5.isValidToken
import com.github.hummel.mpp.lab5.isValidUser
import com.google.gson.Gson
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import java.io.File

val tasks = mutableMapOf<Int, Task>()
val gson = Gson()

fun SchemaBuilder.configureSchema() {
	query("login") {
		resolver { username: String, password: String ->
			if (isValidUser(username, password)) {
				val textResponse = generateToken(username, password)
				textResponse
			} else {
				"Unauthorized"
			}
		}
	}

	query("token") {
		resolver { token: String ->
			if (!isValidToken(token)) {
				"OK"
			}

			"Unauthorized"
		}
	}

	query("get_tasks") {
		resolver { ->
			gson.toJson(tasks)
		}
	}

	query("clear_tasks") {
		resolver { ->
			tasks.clear()

			gson.toJson(tasks)
		}
	}

	query("filter_tasks") {
		resolver { filter: String ->
			val filteredTasks = tasks.asSequence().filter {
				it.value.status == filter || filter == "all"
			}.associate { it.key to it.value }

			gson.toJson(filteredTasks)
		}
	}

	query("edit_task") {
		resolver { index: Int, title: String ->
			tasks[index]!!.title = title

			gson.toJson(tasks)
		}
	}
}

fun Application.configureRouting() {
	routing {
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