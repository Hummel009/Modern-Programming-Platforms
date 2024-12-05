package com.github.hummel.mpp.course.dto.request

data class UserOrdersRequest(
	val userId: Int, val token: String?
)