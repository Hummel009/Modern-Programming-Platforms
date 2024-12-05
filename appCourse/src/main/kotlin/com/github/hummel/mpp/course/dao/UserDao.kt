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
			`balance` DECIMAL(24, 2) NOT NULL
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
			pstmt.executeUpdate() > 0
		} catch (e: SQLException) {
			e.printStackTrace()

			false
		}
	}

	fun findUserByUsername(username: String): User? {
		val sql = "SELECT * FROM users WHERE username = ?"
		return try {
			val pstmt = connection.prepareStatement(sql)
			pstmt.setString(1, username)
			val rs = pstmt.executeQuery()
			if (rs.next()) {
				User(
					rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getDouble("balance")
				)
			} else {
				null
			}
		} catch (e: SQLException) {
			e.printStackTrace()

			null
		}
	}

	fun updateUserUsername(userId: Int, newUsername: String): Boolean {
		val sql = "UPDATE users SET username = ? WHERE id = ?"
		return try {
			val pstmt = connection.prepareStatement(sql)
			pstmt.setString(1, newUsername)
			pstmt.setInt(2, userId)
			pstmt.executeUpdate() > 0
		} catch (e: SQLException) {
			e.printStackTrace()

			false
		}
	}

	fun updateUserPassword(userId: Int, newPassword: String): Boolean {
		val sql = "UPDATE users SET password = ? WHERE id = ?"
		return try {
			val pstmt = connection.prepareStatement(sql)
			pstmt.setString(1, newPassword)
			pstmt.setInt(2, userId)
			pstmt.executeUpdate() > 0
		} catch (e: SQLException) {
			e.printStackTrace()

			false
		}
	}

	fun findUserBalance(userId: Int): Double? {
		val sql = "SELECT balance FROM users WHERE id = ?"
		return try {
			val pstmt = connection.prepareStatement(sql)
			pstmt.setInt(1, userId)
			val rs = pstmt.executeQuery()
			if (rs.next()) {
				rs.getDouble("balance")
			} else {
				null
			}
		} catch (e: SQLException) {
			e.printStackTrace()

			null
		}
	}

	fun updateUserBalance(userId: Int, newBalance: Double): Boolean {
		val sql = "UPDATE users SET balance = ? WHERE id = ?"
		return try {
			val pstmt = connection.prepareStatement(sql)
			pstmt.setDouble(1, newBalance)
			pstmt.setInt(2, userId)
			pstmt.executeUpdate() > 0
		} catch (e: SQLException) {
			e.printStackTrace()

			false
		}
	}
}