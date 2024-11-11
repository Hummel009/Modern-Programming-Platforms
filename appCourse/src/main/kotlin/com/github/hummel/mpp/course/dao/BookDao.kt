package com.github.hummel.mpp.course.dao

import com.github.hummel.mpp.course.connection
import com.github.hummel.mpp.course.entity.Book
import java.sql.SQLException
import kotlin.random.Random

object BookDao {
	fun initTable() {
		val sql = """
		CREATE TABLE IF NOT EXISTS books (
			id INT PRIMARY KEY AUTO_INCREMENT,
			title VARCHAR(255) NOT NULL,
			description TEXT NOT NULL,
			author VARCHAR(255) NOT NULL,
			imgPath VARCHAR(255) NOT NULL,
			price INT NOT NULL
		);
		""".trimIndent()

		try {
			connection.createStatement().execute(sql)
		} catch (e: SQLException) {
			e.printStackTrace()
		}
	}

	fun populateRandomBooks(count: Int) {
		val sql = "INSERT INTO books (title, description, author, imgPath, price) VALUES (?, ?, ?, ?, ?)"
		val titles = listOf("title1", "title2", "title3", "title4", "title5")
		val descriptions = listOf("desc1", "desc2", "desc3", "desc4", "desc5")
		val authors = listOf("author1", "author2", "author3", "author4", "author5")
		val imgPaths = listOf("imgPath1", "imgPath2", "imgPath3", "imgPath4", "imgPath5")

		try {
			val pstmt = connection.prepareStatement(sql)
			repeat(count) {
				pstmt.setString(1, titles[Random.nextInt(titles.size)])
				pstmt.setString(2, descriptions[Random.nextInt(descriptions.size)])
				pstmt.setString(3, authors[Random.nextInt(authors.size)])
				pstmt.setString(4, imgPaths[Random.nextInt(imgPaths.size)])
				pstmt.setInt(5, Random.nextInt(100, 500))
				pstmt.addBatch()
			}
			pstmt.executeBatch()
		} catch (e: SQLException) {
			e.printStackTrace()
		}
	}

	fun getAllBooks(): List<Book> {
		val books = mutableListOf<Book>()
		val sql = "SELECT * FROM books"

		try {
			val stmt = connection.createStatement()
			val rs = stmt.executeQuery(sql)
			while (rs.next()) {
				books.add(
					Book(
						rs.getInt("id"),
						rs.getString("title"),
						rs.getString("description"),
						rs.getString("author"),
						rs.getString("imgPath"),
						rs.getInt("price")
					)
				)
			}
		} catch (e: SQLException) {
			e.printStackTrace()
		}

		return books
	}

	fun getAllAuthors(): List<String> {
		val authors = mutableSetOf<String>()
		val sql = "SELECT DISTINCT author FROM books"

		try {
			val stmt = connection.createStatement()
			val rs = stmt.executeQuery(sql)
			while (rs.next()) {
				authors.add(rs.getString("author"))
			}
		} catch (e: SQLException) {
			e.printStackTrace()
		}

		return authors.toList()
	}
}