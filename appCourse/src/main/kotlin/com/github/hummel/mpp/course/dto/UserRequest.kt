package com.github.hummel.mpp.course.dto

import com.github.hummel.mpp.course.entity.User

data class UserRequest(val id: Int, val username: String, val password: String, val balance: Int) {
	fun toEntity(): User = User(id, username, password, balance)
}