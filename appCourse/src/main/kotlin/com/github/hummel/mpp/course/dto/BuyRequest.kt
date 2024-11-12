package com.github.hummel.mpp.course.dto

data class BuyRequest(val token: String?, val cartData: List<CartItem>) {
	data class CartItem(val id: Int, val quantity: Int)
}