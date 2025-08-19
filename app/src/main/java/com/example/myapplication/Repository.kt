package com.example.myapplication

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Repository {

    companion object {
        private const val TAG = "Repository"
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
            Log.d(TAG, "Request -> GET ${BASE_URL}users")
            val response = api.getUsers()

            Log.d(TAG, "Response <- isSuccessful=${response.isSuccessful}, code=${response.code()}")
            if (response.isSuccessful) {
                val users = response.body().orEmpty()
                Log.d(TAG, "Parsed users size = ${users.size}")
                Result.success(users)
            } else {
                val msg = "HTTP ${response.code()} - ${response.message()}"
                Log.e(TAG, "Failure: $msg")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception: ${e.message}", e)
            Result.failure(e)
        }
    }
}
