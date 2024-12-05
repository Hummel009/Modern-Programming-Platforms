package com.github.hummel.mpp.course.dto.response

data class BookResponse(
	val id: Int,
	var name: String,
	val desc: String,
	val image: String,
	val authorName: String,
	val typeName: String,
	val year: Int,
	val price: Double
)