package com.github.hummel.mpp.course.dto

import com.github.hummel.mpp.course.entity.User

data class UserRequest(val username: String, val password: String) {
	fun toEntity(): User = User(username, password)
}
