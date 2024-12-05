package com.github.hummel.mpp.course.dto.request

data class ChangeUserUsernameRequest(
	val token: String?, val newUsername: String
)