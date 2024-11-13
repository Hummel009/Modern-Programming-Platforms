package com.github.hummel.mpp.course.entity

import com.github.hummel.mpp.course.dto.OrdersResponse

data class Order(val id: Int, val userId: Int, val bookId: Int, val quantity: Int) {
	fun toResponse(): OrdersResponse = OrdersResponse(bookId, quantity)
}