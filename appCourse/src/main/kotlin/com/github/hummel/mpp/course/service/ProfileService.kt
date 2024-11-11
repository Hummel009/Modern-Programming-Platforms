package com.github.hummel.mpp.course.service

import com.github.hummel.mpp.course.dao.UserDao
import com.github.hummel.mpp.course.entity.Token
import com.github.hummel.mpp.course.entity.User

object ProfileService {
	fun getUserData(token: Token): User? {
		val username = token.username

		return UserDao.findUserByUsername(username)
	}

	fun updateUserUsername(token: Token, newUsername: String): Boolean {
		val username = token.username

		return UserDao.changeUserUsername(username, newUsername)
	}

	fun updateUserPassword(token: Token, newPassword: String): Boolean {
		val username = token.username

		return UserDao.changeUserPassword(username, newPassword)
	}
}