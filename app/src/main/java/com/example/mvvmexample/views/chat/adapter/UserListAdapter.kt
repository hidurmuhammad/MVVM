package com.example.mvvmexample.views.chat.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.mvvmexample.R
import com.example.mvvmexample.databinding.UserListItemsBinding
import com.example.mvvmexample.model.userInfo.UserInfos
import com.example.mvvmexample.views.chat.interfaces.UserItemClick
import kotlinx.android.synthetic.main.user_list_items.view.*

class UserListAdapter(
    val context: Context?,
    val clickListener: UserItemClick
) : RecyclerView.Adapter<UserListAdapter.ViewHolder>() {

    var userList: List<UserInfos> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding: UserListItemsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.user_list_items, parent, false
        )
        return ViewHolder(viewBinding)
    }


    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
        if (userList.isNotEmpty()) {
            holder.itemView.profile_name.text = userList.get(position).name.toString()
            holder.itemView.profile_email.text = userList.get(position).email.toString()
            if(userList.get(position).photoUrl!=null) {
                holder.itemView.profile_image.load(userList.get(position).photoUrl) {
                    //crossfade(true)
                    placeholder(R.drawable.ic_baseline_account_circle_24)
                    //transformations(CircleCropTransformation())
                }
            }else{
                holder.itemView.profile_image.setImageResource(R.drawable.ic_baseline_account_circle_24)
            }
        }
    }

    fun setUsers(users: List<UserInfos>) {
        this.userList = users
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val viewBinding: UserListItemsBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun onBind(position: Int) {
            val row = userList[position]
            viewBinding.users = row
            viewBinding.usersClickInterface = clickListener
        }
    }

}