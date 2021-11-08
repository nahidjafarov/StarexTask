package com.example.starextask.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.starextask.R
import com.example.starextask.data.model.PostsModelItem
import com.example.starextask.util.show

class PostsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
    }

    private var postsList = ArrayList<PostsModelItem?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == VIEW_TYPE_ITEM) {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_posts, parent, false)
            PostViewHolder(view)
        } else {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_lazy_loading, parent, false)
            LoadingViewHolder(view)
        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PostViewHolder) {
            if (postsList[position] != null) {
                holder.bindItems(postsList[position]!!)
            }
        } else if (holder is LoadingViewHolder) {
            holder.showLoadingView()
        }
    }

    override fun getItemCount(): Int {
        return postsList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (postsList[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newPostsList: ArrayList<PostsModelItem?>?) {
        if (newPostsList != null) {
            if (postsList.isNotEmpty())
                postsList.removeAt(postsList.size - 1)
            postsList.clear()
            postsList.addAll(newPostsList)
        } else {
            postsList.add(newPostsList)
        }
        notifyDataSetChanged()
    }

    fun getData() = postsList

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val postTitle: TextView = itemView.findViewById(R.id.post_title)
        private val postBody: TextView = itemView.findViewById(R.id.post_body)

        @SuppressLint("SetTextI18n")
        fun bindItems(post: PostsModelItem) {
            postTitle.text = post.title
            postBody.text = post.body
        }

    }

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)

        fun showLoadingView() {
            progressBar.show()
        }
    }

}