package com.github.hummel.mpp.course.service

import com.github.hummel.mpp.course.dao.UserDao
import com.github.hummel.mpp.course.entity.Book

object CartService {
	fun buyBooks(username: String, booksToBuy: List<Book>): Boolean {
		val price = booksToBuy.sumOf { it.price }

		val userBalance = UserDao.findUserBalance(username) ?: return false

		val newBalance = userBalance - price

		if (newBalance <= 0) {
			return false
		}

		return UserDao.updateUserBalance(username, newBalance)
	}
}