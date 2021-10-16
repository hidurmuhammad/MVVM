package com.example.mvvmexample.views.splash

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.mvvmexample.R
import com.example.mvvmexample.views.login.interfaces.LoginActivityInterface


class SplashFragment : Fragment() {
    private lateinit var activityContext: Context
    private lateinit var loginActivityInterface: LoginActivityInterface

    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 1500 //1.5 seconds
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activityContext = context
        loginActivityInterface = context as LoginActivityInterface
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDelayHandler = Handler()
        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)
    }
    private val mRunnable: Runnable = Runnable {
        if(checkSignedIn()||checkEmailPasswordSignedIn()){
            loginActivityInterface.navToHome()
        }else{
            findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
        }
        //(activity as AppCompatActivity).supportActionBar?.show()
    }
    private fun checkSignedIn(): Boolean {
        return loginActivityInterface.checkGoogleSignedIn()
    }
    private fun checkEmailPasswordSignedIn(): Boolean {
        return loginActivityInterface.checkEmailPasswordSignedIn()
    }

}