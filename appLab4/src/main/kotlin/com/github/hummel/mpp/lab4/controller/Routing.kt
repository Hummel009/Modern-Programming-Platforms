package com.github.hummel.mpp.lab4.controller

import com.github.hummel.mpp.lab4.dto.EditTaskRequest
import com.github.hummel.mpp.lab4.dto.FilterRequest
import com.github.hummel.mpp.lab4.dto.TokenRequest
import com.github.hummel.mpp.lab4.dto.UserRequest
import com.github.hummel.mpp.lab4.entity.Task
import com.github.hummel.mpp.lab4.generateToken
import com.github.hummel.mpp.lab4.isValidToken
import com.github.hummel.mpp.lab4.isValidUser
import com.google.gson.Gson
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.consumeEach
import java.io.File

val tasks = mutableMapOf<Int, Task>()
val gson = Gson()

fun Application.configureWebSocket() {
	routing {
		val loginSubscribers = mutableSetOf<WebSocketSession>()
		webSocket("/login") {
			loginSubscribers.add(this)

			try {
				incoming.consumeEach { frame ->
					if (frame is Frame.Text) {
						val jsonRequest = frame.readText()

						val userRequest = gson.fromJson(jsonRequest, UserRequest::class.java)
						val user = userRequest.toEntity()

						if (isValidUser(user)) {
							val textResponse = generateToken(user)
							loginSubscribers.forEach { it.send(Frame.Text(textResponse)) }
						} else {
							loginSubscribers.forEach { it.send(Frame.Text("ERROR")) }
						}
					}
				}
			} catch (e: Exception) {
				e.printStackTrace()
			} finally {
				loginSubscribers.remove(this)
			}
		}

		val tokenSubscribers = mutableSetOf<WebSocketSession>()
		webSocket("/token") {
			tokenSubscribers.add(this)

			try {
				incoming.consumeEach { frame ->
					if (frame is Frame.Text) {
						val jsonRequest = frame.readText()

						val tokenRequest = gson.fromJson(jsonRequest, TokenRequest::class.java)
						val token = tokenRequest.token

						if (isValidToken(token)) {
							tokenSubscribers.forEach { it.send(Frame.Text("OK")) }
						} else {
							tokenSubscribers.forEach { it.send(Frame.Text("ERROR")) }
						}
					}
				}
			} catch (e: Exception) {
				e.printStackTrace()
			} finally {
				tokenSubscribers.remove(this)
			}
		}

		val getTasksSubscribers = mutableSetOf<WebSocketSession>()
		webSocket("/get_tasks") {
			getTasksSubscribers.add(this)

			try {
				incoming.consumeEach { frame ->
					if (frame is Frame.Text) {
						val jsonResponse = Gson().toJson(tasks)

						getTasksSubscribers.forEach { it.send(Frame.Text(jsonResponse)) }
					}
				}
			} catch (e: Exception) {
				e.printStackTrace()
			} finally {
				getTasksSubscribers.remove(this)
			}
		}

		val clearTasksSubscribers = mutableSetOf<WebSocketSession>()
		webSocket("/clear_tasks") {
			clearTasksSubscribers.add(this)

			try {
				incoming.consumeEach { frame ->
					if (frame is Frame.Text) {
						tasks.clear()

						val jsonResponse = gson.toJson(tasks)

						clearTasksSubscribers.forEach { it.send(Frame.Text(jsonResponse)) }
					}
				}
			} catch (e: Exception) {
				e.printStackTrace()
			} finally {
				clearTasksSubscribers.remove(this)
			}
		}

		val editTaskSubscribers = mutableSetOf<WebSocketSession>()
		webSocket("/edit_task") {
			editTaskSubscribers.add(this)

			try {
				incoming.consumeEach { frame ->
					if (frame is Frame.Text) {
						val jsonRequest = frame.readText()

						val editTaskRequest = gson.fromJson(jsonRequest, EditTaskRequest::class.java)
						val taskId = editTaskRequest.index
						val taskTitle = editTaskRequest.title

						tasks[taskId]!!.title = taskTitle

						val jsonResponse = gson.toJson(tasks)

						editTaskSubscribers.forEach { it.send(Frame.Text(jsonResponse)) }
					}
				}
			} catch (e: Exception) {
				e.printStackTrace()
			} finally {
				editTaskSubscribers.remove(this)
			}
		}

		val filterTasksSubscribers = mutableSetOf<WebSocketSession>()
		webSocket("/filter_tasks") {
			filterTasksSubscribers.add(this)

			try {
				incoming.consumeEach { frame ->
					if (frame is Frame.Text) {
						val jsonRequest = frame.readText()

						val filterRequest = gson.fromJson(jsonRequest, FilterRequest::class.java)
						val filterStatus = filterRequest.filter

						val filteredTasks = tasks.asSequence().filter {
							it.value.status == filterStatus || filterStatus == "all"
						}.associate { it.key to it.value }

						val jsonResponse = gson.toJson(filteredTasks)

						filterTasksSubscribers.forEach { it.send(Frame.Text(jsonResponse)) }
					}
				}
			} catch (e: Exception) {
				e.printStackTrace()
			} finally {
				filterTasksSubscribers.remove(this)
			}
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

		route("{...}") {
			handle {
				throw Exception()
			}
		}
	}
}

fun getNextAvailableId(): Int = if (tasks.isEmpty()) 0 else tasks.keys.max() + 1