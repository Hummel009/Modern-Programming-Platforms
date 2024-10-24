package com.github.hummel.mpp.lab6.dto

import com.github.hummel.mpp.lab6.entity.User

data class UserRequest(val username: String, val password: String) {
	fun toEntity(): User = User(username, password)
}
