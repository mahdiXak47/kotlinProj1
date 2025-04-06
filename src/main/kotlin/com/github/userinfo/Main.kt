package com.github.userinfo

import com.github.userinfo.model.GitHubUser
import com.github.userinfo.model.Repository
import com.github.userinfo.service.GitHubService
import com.github.userinfo.service.StorageService
import kotlinx.coroutines.runBlocking
import java.util.Scanner

fun main() = runBlocking {
    val scanner = Scanner(System.`in`)
    val gitHubService = GitHubService()
    val storageService = StorageService()

    while (true) {
        println("\nGitHub User Information System")
        println("1. Get user information using username")
        println("2. Show list of stored users")
        println("3. Search users by username")
        println("4. Search repositories by name")
        println("5. Clear cache")
        println("6. Exit")
        print("Enter your choice: ")

        when (scanner.nextInt()) {
            1 -> {
                print("Enter GitHub username: ")
                val username = scanner.next()
                val userData = gitHubService.getUserInfo(username)
                
                if (userData != null) {
                    val (user, repos) = userData
                    storageService.saveUser(user, repos)
                    displayUserInfo(user, repos)
                }
            }
            2 -> {
                val users = storageService.getAllUsers()
                if (users.isEmpty()) {
                    println("No users stored")
                } else {
                    users.forEach { (user, repos) ->
                        displayUserInfo(user, repos)
                    }
                }
            }
            3 -> {
                print("Enter search query: ")
                val query = scanner.next()
                val results = storageService.searchUsers(query)
                if (results.isEmpty()) {
                    println("No users found")
                } else {
                    results.forEach { (user, repos) ->
                        displayUserInfo(user, repos)
                    }
                }
            }
            4 -> {
                print("Enter repository name to search: ")
                val query = scanner.next()
                val results = storageService.searchRepositories(query)
                if (results.isEmpty()) {
                    println("No repositories found")
                } else {
                    results.forEach { (user, repo) ->
                        println("\nRepository: ${repo.name}")
                        println("Owner: ${user.login}")
                        println("Description: ${repo.description ?: "No description"}")
                        println("Stars: ${repo.stars}")
                        println("Forks: ${repo.forks}")
                        println("URL: ${repo.url}")
                    }
                }
            }
            5 -> {
                gitHubService.clearCache()
                println("Cache cleared successfully")
            }
            6 -> {
                println("Exiting...")
                return@runBlocking
            }
            else -> println("Invalid choice")
        }
    }
}

private fun displayUserInfo(user: GitHubUser, repos: List<Repository>) {
    println("\nUser Information:")
    println("Username: ${user.login}")
    println("Name: ${user.name ?: "Not provided"}")
    println("Bio: ${user.bio ?: "Not provided"}")
    println("Location: ${user.location ?: "Not provided"}")
    println("Email: ${user.email ?: "Not provided"}")
    println("Followers: ${user.followers}")
    println("Following: ${user.following}")
    println("Public Repositories: ${user.publicRepos}")
    println("Account Created: ${user.createdAt}")
    println("\nPublic Repositories:")
    repos.forEach { repo ->
        println("- ${repo.name}: ${repo.description ?: "No description"}")
        println("  Stars: ${repo.stars}, Forks: ${repo.forks}")
    }
} 