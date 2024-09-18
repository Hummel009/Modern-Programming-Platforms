package com.github.hummel.mpp.lab5.bean

import kotlinx.serialization.Serializable

@Serializable
data class TaskWrapper(var id: Int, val task: Task)