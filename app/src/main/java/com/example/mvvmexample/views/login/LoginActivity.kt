package com.example.mvvmexample.views.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mvvmexample.R
import com.example.mvvmexample.firebaseservice.analytics.ViewTracker
import com.example.mvvmexample.model.userInfo.UserInfos
import com.example.mvvmexample.utils.Constants
import com.example.mvvmexample.utils.SessionManagement
import com.example.mvvmexample.utils.TAG
import com.example.mvvmexample.views.MainActivity
import com.example.mvvmexample.views.login.interfaces.LoginActivityInterface
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class LoginActivity : AppCompatActivity(), LoginActivityInterface {
    val TAG: String = "login_success"
    private lateinit var rootView: View
    private var snack: Snackbar? = null
    private lateinit var progressBar: ProgressBar
    // private val RC_GET_AUTH_CODE = 1
    // private var mGoogleApiClient: GoogleApiClient? = null

    lateinit var mGoogleSignInClient: GoogleSignInClient
    val Req_Code: Int = 123
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    //For Screen tracking
    var viewTracker: ViewTracker? = null

    private lateinit var db: FirebaseDatabase
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.INVISIBLE

        rootView = window.decorView.rootView

        FirebaseApp.initializeApp(this)


        validateServerClientID()
        val serverClientId = getString(R.string.client_id)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestServerAuthCode(serverClientId)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize Realtime Database
        db = FirebaseDatabase.getInstance()
        dbRef = db.getReference(USERS_CHILD)
    }

    override fun navToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        //overridePendingTransition(androidx.work.R.anim.fade_in, androidx.work.R.anim.fade_out)
        finish()
    }

    private fun validateServerClientID() {
        val serverClientId = getString(R.string.client_id)
        val suffix = ".apps.googleusercontent.com"
        if (!serverClientId.trim().endsWith(suffix)) {
            val message = "Invalid server client ID in strings.xml, must end with $suffix"
            Log.w(TAG, message)
            snack = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
            snack!!.show()
        }
    }

    override fun googleSignIn() {
        progressBar.visibility = View.VISIBLE
        signInGoogle()
    }

    override fun emailPassSignIn(email: String, password: String) {
        progressBar.visibility = View.VISIBLE
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    progressBar.visibility = View.GONE
                    Log.e(TAG, "signIn: Success!")
                    // update UI with the signed-in user's information
                    val user: FirebaseUser = firebaseAuth.currentUser!!
                    val sharedPreference = SessionManagement(this)
                    sharedPreference.save(Constants.EMAIL, user.email!!.toString())
                    //  sharedPreference.save(Constants.NAME, user.displayName!!.toString())
                    //Post To Analytics
                    viewTracker!!.setEmail(user.email!!)
                    // viewTracker!!.setUserName(user.displayName!!)
                    navToHome()
                } else {
                    Log.e(TAG, "signIn: Fail!", task.exception)
                    progressBar.visibility = View.GONE
                    snack = Snackbar.make(rootView, "signIn Fail!", Snackbar.LENGTH_LONG)
                    snack?.show()
                    Log.e(TAG, "signIn: Fail!", task.exception)
                    emailPassSignOut()
                }
                if (!task.isSuccessful) {
                    progressBar.visibility = View.GONE
                    snack = Snackbar.make(rootView, "Authentication Fail!", Snackbar.LENGTH_LONG)
                    snack?.show()
                    Log.e(TAG, "createAccount: Fail!", task.exception)
                    emailPassSignOut()
                }
            }
    }

    override fun googleSignOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun emailPassSignOut() {
        firebaseAuth.signOut()
    }

    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, Req_Code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Req_Code) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
//            firebaseAuthWithGoogle(account!!)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                UpdateUI(account)
            }
        } catch (e: ApiException) {
            progressBar.visibility = View.GONE
            Log.d("error_for_login", completedTask.exception.toString())
            snack = Snackbar.make(
                rootView,
                "Connection error!" + completedTask.exception.toString(),
                Snackbar.LENGTH_LONG
            )
            snack?.show()

        }
    }

    override fun onStart() {
        super.onStart()

        if (viewTracker == null) {
            viewTracker = ViewTracker(TAG)
        }
    }

    override fun onStop() {
        super.onStop()
        // firebaseAuth.removeAuthStateListener(this.authStateListener)
        viewTracker!!.stopTracking()
    }

    private fun UpdateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                progressBar.visibility = View.GONE
                Log.d("success_login", "test")
                val sharedPreference = SessionManagement(this)
                sharedPreference.save(Constants.EMAIL, account.email!!.toString())
                sharedPreference.save(Constants.NAME, account.displayName!!.toString())

                val user = firebaseAuth.currentUser
                addUserInfoToRealTimeDb(user)


                //Post To Analytics
                viewTracker!!.setEmail(account.email!!)
                viewTracker!!.setUserName(account.displayName!!)

                navToHome()

                //To send Email verification to email address
                /*  sendVerificationEmail()
                  //To check email is verified successfully
                authStateListener= FirebaseAuth.AuthStateListener { firebaseAuth ->
                      val firebaseUser = firebaseAuth.currentUser
                      if (firebaseUser != null) {
                          Log.e(TAG, if (firebaseUser.isEmailVerified) "User is signed in and email is verified" else "Email is not verified")
                          navToHome()
                      }else{
                          Log.e(TAG, "onAuthStateChanged:signed_out");
                          signOut()
                      }
                  }*/


            } else {
                progressBar.visibility = View.GONE
                googleSignOut()
            }
        }
    }

    private fun addUserInfoToRealTimeDb(user: FirebaseUser?) {
        val dbValue = UserInfos(
            user!!.displayName,
            user.email,
            user.photoUrl.toString(), user.uid)
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

    private fun sendVerificationEmail() {
        val user: FirebaseUser = firebaseAuth.currentUser!!
        user.sendEmailVerification().addOnCompleteListener { task ->

            // Re-enable button
            // findViewById<View>(R.id.verify_email_button).isEnabled = true
            if (task.isSuccessful) {
                Toast.makeText(
                    this,
                    "Verification email sent to " + user.email,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Log.e(TAG, "sendEmailVerification", task.exception)
                Toast.makeText(
                    this,
                    "Failed to send verification email.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    override fun checkGoogleSignedIn(): Boolean {
        return GoogleSignIn.getLastSignedInAccount(this) != null
    }

    override fun checkEmailPasswordSignedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
    companion object {
        const val USERS_CHILD = "users"
    }

    /*  fun revokeAccess(){
          Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
              ResultCallback<Status>() { result->

                  Log.d(TAG, "signOut:onResult:" +result.status.toString())
              })
      }
      fun signOut(){
          try {
              Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                  ResultCallback<Status>() { result ->

                      Log.d(TAG, "signOut:onResult:" + result.status.toString())
                  })
          }catch (e:Exception){
              e.printStackTrace()
          }
      }*/
    /*fun getAuthCode() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_GET_AUTH_CODE)
    }*/

    /* private fun validateServerClientID() {
         val serverClientId = getString(R.string.client_id)
         val suffix = ".apps.googleusercontent.com"
         if (!serverClientId.trim().endsWith(suffix)) {
             val message = "Invalid server client ID in strings.xml, must end with $suffix"
             Log.w(TAG, message)
             snack = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
             snack!!.show()
         }
     }*/

    /*  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
          super.onActivityResult(requestCode, resultCode, data)
          if (requestCode == RC_GET_AUTH_CODE) {
              val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
              Log.d(TAG, "onActivityResult:GET_AUTH_CODE:success:" + result!!.signInAccount)
              if (result.isSuccess) {
                  // [START get_auth_code]
                  val acct = result.signInAccount
                  val authCode = acct!!.serverAuthCode!!

                  val httpClient = OkHttpClient()
                  val requestBody = FormBody.Builder()
                      .add("grant_type", "authorization_code")
                      .add("client_id", getString(R.string.client_id))
                      .add("client_secret", "TIWtaxBa-U18rjZX6VOdq-8J")
                      .add("redirect_uri", "")
                      .add("code", authCode)
                      .build()
                  val request = Request.Builder()
                      .url("https://www.googleapis.com/oauth2/v4/token")
                      .post(requestBody)
                      .build()
                  try {
                      Thread {

                          val response = httpClient.newCall(request).execute()
                          try {
                              val sharedPreference = SessionManagement(this)
                              if (!response.isSuccessful) {

                                  progressBar.visibility = View.INVISIBLE
                                  val jsonObject = JSONObject(response.body!!.string())
                                  val message = jsonObject.toString()
                                  Log.i("result", message)

                              } else {
                                  progressBar.visibility = View.INVISIBLE
                                  val jsonObject = JSONObject(response.body!!.string())
                                   val accessToken=jsonObject.get("access_token").toString()
                                   val expires_in=jsonObject.get("expires_in").toString()
                                   val scope=jsonObject.get("scope").toString()
                                   val token_type=jsonObject.get("token_type").toString()
                                  val id_token = jsonObject.get("id_token").toString()

                                  Log.i("result", id_token)
                                  //To store token id
                                  sharedPreference.save(Constants.TOKEN_ID, id_token)
                                  navToHome()
                              }
                          } catch (e: JSONException) {
                              progressBar.visibility = View.INVISIBLE
                              e.printStackTrace()
                          }
                      }.start()

                  } catch (e: java.lang.Exception) {
                      progressBar.visibility = View.INVISIBLE
                      e.printStackTrace()
                  }
              } else {
                  progressBar.visibility = View.INVISIBLE
                  // Show signed-out UI.
                  Log.d(TAG, "SignOut")
                 // revokeAccess()
                  signOut()
              }
          }
      }*/

    /* override fun onPause() {
         super.onPause()
         if(mGoogleApiClient!=null) {
             mGoogleApiClient!!.stopAutoManage(this)
             mGoogleApiClient!!.disconnect()
         }
     }*/


}