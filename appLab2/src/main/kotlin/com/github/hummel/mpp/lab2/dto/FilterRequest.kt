package com.github.hummel.mpp.lab2.dto

import kotlinx.serialization.Serializable

@Serializable
data class FilterRequest(val filter: String)