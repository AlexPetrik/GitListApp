package ru.alexpetrik.gitlistapp

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var retrofit: Retrofit? = null

    fun getClient(baseUrl: String): Retrofit {

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthApiInterceptor())
            .build()

        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
        return retrofit!!
    }

    class AuthApiInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val original: Request = chain.request()

            // Настраиваем запросы
            val request: Request = original.newBuilder()
                .header("Accept", "application/json")
                .header("client_id", BuildConfig.CLIENT_ID)
                .header("login", "AlexPetrik")
                .method(original.method(), original.body())
                .build()
            return chain.proceed(request)
        }

    }

}