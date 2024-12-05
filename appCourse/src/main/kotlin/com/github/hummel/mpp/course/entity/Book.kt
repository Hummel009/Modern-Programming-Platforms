package com.github.hummel.mpp.course.entity

import com.github.hummel.mpp.course.dao.AuthorDao
import com.github.hummel.mpp.course.dao.TypeDao
import com.github.hummel.mpp.course.dto.response.BookResponse

data class Book(
	val id: Int,
	var name: String,
	val desc: String,
	val image: String,
	val authorId: Int,
	val typeId: Int,
	val year: Int,
	val price: Double
) {
	fun toResponse(): BookResponse = BookResponse(
		id = id,
		name = name,
		desc = desc,
		image = image,
		authorName = AuthorDao.findAuthorById(authorId)?.name ?: throw Exception(),
		typeName = TypeDao.findTypeById(typeId)?.name ?: throw Exception(),
		year = year,
		price = price
	)
}