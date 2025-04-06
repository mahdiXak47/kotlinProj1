package com.github.userinfo.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.userinfo.model.GitHubUser
import com.github.userinfo.model.Repository
import java.io.File

class StorageService {
    private val objectMapper = ObjectMapper().registerModule(JavaTimeModule())
    private val storageFile = File("github_users.json")
    private val users = mutableMapOf<String, Pair<GitHubUser, List<Repository>>>()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        if (storageFile.exists()) {
            try {
                val json = storageFile.readText()
                val loadedUsers = objectMapper.readValue<Map<String, Pair<GitHubUser, List<Repository>>>>(json)
                users.putAll(loadedUsers)
            } catch (e: Exception) {
                println("Error loading users: ${e.message}")
            }
        }
    }

    fun saveUser(user: GitHubUser, repos: List<Repository>) {
        users[user.login] = user to repos
        saveToFile()
    }

    fun getUser(username: String): Pair<GitHubUser, List<Repository>>? = users[username]

    fun getAllUsers(): List<Pair<GitHubUser, List<Repository>>> = users.values.toList()

    fun searchUsers(query: String): List<Pair<GitHubUser, List<Repository>>> =
        users.values.filter { (user, _) ->
            user.login.contains(query, ignoreCase = true) ||
            user.name?.contains(query, ignoreCase = true) == true
        }

    fun searchRepositories(query: String): List<Pair<GitHubUser, Repository>> =
        users.values.flatMap { (user, repos) ->
            repos
                .filter { it.name.contains(query, ignoreCase = true) }
                .map { user to it }
        }

    private fun saveToFile() {
        try {
            val json = objectMapper.writeValueAsString(users)
            storageFile.writeText(json)
        } catch (e: Exception) {
            println("Error saving users: ${e.message}")
        }
    }
} 