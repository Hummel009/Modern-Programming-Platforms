package com.github.hummel.mpp.course.dto.request

data class ProfilePasswordRequest(
	val token: String?, val newPassword: String
)