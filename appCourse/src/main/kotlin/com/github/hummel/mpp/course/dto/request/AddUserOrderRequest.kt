package com.github.hummel.mpp.course.dto.request

data class AddUserOrderRequest(
	val cart: List<CartItem>
) {
	data class CartItem(val bookId: Int, val quantity: Int)
}