package com.github.hummel.mpp.course.dao

import com.github.hummel.mpp.course.connection
import com.github.hummel.mpp.course.entity.Type
import java.sql.SQLException

object TypeDao {
	fun initTable() {
		val sql = """
		CREATE TABLE IF NOT EXISTS types (
			`id` INT PRIMARY KEY AUTO_INCREMENT,
			`name` VARCHAR(255) NOT NULL
		);
		""".trimIndent()

		try {
			connection.createStatement().execute(sql)
		} catch (e: SQLException) {
			e.printStackTrace()
		}

		val sqlFill = """INSERT
			INTO types (`name`)
			VALUES (?)
		""".trimIndent()

		val names = listOf(
			"Паэзія", "Проза", "П'еса",
		)

		try {
			val pstmt = connection.prepareStatement(sqlFill)

			for (i in names.indices) {
				pstmt.setString(1, names[i])
				pstmt.addBatch()
			}

			pstmt.executeBatch()
		} catch (e: SQLException) {
			e.printStackTrace()
		}
	}

	fun findAllTypes(): List<Type> {
		val sql = "SELECT * FROM types"

		try {
			val stmt = connection.createStatement()
			val rs = stmt.executeQuery(sql)

			val books = mutableListOf<Type>()
			while (rs.next()) {
				books.add(
					Type(
						id = rs.getInt("id"),
						name = rs.getString("name"),
					)
				)
			}
			return books
		} catch (e: SQLException) {
			e.printStackTrace()

			return emptyList()
		}
	}

	fun findTypeById(typeId: Int): Type? {
		val sql = "SELECT * FROM types WHERE `id` = ?"

		return try {
			val pstmt = connection.prepareStatement(sql)
			pstmt.setInt(1, typeId)

			val rs = pstmt.executeQuery()
			if (rs.next()) {
				Type(
					id = rs.getInt("id"),
					name = rs.getString("name"),
				)
			} else {
				null
			}
		} catch (e: SQLException) {
			e.printStackTrace()

			null
		}
	}
}