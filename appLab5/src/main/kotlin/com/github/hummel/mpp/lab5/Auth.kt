package com.github.hummel.mpp.lab5

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.hummel.mpp.lab5.bean.User

fun isValidToken(token: String?): Boolean {
	if (token == null) {
		return false
	}

	val decoded = JWT.decode(token)
	val username = decoded.getClaim("username").asString()
	val password = decoded.getClaim("password").asString()

	return isValidUser(username, password)
}

fun isValidUser(user: User): Boolean {
	val username = user.username
	val password = user.password

	return isValidUser(username, password)
}

fun isValidUser(username: String, password: String): Boolean {
	val neededUsername = "Hummel009"
	val neededPassword = BCrypt.withDefaults().hashToString(12, "amogus134".toCharArray())
	val usernameRule = username == neededUsername
	val passwordRule = BCrypt.verifyer().verify(password.toCharArray(), neededPassword).verified

	return usernameRule && passwordRule
}

fun generateToken(user: User): String = JWT.create()
	.withClaim("username", user.username)
	.withClaim("password", user.password)
	.sign(Algorithm.HMAC256("secret")).toString()