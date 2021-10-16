package com.example.mvvmexample.views.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mvvmexample.R
import com.example.mvvmexample.model.userInfo.UserInfos
import com.example.mvvmexample.utils.Constants
import com.example.mvvmexample.utils.SessionManagement
import com.example.mvvmexample.utils.TAG
import com.example.mvvmexample.views.login.interfaces.LoginActivityInterface
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*


class SignUpFragment : Fragment(), View.OnClickListener {

    lateinit var loginActivityInterface: LoginActivityInterface
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var rootView: View
    private var snack: Snackbar? = null
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var userName: EditText
    lateinit var confirmPassword: EditText
    lateinit var btnSignUp: Button
    var isUserName = false
    var isEmailValid = false
    var isPasswordValid: Boolean = false
    var isConfirmPasswordValid: Boolean = false
    private lateinit var progressBar: ProgressBar
    private lateinit var db: FirebaseDatabase
    private lateinit var dbRef: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootView = requireActivity().window.decorView.rootView
        initView()
        firebaseAuth = FirebaseAuth.getInstance()
        // Initialize Realtime Database
        db = FirebaseDatabase.getInstance()
        dbRef = db.getReference(USERS_CHILD)

    }

    private fun initView() {
        progressBar = requireActivity().findViewById(R.id.progressBar)
        progressBar.visibility = View.INVISIBLE
        userName = requireActivity().findViewById(R.id.et_user_name)
        email = requireActivity().findViewById(R.id.et_email)
        password = requireActivity().findViewById(R.id.et_password)
        confirmPassword = requireActivity().findViewById(R.id.et_confirm_password)

        btnSignUp = requireActivity().findViewById(R.id.btn_signup)
        btnSignUp.setOnClickListener(this)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivityInterface = context as LoginActivityInterface
    }

    private fun createFbAccount(email: String, password: String) {
        progressBar.visibility = View.VISIBLE
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    progressBar.visibility = View.GONE
                    Log.e(TAG, "createAccount: Success!")

                    // update UI with the signed-in user's information
                    val user = firebaseAuth.currentUser
                     addUserInfoToRealTimeDb(user)
                       try {
                           navController().navigate(
                               SignUpFragmentDirections.actionSignUpFragmentToLoginFragment()
                           )
                       } catch (e: java.lang.Exception) {
                           e.printStackTrace()
                       }
                } else {
                    progressBar.visibility = View.GONE
                    snack = Snackbar.make(rootView, "Authentication Fail!", Snackbar.LENGTH_LONG)
                    snack?.show()
                    Log.e(TAG, "createAccount: Fail!", task.exception)
                }
            }
    }

    private fun navController() = findNavController()

    override fun onClick(view: View?) {
        if (view == btnSignUp) {
            setValidation()
        }
    }

    private fun setValidation() {

        if (userName.text.toString().isEmpty()) {
            userName.error = resources.getString(R.string.name_error)
            isUserName = false
        } else {
            isUserName = true
        }
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
        } else if (confirmPassword.text.toString().isEmpty()) {
            confirmPassword.error = resources.getString(R.string.password_error)
            isConfirmPasswordValid = false
        } else if (confirmPassword.text.length < 6) {
            confirmPassword.error = resources.getString(R.string.error_invalid_password)
            isConfirmPasswordValid = false
        } else if (!((password.text.toString()).equals(confirmPassword.text.toString()))) {
            password.error = resources.getString(R.string.password_match)
            confirmPassword.error = resources.getString(R.string.password_match)
            isPasswordValid = false
            isConfirmPasswordValid = false
        } else {
            isPasswordValid = true
            isConfirmPasswordValid = true
        }

        if (isUserName && isEmailValid && isPasswordValid && isConfirmPasswordValid) {
            val sharedPreference = SessionManagement(requireActivity())
            sharedPreference.save(Constants.NAME, userName.text.toString())
            createFbAccount(email.text.toString(), password.text.toString())

        }

    }

    private fun addUserInfoToRealTimeDb(user: FirebaseUser?) {
        val dbValue = UserInfos(
            userName.text.toString(),
            user!!.email,
            null, user.uid)
        db.reference.child(USERS_CHILD).push().setValue(dbValue,
            DatabaseReference.CompletionListener { databaseError, databaseReference ->
                if (databaseError != null) {
                    Log.d(
                        TAG, "Unable to write message to database.",
                        databaseError.toException()
                    )
                    return@CompletionListener
                }
            })

    }

    companion object {
        const val USERS_CHILD = "users"
    }

}