package com.github.hummel.mpp.course.dao

import com.github.hummel.mpp.course.connection
import com.github.hummel.mpp.course.entity.Order
import com.github.hummel.mpp.course.entity.OrderItem
import java.sql.SQLException
import java.sql.Statement

object OrderDao {
	fun initTable() {
		val sql1 = """
		CREATE TABLE IF NOT EXISTS orders (
			id INT PRIMARY KEY AUTO_INCREMENT,
			user_id INT NOT NULL,
			FOREIGN KEY (user_id) REFERENCES users(id)
		);
		""".trimIndent()

		val sql2 = """
		CREATE TABLE IF NOT EXISTS order_items (
			id INT PRIMARY KEY AUTO_INCREMENT,
			order_id INT NOT NULL,
			book_id INT NOT NULL,
			quantity INT NOT NULL,
			FOREIGN KEY (order_id) REFERENCES orders(id),
			FOREIGN KEY (book_id) REFERENCES books(id)
		);
		""".trimIndent()

		try {
			connection.createStatement().execute(sql1)
			connection.createStatement().execute(sql2)
		} catch (e: SQLException) {
			e.printStackTrace()
		}
	}

	fun addOrder(userId: Int, booksIds: List<Int>, quantities: List<Int>): Boolean {
		val sqlOrder = "INSERT INTO orders (user_id) VALUES (?)"
		val sqlOrderItem = "INSERT INTO order_items (order_id, book_id, quantity) VALUES (?, ?, ?)"

		return try {
			connection.autoCommit = false

			val pstmtOrder = connection.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)
			pstmtOrder.setInt(1, userId)
			pstmtOrder.executeUpdate()

			val generatedKeys = pstmtOrder.generatedKeys
			if (!generatedKeys.next()) {
				throw SQLException("Creating order failed, no ID obtained.")
			}
			val lastOrderId = generatedKeys.getInt(1)

			val pstmtOrderItem = connection.prepareStatement(sqlOrderItem)

			booksIds.zip(quantities) { bookId, quantity ->
				pstmtOrderItem.setInt(1, lastOrderId)
				pstmtOrderItem.setInt(2, bookId)
				pstmtOrderItem.setInt(3, quantity)
				pstmtOrderItem.addBatch()
			}

			val results = pstmtOrderItem.executeBatch()
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

	fun findAllUserOrders(userId: Int): List<Order> {
		val sql = """
			SELECT o.id AS order_id, o.user_id, oi.id AS item_id, oi.book_id, oi.quantity 
			FROM orders o 
			LEFT JOIN order_items oi ON o.id = oi.order_id 
			WHERE o.user_id = ?
		"""

		val ordersMap = mutableMapOf<Int, Order>()

		try {
			val pstmt = connection.prepareStatement(sql)
			pstmt.setInt(1, userId)
			val rs = pstmt.executeQuery()

			while (rs.next()) {
				val orderId = rs.getInt("order_id")
				val order = ordersMap.getOrPut(orderId) {
					Order(orderId, userId, mutableListOf())
				}

				if (rs.getObject("item_id") != null) {
					val orderItem = OrderItem(
						rs.getInt("item_id"), orderId, rs.getInt("book_id"), rs.getInt("quantity")
					)
					order.orderItems.add(orderItem)
				}
			}
		} catch (e: SQLException) {
			e.printStackTrace()
		} finally {
			return ordersMap.values.toList()
		}
	}
}