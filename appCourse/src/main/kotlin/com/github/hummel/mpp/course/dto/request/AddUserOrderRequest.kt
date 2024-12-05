package com.github.hummel.mpp.course.dto.request

data class AddUserOrderRequest(
	val cartData: List<CartItem>
) {
	data class CartItem(val id: Int, val quantity: Int)
}