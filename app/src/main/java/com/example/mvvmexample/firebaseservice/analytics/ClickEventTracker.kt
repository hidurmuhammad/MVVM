package com.example.mvvmexample.firebaseservice.analytics

import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.mvvmexample.app.MVVMExampleApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import java.util.*

class ClickEventTracker {
    var params = ArrayList<String>()
    var bundle: Bundle? = null
    var key = ""
    var view: View? = null
    var clickKeys = ClickEventKeys()
    var currentScreen = ""
    var previousScreen = ""
    var photoNames = ""
    private lateinit var firebaseAnalytics: FirebaseAnalytics

     constructor(view: View?) {
        this.bundle = Bundle()
        this.view = view!!
    }

    private fun postToAnalytics() {
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(key, bundle)
    }

    fun setScreenValues(screenName: String) {
        this.currentScreen = screenName
        getValues()
    }
    fun setPreviousScreenValues(screenName: String) {
        this.previousScreen = screenName
        getValues()
    }

    fun setPhotoName(photoName: String) {
        this.photoNames = photoName
        getValues()
    }

    private fun getValues() {
        key = clickKeys.getKey(view!!)
        params = clickKeys.getParams(key)
        if (!key.equals("")) {
            if (key == ClickEventKeys().HAMBURGER_MENU) {
               bundle!!.putString(ClickEventKeys().CURRENT_SCREEN, currentScreen)
           }else if (key == ClickEventKeys().PHOTO_ITEM_CLICK) {
                bundle!!.putString(ClickEventKeys().CURRENT_SCREEN, currentScreen)
                bundle!!.putString(ClickEventKeys().PHOTO_ITEM_NAME, photoNames)

            }
        }
                if (bundle != null) {
            Log.d("ClickTracker", "postToAnalytics: Posted$bundle")
            postToAnalytics()
        }
    }

}