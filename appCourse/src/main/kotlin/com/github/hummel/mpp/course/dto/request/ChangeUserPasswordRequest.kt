package com.github.hummel.mpp.course.dto.request

data class ChangeUserPasswordRequest(
	val token: String?, val newPassword: String
)