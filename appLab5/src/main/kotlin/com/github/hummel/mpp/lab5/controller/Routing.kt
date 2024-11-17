package com.github.hummel.mpp.lab5.controller

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.github.hummel.mpp.lab5.entity.Task
import com.github.hummel.mpp.lab5.generateToken
import com.github.hummel.mpp.lab5.isValidToken
import com.github.hummel.mpp.lab5.isValidUser
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

fun SchemaBuilder.configureSchema() {
	mutation("login") {
		resolver { username: String, password: String ->
			if (isValidUser(username, password)) {
				generateToken(username, password)
			} else {
				"Unauthorized"
			}
		}
	}

	mutation("token") {
		resolver { token: String ->
			if (isValidToken(token)) {
				"OK"
			} else {
				"Unauthorized"
			}
		}
	}

	query("get_tasks") {
		resolver { ->
			gson.toJson(tasks)
		}
	}

	mutation("clear_tasks") {
		resolver { ->
			tasks.clear()

			gson.toJson(tasks)
		}
	}

	mutation("filter_tasks") {
		resolver { filter: String ->
			val filteredTasks = tasks.asSequence().filter {
				it.value.status == filter || filter == "all"
			}.associate { it.key to it.value }

			gson.toJson(filteredTasks)
		}
	}

	mutation("edit_task") {
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