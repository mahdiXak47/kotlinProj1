package com.github.userinfo.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class GitHubUser(
    val login: String,
    val followers: Int,
    val following: Int,
    @JsonProperty("created_at")
    val createdAt: LocalDateTime,
    @JsonProperty("public_repos")
    val publicRepos: Int,
    val name: String?,
    val bio: String?,
    val location: String?,
    val email: String?
)

data class Repository(
    val name: String,
    val description: String?,
    @JsonProperty("html_url")
    val url: String,
    @JsonProperty("stargazers_count")
    val stars: Int,
    @JsonProperty("forks_count")
    val forks: Int,
    @JsonProperty("updated_at")
    val updatedAt: LocalDateTime
) 