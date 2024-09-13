package com.github.hummel.mpp.lab3.bean

import kotlinx.serialization.Serializable

@Serializable
data class FilterRequest(val filterStatus: String)