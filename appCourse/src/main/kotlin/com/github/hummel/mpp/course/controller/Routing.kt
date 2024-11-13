package com.github.hummel.mpp.course.controller

import com.github.hummel.mpp.course.dto.BuyRequest
import com.github.hummel.mpp.course.dto.ChangePasswordRequest
import com.github.hummel.mpp.course.dto.ChangeUsernameRequest
import com.github.hummel.mpp.course.dto.FilterBooksRequest
import com.github.hummel.mpp.course.dto.LoginRequest
import com.github.hummel.mpp.course.dto.OrdersRequest
import com.github.hummel.mpp.course.dto.ProfileRequest
import com.github.hummel.mpp.course.dto.RegisterRequest
import com.github.hummel.mpp.course.dto.TokenRequest
import com.github.hummel.mpp.course.service.AuthService
import com.github.hummel.mpp.course.service.CartService
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
		get("/authors") {
			val authors = listOf("all") + MainService.getUniqueAuthors()

			val jsonResponse = gson.toJson(authors)

			call.respond(jsonResponse)
		}

		route("/books") {
			get {
				val books = MainService.getAllBooks()

				val jsonResponse = gson.toJson(books)

				call.respond(jsonResponse)
			}

			post("/filter") {
				val jsonRequest = call.receiveText()

				val request = gson.fromJson(jsonRequest, FilterBooksRequest::class.java)
				val author = request.author

				val booksToShow = MainService.getBooksOfAuthor(author)

				val jsonResponse = gson.toJson(booksToShow)

				call.respond(jsonResponse)
			}
		}

		post("/buy") {
			val jsonRequest = call.receiveText()

			val request = gson.fromJson(jsonRequest, BuyRequest::class.java)
			val token = AuthService.decomposeToken(request.token)

			val userId = request.userId
			val username = token?.username
			val password = token?.password

			if (AuthService.areCredentialsValid(username, password)) {
				val ids = request.cartData.map { it.id }
				val quantities = request.cartData.map { it.quantity }
				val booksToBuy = MainService.getBooksWithIds(ids)

				if (CartService.buyBooks(userId, booksToBuy, quantities)) {
					call.respond(HttpStatusCode.OK)
				} else {
					call.respond(HttpStatusCode.BadRequest)
				}
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

		route("/profile") {
			post {
				val jsonRequest = call.receiveText()

				val request = gson.fromJson(jsonRequest, ProfileRequest::class.java)
				val token = AuthService.decomposeToken(request.token)

				val username = token?.username
				val password = token?.password

				if (AuthService.areCredentialsValid(username, password)) {
					val user = ProfileService.getUserData(username!!)!!

					val jsonResponse = gson.toJson(user.toResponse())

					call.respond(jsonResponse)
				} else {
					call.respond(HttpStatusCode.Unauthorized)
				}
			}

			post("/orders") {
				val jsonRequest = call.receiveText()

				val request = gson.fromJson(jsonRequest, OrdersRequest::class.java)
				val token = AuthService.decomposeToken(request.token)

				val userId = request.userId
				val username = token?.username
				val password = token?.password

				if (AuthService.areCredentialsValid(username, password)) {
					val orders = ProfileService.getUserOrders(userId)

					val jsonResponse = gson.toJson(orders.map { it.toResponse() })

					call.respond(jsonResponse)
				} else {
					call.respond(HttpStatusCode.Unauthorized)
				}
			}

			post("/password") {
				val jsonRequest = call.receiveText()

				val request = gson.fromJson(jsonRequest, ChangePasswordRequest::class.java)
				val token = AuthService.decomposeToken(request.token)

				val userId = request.userId
				val username = token?.username
				val password = token?.password
				val newPassword = request.newPassword

				if (AuthService.areCredentialsValid(username, password)) {
					if (ProfileService.changeUserPassword(userId, newPassword)) {
						call.respond(HttpStatusCode.OK)
					} else {
						call.respond(HttpStatusCode.BadRequest)
					}
				} else {
					call.respond(HttpStatusCode.Unauthorized)
				}
			}

			post("/username") {
				val jsonRequest = call.receiveText()

				val request = gson.fromJson(jsonRequest, ChangeUsernameRequest::class.java)
				val token = AuthService.decomposeToken(request.token)

				val userId = request.userId
				val username = token?.username
				val password = token?.password
				val newUsername = request.newUsername

				if (AuthService.areCredentialsValid(username, password)) {
					if (ProfileService.changeUserUsername(userId, newUsername)) {
						call.respond(HttpStatusCode.OK)
					} else {
						call.respond(HttpStatusCode.BadRequest)
					}
				} else {
					call.respond(HttpStatusCode.Unauthorized)
				}
			}
		}

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

		get("{...}") {
			call.respond(HttpStatusCode.NotFound)
		}
	}
}