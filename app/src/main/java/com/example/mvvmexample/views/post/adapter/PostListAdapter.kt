package com.example.mvvmexample.views.post.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmexample.R
import com.example.mvvmexample.databinding.PostListItemsBinding
import com.example.mvvmexample.model.posts.PostsData

class PostListAdapter(val context: Context?,

) : RecyclerView.Adapter<PostListAdapter.ViewHolder>(){

    var postList: List<PostsData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding: PostListItemsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.post_list_items, parent, false
        )
        return ViewHolder(viewBinding)
    }


    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)

    }

    fun setPosts(posts: List<PostsData>) {
        this.postList = posts
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val viewBinding: PostListItemsBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun onBind(position: Int) {
            val row = postList[position]
            viewBinding.posts = row
           // viewBinding.cl = clickListener
        }
    }

}