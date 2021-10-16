package com.example.mvvmexample.views

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.mvvmexample.R
import com.example.mvvmexample.utils.TAG
import com.example.mvvmexample.utils.replaceFragment
import com.example.mvvmexample.views.login.LoginActivity
import com.example.mvvmexample.views.photo.PhotoDetailsFragment
import com.example.mvvmexample.views.photo.PhotoListFragment
import com.example.mvvmexample.views.post.CreatePostFragment
import com.example.mvvmexample.views.todos.TodosListFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.inappmessaging.FirebaseInAppMessaging
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    View.OnClickListener {
    var navigationView: NavigationView? = null
    var drawer: DrawerLayout? = null
    var toolbarMain: Toolbar? = null
    var mDrawerToggle: ActionBarDrawerToggle? = null
    private lateinit var photoDetailsFragment: PhotoDetailsFragment
    private lateinit var photoListFragment: PhotoListFragment
    private lateinit var todosListFragment: TodosListFragment
    private lateinit var createPostFragment: CreatePostFragment

    private lateinit var llPhotos: LinearLayout
    private lateinit var llTodos: LinearLayout
    private lateinit var llPosts: LinearLayout
    private lateinit var llChats: LinearLayout

    var navHostFragment: NavHostFragment? = null
    var navController: NavController? = null

    private var mFirebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    private var APP_UPDATION_KEY = "app_updation"
    private var COLOR_CONFIG_KEY = "color_background"

    private val REQUEST_UPDATE = 100

    // Creates instance of the manager.
    private var appUpdateManager: AppUpdateManager? = null
    var ivLogout: ImageView? = null

    lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    private var snack: Snackbar? = null
    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        setContentView(R.layout.activity_main)
        rootView = window.decorView.rootView
        toolbarMain = findViewById(R.id.toolbar)
        setSupportActionBar(toolbarMain)
        (supportActionBar)?.setDisplayShowTitleEnabled(true)
        FirebaseApp.initializeApp(this)
        //Find Installation Id
        findFireBaseClientId()
        //Find Firebase Token
        findFireBaseToken()
        getFirebaseNotificationIntent()
        getFirebaseInAppMessaging()
        initViews()
        setupNavigation()

        //In-App update
        appUpdateManager = AppUpdateManagerFactory.create(this)

        // Before starting an update, register a listener for updates.
        appUpdateManager!!.registerListener(listener)

        checkUpdate()
        //Enable Debug mode for frequent fetches
        val configSettings = FirebaseRemoteConfigSettings.Builder().build()
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        getRemoteConfigValues()

//For Logout Purpose
        validateServerClientID()
        val serverClientId = getString(R.string.client_id)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestServerAuthCode(serverClientId)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        firebaseAuth = FirebaseAuth.getInstance()

    }


    private fun initViews() {
        photoDetailsFragment = PhotoDetailsFragment()
        photoListFragment = PhotoListFragment()
        todosListFragment = TodosListFragment()
        createPostFragment = CreatePostFragment()



        llPhotos = findViewById(R.id.ll_photos)
        llTodos = findViewById(R.id.ll_todos)
        llPosts = findViewById(R.id.ll_posts)
        llChats=findViewById(R.id.ll_chats)
        llPhotos.setOnClickListener(this)
        llTodos.setOnClickListener(this)
        llPosts.setOnClickListener(this)
        llChats.setOnClickListener(this)
    }

    private fun setupNavigation() {

        drawer = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        //navController = (supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment).navController
        navController = navHostFragment!!.navController
        // Drawer Open close listener
        mDrawerToggle = object : ActionBarDrawerToggle(
            this,
            drawer,
            toolbarMain,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                invalidateOptionsMenu()
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                Log.d("onDrawerClosed: ", title.toString())
                invalidateOptionsMenu()
            }
        }
        drawer!!.addDrawerListener(mDrawerToggle as ActionBarDrawerToggle)
        (mDrawerToggle as ActionBarDrawerToggle).syncState()
        //Set the mDrawerToggle back ground color white
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (mDrawerToggle as ActionBarDrawerToggle).drawerArrowDrawable.color =
                getColor(R.color.black)
        } else {
            (mDrawerToggle as ActionBarDrawerToggle).drawerArrowDrawable.color =
                resources.getColor(R.color.black)
        }

        navigationView?.setNavigationItemSelectedListener(this)
    }

    fun drawerToggle() {
        if (drawer!!.isDrawerOpen(GravityCompat.START)) {
            drawer!!.closeDrawer(GravityCompat.START)
        } else {
            drawer!!.openDrawer(GravityCompat.START)
        }
    }
    private fun findFireBaseClientId() {
        FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Installations", "Installation ID: " + task.result)
            } else {
                Log.e("Installations", "Unable to get Installation ID")
            }
        }
    }
    private fun findFireBaseToken() {
        FirebaseInstallations.getInstance().getToken(/* forceRefresh */ true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Installations", "Installation auth token: " + task.result?.token)
                } else {
                    Log.e("Installations", "Unable to get Installation auth token")
                }
            }
    }
    //In-App update
    private fun checkUpdate() {
        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager?.appUpdateInfo
        // Checks that the platform will allow the specified type of update.
        Log.d(TAG, "Checking for updates")
        appUpdateInfoTask?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                // Request the update.
                Log.d(TAG, "Update available")
                appUpdateManager!!.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                    AppUpdateType.FLEXIBLE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    REQUEST_UPDATE
                )
            } else {
                Log.d(TAG, "No Update available")
            }
        }


    }

    override fun onStop() {
        super.onStop()
        // When status updates are no longer needed, unregister the listener.
        appUpdateManager!!.unregisterListener(listener)
    }

    private val listener: InstallStateUpdatedListener =
        InstallStateUpdatedListener { installState ->
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                // After the update is downloaded, show a notification
                // and request user confirmation to restart the app.
                Log.d(TAG, "An update has been downloaded")
                popupSnackbarForCompleteUpdate()
            }
        }

    // Displays the snackbar notification and call to action.
    fun popupSnackbarForCompleteUpdate() {
        Snackbar.make(
            findViewById(R.id.activity_main_layout),
            "An update has just been downloaded.",
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction("RESTART") { appUpdateManager!!.completeUpdate() }
            setActionTextColor(resources.getColor(R.color.green))
            show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_UPDATE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    Log.d(TAG, "" + "Result Ok")
                    //  handle user's approval }
                }
                Activity.RESULT_CANCELED -> {
                    {
                        //if you want to request the update again just call checkUpdate()
                    }
                    Log.d(TAG, "" + "Result Cancelled")
                    //  handle user's rejection  }
                    checkUpdate()
                }
                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    //if you want to request the update again just call checkUpdate()
                    Log.d(TAG, "" + "Update Failure")
                    //  handle update failure
                    checkUpdate()
                }
            }
        }
    }

    // Checks that the update is not stalled during 'onResume()'.
// However, you should execute this check at all app entry points.
    override fun onResume() {
        super.onResume()

        appUpdateManager!!.appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                // If the update is downloaded but not installed,
                // notify the user to complete the update.
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackbarForCompleteUpdate()
                }
            }
    }


    private fun getRemoteConfigValues() {
        //here set the cache expiration duration to 1 hour
        //It means app will refresh after every 1 hour to check
        // if some changes are there in remote config
        val cacheExpiration: Long = 500

        mFirebaseRemoteConfig.fetch(cacheExpiration).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d("Fetch Succeeded", "Test")
                mFirebaseRemoteConfig.fetchAndActivate()
            } else {
                Log.d("Fetch Failed", "Test")
            }
            //changing the textview and backgorund color
            setRemoteConfigValues()
        }

    }

    private fun setRemoteConfigValues() {
        val remoteVersionName = mFirebaseRemoteConfig.getString(APP_UPDATION_KEY)
        if (remoteVersionName.isNotEmpty()) {
            // main_layout.setBackgroundColor(Color.parseColor(remoteValueBackground))
            Log.d("Get_Remote_Value", remoteVersionName.toString())
            val appVersion = getAppVersion(applicationContext)
            Log.d("GetRemoteValue1", appVersion.toString())
            val currentAppVersion = appVersion.toDouble()
            val remoteAppVersion: Double = remoteVersionName.toDouble()
            if (remoteAppVersion > currentAppVersion) {
                Log.d("updating", "start")
                updateApp()
            }
        }
    }

    private fun updateApp() {
        val packageName = "com.facebook.katana"
        //launch play store
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (e: Exception) {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }

    private fun getAppVersion(context: Context): String {
        var result = ""
        try {
            result = context.packageManager.getPackageInfo(context.packageName, 0).versionName
            result = result.replace("[a-zA-Z]|-".toRegex(), "")
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, e.message!!)
        }
        return result
    }

    private fun getFirebaseInAppMessaging() {
        FirebaseInAppMessaging.getInstance()
    }

    private fun getFirebaseNotificationIntent() {
        val intent = intent
        val message = intent.getStringExtra("message")
        if (!message.isNullOrEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("Notification")
                .setMessage(message)
                .setPositiveButton("Ok", { dialog, which -> }).show()
        }
    }

    private fun addPhotoListFragment() {
        clearBackStack()
        //Show first fragment initially
        replaceFragment(PhotoListFragment(), R.id.container)
    }

    private fun addTodoListFragment() {
        clearBackStack()
        //Show first fragment initially
        replaceFragment(TodosListFragment(), R.id.container)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onBackPressed() {
        if (drawer!!.isDrawerOpen(GravityCompat.START)) {
            drawer!!.closeDrawer(GravityCompat.START)
        } /*else if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
            }*/ else {
            when (NavHostFragment.findNavController(navHostFragment!!).currentDestination!!.id) {
                R.id.photoListFragment -> {
                    finish()
                }
                R.id.photoDetailsFragment -> {
                    super.onBackPressed()
                }
                R.id.todosListFragment -> {
                    navController?.navigate(R.id.photoListFragment)
                }
                R.id.postListFragment -> {
                    navController?.navigate(R.id.photoListFragment)
                }
                R.id.userListFragment -> {
                    navController?.navigate(R.id.photoListFragment)
                }
                R.id.chatFragment -> {
                    navController?.navigate(R.id.userListFragment)
                }
                else -> {
                    super.onBackPressed()
                }
            }
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View?) {
        if (view == llPhotos) {
            drawerToggle()
            //  addPhotoListFragment()
            navController?.navigate(R.id.photoListFragment)
        }
        if (view == llTodos) {
            drawerToggle()
            // addTodoListFragment()
            navController?.navigate(R.id.todosListFragment)
        }
        if (view == llPosts) {
            drawerToggle()
            navController?.navigate(R.id.postListFragment)
        }
        if (view==llChats){
            drawerToggle()
            navController?.navigate(R.id.userListFragment)
        }
    }

    private fun clearBackStack() {
        val manager: FragmentManager = supportFragmentManager
        if (manager.backStackEntryCount > 0) {
            val first = manager.getBackStackEntryAt(0)
            manager.popBackStack(first.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.logout, menu)
        val menuItem = menu.findItem(R.id.logout)
        // View actionView = MenuItemCompat.getActionView(menuItem);
        val actionView = menuItem.actionView
        ivLogout = actionView.findViewById(R.id.iv_logout)
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        try {
            ivLogout!!.setBackground(
                ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.ic_baseline_power_settings_new_24
                )
            )
        } catch (e: NotFoundException) {
            e.printStackTrace()
        }
        /*  } else {
            //Safely create our VectorDrawable on pre-L android versions.
            try {
                ivGoNotification.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_bell));
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
        }*/
        actionView.setOnClickListener { v: View? ->
            signOut()
            finish()

        }
        return true
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
    fun signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        if ( firebaseAuth.currentUser!=null){
            firebaseAuth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

   /* @JvmName("onOptionsItemSelected1")
    @Suppress("INAPPLICABLE_JVM_NAME")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.iv_logout) {
            // Do something
            true
        } else super.onOptionsItemSelected(item)
    }*/
}