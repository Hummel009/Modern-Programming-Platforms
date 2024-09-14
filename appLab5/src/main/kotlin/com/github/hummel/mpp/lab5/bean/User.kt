package com.github.hummel.mpp.lab5.bean

import kotlinx.serialization.Serializable

@Serializable
data class User(val username: String, val password: String)
