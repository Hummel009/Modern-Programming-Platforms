package com.github.hummel.mpp.lab6

import com.github.hummel.mpp.lab6.controller.gson
import com.github.hummel.mpp.lab6.dto.EditTaskRequest
import com.github.hummel.mpp.lab6.dto.FilterRequest
import com.github.hummel.mpp.lab6.dto.TokenRequest
import com.github.hummel.mpp.lab6.dto.UserRequest
import com.github.hummel.mpp.lab6.entity.Task
import com.github.hummel.mpp.lab6.grpc.ServerGrpc.ServerImplBase
import com.github.hummel.mpp.lab6.grpc.StringReply
import com.github.hummel.mpp.lab6.grpc.StringRequest
import io.grpc.Grpc
import io.grpc.InsecureServerCredentials
import io.grpc.stub.StreamObserver
import io.ktor.http.HttpStatusCode
import java.util.concurrent.TimeUnit

val tasks = mutableMapOf<Int, Task>()

fun main() {
	val port = 50051

	var server = Grpc.newServerBuilderForPort(
		port, InsecureServerCredentials.create()
	).addService(ServerImpl()).build().start()

	Runtime.getRuntime().addShutdownHook(Thread(Runnable {
		try {
			server.shutdown().awaitTermination(30, TimeUnit.SECONDS)
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}))

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
		val filter = filterRequest.filter

		val filteredTasks = tasks.asSequence().filter {
			it.value.status == filter || filter == "all"
		}.associate { it.key to it.value }

		val reply = StringReply.newBuilder().setValue(gson.toJson(filteredTasks)).build()

		responseObserver.onNext(reply)
		responseObserver.onCompleted()
	}

	override fun editTask(jsonRequest: StringRequest, responseObserver: StreamObserver<StringReply>) {
		val editTaskRequest = gson.fromJson(jsonRequest.getValue(), EditTaskRequest::class.java)
		val index = editTaskRequest.index
		val title = editTaskRequest.title

		tasks[index]!!.title = title

		val reply = StringReply.newBuilder().setValue(gson.toJson(tasks)).build()

		responseObserver.onNext(reply)
		responseObserver.onCompleted()
	}
}

fun getNextAvailableId(): Int = if (tasks.isEmpty()) 0 else tasks.keys.max() + 1