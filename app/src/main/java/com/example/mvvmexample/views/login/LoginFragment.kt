package com.example.mvvmexample.views.login

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mvvmexample.R
import com.example.mvvmexample.views.login.interfaces.LoginActivityInterface
import com.example.mvvmexample.views.photo.PhotoListFragmentDirections
import com.google.android.gms.common.SignInButton


class LoginFragment : Fragment(), View.OnClickListener {
    lateinit var bSignIn: SignInButton
    lateinit var loginActivityInterface: LoginActivityInterface
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var btnLogin: Button
    lateinit var signUp: TextView
    lateinit var forgetPassword: TextView
    var isEmailValid = false
    var isPasswordValid: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

    }

    private fun initViews() {
        bSignIn = requireActivity().findViewById(R.id.btn_sign_in)
        bSignIn.setOnClickListener(this)
        email = requireActivity().findViewById(R.id.email)
        password = requireActivity().findViewById(R.id.password)
        btnLogin = requireActivity().findViewById(R.id.btn_login)
        signUp = requireActivity().findViewById(R.id.login_signup)
        forgetPassword=requireActivity().findViewById(R.id.tv_forget_password)

        btnLogin.setOnClickListener(this)
        signUp.setOnClickListener(this)
        forgetPassword.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        if (view == bSignIn) {
            loginActivityInterface.googleSignIn()
        } else if (view == btnLogin) {
            setValidation()
        }else if (view==signUp){
            try {
                navController().navigate(
                    LoginFragmentDirections.actionLoginFragmentToSignUpFragment()
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else if (view==forgetPassword){
            try {
                navController().navigate(
                    LoginFragmentDirections.actionLoginFragmentToResetPasswordFragment()
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun navController() = findNavController()
    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivityInterface = context as LoginActivityInterface
    }

    private fun setValidation() {
        // Check for a valid email address.
        if (email.text.toString().isEmpty()) {
            email.error = resources.getString(R.string.email_error)
            isEmailValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
            email.error = resources.getString(R.string.error_invalid_email)
            isEmailValid = false
        } else {
            isEmailValid = true
        }

        // Check for a valid password.
        if (password.text.toString().isEmpty()) {
            password.error = resources.getString(R.string.password_error)
            isPasswordValid = false
        } else if (password.text.length < 6) {
            password.error = resources.getString(R.string.error_invalid_password)
            isPasswordValid = false
        } else {
            isPasswordValid = true
        }

        if (isEmailValid && isPasswordValid) {
            loginActivityInterface.emailPassSignIn(email.text.toString(), password.text.toString())
        }

    }


}