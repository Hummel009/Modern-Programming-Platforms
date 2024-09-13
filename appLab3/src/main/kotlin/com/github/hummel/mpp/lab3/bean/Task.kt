package com.github.hummel.mpp.lab3.bean

import kotlinx.serialization.Serializable

@Serializable
data class Task(val title: String, val status: String, val dueDate: String, val file: String?)