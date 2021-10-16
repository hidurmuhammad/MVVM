package com.example.mvvmexample.firebaseservice.analytics

import com.example.mvvmexample.views.login.LoginActivity
import com.example.mvvmexample.views.photo.PhotoDetailsFragment
import com.example.mvvmexample.views.photo.PhotoListFragment
import com.example.mvvmexample.views.post.CreatePostFragment
import com.example.mvvmexample.views.post.PostListFragment
import com.example.mvvmexample.views.todos.TodosListFragment
import java.util.*

class ViewTrackerKeys {

    //View Keys
    val PHOTO_FRAGMENT_VIEW_PARAM = "photo_page_view"
    val PHOTO_DETAILS_FRAGMENT_VIEW_PARAM = "photo_details_page_view"

    val POST_FRAGMENT_VIEW_PARAM = "post_page_view"
    val TODO_FRAGMENT_VIEW_PARAM = "todo_page_view"
    val CREATE_POST_FRAGMENT_VIEW_PARAM = "create_post_page_view"
    val LOGIN_SUCCESS="login_success"

    //View values
    val USER_ENGAGEMENT = "user_engagement"
    val PREVIOUS_SCREEN = "previous_screen"
    val PHOTO_NAME = "photo_name"
    val USER_NAME="user_name"
    val EMAIL="email"

    // Helper class for viewTracker,Classname of the Fragment/Activity is passed ,response is the associated firebase parameter key

    fun getKey(className: String): String {
        if (className == PhotoListFragment().TAG) return PHOTO_FRAGMENT_VIEW_PARAM
        if (className == PhotoDetailsFragment().TAG) return PHOTO_DETAILS_FRAGMENT_VIEW_PARAM
        if (className == PostListFragment().TAG) return POST_FRAGMENT_VIEW_PARAM
        if (className == TodosListFragment().TAG) return TODO_FRAGMENT_VIEW_PARAM
        if (className == CreatePostFragment().TAG) return CREATE_POST_FRAGMENT_VIEW_PARAM
        if (className == LoginActivity().TAG) return LOGIN_SUCCESS

        return ""
    }

    //Parameter Keys
    fun getParams(keyName: String): ArrayList<String> {
        val bundleParams = ArrayList<String>()
        if (keyName == PHOTO_FRAGMENT_VIEW_PARAM) {
            bundleParams.add(USER_ENGAGEMENT)
        }
        if (keyName == PHOTO_DETAILS_FRAGMENT_VIEW_PARAM) {
            bundleParams.add(PREVIOUS_SCREEN)
            bundleParams.add(PHOTO_NAME)
        }
        if (keyName == TODO_FRAGMENT_VIEW_PARAM) {
            bundleParams.add(PREVIOUS_SCREEN)
            bundleParams.add(USER_ENGAGEMENT)

        }
        if (keyName == POST_FRAGMENT_VIEW_PARAM) {
            bundleParams.add(PREVIOUS_SCREEN)
            bundleParams.add(USER_ENGAGEMENT)
        }
        if (keyName == CREATE_POST_FRAGMENT_VIEW_PARAM) {
            bundleParams.add(PREVIOUS_SCREEN)
        }
        if (keyName == LOGIN_SUCCESS) {
            bundleParams.add(USER_NAME)
            bundleParams.add(EMAIL)
        }
        return bundleParams
    }

}