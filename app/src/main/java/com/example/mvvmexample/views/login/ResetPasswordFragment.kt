package com.example.mvvmexample.views.login

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mvvmexample.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_reset_password.*

class ResetPasswordFragment : Fragment(), View.OnClickListener {

    lateinit var email: EditText
    lateinit var btnReset: Button
    lateinit var tvBack:TextView
    var isEmailValid = false
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var rootView: View
    private var snack: Snackbar? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootView = requireActivity().window.decorView.rootView
        initView()
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun initView() {

        progressBar = requireActivity().findViewById(R.id.progressBar)
        progressBar.visibility = View.INVISIBLE
        email = requireActivity().findViewById(R.id.et_email)
        tvBack=requireActivity().findViewById(R.id.tv_back)

        btnReset = requireActivity().findViewById(R.id.btn_reset)
        btnReset.setOnClickListener(this)
        tvBack.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        if (view == btnReset) {
            setValidation()
        }else if (view==tvBack){
            try {
                navController().navigate(
                    ResetPasswordFragmentDirections.actionResetPasswordFragmentToLoginFragment()
                )
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
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

        if (isEmailValid) {
            resetPassword(et_email.text.toString())
        }

    }
    private fun navController() = findNavController()

  private  fun resetPassword(email: String) {
      progressBar.visibility = View.VISIBLE
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progressBar.visibility = View.GONE
                    snack = Snackbar.make(rootView, "Check email to reset your password!", Snackbar.LENGTH_LONG)
                    snack?.show()
                } else {
                    progressBar.visibility = View.GONE
                    snack = Snackbar.make(rootView, "Fail to send reset password email!", Snackbar.LENGTH_LONG)
                    snack?.show()
                }
            }
    }
}