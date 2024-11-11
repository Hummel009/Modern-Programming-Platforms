package com.github.hummel.mpp.course.service

import at.favre.lib.crypto.bcrypt.BCrypt
import com.github.hummel.mpp.course.dao.UserDao
import com.github.hummel.mpp.course.entity.User

object ProfileService {
	fun getUserData(username: String): User? = UserDao.findUserByUsername(username)

	fun changeUserUsername(username: String, newUsername: String): Boolean =
		UserDao.updateUserUsername(username, newUsername)

	fun changeUserPassword(username: String, newPassword: String): Boolean {
		val hashedPassword = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray())

		return UserDao.updateUserPassword(username, hashedPassword)
	}
}