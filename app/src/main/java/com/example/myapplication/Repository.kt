package com.example.myapplication

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class Repository {
    private val api: ApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/") // same as before
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(ApiService::class.java)
    }

    suspend fun getUsers(): List<User> = withContext(Dispatchers.IO) {
        try {
            api.getUsers()
        } catch (e: Exception) {
            throw Exception(mapToUserMessage(e))
        }
    }

    private fun mapToUserMessage(e: Exception): String = when (e) {
        is UnknownHostException -> "No internet connection or server unreachable"
        is SocketTimeoutException -> "Request timed out, please try again"
        is IOException -> "Network error, please check your connection"
        is HttpException -> when (e.code()) {
            400 -> "Bad request (400)"
            401 -> "Unauthorized (401)"
            403 -> "Forbidden (403)"
            404 -> "Not found (404)"
            500 -> "Internal server error (500)"
            else -> "HTTP error: ${e.code()}"
        }
        else -> "An unexpected error occurred"
    }
}
