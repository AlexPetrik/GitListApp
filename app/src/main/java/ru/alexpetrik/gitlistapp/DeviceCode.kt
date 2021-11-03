package ru.alexpetrik.gitlistapp

data class DeviceCode(
    val code: String,
    val userCode: String,
    val verificationUri: String,
    val expiresIn: Int,
    val interval: Int
)
