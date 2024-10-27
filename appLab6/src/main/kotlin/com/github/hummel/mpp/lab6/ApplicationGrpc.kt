package com.github.hummel.mpp.lab6

import com.github.hummel.mpp.lab6.controller.gson
import com.github.hummel.mpp.lab6.dto.UserRequest
import com.github.hummel.mpp.lab6.grpc.ServerGrpc.ServerImplBase
import com.github.hummel.mpp.lab6.grpc.StringReply
import com.github.hummel.mpp.lab6.grpc.StringRequest
import io.grpc.Grpc
import io.grpc.InsecureServerCredentials
import io.grpc.stub.StreamObserver
import io.ktor.http.HttpStatusCode
import java.util.concurrent.TimeUnit

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
	override fun login(req: StringRequest, responseObserver: StreamObserver<StringReply>) {
		val userRequest = gson.fromJson(req.getValue(), UserRequest::class.java)
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
}