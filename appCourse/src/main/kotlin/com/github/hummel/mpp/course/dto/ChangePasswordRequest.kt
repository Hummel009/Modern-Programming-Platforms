package com.github.hummel.mpp.course.dto

data class ChangePasswordRequest(
	val userId: Int, val token: String, val newPassword: String
)