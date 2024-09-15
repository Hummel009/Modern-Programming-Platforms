package com.github.hummel.mpp.lab3.controller

import com.github.hummel.mpp.lab3.bean.EditTaskRequest
import com.github.hummel.mpp.lab3.bean.FilterRequest
import com.github.hummel.mpp.lab3.bean.Task
import com.github.hummel.mpp.lab3.bean.User
import com.github.hummel.mpp.lab3.generateToken
import com.github.hummel.mpp.lab3.isValidToken
import com.github.hummel.mpp.lab3.isValidUser
import io.ktor.http.Cookie
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import java.io.File

val tasks = mutableMapOf<Int, Task>()

fun Application.configureRouting() {
	routing {
		post("/login") {
			val user = call.receive<User>()

			if (isValidUser(user)) {
				val token = generateToken(user)

				call.response.cookies.append(
					Cookie(name = "jwt", value = token, httpOnly = true, secure = false)
				)
				call.respond(HttpStatusCode.OK)
			} else {
				call.respond(HttpStatusCode.Unauthorized)
			}
		}

		get("/token") {
			val token = call.request.cookies["jwt"]

			if (isValidToken(token)) {
				call.respond(HttpStatusCode.OK)
			} else {
				call.respond(HttpStatusCode.Unauthorized)
			}
		}

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

			tasks.put(getNextAvailableId(), Task(title, status, dueDate, fileName))

			call.respond(HttpStatusCode.OK)
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

			call.respond(HttpStatusCode.OK)
		}

		put("/edit-task") {
			val request = call.receive<EditTaskRequest>()

			val index = request.index
			val title = request.title

			tasks[index]!!.title = title

			call.respond(HttpStatusCode.OK)
		}

		post("/{...}") {
			call.respond(HttpStatusCode.NotFound)
		}
	}
}

fun getNextAvailableId(): Int = if (tasks.isEmpty()) 0 else tasks.keys.max() + 1