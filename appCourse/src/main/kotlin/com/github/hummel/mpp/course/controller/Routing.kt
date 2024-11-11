package com.github.hummel.mpp.course.controller

import com.github.hummel.mpp.course.dto.ChangePasswordRequest
import com.github.hummel.mpp.course.dto.ChangeUsernameRequest
import com.github.hummel.mpp.course.dto.EditTaskRequest
import com.github.hummel.mpp.course.dto.FilterTasksRequest
import com.github.hummel.mpp.course.dto.LoginRequest
import com.github.hummel.mpp.course.dto.ProfileRequest
import com.github.hummel.mpp.course.dto.RegisterRequest
import com.github.hummel.mpp.course.dto.TokenRequest
import com.github.hummel.mpp.course.entity.Order
import com.github.hummel.mpp.course.service.AuthService
import com.github.hummel.mpp.course.service.ProfileService
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

val tasks = mutableMapOf<Int, Order>()
val gson = Gson()

fun Application.configureRouting() {
	routing {
		post("/register") {
			val jsonRequest = call.receiveText()

			val request = gson.fromJson(jsonRequest, RegisterRequest::class.java)

			val username = request.username
			val password = request.password

			val success = AuthService.registerUser(username, password)

			if (success) {
				val textResponse = AuthService.generateToken(username, password)

				call.respond(textResponse)
			} else {
				call.respond(HttpStatusCode.Unauthorized)
			}
		}

		post("/login") {
			val jsonRequest = call.receiveText()

			val request = gson.fromJson(jsonRequest, LoginRequest::class.java)

			val username = request.username
			val password = request.password

			if (AuthService.areCredentialsValid(username, password)) {
				val textResponse = AuthService.generateToken(username, password)

				call.respond(textResponse)
			} else {
				call.respond(HttpStatusCode.Unauthorized)
			}
		}

		post("/token") {
			val jsonRequest = call.receiveText()

			val request = gson.fromJson(jsonRequest, TokenRequest::class.java)
			val token = AuthService.decomposeToken(request.token)

			val username = token?.username
			val password = token?.password

			if (AuthService.areCredentialsValid(username, password)) {
				call.respond(HttpStatusCode.OK)
			} else {
				call.respond(HttpStatusCode.Unauthorized)
			}
		}

		post("/profile") {
			val jsonRequest = call.receiveText()

			val request = gson.fromJson(jsonRequest, ProfileRequest::class.java)
			val token = AuthService.decomposeToken(request.token)

			val username = token?.username
			val password = token?.password

			if (AuthService.areCredentialsValid(username, password)) {
				val user = ProfileService.getUserData(username!!)

				if (user != null) {
					call.respond(gson.toJson(user))
				} else {
					call.respond(HttpStatusCode.Unauthorized)
				}
			} else {
				call.respond(HttpStatusCode.Unauthorized)
			}
		}

		post("/change-username") {
			val jsonRequest = call.receiveText()

			val request = gson.fromJson(jsonRequest, ChangeUsernameRequest::class.java)
			val token = AuthService.decomposeToken(request.token)
			val newUsername = request.username

			val username = token?.username
			val password = token?.password

			if (AuthService.areCredentialsValid(username, password)) {
				if (ProfileService.changeUserUsername(username!!, newUsername)) {
					call.respond(HttpStatusCode.OK)
				} else {
					call.respond(HttpStatusCode.BadRequest)
				}
			} else {
				call.respond(HttpStatusCode.Unauthorized)
			}
		}

		post("/change-password") {
			val jsonRequest = call.receiveText()

			val request = gson.fromJson(jsonRequest, ChangePasswordRequest::class.java)
			val token = AuthService.decomposeToken(request.token)
			val newPassword = request.password

			val username = token?.username
			val password = token?.password

			if (AuthService.areCredentialsValid(username, password)) {
				if (ProfileService.changeUserPassword(username!!, newPassword)) {
					call.respond(HttpStatusCode.OK)
				} else {
					call.respond(HttpStatusCode.BadRequest)
				}
			} else {
				call.respond(HttpStatusCode.Unauthorized)
			}
		}

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

			val request = gson.fromJson(jsonRequest, FilterTasksRequest::class.java)
			val filter = request.filter

			val filteredTasks = tasks.asSequence().filter {
				it.value.author == filter || filter == "all"
			}.associate { it.key to it.value }

			val jsonResponse = gson.toJson(filteredTasks)

			call.respond(jsonResponse)
		}

		put("/edit-task") {
			val jsonRequest = call.receiveText()

			val request = gson.fromJson(jsonRequest, EditTaskRequest::class.java)
			val index = request.index
			val title = request.title

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

			tasks.put(getNextAvailableId(), Order(title, status, dueDate, "fileName"))

			call.respond(HttpStatusCode.OK)
		}

		get("{...}") {
			call.respond(HttpStatusCode.NotFound)
		}
	}
}

fun getNextAvailableId(): Int = if (tasks.isEmpty()) 0 else tasks.keys.max() + 1