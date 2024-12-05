package com.github.hummel.mpp.course.dao

import com.github.hummel.mpp.course.connection
import com.github.hummel.mpp.course.entity.Author
import java.sql.SQLException

object AuthorDao {
	fun initTable() {
		val sql = """
		CREATE TABLE IF NOT EXISTS authors (
			`id` INT PRIMARY KEY AUTO_INCREMENT,
			`name` VARCHAR(255) NOT NULL,
			`biography` TEXT NOT NULL
		);
		""".trimIndent()

		try {
			connection.createStatement().execute(sql)
		} catch (e: SQLException) {
			e.printStackTrace()
		}

		val sqlFill = """INSERT INTO authors (`name`, `biography`) VALUES (?, ?)""".trimIndent()

		val names = listOf(
			"Адам Міцкевіч",
			"Янка Купала",
			"Якуб Колас",
			"Максім Багдановіч",
			"Уладзімір Караткевіч"
		)
		val biographies = listOf(
			"Адам Міцкевіч (1798-1855) — польскі паэт і драматург, які лічыцца адным з найважнейшых прадстаўнікоў польскай літаратуры. Яго творы адлюстроўваюць глыбокія пачуцці і нацыянальную ідэнтычнасць.",
			"Янка Купала (1882-1942) — беларускі паэт, драматург і грамадскі дзеяч, які з'яўляецца адным з класікаў беларускай літаратуры. Яго творы адлюстроўваюць дух беларускага народа.",
			"Якуб Колас (1882-1956) — беларускі пісьменнік, паэт і грамадскі дзеяч. Яго творы часта закранаюць тэмы роднай зямлі і нацыянальнай самасвядомасці.",
			"Максім Багдановіч (1891-1917) — беларускі паэт, які зрабіў значны ўклад у развіццё беларускай літаратуры. Яго творы адлюстроўваюць глыбокія пачуцці і любоў да роднай зямлі.",
			"Уладзімір Караткевіч (1930-1984) — беларускі пісьменнік, паэт і драматург, вядомы сваімі гістарычнымі раманаў і паэзіяй."
		)

		try {
			val pstmt = connection.prepareStatement(sqlFill)

			for (i in names.indices) {
				pstmt.setString(1, names[i])
				pstmt.setString(2, biographies[i])
				pstmt.addBatch()
			}

			pstmt.executeBatch()
		} catch (e: SQLException) {
			e.printStackTrace()
		}
	}

	fun findAuthorByName(name: String): Author? {
		val sql = "SELECT * FROM authors WHERE `name` = ?"

		return try {
			val pstmt = connection.prepareStatement(sql)
			pstmt.setString(1, name)

			val rs = pstmt.executeQuery()
			if (rs.next()) {
				Author(
					id = rs.getInt("id"),
					name = rs.getString("name"),
					biography = rs.getString("biography")
				)
			} else {
				null
			}
		} catch (e: SQLException) {
			e.printStackTrace()

			null
		}
	}

	fun findUniqueAuthors(): List<String> {
		val sql = "SELECT DISTINCT `name` FROM authors"

		return try {
			val stmt = connection.createStatement()
			val rs = stmt.executeQuery(sql)

			val authors = mutableSetOf<String>()
			while (rs.next()) {
				authors.add(rs.getString("name"))
			}
			authors.toList()
		} catch (e: SQLException) {
			e.printStackTrace()
			emptyList()
		}
	}
}