package com.example.mvvmexample.views.post.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmexample.R
import com.example.mvvmexample.model.posts.CreatePost
import kotlinx.android.synthetic.main.create_post_items.view.*

class CreatePostAdapter  (var listener:PostListener) : RecyclerView.Adapter<CreatePostAdapter.HomeViewHolder>() {

    private var data: ArrayList<CreatePost>? = null

    interface PostListener {
        fun onItemDeleted(postModel: CreatePost, position: Int)
    }

    fun setData(list: ArrayList<CreatePost>) {
        data = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.create_post_items, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        if (data?.size!! >0) {
            val item = data?.get(position)
            holder.bindView(item)
            holder.itemView.img_delete.setOnClickListener {
                item?.let { it1 ->
                    listener.onItemDeleted(it1, position)
                }
            }
        }
    }

    fun addData(postModel: CreatePost) {
        Log.d("tsvv",postModel.body.toString());
        data?.add(0, postModel)
        notifyItemInserted(0)
    }

    fun removeData(position: Int) {
        data?.removeAt(position)
        notifyDataSetChanged()
    }

    class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(item: CreatePost?) {
            itemView.tv_item_title.text = item?.title
            itemView.tv_item_body.text = item?.body
        }

    }
}