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
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach

val tasks: MutableMap<Int, Task> = mutableMapOf()

private val gson: Gson = Gson()

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
							loginSubscribers.forEach { it.send(Frame.Text("Unauthorized")) }
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
							tokenSubscribers.forEach { it.send(Frame.Text("Unauthorized")) }
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
						val jsonResponse = gson.toJson(tasks)

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

		val filterTasksSubscribers = mutableSetOf<WebSocketSession>()
		webSocket("/filter_tasks") {
			filterTasksSubscribers.add(this)

			try {
				incoming.consumeEach { frame ->
					if (frame is Frame.Text) {
						val jsonRequest = frame.readText()

						val filterRequest = gson.fromJson(jsonRequest, FilterRequest::class.java)
						val filterStatus = filterRequest.status

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

		val editTaskSubscribers = mutableSetOf<WebSocketSession>()
		webSocket("/edit_task") {
			editTaskSubscribers.add(this)

			try {
				incoming.consumeEach { frame ->
					if (frame is Frame.Text) {
						val jsonRequest = frame.readText()

						val editTaskRequest = gson.fromJson(jsonRequest, EditTaskRequest::class.java)
						val index = editTaskRequest.taskId
						val title = editTaskRequest.newTitle

						tasks.getValue(index).title = title

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
	}
}

fun Application.configureRouting() {
	routing {
		post("/tasks/add") {
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

		get("{...}") {
			call.respond(HttpStatusCode.NotFound)
		}
	}
}

fun getNextAvailableId(): Int = if (tasks.isEmpty()) 0 else tasks.keys.max() + 1