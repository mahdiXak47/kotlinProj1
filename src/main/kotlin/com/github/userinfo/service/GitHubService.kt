package com.github.userinfo.service

import com.github.userinfo.api.GitHubApi
import com.github.userinfo.model.GitHubUser
import com.github.userinfo.model.Repository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

class GitHubService {
    private val api: GitHubApi
    private val cache = ConcurrentHashMap<String, Pair<GitHubUser, List<Repository>>>()
    private val cacheDuration = Duration.ofMinutes(30)

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create())
            .build()

        api = retrofit.create(GitHubApi::class.java)
    }

    suspend fun getUserInfo(username: String): Pair<GitHubUser, List<Repository>>? {
        // Check cache first
        val cachedData = cache[username]
        if (cachedData != null) {
            return cachedData
        }

        try {
            // Fetch user data
            val userResponse = api.getUser(username)
            if (!userResponse.isSuccessful) {
                handleError(userResponse.code(), "Failed to fetch user data")
                return null
            }
            val user = userResponse.body() ?: return null

            // Fetch repositories
            val reposResponse = api.getUserRepositories(username)
            if (!reposResponse.isSuccessful) {
                handleError(reposResponse.code(), "Failed to fetch repositories")
                return null
            }
            val repositories = reposResponse.body() ?: emptyList()

            // Cache the results
            val result = user to repositories
            cache[username] = result
            return result

        } catch (e: Exception) {
            println("Error fetching user info: ${e.message}")
            return null
        }
    }

    private fun handleError(code: Int, message: String) {
        when (code) {
            404 -> println("User not found")
            403 -> println("Rate limit exceeded. Please try again later")
            401 -> println("Authentication required")
            else -> println("$message (Error code: $code)")
        }
    }

    fun clearCache() {
        cache.clear()
    }
} 