package com.github.hummel.mpp.course.service

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.hummel.mpp.course.dao.AuthDao
import com.github.hummel.mpp.course.entity.User
import com.sun.org.apache.xpath.internal.operations.Bool

object AuthService {
	fun addUser(user: User): Boolean {
		val hashedPassword = BCrypt.withDefaults().hashToString(12, user.password.toCharArray())

		return AuthDao.addUser(user.username, hashedPassword)
	}

	fun isValidToken(token: String?): Boolean {
		return try {
			val decoded = JWT.decode(token)
			val username = decoded.getClaim("username").asString()
			val password = decoded.getClaim("password").asString()
			isValidUser(username, password)
		} catch (_: Exception) {
			false
		}
	}

	fun isValidUser(user: User): Boolean {
		val username = user.username
		val password = user.password

		return isValidUser(username, password)
	}

	fun generateToken(user: User): String = JWT.create().apply {
		withClaim("username", user.username)
		withClaim("password", user.password)
		sign(Algorithm.HMAC256("secret"))
	}.toString()

	private fun isValidUser(username: String, password: String): Boolean {
		val user = AuthDao.findUserByUsername(username) ?: return false

		return BCrypt.verifyer().verify(password.toCharArray(), user.password).verified
	}
}