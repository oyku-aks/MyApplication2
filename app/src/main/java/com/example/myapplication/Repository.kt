package com.example.myapplication

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Repository {

    companion object {
        private const val BASE_URL = "https://jsonplaceholder.typicode.com/"
    }

    private val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    suspend fun getUsers(): Result<List<User>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getUsers()
            if (response.isSuccessful) {
                Result.success(response.body().orEmpty())
            } else {
                val errorBody = response.errorBody()?.string().orEmpty()
                val msg = buildString {
                    append("HTTP ${response.code()} - ${response.message()}")
                    if (errorBody.isNotBlank()) append(" | $errorBody")
                }
                Result.failure(IllegalStateException(msg))
            }
        } catch (e: Exception) {
            Result.failure(RuntimeException("Network request failed: ${e.message}", e))
        }
    }
}
