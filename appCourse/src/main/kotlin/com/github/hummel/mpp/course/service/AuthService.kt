package com.github.hummel.mpp.course.service

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.hummel.mpp.course.dao.UserDao
import com.github.hummel.mpp.course.entity.Token

object AuthService {
	fun registerUser(username: String, password: String): Boolean {
		val hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray())

		return UserDao.addUser(username, hashedPassword)
	}

	fun generateToken(username: String, password: String): String = JWT.create().apply {
		withClaim("username", username)
		withClaim("password", password)
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

	fun areCredentialsValid(username: String?, password: String?): Boolean {
		username ?: return false
		password ?: return false

		val user = UserDao.findUserByUsername(username) ?: return false
		val hashedPassword = user.password

		return BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified
	}
}