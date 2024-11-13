package com.github.hummel.mpp.course.entity

import com.github.hummel.mpp.course.dto.ProfileResponse

data class User(val id: Int, val username: String, val hashedPassword: String, val balance: Double) {
	fun toResponse(): ProfileResponse = ProfileResponse(id, username, balance)
}