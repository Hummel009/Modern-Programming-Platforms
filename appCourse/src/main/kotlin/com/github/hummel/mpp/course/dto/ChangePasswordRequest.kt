package com.github.hummel.mpp.course.dto

data class ChangePasswordRequest(val token: String, val newPassword: String)