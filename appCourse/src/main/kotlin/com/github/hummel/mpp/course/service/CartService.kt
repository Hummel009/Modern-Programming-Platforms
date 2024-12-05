package com.github.hummel.mpp.course.service

import com.github.hummel.mpp.course.dao.OrderDao
import com.github.hummel.mpp.course.dao.UserDao
import com.github.hummel.mpp.course.entity.Book

object CartService {
	fun addUserOrder(userId: Int, booksToBuy: List<Book>, quantities: List<Int>): Boolean {
		val price = booksToBuy.zip(quantities) { book, quantity -> book.price * quantity }.sum()

		val userBalance = UserDao.findUserById(userId)?.balance ?: return false

		val newBalance = userBalance - price

		if (newBalance <= 0) {
			return false
		}

		if (!OrderDao.addUserOrder(userId, booksToBuy.map { it.id }.toList(), quantities)) {
			return false
		}

		return UserDao.updateUserBalance(userId, newBalance)
	}
}