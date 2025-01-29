package com.chaitany.agewell

data class Medicine(
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val quantity: Int = 0,
    val schedule: List<String> = listOf(),
    val userPhone: String = ""  // Changed from userId to userPhone
)