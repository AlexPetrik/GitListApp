package ru.alexpetrik.gitlistapp

object Common {
    private const val BASE_URL = "https://github.com/login/oauth/authorize/"
    val retrofitService: GitListApi
        get() = RetrofitClient.getClient(BASE_URL).create(GitListApi::class.java)
}
