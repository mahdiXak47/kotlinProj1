package org.example.api

import org.example.model.GitRepo
import org.example.model.GitUser
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Call

interface GitApi {
    @GET("users/{username}")
    fun getUserData(@Path("username") username: String): Call<GitUser>

    @GET("users/{username}/repos")
    fun getUserRepos(@Path("username") username: String): Call<List<GitRepo>>
}