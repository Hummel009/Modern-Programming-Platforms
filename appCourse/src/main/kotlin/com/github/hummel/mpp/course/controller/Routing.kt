package com.github.hummel.mpp.course.controller

import com.github.hummel.mpp.course.dto.ChangePasswordRequest
import com.github.hummel.mpp.course.dto.ChangeUsernameRequest
import com.github.hummel.mpp.course.dto.LoginRequest
import com.github.hummel.mpp.course.dto.ProfileRequest
import com.github.hummel.mpp.course.dto.RegisterRequest
import com.github.hummel.mpp.course.dto.TokenRequest
import com.github.hummel.mpp.course.service.AuthService
import com.github.hummel.mpp.course.service.MainService
import com.github.hummel.mpp.course.service.ProfileService
import com.google.gson.Gson
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

val gson = Gson()

fun Application.configureRouting() {
	routing {
		post("/register") {
			val jsonRequest = call.receiveText()

			val request = gson.fromJson(jsonRequest, RegisterRequest::class.java)

			val username = request.username
			val password = request.password

			val success = AuthService.registerUser(username, password)

			if (success) {
				val textResponse = AuthService.generateToken(username, password)

				call.respond(textResponse)
			} else {
				call.respond(HttpStatusCode.Unauthorized)
			}
		}

		post("/login") {
			val jsonRequest = call.receiveText()

			val request = gson.fromJson(jsonRequest, LoginRequest::class.java)

			val username = request.username
			val password = request.password

			if (AuthService.areCredentialsValid(username, password)) {
				val textResponse = AuthService.generateToken(username, password)

				call.respond(textResponse)
			} else {
				call.respond(HttpStatusCode.Unauthorized)
			}
		}

		post("/token") {
			val jsonRequest = call.receiveText()

			val request = gson.fromJson(jsonRequest, TokenRequest::class.java)
			val token = AuthService.decomposeToken(request.token)

			val username = token?.username
			val password = token?.password

			if (AuthService.areCredentialsValid(username, password)) {
				call.respond(HttpStatusCode.OK)
			} else {
				call.respond(HttpStatusCode.Unauthorized)
			}
		}

		route("/profile") {
			post {
				val jsonRequest = call.receiveText()

				val request = gson.fromJson(jsonRequest, ProfileRequest::class.java)
				val token = AuthService.decomposeToken(request.token)

				val username = token?.username
				val password = token?.password

				if (AuthService.areCredentialsValid(username, password)) {
					val user = ProfileService.getUserData(username!!)

					if (user != null) {
						call.respond(gson.toJson(user))
					} else {
						call.respond(HttpStatusCode.Unauthorized)
					}
				} else {
					call.respond(HttpStatusCode.Unauthorized)
				}
			}

			post("/username") {
				val jsonRequest = call.receiveText()

				val request = gson.fromJson(jsonRequest, ChangeUsernameRequest::class.java)
				val token = AuthService.decomposeToken(request.token)
				val newUsername = request.newUsername

				val username = token?.username
				val password = token?.password

				if (AuthService.areCredentialsValid(username, password)) {
					if (ProfileService.changeUserUsername(username!!, newUsername)) {
						call.respond(HttpStatusCode.OK)
					} else {
						call.respond(HttpStatusCode.BadRequest)
					}
				} else {
					call.respond(HttpStatusCode.Unauthorized)
				}
			}

			post("/password") {
				val jsonRequest = call.receiveText()

				val request = gson.fromJson(jsonRequest, ChangePasswordRequest::class.java)
				val token = AuthService.decomposeToken(request.token)
				val newPassword = request.newPassword

				val username = token?.username
				val password = token?.password

				if (AuthService.areCredentialsValid(username, password)) {
					if (ProfileService.changeUserPassword(username!!, newPassword)) {
						call.respond(HttpStatusCode.OK)
					} else {
						call.respond(HttpStatusCode.BadRequest)
					}
				} else {
					call.respond(HttpStatusCode.Unauthorized)
				}
			}
		}

		route("books") {
			get {
				val books = MainService.getAllBooks()

				val jsonResponse = gson.toJson(books)

				call.respond(jsonResponse)
			}

			get("/authors") {
				val authors = MainService.getAllAuthors()

				val jsonResponse = gson.toJson(authors)

				call.respond(jsonResponse)
			}
		}

		get("{...}") {
			call.respond(HttpStatusCode.NotFound)
		}
	}
}