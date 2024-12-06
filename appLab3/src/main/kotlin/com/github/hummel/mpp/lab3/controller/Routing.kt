package com.github.hummel.mpp.lab3.controller

import com.github.hummel.mpp.lab3.dto.EditTaskRequest
import com.github.hummel.mpp.lab3.dto.UserRequest
import com.github.hummel.mpp.lab3.entity.Task
import com.github.hummel.mpp.lab3.generateToken
import com.github.hummel.mpp.lab3.isValidToken
import com.github.hummel.mpp.lab3.isValidUser
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

val tasks: MutableMap<Int, Task> = mutableMapOf()

private val gson: Gson = Gson()

fun Application.configureRouting() {
	routing {
		route("/api/v1") {
			route("/tasks") {
				get {
					val jsonResponse = gson.toJson(tasks)

					call.respond(jsonResponse)
				}

				delete {
					tasks.clear()

					val jsonResponse = gson.toJson(tasks)

					call.respond(jsonResponse)
				}

				post("/add") {
					val multipart = call.receiveMultipart()

					var title = ""
					var status = ""
					var dueDate = ""

					multipart.forEachPart { part ->
						when (part) {
							is PartData.FormItem -> {
								when (part.name) {
									"title" -> title = part.value
									"status" -> status = part.value
									"dueDate" -> dueDate = part.value
								}
							}

							is PartData.FileItem -> {}
							is PartData.BinaryChannelItem -> {}
							is PartData.BinaryItem -> {}
						}
						part.dispose()
					}

					tasks[getNextAvailableId()] = Task(title, status, dueDate)

					call.respond(HttpStatusCode.OK)
				}

				put("/{taskId}") {
					val taskId = call.parameters["taskId"]?.toInt() ?: throw Exception()

					val jsonRequest = call.receiveText()
					val editTaskRequest = gson.fromJson(jsonRequest, EditTaskRequest::class.java)

					val title = editTaskRequest.newTitle

					tasks.getValue(taskId).title = title

					val jsonResponse = gson.toJson(tasks)

					call.respond(jsonResponse)
				}

				get("/statuses/{status}") {
					val status = call.parameters["status"] ?: throw Exception()

					val filteredTasks = tasks.asSequence().filter {
						status == "all" || it.value.status == status
					}.associate { it.key to it.value }

					val jsonResponse = gson.toJson(filteredTasks)

					call.respond(jsonResponse)
				}
			}

			post("/login") {
				val jsonRequest = call.receiveText()

				val userRequest = gson.fromJson(jsonRequest, UserRequest::class.java)
				val user = userRequest.toEntity()

				if (isValidUser(user)) {
					val textResponse = generateToken(user)

					call.respond(textResponse)
				} else {
					call.respond(HttpStatusCode.Unauthorized)
				}
			}

			post("/token") {
				val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")

				if (isValidToken(token)) {
					call.respond(HttpStatusCode.OK)
				} else {
					call.respond(HttpStatusCode.Unauthorized)
				}
			}
		}
	}
}

fun getNextAvailableId(): Int = if (tasks.isEmpty()) 0 else tasks.keys.max() + 1