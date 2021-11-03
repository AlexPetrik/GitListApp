package ru.alexpetrik.gitlistapp

import android.app.Application
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory;

class GitListApplication : Application() {

//    private lateinit var githubApi : GitListApi
//
//    override fun onCreate() {
//        super.onCreate()
//
//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://github.com")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        githubApi = retrofit.create(GitListApi::class.java)
//    }
//
//    fun getApi() = githubApi
}