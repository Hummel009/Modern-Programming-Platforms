package com.github.hummel.mpp.course.entity

data class OrderFull(
	val number: Int,
	val totalPrice: Double,
	val books: List<Book>,
	val quantities: List<Int>
)