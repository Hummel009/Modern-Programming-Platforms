package com.github.hummel.mpp.lab6

import com.github.hummel.mpp.lab6.dto.EditTaskRequest
import com.github.hummel.mpp.lab6.dto.FilterRequest
import com.github.hummel.mpp.lab6.dto.TokenRequest
import com.github.hummel.mpp.lab6.dto.UserRequest
import com.github.hummel.mpp.lab6.entity.Task
import com.github.hummel.mpp.lab6.grpc.AddRequest
import com.github.hummel.mpp.lab6.grpc.ServerGrpc.ServerImplBase
import com.github.hummel.mpp.lab6.grpc.StringReply
import com.github.hummel.mpp.lab6.grpc.StringRequest
import com.google.gson.Gson
import io.grpc.Grpc
import io.grpc.InsecureServerCredentials
import io.grpc.stub.StreamObserver
import io.ktor.http.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

val tasks: MutableMap<Int, Task> = mutableMapOf()

private val gson: Gson = Gson()

fun main() {
	for (i in 0..9) {
		tasks[i] = Task(
			title = "title$i",
			status = if (Random.nextBoolean()) "completed" else "pending",
			dueDate = "dueDate$i",
			file = null
		)
	}

	val port = 2998

	val server = Grpc.newServerBuilderForPort(
		port, InsecureServerCredentials.create()
	).addService(ServerImpl()).build().start()

	Runtime.getRuntime().addShutdownHook(Thread {
		try {
			server.shutdown().awaitTermination(30, TimeUnit.SECONDS)
		} catch (e: Exception) {
			e.printStackTrace()
		}
	})

	server.awaitTermination()
}

class ServerImpl : ServerImplBase() {
	override fun login(jsonRequest: StringRequest, responseObserver: StreamObserver<StringReply>) {
		val userRequest = gson.fromJson(jsonRequest.getValue(), UserRequest::class.java)
		val user = userRequest.toEntity()

		val reply = if (isValidUser(user)) {
			val textResponse = generateToken(user)

			StringReply.newBuilder().setValue(textResponse).build()
		} else {
			StringReply.newBuilder().setValue(HttpStatusCode.Unauthorized.toString()).build()
		}

		responseObserver.onNext(reply)
		responseObserver.onCompleted()
	}

	override fun token(jsonRequest: StringRequest, responseObserver: StreamObserver<StringReply>) {
		val tokenRequest = gson.fromJson(jsonRequest.getValue(), TokenRequest::class.java)
		val token = tokenRequest.token

		val reply = if (isValidToken(token)) {
			StringReply.newBuilder().setValue(HttpStatusCode.OK.toString()).build()
		} else {
			StringReply.newBuilder().setValue(HttpStatusCode.Unauthorized.toString()).build()
		}

		responseObserver.onNext(reply)
		responseObserver.onCompleted()
	}

	override fun getTasks(jsonRequest: StringRequest, responseObserver: StreamObserver<StringReply>) {
		val reply = StringReply.newBuilder().setValue(gson.toJson(tasks)).build()

		responseObserver.onNext(reply)
		responseObserver.onCompleted()
	}

	override fun clearTasks(jsonRequest: StringRequest, responseObserver: StreamObserver<StringReply>) {
		tasks.clear()

		val reply = StringReply.newBuilder().setValue(gson.toJson(tasks)).build()

		responseObserver.onNext(reply)
		responseObserver.onCompleted()
	}

	override fun filterTasks(jsonRequest: StringRequest, responseObserver: StreamObserver<StringReply>) {
		val filterRequest = gson.fromJson(jsonRequest.getValue(), FilterRequest::class.java)
		val filter = filterRequest.status

		val filteredTasks = tasks.asSequence().filter {
			it.value.status == filter || filter == "all"
		}.associate { it.key to it.value }

		val reply = StringReply.newBuilder().setValue(gson.toJson(filteredTasks)).build()

		responseObserver.onNext(reply)
		responseObserver.onCompleted()
	}

	override fun editTask(jsonRequest: StringRequest, responseObserver: StreamObserver<StringReply>) {
		val editTaskRequest = gson.fromJson(jsonRequest.getValue(), EditTaskRequest::class.java)
		val index = editTaskRequest.taskId
		val title = editTaskRequest.newTitle

		tasks.getValue(index).title = title

		val reply = StringReply.newBuilder().setValue(gson.toJson(tasks)).build()

		responseObserver.onNext(reply)
		responseObserver.onCompleted()
	}

	override fun addTask(task: AddRequest, responseObserver: StreamObserver<StringReply>) {
		tasks[getNextAvailableId()] = Task(task.title, task.status, task.dueDate, null)

		val reply = StringReply.newBuilder().setValue(gson.toJson(tasks)).build()

		responseObserver.onNext(reply)
		responseObserver.onCompleted()
	}
}

fun getNextAvailableId(): Int = if (tasks.isEmpty()) 0 else tasks.keys.max() + 1