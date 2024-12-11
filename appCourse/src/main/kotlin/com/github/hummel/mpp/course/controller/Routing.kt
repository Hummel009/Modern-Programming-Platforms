package com.github.hummel.mpp.course.controller

import com.github.hummel.mpp.course.dto.request.*
import com.github.hummel.mpp.course.service.AuthService
import com.github.hummel.mpp.course.service.CartService
import com.github.hummel.mpp.course.service.MainService
import com.github.hummel.mpp.course.service.ProfileService
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

val gson: Gson = Gson()

fun Application.configureRouting() {
	routing {
		route("/api/v1") {
			route("/users") {
				get("/info") {
					val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")
					val decomposedToken = AuthService.decomposeToken(token)

					val username = decomposedToken?.username
					val password = decomposedToken?.password

					if (AuthService.areCredentialsValid(username, password)) {
						username ?: throw Exception()

						val user = ProfileService.getUserData(username)

						val jsonResponse = gson.toJson(user.toResponse())

						call.respond(jsonResponse)
					} else {
						call.respond(HttpStatusCode.Unauthorized)
					}
				}

				put("/{userId}/balance") {
					val userId = call.parameters["userId"]?.toInt() ?: throw Exception()

					val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")
					val decomposedToken = AuthService.decomposeToken(token)

					val jsonRequest = call.receiveText()
					val request = gson.fromJson(jsonRequest, ChangeUserBalanceRequest::class.java)

					val username = decomposedToken?.username
					val password = decomposedToken?.password
					val rechargeBalance = request.rechargeBalance

					if (AuthService.areCredentialsValid(username, password)) {
						if (ProfileService.rechargeUserBalance(userId, rechargeBalance)) {
							call.respond(HttpStatusCode.OK)
						} else {
							call.respond(HttpStatusCode.BadRequest)
						}
					} else {
						call.respond(HttpStatusCode.Unauthorized)
					}
				}

				put("/{userId}/password") {
					val userId = call.parameters["userId"]?.toInt() ?: throw Exception()

					val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")
					val decomposedToken = AuthService.decomposeToken(token)

					val jsonRequest = call.receiveText()
					val request = gson.fromJson(jsonRequest, ChangeUserPasswordRequest::class.java)

					val username = decomposedToken?.username
					val password = decomposedToken?.password
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

				put("/{userId}/username") {
					val userId = call.parameters["userId"]?.toInt() ?: throw Exception()

					val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")
					val decomposedToken = AuthService.decomposeToken(token)

					val jsonRequest = call.receiveText()
					val request = gson.fromJson(jsonRequest, ChangeUserUsernameRequest::class.java)

					val username = decomposedToken?.username
					val password = decomposedToken?.password
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

			route("/authors") {
				get {
					val authors = MainService.getAllAuthors()

					val jsonResponse = gson.toJson(authors)

					call.respond(jsonResponse)
				}

				get("/{authorName}/books") {
					val filterValue = call.parameters["authorName"] ?: throw Exception()

					val booksToShow = if (filterValue == "all") {
						MainService.getAllBooks()
					} else {
						MainService.getBooksOfAuthor(filterValue.toInt())
					}

					val jsonResponse = gson.toJson(booksToShow.map { it.toResponse() })

					call.respond(jsonResponse)
				}
			}

			route("/types") {
				get {
					val types = MainService.getAllTypes()

					val jsonResponse = gson.toJson(types)

					call.respond(jsonResponse)
				}

				get("/{typeName}/books") {
					val filterValue = call.parameters["typeName"] ?: throw Exception()

					val booksToShow = if (filterValue == "all") {
						MainService.getAllBooks()
					} else {
						MainService.getBooksOfType(filterValue.toInt())
					}

					val jsonResponse = gson.toJson(booksToShow.map { it.toResponse() })

					call.respond(jsonResponse)
				}
			}

			route("/books") {
				get {
					val books = MainService.getAllBooks()

					val jsonResponse = gson.toJson(books.map { it.toResponse() })

					call.respond(jsonResponse)
				}

				get("/years") {
					val years = MainService.getAllYears()

					val jsonResponse = gson.toJson(years)

					call.respond(jsonResponse)
				}

				get("/years/{year}") {
					val filterValue = call.parameters["year"] ?: throw Exception()

					val booksToShow = if (filterValue == "all") {
						MainService.getAllBooks()
					} else {
						MainService.getBooksSinceYear(filterValue.toInt())
					}

					val jsonResponse = gson.toJson(booksToShow.map { it.toResponse() })

					call.respond(jsonResponse)
				}
			}

			route("/orders") {
				get("/{userId}") {
					val userId = call.parameters["userId"]?.toInt() ?: throw Exception()

					val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")
					val decomposedToken = AuthService.decomposeToken(token)

					val username = decomposedToken?.username
					val password = decomposedToken?.password

					if (AuthService.areCredentialsValid(username, password)) {
						val orders = ProfileService.getUserOrders(userId)

						val jsonResponse = gson.toJson(orders.map { it.toResponse() })

						call.respond(jsonResponse)
					} else {
						call.respond(HttpStatusCode.Unauthorized)
					}
				}

				post("/{userId}/add") {
					val userId = call.parameters["userId"]?.toInt() ?: throw Exception()

					val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")
					val decomposedToken = AuthService.decomposeToken(token)

					val jsonRequest = call.receiveText()
					val request = gson.fromJson(jsonRequest, AddUserOrderRequest::class.java)

					val username = decomposedToken?.username
					val password = decomposedToken?.password

					if (AuthService.areCredentialsValid(username, password)) {
						val ids = request.cart.map { it.bookId }
						val books = MainService.getAllBooks().filter { ids.contains(it.id) }
						val quantities = request.cart.map { it.quantity }

						if (CartService.addUserOrder(userId, books, quantities)) {
							call.respond(HttpStatusCode.OK)
						} else {
							call.respond(HttpStatusCode.BadRequest)
						}
					} else {
						call.respond(HttpStatusCode.Unauthorized)
					}
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
				val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")
				val decomposedToken = AuthService.decomposeToken(token)

				val username = decomposedToken?.username
				val password = decomposedToken?.password

				if (AuthService.areCredentialsValid(username, password)) {
					call.respond(HttpStatusCode.OK)
				} else {
					call.respond(HttpStatusCode.Unauthorized)
				}
			}
		}
	}
}