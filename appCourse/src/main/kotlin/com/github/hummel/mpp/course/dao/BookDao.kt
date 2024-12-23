package com.github.hummel.mpp.course.dao

import com.github.hummel.mpp.course.connection
import com.github.hummel.mpp.course.entity.Book
import java.sql.SQLException

object BookDao {
	fun initTable() {
		val sql = """
		CREATE TABLE IF NOT EXISTS books (
			`id` INT PRIMARY KEY AUTO_INCREMENT,
			`name` VARCHAR(255) NOT NULL,
			`desc` VARCHAR(255) NOT NULL,
			`image` VARCHAR(255) NOT NULL,
			`author_id` INT NOT NULL,
			`type_id` INT NOT NULL,
			`year` INT NOT NULL,
			`price` DECIMAL(24, 2) NOT NULL,
			FOREIGN KEY (`author_id`) REFERENCES authors(`id`),
			FOREIGN KEY (`type_id`) REFERENCES types(`id`)
		);
		""".trimIndent()

		try {
			connection.createStatement().execute(sql)
		} catch (e: SQLException) {
			e.printStackTrace()
		}

		val sqlFill = """INSERT
			INTO books (`name`, `desc`, `image`, `author_id`, `type_id`, `year`, `price`)
			VALUES (?, ?, ?, ?, ?, ?, ?)
		""".trimIndent()

		val names = listOf(
			"Lenovo RD87465326",
			"Lenovo RD87465342",
			"Lenovo RDw4r44r",
			"Lenovo RDq234r2",
			"Asus R84r4t35t",
			"Asus R3534trwfr",
			"Asus Rwrfawr34",
			"Asus R657587"
		)

		val descs = listOf(
			"Очень хороший ноутбук от фирмы Lenovo.",
			"Очень хороший ноутбук от фирмы Lenovo.",
			"Очень хороший ноутбук от фирмы Lenovo.",
			"Очень хороший ноутбук от фирмы Lenovo.",
			"Очень хороший ноутбук от фирмы Asus.",
			"Очень хороший ноутбук от фирмы Asus.",
			"Очень хороший ноутбук от фирмы Asus.",
			"Очень хороший ноутбук от фирмы Asus.",
		)

		val images = listOf(
			"authors/lenovo.jpg",
			"authors/lenovo.jpg",
			"authors/lenovo.jpg",
			"authors/lenovo.jpg",
			"authors/asus.jpg",
			"authors/asus.jpg",
			"authors/asus.jpg",
			"authors/asus.jpg"
		)

		val authorIds = listOf(
			1,
			1,
			1,
			1,
			2,
			2,
			2,
			2,
		)

		val typeIds = listOf(
			1,
			1,
			1,
			1,
			2,
			2,
			2,
			2,
		)

		val years = listOf(
			2020,
			2020,
			2020,
			2020,
			2021,
			2021,
			2021,
			2021,
		)

		val prices = listOf(
			1029.0,
			1039.0,
			1049.0,
			1059.0,
			1069.0,
			1079.0,
			1089.0,
			1099.0,
		)

		try {
			val pstmt = connection.prepareStatement(sqlFill)

			for (i in names.indices) {
				pstmt.setString(1, names[i])
				pstmt.setString(2, descs[i])
				pstmt.setString(3, images[i])
				pstmt.setInt(4, authorIds[i])
				pstmt.setInt(5, typeIds[i])
				pstmt.setInt(6, years[i])
				pstmt.setDouble(7, prices[i])
				pstmt.addBatch()
			}

			pstmt.executeBatch()
		} catch (e: SQLException) {
			e.printStackTrace()
		}
	}

	fun findAllBooks(): List<Book> {
		val sql = "SELECT * FROM books"

		try {
			val stmt = connection.createStatement()
			val rs = stmt.executeQuery(sql)

			val books = mutableListOf<Book>()
			while (rs.next()) {
				books.add(
					Book(
						id = rs.getInt("id"),
						name = rs.getString("name"),
						desc = rs.getString("desc"),
						image = rs.getString("image"),
						authorId = rs.getInt("author_id"),
						typeId = rs.getInt("type_id"),
						year = rs.getInt("year"),
						price = rs.getDouble("price"),
					)
				)
			}
			return books
		} catch (e: SQLException) {
			e.printStackTrace()

			return emptyList()
		}
	}

	fun findUniqueYears(): List<Int> {
		val sql = "SELECT DISTINCT `year` FROM books"

		try {
			val stmt = connection.createStatement()
			val rs = stmt.executeQuery(sql)

			val years = mutableSetOf<Int>()
			while (rs.next()) {
				years.add(rs.getInt("year"))
			}
			return years.toList()
		} catch (e: SQLException) {
			e.printStackTrace()

			return emptyList()
		}
	}
}