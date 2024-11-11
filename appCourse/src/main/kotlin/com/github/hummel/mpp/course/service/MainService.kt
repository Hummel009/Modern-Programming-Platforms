package com.github.hummel.mpp.course.service

import com.github.hummel.mpp.course.dao.BookDao
import com.github.hummel.mpp.course.entity.Book

object MainService {
	fun getAllBooks(): List<Book> = BookDao.getAllBooks()
	fun getAllAuthors(): List<String> = BookDao.getAllAuthors()
}