package com.github.hummel.mpp.course

import com.github.hummel.mpp.course.controller.configureRouting
import com.github.hummel.mpp.course.dao.BookDao
import com.github.hummel.mpp.course.dao.OrderDao
import com.github.hummel.mpp.course.dao.UserDao
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.CORS
import java.sql.Connection
import java.sql.DriverManager

lateinit var connection: Connection

fun main() {
	embeddedServer(Netty, port = 2999, module = Application::module).start(wait = true)
}

fun Application.module() {
	install(CORS) {
		anyHost()
		allowCredentials = true
		allowMethod(HttpMethod.Delete)
		allowMethod(HttpMethod.Post)
		allowMethod(HttpMethod.Put)
		allowMethod(HttpMethod.Get)
		allowHeader(HttpHeaders.ContentType)
	}

	configureDatabase()
	configureRouting()
}

fun Application.configureDatabase() {
	Class.forName("org.postgresql.Driver")

	connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "root", "")

	UserDao.initTable()
	BookDao.initTable()
	OrderDao.initTable()
}