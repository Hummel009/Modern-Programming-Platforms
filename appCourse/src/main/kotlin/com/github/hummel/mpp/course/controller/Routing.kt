package com.github.hummel.mpp.course.controller

import com.github.hummel.mpp.course.dto.*
import com.github.hummel.mpp.course.entity.OrderFull
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

				val request = gson.fromJson(jsonRequest, BooksFilterRequest::class.java)
				val author = request.author

				val booksToShow = MainService.getBooksOfAuthor(author)

				val jsonResponse = gson.toJson(booksToShow)

				call.respond(jsonResponse)
			}

			post("/ids") {
				val jsonRequest = call.receiveText()

				val request = gson.fromJson(jsonRequest, BooksIdsRequest::class.java)
				val ids = request.bookIds

				val booksByIds = MainService.getBooksWithIds(ids)

				val jsonResponse = gson.toJson(booksByIds)

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
					username ?: throw Exception()

					val user = ProfileService.getUserData(username) ?: throw Exception()

					val jsonResponse = gson.toJson(user.erasePassword())

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

					val ordersNums = List(orders.size) { it + 1 }
					val ordersBooks = orders.map {
						MainService.getBooksWithIds(it.orderItems.map { item ->
							item.bookId
						})
					}
					val ordersBooksQuantities = orders.map {
						it.orderItems.map { item ->
							item.quantity
						}
					}
					val orderPrices = orders.mapIndexed { index, order ->
						order.orderItems.zip(ordersBooks[index]) { orderItem, book ->
							orderItem.quantity * book.price
						}.sum()
					}

					val ordersFull = ordersNums.indices.map { index ->
						OrderFull(
							number = ordersNums[index],
							totalPrice = orderPrices[index],
							books = ordersBooks[index],
							quantities = ordersBooksQuantities[index]
						)
					}

					val jsonResponse = gson.toJson(ordersFull)

					call.respond(jsonResponse)
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