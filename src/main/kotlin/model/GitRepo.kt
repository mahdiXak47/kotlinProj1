package org.example.model

import com.google.gson.annotations.SerializedName

data class GitRepo(
    val name: String,
    val description: String?,
    @SerializedName("stargazers_count") val stars: Int,
    @SerializedName("forks_count") val forks: Int,
    @SerializedName("html_url") val url: String
)
