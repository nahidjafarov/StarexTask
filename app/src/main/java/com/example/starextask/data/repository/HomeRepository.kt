package com.example.starextask.data.repository

import com.example.starextask.data.model.PostsModel
import com.example.starextask.data.network.ApiInterface
import com.example.starextask.data.network.SafeApiRequest

class HomeRepository(
    private val api: ApiInterface
) : SafeApiRequest() {

    suspend fun getPosts(
        id: String?
    ): PostsModel {
        return apiRequest { api.getPostsResultData(id) }
    }

    suspend fun getAllPosts(): PostsModel {
        return apiRequest { api.getAllPostsResultData() }
    }


}