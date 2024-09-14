package com.github.hummel.mpp.lab2.bean;

import kotlinx.serialization.Serializable

@Serializable
data class EditTaskRequest(val title: String)