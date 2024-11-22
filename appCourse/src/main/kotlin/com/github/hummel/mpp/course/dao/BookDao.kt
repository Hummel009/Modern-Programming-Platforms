package com.github.hummel.mpp.course.dao

import com.github.hummel.mpp.course.connection
import com.github.hummel.mpp.course.entity.Book
import java.sql.SQLException

object BookDao {
	fun initTable() {
		val sql = """
		CREATE TABLE IF NOT EXISTS books (
			id INT PRIMARY KEY AUTO_INCREMENT,
			title VARCHAR(255) NOT NULL,
			description TEXT NOT NULL,
			author VARCHAR(255) NOT NULL,
			imgPath VARCHAR(255) NOT NULL,
			price DECIMAL(24, 2) NOT NULL
		);
		""".trimIndent()

		try {
			connection.createStatement().execute(sql)
		} catch (e: SQLException) {
			e.printStackTrace()
		}

		val sqlFill = "INSERT INTO books (title, description, author, imgPath, price) VALUES (?, ?, ?, ?, ?)"
		val names = listOf(
			"Пан Тадэвуш",
			"Паўлінка",
			"Новая зямля",
			"Пярсцёнак",
			"У школе",
			"Шыпшына",
			"Вянок",
			"Зорка Венера",
			"Каласы пад сярпом тваім"
		)
		val descriptions = listOf(
			"Твор вялікага паэта Адама Міцкевіча, які адзначаецца глыбокім і лірычным падыходам да развіцця паэтычнага мастацтва. У гэтай кнізе вы адкрываеце для сябе крыніцу непаўторнага беларускага культурнага асяроддзя.",
			"Твор вялікага беларускага паэта Янкі Купалы, у якім адлюстроўваецца яго глыбокае пачуццё нацыянальнай ідэнтычнасці і любові да прыроды. Кніга перадае той непаўторны дух, які ляжыць у аснове беларускай літаратуры.",
			"Зборнік твораў выдаючага беларускага паэта Якуба Коласа. У гэтым зборніку адлюстроўваецца шматгадовая творчасць паэта.",
			"Твор Максіма Багдановіча, прадстаўніка выдаючайся групы «Маладосць». У гэтай кнізе вы знойдзеце глыбокія пачуцці любові да роднай зямлі, грамадства і прыроды.",
			"Твор Максіма Багдановіча, у якім адлюстроўваецца аўтабіяграфічныя моманты з жыцця паэта, які развіваў сваю творчасць у школьныя гады. Кніга звартае ўвагу на важнасць асвятлення тэмы адукацыі і выхавання ў мастацтве.",
			"Твор Максіма Багдановіча, у якім адлюстроўваецца яго ўзровень да праблемы чалавечага стасунку да прыроды. Кніга - асалода для тых, каму падабаецца экалагічная тэма ў літаратуры.",
			"Зборнік твораў Максіма Багдановіча, у якім яго лірычнасць і філасофская глыбіня пераплітаюцца ў вянку паэтычных абразаў. Кніга адкрые для вас светлы і глыбокі свет літаратурнага мастацтва.",
			"Твор Максіма Багдановіча, у якім адлюстроўваецца яго бачанне прыроды як галоўнага элемента чалавечага існавання. Кніга - нумар адзін для тых, каму падабаецца лірыка і эстэтыка прыроды.",
			"Твор Уладзіміра Караткевіча, выдатнага паэта і культуртворца. У гэтай кнізе вы адчуеце эмоцыі і пачуцці беларускай нацыі, якія былі актуальнымі для часоў, у якіх жыў паэт."
		)
		val authors = listOf(
			"Адам Міцкевіч",
			"Янка Купала",
			"Якуб Колас",
			"Максім Багдановіч",
			"Максім Багдановіч",
			"Максім Багдановіч",
			"Максім Багдановіч",
			"Максім Багдановіч",
			"Уладзімір Караткевіч"
		)
		val imgPaths = listOf(
			"books/am_tadewusz.jpg",
			"books/jk_paulinka.jpg",
			"books/jk_ziamlia.jpg",
			"books/mb_piarscionak.jpg",
			"books/mb_szkola.jpg",
			"books/mb_szypszyna.jpg",
			"books/mb_wianok.jpg",
			"books/mb_wieniera.jpg",
			"books/uk_kalasy.jpg"
		)
		val prices = listOf(
			1.19, 1.29, 1.39, 1.49, 1.59, 1.69, 1.79, 1.89, 1.99
		)

		try {
			val pstmt = connection.prepareStatement(sqlFill)

			for (i in names.indices) {
				pstmt.setString(1, names[i])
				pstmt.setString(2, descriptions[i])
				pstmt.setString(3, authors[i])
				pstmt.setString(4, imgPaths[i])
				pstmt.setDouble(5, prices[i])
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
						rs.getInt("id"),
						rs.getString("title"),
						rs.getString("description"),
						rs.getString("author"),
						rs.getString("imgPath"),
						rs.getDouble("price")
					)
				)
			}
			return books
		} catch (e: SQLException) {
			e.printStackTrace()

			return emptyList()
		}
	}

	fun findUniqueAuthors(): List<String> {
		val sql = "SELECT DISTINCT author FROM books"

		try {
			val stmt = connection.createStatement()
			val rs = stmt.executeQuery(sql)

			val authors = mutableSetOf<String>()
			while (rs.next()) {
				authors.add(rs.getString("author"))
			}
			return authors.toList()
		} catch (e: SQLException) {
			e.printStackTrace()

			return emptyList()
		}
	}
}