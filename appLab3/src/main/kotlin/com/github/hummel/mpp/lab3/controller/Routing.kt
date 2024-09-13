package com.github.hummel.mpp.lab3.controller

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.hummel.mpp.lab3.bean.FilterRequest
import com.github.hummel.mpp.lab3.bean.Task
import com.github.hummel.mpp.lab3.bean.User
import com.github.hummel.mpp.lab3.tasks
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.utils.io.jvm.javaio.toInputStream
import java.io.File
import java.util.UUID

private const val JWT_ISSUER = "h009_issuer"

fun Application.configureRouting() {
	routing {
		authenticate {
			get("/secure-endpoint") {
				call.respondText("This is a secure endpoint")
			}
		}

		post("/login") {
			val user = call.receive<User>()
			if (isValidUser(user)) {
				val token = JWT.create().withClaim("username", user.username)
					.withClaim("password", UUID.randomUUID().toString())
					.withIssuer(JWT_ISSUER).sign(Algorithm.none())
				call.response.cookies.append(HttpHeaders.SetCookie, "token=$token; HttpOnly; Path=/")
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

			tasks.add(Task(title, status, dueDate, fileName))

			call.respond(HttpStatusCode.Created)
		}

		post("/filter-tasks") {
			val request = call.receive<FilterRequest>()
			val filterStatus = request.filterStatus

			val filteredTasks = tasks.asSequence().filter {
				it.status == filterStatus || filterStatus == "all"
			}.toMutableList()

			call.respond(filteredTasks)
		}
	}
}

fun isValidUser(user: User): Boolean {
	val bcryptHashString = BCrypt.withDefaults().hashToString(12, "amogus134".toCharArray())

	return BCrypt.verifyer().verify(user.password.toCharArray(), bcryptHashString).verified
}