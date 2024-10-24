package com.github.hummel.mpp.lab6.grpc

import com.github.hummel.mpp.lab6.grpc.GreeterGrpc.GreeterImplBase
import com.github.hummel.mpp.lab6.grpc.GrpcServer.GreeterImpl
import io.grpc.Grpc
import io.grpc.InsecureServerCredentials
import io.grpc.Server
import io.grpc.stub.StreamObserver
import java.util.concurrent.TimeUnit

fun main() {
	val server = GrpcServer()
	server.start()
	server.blockUntilShutdown()
}

class GrpcServer {
	private lateinit var server: Server

	fun start() {
		val port = 50051

		server = Grpc.newServerBuilderForPort(
			port, InsecureServerCredentials.create()
		).addService(GreeterImpl()).build().start()

		Runtime.getRuntime().addShutdownHook(Thread(Runnable {
			try {
				stop()
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}))
	}

	fun stop() {
		server.shutdown().awaitTermination(30, TimeUnit.SECONDS)
	}

	fun blockUntilShutdown() {
		server.awaitTermination()
	}

	class GreeterImpl : GreeterImplBase() {
		override fun sayHello(req: HelloRequest, responseObserver: StreamObserver<HelloReply>) {
			val reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build()
			responseObserver.onNext(reply)
			responseObserver.onCompleted()
		}
	}
}