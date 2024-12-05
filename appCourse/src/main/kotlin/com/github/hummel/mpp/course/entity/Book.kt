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
	fun toResponse(): BookResponse {
		val authorName = AuthorDao.findAuthorById(authorId)?.name ?: throw Exception()
		val typeName = TypeDao.findTypeById(typeId)?.name ?: throw Exception()

		return BookResponse(
			id = id,
			name = name,
			desc = desc,
			image = image,
			authorName = authorName,
			typeName = typeName,
			year = year,
			price = price
		)
	}
}