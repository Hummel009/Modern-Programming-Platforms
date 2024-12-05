package com.github.hummel.mpp.course.dto.request

data class CartBuyRequest(
	val userId: Int, val token: String?, val cartData: List<CartItem>
) {
	data class CartItem(val id: Int, val quantity: Int)
}