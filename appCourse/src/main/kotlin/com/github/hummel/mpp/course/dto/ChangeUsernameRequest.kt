package com.github.hummel.mpp.course.dto

data class ChangeUsernameRequest(
	val userId: Int, val token: String, val newUsername: String
)