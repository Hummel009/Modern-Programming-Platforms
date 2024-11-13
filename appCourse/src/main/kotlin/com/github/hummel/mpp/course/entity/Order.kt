package com.github.hummel.mpp.course.entity

data class Order(val id: Int, val userId: Int, val orderItems: MutableList<OrderItem>) {
	data class OrderItem(val id: Int, val orderId: Int, val bookId: Int, val quantity: Int)
}