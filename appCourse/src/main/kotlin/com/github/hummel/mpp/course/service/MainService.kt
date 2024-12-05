package com.github.hummel.mpp.course.service

import com.github.hummel.mpp.course.dao.AuthorDao
import com.github.hummel.mpp.course.dao.BookDao
import com.github.hummel.mpp.course.entity.Book

object MainService {
	fun getAllBooks(): List<Book> = BookDao.findAllBooks().sortedBy { it.year }

	fun getUniqueAuthors(): List<String> = AuthorDao.findUniqueAuthors().sortedBy { it }

	fun getUniqueTypes(): List<String> = BookDao.findUniqueTypes().sortedBy { it }

	fun getUniqueYears(): List<String> = BookDao.findUniqueYears().sortedBy { it }

	fun getBooksWithIds(ids: List<Int>): List<Book> {
		val books = BookDao.findAllBooks()
		val filteredBooks = books.filter { (id, _, _, _, _, _, _, _) ->
			ids.contains(id)
		}.toList().sortedBy { it.year }
		return filteredBooks
	}

	fun getBooksOfAuthor(authorName: String): List<Book> {
		val books = BookDao.findAllBooks()
		val author = AuthorDao.findAuthorByName(authorName) ?: throw Exception()

		val filteredBooks = books.filter {
			authorName == "all" || it.authorId == author.id
		}.toList().sortedBy { it.year }
		return filteredBooks
	}

	fun getBooksOfType(type: String): List<Book> {
		val books = BookDao.findAllBooks()
		val filteredBooks = books.filter {
			type == "all" || it.type == type
		}.toList().sortedBy { it.year }
		return filteredBooks
	}

	fun getBooksSinceYear(year: String): List<Book> {
		val books = BookDao.findAllBooks()
		val filteredBooks = books.filter {
			year == "all" || it.year.toInt() >= year.toInt()
		}.toList().sortedBy { it.year }
		return filteredBooks
	}
}