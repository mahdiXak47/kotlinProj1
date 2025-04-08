package org.example.model

import com.google.gson.annotations.SerializedName

data class GitUser(
    @SerializedName("login")
    val username: String? = null,

    @SerializedName("followers")
    val numOfFollowers: Int = 0,

    @SerializedName("following")
    val numOfFollowing: Int = 0,

    @SerializedName("created_at")
    val accountCreationTime: String? = null,

    var repositories: List<GitRepo> = listOf()

)