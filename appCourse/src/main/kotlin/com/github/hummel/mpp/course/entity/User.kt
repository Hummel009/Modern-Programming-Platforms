package com.github.hummel.mpp.course.entity

import com.github.hummel.mpp.course.dto.response.UserResponse

data class User(val id: Int, val username: String, val hashedPassword: String, val balance: Double) {
	fun toResponse(): UserResponse = UserResponse(
		id = id, username = username, balance = balance
	)
}