package com.github.hummel.mpp.course.entity

import com.github.hummel.mpp.course.dto.response.OrderResponse
import com.github.hummel.mpp.course.service.MainService

data class Order(val id: Int, val userId: Int, val orderItems: MutableList<OrderItem>) {
	data class OrderItem(val id: Int, val orderId: Int, val bookId: Int, val quantity: Int)

	fun toResponse(): OrderResponse {
		val bookIds = orderItems.map { it.bookId }
		val books = MainService.getAllBooks().filter { bookIds.contains(it.id) }
		val quantities = orderItems.map { it.quantity }

		return OrderResponse(
			books = books, quantities = quantities
		)
	}
}