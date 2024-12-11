package com.github.hummel.mpp.course.dto.response

import com.github.hummel.mpp.course.entity.Book

data class OrderResponse(
	val books: List<Book>, val quantities: List<Int>
)