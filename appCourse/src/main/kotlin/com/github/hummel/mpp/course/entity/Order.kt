package com.github.hummel.mpp.course.entity

import com.github.hummel.mpp.course.dto.OrdersResponse

data class Order(val id: Int, val userId: Int, val orderItems: MutableList<OrderItem>) {
	fun toResponse(): OrdersResponse = OrdersResponse(id, orderItems)
}