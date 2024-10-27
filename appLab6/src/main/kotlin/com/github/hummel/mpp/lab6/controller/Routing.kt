package com.github.hummel.mpp.lab6.controller

import com.github.hummel.mpp.lab6.entity.Task
import com.github.hummel.mpp.lab6.getNextAvailableId
import com.github.hummel.mpp.lab6.grpc.ServerGrpc
import com.github.hummel.mpp.lab6.grpc.StringRequest
import com.github.hummel.mpp.lab6.tasks
import com.google.gson.Gson
import io.grpc.Grpc
import io.grpc.InsecureChannelCredentials
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

val gson = Gson()

fun Application.configureRouting() {
	val channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build()
	val grpcServer = ServerGrpc.newBlockingStub(channel)

	routing {
		post("/login") {
			val jsonRequest = call.receiveText()

			val request = StringRequest.newBuilder().setValue(jsonRequest).build()
			var response = grpcServer.login(request)

			if (response.getValue() == HttpStatusCode.Unauthorized.toString()) {
				call.respond(HttpStatusCode.Unauthorized)
			} else {
				call.respond(response.getValue())
			}
		}

		post("/token") {
			val jsonRequest = call.receiveText()

			val request = StringRequest.newBuilder().setValue(jsonRequest).build()
			var response = grpcServer.token(request)

			if (response.getValue() == HttpStatusCode.Unauthorized.toString()) {
				call.respond(HttpStatusCode.Unauthorized)
			} else {
				call.respond(HttpStatusCode.OK)
			}
		}

		get("/get-tasks") {
			val request = StringRequest.newBuilder().setValue("").build()
			var response = grpcServer.getTasks(request)

			call.respond(response.getValue())
		}

		delete("/clear-tasks") {
			val request = StringRequest.newBuilder().setValue("").build()
			var response = grpcServer.clearTasks(request)

			call.respond(response.getValue())
		}

		post("/filter-tasks") {
			val jsonRequest = call.receiveText()

			val request = StringRequest.newBuilder().setValue(jsonRequest).build()
			var response = grpcServer.filterTasks(request)

			call.respond(response.getValue())
		}

		put("/edit-task") {
			val jsonRequest = call.receiveText()

			val request = StringRequest.newBuilder().setValue(jsonRequest).build()
			var response = grpcServer.editTask(request)

			call.respond(response.getValue())
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