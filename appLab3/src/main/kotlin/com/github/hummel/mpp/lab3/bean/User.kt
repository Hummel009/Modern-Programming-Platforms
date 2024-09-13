package com.github.hummel.mpp.lab3.bean;

import kotlinx.serialization.Serializable

@Serializable
data class User(val username: String, val password: String)
