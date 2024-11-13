package com.github.hummel.mpp.course.dao

import com.github.hummel.mpp.course.connection
import com.github.hummel.mpp.course.entity.Order
import java.sql.SQLException

object OrderDao {
	fun initTable() {
		val sql = """
		CREATE TABLE IF NOT EXISTS orders (
			id INT PRIMARY KEY AUTO_INCREMENT,
			user_id INT NOT NULL,
			book_id INT NOT NULL,
			quantity INT NOT NULL,
			FOREIGN KEY (user_id) REFERENCES users(id),
			FOREIGN KEY (book_id) REFERENCES books(id)
		);
		""".trimIndent()

		try {
			connection.createStatement().execute(sql)
		} catch (e: SQLException) {
			e.printStackTrace()
		}
	}

	fun addOrdersBatch(userId: Int, booksIds: List<Int>, quantities: List<Int>): Boolean {
		val sql = "INSERT INTO orders (user_id, book_id, quantity) VALUES (?, ?, ?)"
		return try {
			connection.autoCommit = false
			val pstmt = connection.prepareStatement(sql)

			booksIds.zip(quantities) { bookId, quantity ->
				pstmt.setInt(1, userId)
				pstmt.setInt(2, bookId)
				pstmt.setInt(3, quantity)
				pstmt.addBatch()
			}

			val results = pstmt.executeBatch()
			connection.commit()

			results.all { it > 0 }
		} catch (e: SQLException) {
			e.printStackTrace()
			connection.rollback()
			false
		} finally {
			connection.autoCommit = true
		}
	}

	fun getUserOrders(userId: Int): List<Order> {
		val sql = "SELECT * FROM orders WHERE user_id = ?"

		val orders = mutableListOf<Order>()
		try {
			val pstmt = connection.prepareStatement(sql)
			pstmt.setInt(1, userId)
			val rs = pstmt.executeQuery()

			while (rs.next()) {
				orders.add(
					Order(
						rs.getInt("id"), rs.getInt("user_id"), rs.getInt("book_id"), rs.getInt("quantity")
					)
				)
			}
		} catch (e: SQLException) {
			e.printStackTrace()
		} finally {
			return orders
		}
	}
}