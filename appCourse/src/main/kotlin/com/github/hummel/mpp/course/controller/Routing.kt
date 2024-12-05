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
		route("/authors") {
			get {
				val authors = MainService.getAllAuthors()

				val jsonResponse = gson.toJson(authors)

				call.respond(jsonResponse)
			}
		}

		route("/types") {
			get {
				val types = MainService.getAllTypes()

				val jsonResponse = gson.toJson(types)

				call.respond(jsonResponse)
			}
		}

		route("/years") {
			get {
				val years = MainService.getAllYears()

				val jsonResponse = gson.toJson(years)

				call.respond(jsonResponse)
			}
		}

		route("/books") {
			get {
				val books = MainService.getAllBooks()

				val jsonResponse = gson.toJson(books.map { it.toResponse() })

				call.respond(jsonResponse)
			}

			post("/ids") {
				val jsonRequest = call.receiveText()

				val request = gson.fromJson(jsonRequest, BooksIdsRequest::class.java)
				val ids = request.bookIds

				val booksWithIds = MainService.getBooksWithIds(ids)

				val jsonResponse = gson.toJson(booksWithIds.map { it.toResponse() })

				call.respond(jsonResponse)
			}
		}

		route("/user") {
			post {
				val jsonRequest = call.receiveText()

				val request = gson.fromJson(jsonRequest, UserRequest::class.java)
				val token = AuthService.decomposeToken(request.token)

				val username = token?.username
				val password = token?.password

				if (AuthService.areCredentialsValid(username, password)) {
					username ?: throw Exception()

					val user = ProfileService.getUserData(username)

					val jsonResponse = gson.toJson(user.toResponse())

					call.respond(jsonResponse)
				} else {
					call.respond(HttpStatusCode.Unauthorized)
				}
			}

			post("/orders") {
				val jsonRequest = call.receiveText()

				val request = gson.fromJson(jsonRequest, UserOrdersRequest::class.java)
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
		}

		route("/main") {
			post("/authors") {
				val jsonRequest = call.receiveText()

				val request = gson.fromJson(jsonRequest, MainRequest::class.java)
				val filterValue = request.filterValue

				val booksToShow = if (filterValue == "all") {
					MainService.getAllBooks()
				} else {
					MainService.getBooksOfAuthor(filterValue.toInt())
				}

				val jsonResponse = gson.toJson(booksToShow.map { it.toResponse() })

				call.respond(jsonResponse)
			}

			post("/types") {
				val jsonRequest = call.receiveText()

				val request = gson.fromJson(jsonRequest, MainRequest::class.java)
				val filterValue = request.filterValue

				val booksToShow = if (filterValue == "all") {
					MainService.getAllBooks()
				} else {
					MainService.getBooksOfType(filterValue.toInt())
				}

				val jsonResponse = gson.toJson(booksToShow.map { it.toResponse() })

				call.respond(jsonResponse)
			}

			post("/years") {
				val jsonRequest = call.receiveText()

				val request = gson.fromJson(jsonRequest, MainRequest::class.java)
				val filterValue = request.filterValue

				val booksToShow = if (filterValue == "all") {
					MainService.getAllBooks()
				} else {
					MainService.getBooksSinceYear(filterValue.toInt())
				}

				val jsonResponse = gson.toJson(booksToShow.map { it.toResponse() })

				call.respond(jsonResponse)
			}
		}

		route("/cart") {
			post("/buy") {
				val jsonRequest = call.receiveText()

				val request = gson.fromJson(jsonRequest, CartBuyRequest::class.java)
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
		}

		route("/profile") {
			put("/balance") {
				val jsonRequest = call.receiveText()

				val request = gson.fromJson(jsonRequest, ProfileBalanceRequest::class.java)
				val token = AuthService.decomposeToken(request.token)

				val userId = request.userId
				val username = token?.username
				val password = token?.password
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

			put("/password") {
				val jsonRequest = call.receiveText()

				val request = gson.fromJson(jsonRequest, ProfilePasswordRequest::class.java)
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

			put("/username") {
				val jsonRequest = call.receiveText()

				val request = gson.fromJson(jsonRequest, ProfileUsernameRequest::class.java)
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

		route("/login") {
			post {
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
		}

		route("/register") {
			post {
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