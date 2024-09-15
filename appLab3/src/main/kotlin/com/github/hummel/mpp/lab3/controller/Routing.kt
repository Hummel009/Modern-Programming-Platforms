package com.github.hummel.mpp.lab3.controller

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.hummel.mpp.lab3.bean.EditTaskRequest
import com.github.hummel.mpp.lab3.bean.FilterRequest
import com.github.hummel.mpp.lab3.bean.Task
import com.github.hummel.mpp.lab3.bean.User
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
import kotlin.text.toInt

val tasks = mutableMapOf<Int, Task>()

fun Application.configureRouting() {
	routing {
		post("/login") {
			val user = call.receive<User>()
			if (isValidUser(user)) {
				val token = JWT.create()
					.withClaim("username", user.username)
					.withClaim("password", user.password)
					.sign(Algorithm.HMAC256("secret"))
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
			if (token == null) {
				call.respond(HttpStatusCode.Unauthorized)
				return@get
			}

			try {
				val decoded = JWT.decode(token)
				val username = decoded.getClaim("username").asString()
				val password = decoded.getClaim("password").asString()

				if (isValidUser(User(username, password))) {
					call.respond(HttpStatusCode.OK)
				} else {
					throw Exception()
				}
			} catch (_: Exception) {
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

		post("/{...}") {
			call.respond(HttpStatusCode.NotFound)
		}
	}
}

fun getNextAvailableId(): Int = if (tasks.isEmpty()) 0 else tasks.keys.max() + 1

fun isValidUser(user: User): Boolean {
	val neededUsername = "Hummel009"
	val neededPassword = BCrypt.withDefaults().hashToString(12, "amogus134".toCharArray())
	val usernameRule = user.username == neededUsername
	val passwordRule = BCrypt.verifyer().verify(user.password.toCharArray(), neededPassword).verified

	return usernameRule && passwordRule
}