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
			"Пан Тадэвуш",
			"Паўлінка",
			"Новая зямля",
			"Пярсцёнак",
			"У школе",
			"Шыпшына",
			"Вянок",
			"Зорка Венера",
			"Каласы пад сярпом тваім",
		)

		val descs = listOf(
			"Гістарычны раман Адама Міцкевіча, які апісвае падзеі, што адбываюцца ў Літве ў пачатку XIX стагоддзя. Галоўныя героі — шляхцічы, якія спрабуюць вырашыць свае канфлікты.",
			"Раман Янкі Купалы, які раскрывае жыццё і любоў простага беларуса. У цэнтры сюжэта — гісторыя кахання паміж Паўлінай і яе абраннікам, якая адлюстроўвае традыцыі і звычаі.",
			"Твор Максіма Багдановіча, які адлюстроўвае цяжкасці жыцця беларусаў у перыяд пасля рэвалюцыі. Раман даследуе тэмы ідэнтычнасці і пошуку новага месца ў свеце. Вельмі рэкамендуецца!",
			"Кніга, якая даследуе чалавечыя адносіны праз прызму кахання і страты. Галоўны герой перажывае драматычныя падзеі, якія змяняюць яго жыццё назаўсёды. Чытайце ўсе!",
			"Аповесць, якая апісвае школьнае жыццё і праблемы падлеткаў. У цэнтры сюжэта — сяброўства, канфлікты і першыя каханні. Вельмі духоўная і памяркоўная кніга, рэкамендавана.",
			"Літаратурны твор, які даследуе тэмы прыроды і чалавечых эмоцый. Галоўны герой знаходзіць сябе ў свеце шыпшыны, дзе кожны крок адкрывае новыя магчымасці. Вельмі рэкамендуецца!",
			"Кніга, якая складаецца з розных гісторый пра каханне і сямейныя каштоўнасці. Кожная гісторыя падкрэслівае важнасць традыцый у жыцці беларусаў. Абавязкова прачытайце!",
			"Раман, які даследуе тэму кахання ў складаных умовах. Галоўныя героі спрабуюць знайсці шлях адзін да аднаго на фоне сацыяльных праблем. Рэкамендацыя для вас. Вельмі рэкамендуецца!",
			"Твор Якуба Коласа, які адлюстроўвае жыццё беларусаў у часе цяжкіх выпрабаванняў. Кніга паказвае іх мужнасць і стойкасць у барацьбе за сваё месца ў свеце. Вельмі рэкамендуецца!",
		)

		val images = listOf(
			"books/am_tadewusz.jpg",
			"books/mb_piarscionak.jpg",
			"books/mb_szkola.jpg",
			"books/mb_szypszyna.jpg",
			"books/mb_wianok.jpg",
			"books/mb_wieniera.jpg",
			"books/uk_kalasy.jpg",
			"books/jk_ziamlia.jpg",
			"books/jk_paulinka.jpg",
		)

		val authorIds = listOf(
			1, // Пан Тадэвуш
			2, // Пярсцёнак
			2, // У школе
			2, // Шыпшына
			2, // Вянок
			2, // Зорка Венера
			3, // Каласы пад сярпом тваім
			4, // Новая зямля
			5, // Паўлінка
		)

		val typeIds = listOf(
			1, // Пан Тадэвуш
			2, // Пярсцёнак
			1, // У школе
			2, // Шыпшына
			2, // Вянок
			1, // Зорка Венера
			2, // Каласы пад сярпом тваім
			1, // Новая зямля
			3, // Паўлінка
		)

		val years = listOf(
			1834, // Пан Тадэвуш
			1958, // Пярсцёнак
			1949, // У школе
			1970, // Шыпшына
			1965, // Вянок
			1980, // Зорка Венера
			1965, // Каласы пад сярпом тваім
			1959, // Новая зямля
			1959, // Паўлінка
		)

		val prices = listOf(
			1.19, // Пан Тадэвуш
			1.29, // Пярсцёнак
			1.39, // У школе
			1.49, // Шыпшына
			1.59, // Вянок
			1.69, // Зорка Венера
			1.79, // Каласы пад сярпом тваім
			1.89, // Новая зямля
			1.99, // Паўлінка
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