package com.github.hummel.mpp.course.dao

import com.github.hummel.mpp.course.connection
import com.github.hummel.mpp.course.entity.User
import java.sql.SQLException

object UserDao {
	fun initTable() {
		val sql = """
		CREATE TABLE IF NOT EXISTS users (
			`id` INT PRIMARY KEY AUTO_INCREMENT,
			`username` VARCHAR(255) UNIQUE NOT NULL,
			`password` VARCHAR(1024) NOT NULL,
			`balance` INT NOT NULL DEFAULT 1000
		);
		""".trimIndent()

		try {
			connection.createStatement().execute(sql)
		} catch (e: SQLException) {
			e.printStackTrace()
		}
	}

	fun addUser(username: String, hashedPassword: String): Boolean {
		val sql = "INSERT INTO users (username, password) VALUES (?, ?)"
		return try {
			val pstmt = connection.prepareStatement(sql)
			pstmt.setString(1, username)
			pstmt.setString(2, hashedPassword)
			pstmt.executeUpdate()

			true
		} catch (e: SQLException) {
			e.printStackTrace()

			false
		}
	}

	fun findUserByUsername(username: String): User? {
		val sql = "SELECT * FROM users WHERE username = ?"
		try {
			val pstmt = connection.prepareStatement(sql)
			pstmt.setString(1, username)
			val rs = pstmt.executeQuery()
			if (rs.next()) {
				return User(
					rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getInt("balance")
				)
			}
		} catch (e: SQLException) {
			e.printStackTrace()
		}
		return null
	}

	fun updateUserUsername(username: String, newUsername: String): Boolean {
		val sql = "UPDATE users SET username = ? WHERE username = ?"
		return try {
			val pstmt = connection.prepareStatement(sql)
			pstmt.setString(1, newUsername)
			pstmt.setString(2, username)
			pstmt.executeUpdate() > 0
		} catch (e: SQLException) {
			e.printStackTrace()
			false
		}
	}

	fun updateUserPassword(username: String, newPassword: String): Boolean {
		val sql = "UPDATE users SET password = ? WHERE username = ?"
		return try {
			val pstmt = connection.prepareStatement(sql)
			pstmt.setString(1, newPassword)
			pstmt.setString(2, username)
			pstmt.executeUpdate() > 0
		} catch (e: SQLException) {
			e.printStackTrace()
			false
		}
	}
}