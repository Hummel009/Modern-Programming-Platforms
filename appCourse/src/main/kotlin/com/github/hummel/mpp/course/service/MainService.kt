package com.github.hummel.mpp.course.service

import com.github.hummel.mpp.course.dao.AuthorDao
import com.github.hummel.mpp.course.dao.BookDao
import com.github.hummel.mpp.course.dao.TypeDao
import com.github.hummel.mpp.course.entity.Author
import com.github.hummel.mpp.course.entity.Book
import com.github.hummel.mpp.course.entity.Type

object MainService {
	fun getAllBooks(): List<Book> = BookDao.findAllBooks().sortedBy { it.year }

	fun getAllAuthors(): List<Author> = AuthorDao.findAllAuthors().sortedBy { it.name }

	fun getAllTypes(): List<Type> = TypeDao.findAllTypes().sortedBy { it.name }

	fun getAllYears(): List<Int> = BookDao.findUniqueYears().sortedBy { it }

	fun getBooksOfAuthor(authorId: Int): List<Book> {
		val books = BookDao.findAllBooks()

		val filteredBooks = books.filter {
			it.authorId == authorId
		}.toList().sortedBy {
			it.year
		}

		return filteredBooks
	}

	fun getBooksOfType(typeId: Int): List<Book> {
		val books = BookDao.findAllBooks()

		val filteredBooks = books.filter {
			it.typeId == typeId
		}.toList().sortedBy {
			it.year
		}

		return filteredBooks
	}

	fun getBooksSinceYear(year: Int): List<Book> {
		val books = BookDao.findAllBooks()

		val filteredBooks = books.filter {
			it.year >= year
		}.toList().sortedBy {
			it.year
		}

		return filteredBooks
	}
}