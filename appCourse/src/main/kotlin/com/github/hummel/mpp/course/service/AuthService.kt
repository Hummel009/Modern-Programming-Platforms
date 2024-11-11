package com.github.hummel.mpp.course.service

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.hummel.mpp.course.dao.UserDao
import com.github.hummel.mpp.course.entity.Token
import com.github.hummel.mpp.course.entity.User

object AuthService {
	fun registerUser(user: User): Boolean {
		val hashedPassword = BCrypt.withDefaults().hashToString(12, user.password.toCharArray())

		return UserDao.addUser(user.username, hashedPassword)
	}

	fun generateToken(user: User): String = JWT.create().apply {
		withClaim("username", user.username)
		withClaim("password", user.password)
	}.sign(Algorithm.HMAC256("secret"))

	fun decomposeToken(token: String?): Token? {
		return try {
			val decoded = JWT.decode(token)
			val username = decoded.getClaim("username").asString()
			val password = decoded.getClaim("password").asString()
			Token(username, password)
		} catch (_: Exception) {
			null
		}
	}

	fun isValidUser(token: Token?): Boolean {
		token ?: return false

		val username = token.username
		val password = token.password

		return isValidUser(username, password)
	}

	fun isValidUser(user: User): Boolean {
		val username = user.username
		val password = user.password

		return isValidUser(username, password)
	}

	private fun isValidUser(username: String, password: String): Boolean {
		val user = UserDao.findUserByUsername(username) ?: return false
		val hashedPassword = user.password

		return BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified
	}
}