package com.example.mvvmexample.views.chat.interfaces

import android.view.View
import com.example.mvvmexample.model.userInfo.UserInfos

interface UserItemClick {
    fun userItemClick(view: View, data: UserInfos)
}