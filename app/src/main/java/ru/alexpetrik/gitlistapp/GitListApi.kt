package ru.alexpetrik.gitlistapp

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface GitListApi {
    @POST("device_code")
    fun getDeviceCode() : Call<DeviceCode>
}