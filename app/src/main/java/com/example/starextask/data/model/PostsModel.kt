package com.example.starextask.data.model

class PostsModel : ArrayList<PostsModelItem>()

data class PostsModelItem(
    val body: String,
    val id: Int,
    val title: String,
    val userId: Int
)