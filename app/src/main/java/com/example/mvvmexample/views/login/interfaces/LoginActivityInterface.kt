package com.example.mvvmexample.views.login.interfaces

interface LoginActivityInterface {
    fun navToHome()
    fun googleSignIn()
    fun emailPassSignIn(email:String,password:String)
    fun googleSignOut()
    fun emailPassSignOut()
    fun checkGoogleSignedIn(): Boolean
    fun checkEmailPasswordSignedIn():Boolean

}
