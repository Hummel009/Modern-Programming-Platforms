package com.github.hummel.mpp.course.dto

data class ProfileUsernameRequest(
	val userId: Int, val token: String, val newUsername: String
)