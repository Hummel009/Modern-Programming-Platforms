package com.github.hummel.mpp.course.service

import at.favre.lib.crypto.bcrypt.BCrypt
import com.github.hummel.mpp.course.dao.OrderDao
import com.github.hummel.mpp.course.dao.UserDao
import com.github.hummel.mpp.course.entity.Order
import com.github.hummel.mpp.course.entity.User

object ProfileService {
	fun getUserData(username: String): User = UserDao.findUserByUsername(username) ?: throw Exception()

	fun changeUserUsername(userId: Int, newUsername: String): Boolean =
		UserDao.updateUserUsername(userId, newUsername)

	fun changeUserPassword(userId: Int, newPassword: String): Boolean {
		val hashedPassword = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray())

		return UserDao.updateUserPassword(userId, hashedPassword)
	}

	fun getUserOrders(userId: Int): List<Order> = OrderDao.findAllUserOrders(userId)


	fun rechargeUserBalance(userId: Int, rechargeBalance: Double): Boolean {
		val userBalance = UserDao.findUserById(userId)?.balance ?: return false

		val newBalance = userBalance + rechargeBalance

		return UserDao.updateUserBalance(userId, newBalance)
	}
}