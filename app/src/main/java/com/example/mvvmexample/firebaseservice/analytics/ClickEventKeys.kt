package com.example.mvvmexample.firebaseservice.analytics

import android.view.View
import com.example.mvvmexample.R
import java.util.*

class ClickEventKeys {
    //Click Keys
    val HAMBURGER_MENU = "hamburger_menu_view"
    val PHOTO_ITEM_CLICK = "photo_item_click"

    //Click values
    val CURRENT_SCREEN = "current_screen"
    val PHOTO_ITEM_NAME="photo_item_name"

    //Widget ID
    fun getKey(sender: View): String {
         if (sender.id == R.id.nav_view) {
            return HAMBURGER_MENU
        }else if (sender.id == R.id.card) {
            return PHOTO_ITEM_CLICK
        }
        return ""
    }


    fun getParams(keyName: String): ArrayList<String> {
        val bundleParams = ArrayList<String>()
         if (keyName == HAMBURGER_MENU) {
            bundleParams.add(CURRENT_SCREEN)
        } else if (keyName == PHOTO_ITEM_CLICK) {
            bundleParams.add(CURRENT_SCREEN)
             bundleParams.add(PHOTO_ITEM_NAME)
        }
        return bundleParams
    }

}