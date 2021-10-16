package com.example.mvvmexample.firebaseservice.analytics

import android.os.Bundle
import android.util.Log
import com.example.mvvmexample.app.MVVMExampleApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import java.util.*

class ViewTracker {
    val TAG = ViewTracker::class.java.simpleName

    //Bundle to post to firebase
    private var bundle: Bundle? = null

    //Bundle parameters list
    private var params = ArrayList<String>()

    //Tracking start time in a view.Setter called in constructor
    private var startTime: Long? = null

    //Total engaged time in a view.Set in stopTracking method
    private var engagedTime: Long? = null

    //Previous Screen of view in consideration
    private var previousScreen = ""

    //Class name of View(Fragment or Activity)
    private var className: String? = null

    //Bundle key for posting to firebase
    private var key = ""

    //Helper class for setting params based on keys
    private val trackerKeys: ViewTrackerKeys = ViewTrackerKeys()

    private var photoName=""

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var userName=""

    private var email=""

    constructor(className:String?)  {
        bundle = Bundle()
        this.className = className
        startTracking()
        setKeys()
    }


    private fun startTracking() {
        startTime = System.currentTimeMillis()
    }

    private fun setKeys() {
        key = trackerKeys.getKey(className!!)
        params = trackerKeys.getParams(key)
    }
    fun stopTracking() {
        val endTime: Long
        endTime = System.currentTimeMillis()
        engagedTime = endTime - startTime!!
        postToAnalytics()
        Log.d(TAG, "stopTracking: $engagedTime")
    }

    fun setPreviousScreen(previousScreen: String?) {
        this.previousScreen = previousScreen!!
    }

    fun setPhotoName(photoName: String?) {
        this.photoName = photoName!!
    }

    fun setUserName(userName: String?) {
        this.userName = userName!!
    }
    fun setEmail(email: String?) {
        this.email = email!!
    }


    /**
     * To populate bundle based on Params in params list and post to analytics
     */
    private fun postToAnalytics() {
        if (params.contains(ViewTrackerKeys().USER_ENGAGEMENT)) {
            bundle!!.putString(ViewTrackerKeys().USER_ENGAGEMENT, engagedTime.toString())
        }

        if (params.contains(ViewTrackerKeys().PREVIOUS_SCREEN)) {
            bundle!!.putString(ViewTrackerKeys().PREVIOUS_SCREEN, previousScreen)
        }
        if (params.contains(ViewTrackerKeys().PHOTO_NAME)) {
            bundle!!.putString(ViewTrackerKeys().PHOTO_NAME, photoName)
        }
        if (params.contains(ViewTrackerKeys().USER_NAME)) {
            bundle!!.putString(ViewTrackerKeys().USER_NAME, userName)
        }
        if (params.contains(ViewTrackerKeys().EMAIL)) {
            bundle!!.putString(ViewTrackerKeys().EMAIL, email)
        }


        if (key != "") {
            // Obtain the FirebaseAnalytics instance.
            firebaseAnalytics = Firebase.analytics
            firebaseAnalytics.logEvent(key, bundle)
            Log.d(TAG, "postToAnalytics: $bundle")
        }
    }
}

