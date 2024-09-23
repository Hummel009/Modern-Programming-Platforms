package com.github.hummel.mpp.lab4.dto

import com.github.hummel.mpp.lab4.entity.User

data class UserRequest(val username: String, val password: String) {
	fun toEntity(): User = User(username, password)
}