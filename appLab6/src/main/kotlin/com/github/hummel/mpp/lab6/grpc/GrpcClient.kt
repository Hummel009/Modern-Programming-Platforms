package com.github.hummel.mpp.lab6.grpc

import io.grpc.Channel
import io.grpc.Grpc
import io.grpc.InsecureChannelCredentials
import java.util.concurrent.TimeUnit

fun main() {
	var user = "world"
	var target = "localhost:50051"

	val channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create()).build()
	try {
		val client = GrpcClient(channel)
		client.greet(user)
	} finally {
		channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS)
	}
}

class GrpcClient(channel: Channel) {
	private val blockingStub = GreeterGrpc.newBlockingStub(channel)

	fun greet(name: String) {
		println("Will try to greet $name...")
		val request = HelloRequest.newBuilder().setName(name).build()
		var response = blockingStub.sayHello(request)
		println("Greeting: " + response.getMessage())
	}
}