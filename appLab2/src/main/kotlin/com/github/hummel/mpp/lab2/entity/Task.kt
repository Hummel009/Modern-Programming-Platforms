package com.github.hummel.mpp.lab2.entity

import kotlinx.serialization.Serializable

@Serializable
data class Task(var title: String, val status: String, val dueDate: String, val file: String?)