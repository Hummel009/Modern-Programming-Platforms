package com.github.hummel.mpp.lab6.controller

import com.github.hummel.mpp.lab6.grpc.AddRequest
import com.github.hummel.mpp.lab6.grpc.ServerGrpc
import com.github.hummel.mpp.lab6.grpc.StringRequest
import io.grpc.Grpc
import io.grpc.InsecureChannelCredentials
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
	val channel = Grpc.newChannelBuilder("localhost:2998", InsecureChannelCredentials.create()).build()
	val grpcServer = ServerGrpc.newBlockingStub(channel)

	routing {
		route("/api/v1") {
			route("/tasks") {
				get {
					val request = StringRequest.newBuilder().setValue("").build()
					val response = grpcServer.getTasks(request)

					call.respond(response.getValue())
				}

				delete {
					val request = StringRequest.newBuilder().setValue("").build()
					val response = grpcServer.clearTasks(request)

					call.respond(response.getValue())
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

					val request = AddRequest.newBuilder().setTitle(title).setStatus(status).setDueDate(dueDate).build()
					grpcServer.addTask(request)

					call.respond(HttpStatusCode.OK)
				}

				put("/edit") {
					val jsonRequest = call.receiveText()

					val request = StringRequest.newBuilder().setValue(jsonRequest).build()
					val response = grpcServer.editTask(request)

					call.respond(response.getValue())
				}

				get("/filter") {
					val jsonRequest = call.receiveText()

					val request = StringRequest.newBuilder().setValue(jsonRequest).build()
					val response = grpcServer.filterTasks(request)

					call.respond(response.getValue())
				}
			}

			post("/login") {
				val jsonRequest = call.receiveText()

				val request = StringRequest.newBuilder().setValue(jsonRequest).build()
				val response = grpcServer.login(request)

				if (response.getValue() == HttpStatusCode.Unauthorized.toString()) {
					call.respond(HttpStatusCode.Unauthorized)
				} else {
					call.respond(response.getValue())
				}
			}

			post("/token") {
				val jsonRequest = call.receiveText()

				val request = StringRequest.newBuilder().setValue(jsonRequest).build()
				val response = grpcServer.token(request)

				if (response.getValue() == HttpStatusCode.Unauthorized.toString()) {
					call.respond(HttpStatusCode.Unauthorized)
				} else {
					call.respond(HttpStatusCode.OK)
				}
			}
		}
	}
}