package com.github.hummel.mpp.course.entity

data class Book(
	val id: Int,
	var title: String,
	val description: String,
	val author: String,
	val imgPath: String,
	val price: Double,
	val type: String,
	val year: String
)