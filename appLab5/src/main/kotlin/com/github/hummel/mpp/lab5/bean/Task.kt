package com.github.hummel.mpp.lab5.bean

import kotlinx.serialization.Serializable

@Serializable
data class Task(var title: String, val status: String, val dueDate: String, val file: String?)