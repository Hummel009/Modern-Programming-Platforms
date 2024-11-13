package com.github.hummel.mpp.course.service

import com.github.hummel.mpp.course.dao.BookDao
import com.github.hummel.mpp.course.entity.Book

object MainService {
	fun getAllBooks(): List<Book> = BookDao.findAllBooks()

	fun getUniqueAuthors(): List<String> = BookDao.findUniqueAuthors()

	fun getBooksWithIds(ids: List<Int>): List<Book> {
		val books = BookDao.findAllBooks()
		val filteredBooks = books.filter { book ->
			ids.contains(book.id)
		}.toList()
		return filteredBooks
	}

	fun getBooksOfAuthor(author: String): List<Book> {
		val books = BookDao.findAllBooks()
		val filteredBooks = books.filter {
			it.author == author || author == "all"
		}.toList()
		return filteredBooks
	}
}