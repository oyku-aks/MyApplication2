// Repository.kt
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
            .baseUrl("https://jsonplaceholder.typicode.com/") // mevcut baseUrl yapınla uyumlu
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(ApiService::class.java)
    }

    suspend fun getUsers(): List<User> = withContext(Dispatchers.IO) {
        try {
            // ApiService.getUsers(): suspend çağrı (mevcut yapın)
            api.getUsers()
        } catch (e: Exception) {
            // Tüm istisnaları tek noktada kullanıcı-dostu mesaja map et
            throw Exception(mapToUserMessage(e))
        }
    }

    private fun mapToUserMessage(e: Exception): String = when (e) {
        is UnknownHostException -> "İnternet bağlantısı yok ya da sunucuya ulaşılamıyor"
        is SocketTimeoutException -> "İstek zaman aşımına uğradı, lütfen tekrar deneyin"
        is IOException -> "Ağ hatası oluştu, bağlantınızı kontrol edin"
        is HttpException -> when (e.code()) {
            400 -> "Geçersiz istek (400)"
            401 -> "Yetkisiz erişim (401)"
            403 -> "Erişim engellendi (403)"
            404 -> "İçerik bulunamadı (404)"
            500 -> "Sunucu hatası (500)"
            else -> "HTTP hatası: ${e.code()}"
        }
        else -> "Bilinmeyen bir hata oluştu"
    }
}
