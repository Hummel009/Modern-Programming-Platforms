package com.github.hummel.mpp.course

import com.github.hummel.mpp.course.controller.configureRouting
import com.github.hummel.mpp.course.dao.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
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
		allowMethod(HttpMethod.Post)
		allowMethod(HttpMethod.Put)
		allowMethod(HttpMethod.Get)
		allowMethod(HttpMethod.Options)
		allowHeader(HttpHeaders.ContentType)
		allowHeader(HttpHeaders.Authorization)
		allowHeader(HttpHeaders.AccessControlAllowOrigin)
	}

	configureDatabase()
	configureRouting()
}

fun configureDatabase() {
	Class.forName("org.h2.Driver")

	connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "root", "")

	AuthorDao.initTable()
	TypeDao.initTable()
	BookDao.initTable()
	UserDao.initTable()
	OrderDao.initTable()
}