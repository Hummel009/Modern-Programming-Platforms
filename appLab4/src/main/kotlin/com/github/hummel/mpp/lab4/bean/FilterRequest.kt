package com.github.hummel.mpp.lab4.bean

import kotlinx.serialization.Serializable

@Serializable
data class FilterRequest(val filterStatus: String)