package com.example.starextask.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.starextask.data.model.PostsModel
import com.example.starextask.data.model.PostsModelItem
import com.example.starextask.data.repository.HomeRepository
import com.example.starextask.util.ApiException
import com.example.starextask.util.NoInternetException
import com.example.starextask.util.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val repository: HomeRepository
) : ViewModel() {

    private var clearPage = 0
    private var postsList = ArrayList<PostsModelItem?>()

    private val _postsLiveData = MutableLiveData<State<ArrayList<PostsModelItem?>>>()
    val postsLiveData: LiveData<State<ArrayList<PostsModelItem?>>>
        get() = _postsLiveData

    private val _idQueryLiveData = MutableLiveData<String>()
    val idQueryLiveData: LiveData<String>
        get() = _idQueryLiveData

    private lateinit var postsResponse: PostsModel

    init {
        _idQueryLiveData.value = ""
    }

    fun getPosts() {
        if (clearPage == 1) {
            postsList.clear()
            _postsLiveData.postValue(State.loading())
        } else {
            if (postsList.isNotEmpty() && postsList.last() == null)
                postsList.removeAt(postsList.size - 1)
        }
        viewModelScope.launch(Dispatchers.IO) {
            if (_idQueryLiveData.value != null && _idQueryLiveData.value!!.isNotEmpty()) {
                try {
                    postsResponse = repository.getPosts(
                        _idQueryLiveData.value,
                    )
                    withContext(Dispatchers.Main) {
                        postsList.addAll(postsResponse)
                        _postsLiveData.postValue(State.success(postsList))
                    }
                } catch (e: ApiException) {
                    withContext(Dispatchers.Main) {
                        _postsLiveData.postValue(State.error(e.message!!))
                    }
                } catch (e: NoInternetException) {
                    withContext(Dispatchers.Main) {
                        _postsLiveData.postValue(State.error(e.message!!))
                    }
                }
            }else {
                try {
                    postsResponse = repository.getAllPosts()
                    withContext(Dispatchers.Main) {
                        postsList.addAll(postsResponse)
                        _postsLiveData.postValue(State.success(postsList))
                    }
                } catch (e: ApiException) {
                    withContext(Dispatchers.Main) {
                        _postsLiveData.postValue(State.error(e.message!!))
                    }
                } catch (e: NoInternetException) {
                    withContext(Dispatchers.Main) {
                        _postsLiveData.postValue(State.error(e.message!!))
                    }
                }
            }

        }
    }

    fun searchPost(id: String?) {
        _idQueryLiveData.value = id
        clearPage = 1
        getPosts()
    }

}