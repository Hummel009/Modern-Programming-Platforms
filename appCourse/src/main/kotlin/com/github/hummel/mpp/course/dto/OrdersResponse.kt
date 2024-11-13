package com.github.hummel.mpp.course.dto

import com.github.hummel.mpp.course.entity.OrderItem

data class OrdersResponse(
	val orderId: Int, val orderItems: List<OrderItem>
)