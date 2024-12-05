package com.github.hummel.mpp.course.dto.request

data class ChangeUserBalanceRequest(
	val token: String?, val rechargeBalance: Double
)