package com.github.hummel.mpp.course.dto.request

data class ProfilePasswordRequest(
	val userId: Int, val token: String?, val newPassword: String
)