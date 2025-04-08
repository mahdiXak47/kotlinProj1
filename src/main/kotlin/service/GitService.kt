package org.example.service

import com.google.gson.Gson
import org.example.api.GitApi
import org.example.model.GitUser
import java.io.File
import com.google.gson.reflect.TypeToken


class GitService(private val api: GitApi) {
    private val usersCache = mutableMapOf<String, GitUser>()
    private val cacheFile = File("src/main/resources/github_cache.json")
    private val gson = Gson()

    init {
        loadCacheFromFile()
    }

    fun getUserInfo(username: String): GitUser? {
        if (usersCache.containsKey(username)) {
            println("User found in cache!")
            return usersCache[username]
        }

        try {
            val userResponse = api.getUserData(username).execute()
            if (!userResponse.isSuccessful) {
                println("Failed to fetch user data: ${userResponse.code()}")
                return null
            }

            val user = userResponse.body() ?: run {
                println("User data is null")
                return null
            }

            val reposResponse = api.getUserRepos(username).execute()
            val repos = if (reposResponse.isSuccessful) {
                reposResponse.body() ?: emptyList()
            } else {
                println("Failed to fetch repositories: ${reposResponse.code()}")
                emptyList()
            }

            val completeUser = user.copy(repositories = repos)
            usersCache[username] = completeUser
            saveCacheToFile()
            println("User data cached successfully!")
            return completeUser
        } catch (e: Exception) {
            println("Error fetching data: ${e.message}")
            return null
        }
    }

    fun displayUsers() {
        if (usersCache.isEmpty()) {
            println("Cache is empty")
        } else {
            println("Cached users (${usersCache.size} total):")
            usersCache.forEach { (username, user) ->
                println("\nUsername: $username")
                displayUserInfo(user)
            }
        }
    }

    fun searchUserByUsername(username: String) {
        val user = usersCache[username]
        if (user != null) {
            println("User found:")
            displayUserInfo(user)
        } else {
            println("User '$username' not found in cache")
        }
    }

    fun searchRepoByName(repoName: String) {
        val matchingRepos = usersCache.flatMap { (username, user) ->
            user.repositories.filter { repo ->
                repo.name.contains(repoName, ignoreCase = true)
            }.map { repo -> username to repo }
        }

        if (matchingRepos.isEmpty()) {
            println("No repositories found matching '$repoName'")
        } else {
            println("Found ${matchingRepos.size} repositories:")
            matchingRepos.forEach { (username, repo) ->
                println("\nRepository: ${repo.name}")
                println("Owner: $username")
                println("Description: ${repo.description ?: "No description"}")
                println("Stars: ${repo.stars}")
                println("Forks: ${repo.forks}")
                println("URL: ${repo.url}")
            }
        }
    }

    fun displayUserInfo(userData: GitUser) {
        println("Username: ${userData.username}")
        println("Followers: ${userData.numOfFollowers}")
        println("Following: ${userData.numOfFollowing}")
        println("Account created: ${userData.accountCreationTime}")
        println("Repositories (${userData.repositories.size}):")
        userData.repositories.forEach { repo ->
            println("- ${repo.name} (★ ${repo.stars})")
        }
    }

    fun clearCache() {
        usersCache.clear()
        if (cacheFile.exists()) {
            cacheFile.delete()
        }
        println("Cache cleared successfully")
    }

    private fun saveCacheToFile() {
        try {
            cacheFile.parentFile?.mkdirs() // Ensure directory exists
            cacheFile.writeText(gson.toJson(usersCache))
        } catch (e: Exception) {
            println("Error saving cache: ${e.message}")
        }
    }

    private fun loadCacheFromFile() {
        try {
            if (cacheFile.exists()) {
                val json = cacheFile.readText()
                val type = object : TypeToken<Map<String, GitUser>>() {}.type
                val loadedCache: Map<String, GitUser> = gson.fromJson(json, type)
                usersCache.putAll(loadedCache)
                println("Loaded ${usersCache.size} users from cache file")
            }
        } catch (e: Exception) {
            println("Error loading cache: ${e.message}")
        }
    }
}