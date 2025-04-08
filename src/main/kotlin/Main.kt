package org.example

import org.example.api.GitApi
import org.example.service.GitService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

fun main() {
    val scanner = Scanner(System.`in`)
    val baseUrl = "https://api.github.com/"
    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val api = retrofit.create(GitApi::class.java)
    val service = GitService(api)


    while (true) {
        println("\nGitHub User Information System")
        println("1. Get user information using username")
        println("2. Show list of stored users")
        println("3. Search users by username")
        println("4. Search repositories by name")
        println("5. Exit")
        println("6. Clear application cache")
        print("Enter your choice: ")

        when (scanner.nextInt()) {
            1 -> {
                print("Enter GitHub username: ")
                val username = scanner.next()
                val userData = service.getUserInfo(username)
                if (userData != null) {
                    service.displayUserInfo(userData)
                } else {
                    println("failed to fetch the data!")
                }
            }

            2 -> {
                service.displayUsers()
            }

            3 -> {
                print("Enter search query: ")
                val query = scanner.next()
                service.searchUserByUsername(query)
            }

            4 -> {
                print("Enter repository name to search: ")
                val query = scanner.next()
                service.searchRepoByName(query)
            }

            5 -> {
                println("Exiting...")
                break
            }

            6 -> {
                print("Are you sure you want to clear the cache? (y/n): ")
                if (scanner.next().equals("y", ignoreCase = true)) {
                    service.clearCache()
                } else {
                    println("Cache clearing cancelled")
                }
            }

            else -> println("Invalid choice")
        }
        println()
    }
}