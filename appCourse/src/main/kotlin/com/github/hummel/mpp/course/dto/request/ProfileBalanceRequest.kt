package com.github.hummel.mpp.course.dto.request

data class ProfileBalanceRequest(
	val userId: Int, val token: String?, val rechargeBalance: Double
)