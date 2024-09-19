package com.github.hummel.mpp.lab5.controller

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.github.hummel.mpp.lab5.bean.Task
import com.github.hummel.mpp.lab5.bean.TaskWrapper
import com.github.hummel.mpp.lab5.generateToken
import com.github.hummel.mpp.lab5.isValidToken
import com.github.hummel.mpp.lab5.isValidUser
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

fun SchemaBuilder.configureSchema() {
	mutation("edit_task") {
		resolver { index: Int, title: String ->
			tasks[index]!!.title = title

			"OK"
		}
	}

	mutation("clear_tasks") {
		resolver { ->
			tasks.clear()

			"OK"
		}
	}

	query("get_tasks") {
		resolver { ->
			tasks.toTaskWrapperList()
		}
	}

	query("filter_tasks") {
		resolver { filterStatus: String ->
			tasks.asSequence().filter {
				it.value.status == filterStatus || filterStatus == "all"
			}.associate { it.key to it.value }.toMutableMap().toTaskWrapperList()
		}
	}

	mutation("login") {
		resolver { username: String, password: String ->
			if (isValidUser(username, password)) {
				val token = generateToken(username, password)

				token
			} else {
				throw Exception()
			}
		}
	}
}

fun Application.configureRouting() {
	routing {
		get("/token") {
			val token = call.request.cookies["jwt"]

			if (isValidToken(token)) {
				call.respond(HttpStatusCode.OK)
			} else {
				call.respond(HttpStatusCode.Unauthorized)
			}
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

		post("/{...}") {
			call.respond(HttpStatusCode.NotFound)
		}
	}
}

fun getNextAvailableId(): Int = if (tasks.isEmpty()) 0 else tasks.keys.max() + 1

fun MutableMap<Int, Task>.toTaskWrapperList(): List<TaskWrapper> = map { TaskWrapper(it.key, it.value) }