package com.example.mvvmexample.views.post

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvmexample.R
import com.example.mvvmexample.databinding.FragmentCreatePostBinding
import com.example.mvvmexample.firebaseservice.analytics.ClickEventTracker
import com.example.mvvmexample.firebaseservice.analytics.ViewTracker
import com.example.mvvmexample.model.posts.CreatePost
import com.example.mvvmexample.utils.Constants
import com.example.mvvmexample.utils.SessionManagement
import com.example.mvvmexample.viewmodel.CreatePostViewModel
import com.example.mvvmexample.views.post.adapter.CreatePostAdapter
import com.example.mvvmexample.views.todos.TodosListFragment
import kotlinx.android.synthetic.main.create_post_dialog.view.*
import kotlinx.android.synthetic.main.fragment_create_post.*


class CreatePostFragment : Fragment(), View.OnClickListener, CreatePostAdapter.PostListener {
    val TAG: String = TodosListFragment::class.java.simpleName
    lateinit var mViewDataBinding: FragmentCreatePostBinding
    private lateinit var viewModelForPost: CreatePostViewModel
    private lateinit var adapter: CreatePostAdapter


    private lateinit var progressBar: ProgressBar
    var toolbar: Toolbar? = null
    var previousScreen = ""

    //For Screen tracking and engagement measurement
    var viewTracker: ViewTracker? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mViewDataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_create_post, container, false
        )
        val mRootView = mViewDataBinding.root
        mViewDataBinding.lifecycleOwner = this
        return mRootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    @JvmName("onCreateOptionsMenu1")
    @Suppress("INAPPLICABLE_JVM_NAME")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.post_menu, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }


    @JvmName("onOptionsItemSelected1")
    @Suppress("INAPPLICABLE_JVM_NAME")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_create_post -> showCreatePOstDialog()
        }
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelForPost = ViewModelProvider(requireActivity())[CreatePostViewModel::class.java]
        initViews()

    }

    private fun initViews() {
        val sharedPreference = SessionManagement(requireActivity())
        if (sharedPreference.getValueString(Constants.PREVIOUS_SCREEN) != null) {
            previousScreen = sharedPreference.getValueString(Constants.PREVIOUS_SCREEN)!!
        }
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar?.visibility = View.VISIBLE
        progressBar = requireActivity().findViewById(R.id.progress_bar)

        val drawer: DrawerLayout = requireActivity().findViewById(R.id.drawer_layout)
        val toggle = object : ActionBarDrawerToggle(
            activity,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                var currentScreen = ""
                currentScreen = CreatePostFragment().TAG
                val clickEventTracker = ClickEventTracker(drawerView)
                clickEventTracker.setScreenValues(currentScreen)
            }

        }
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        toggle.isDrawerIndicatorEnabled = true

        initAdapter()

        viewModelForPost.fetchAllPosts()

        populateAllPosts()


    }

    private fun initAdapter() {
        adapter = CreatePostAdapter(this)
        rv_create_posts.layoutManager = LinearLayoutManager(requireContext())
        rv_create_posts.adapter = adapter
    }

    private fun populateAllPosts() {
        viewModelForPost.postModelListLiveData?.observe(requireActivity(), Observer {
            if (it != null) {
                rv_create_posts.visibility = View.VISIBLE
                adapter.setData(it as ArrayList<CreatePost>)
            } else {
                showToast("Something went wrong")
            }
            progressBar.visibility = View.GONE
        })
    }

    private fun showCreatePOstDialog() {
        val dialog = Dialog(requireContext())
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.create_post_dialog, null)
        dialog.setContentView(view)

        var title = ""
        var body = ""

        view.btn_submit.setOnClickListener {
            title = view.et_title.text.toString().trim()
            body = view.et_body.text.toString().trim()

            if (title.isNotEmpty() && body.isNotEmpty()) {
                val postModel = CreatePost()
                postModel.userId = 1
                postModel.title = title
                postModel.body = body

                viewModelForPost.createPost(postModel)

                viewModelForPost.createPostLiveData?.observe(this, Observer {
                    if (it != null) {
                        adapter.addData(postModel)
                        rv_create_posts.smoothScrollToPosition(0)
                    } else {
                        showToast("Cannot create post at the moment")
                    }
                    dialog.cancel()
                })

            } else {
                showToast("Please fill data carefully!")
            }

        }

        dialog.show()

        val window = dialog.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

    }


    override fun onItemDeleted(postModel: CreatePost, position: Int) {
        postModel.id?.let { viewModelForPost.deletePost(it) }
        viewModelForPost.deletePostLiveData?.observe(this, Observer {
            if (it != null) {
                adapter.removeData(position)
            } else {
                showToast("Cannot delete post at the moment!")
            }
        })

    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {

    }

    override fun onStart() {
        super.onStart()
        if (viewTracker == null) {
            viewTracker = ViewTracker(TAG)
        }
    }

    override fun onStop() {
        super.onStop()
        viewTracker!!.setPreviousScreen(previousScreen)
        viewTracker!!.stopTracking()
    }


    /*private fun createPost(title: String?, body: String?) {
        // Create the Constraints
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val postCreate = Data.Builder()
            .putString("title", title!!)
            .putInt("userId", 1)
            .putString("body", body!!)
            .build()
        //worker for create api request
        val createPost = OneTimeWorkRequestBuilder<CreatePosts>()
            .setConstraints(constraints)
            .setInputData(postCreate)
             .setBackoffCriteria(
                 BackoffPolicy.EXPONENTIAL,
                 OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                 TimeUnit.MILLISECONDS
             )
            .build()

        WorkManager.getInstance(requireContext())
            .beginWith(createPost)
            .enqueue()

        // Observe the result the work
        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(createPost.id)
            .observe(viewLifecycleOwner, Observer { workInfo ->
                if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                    // FINISHED SUCCESSFULLY!
                    Log.d("Tsted", "tstr")
                    //   (context as AppCompatActivity).onBackPressed()
                    requireActivity().onBackPressed()
                }
            })
    }

    //Create Api call
    class CreatePosts(context: Context, params: WorkerParameters) :
        CoroutineWorker(context, params) {

        @SuppressLint("RestrictedApi")
        override suspend fun doWork(): Result {

            //get Input Data back using "inputData" variable
            val title = inputData.getString("title")
            val userId = inputData.getInt("userId", 0)
            val body = inputData.getString("body")
            val postData = CreatePost(title, userId, body)
            val gson = Gson()
            val jsonParser = JsonParser()
            val json = gson.toJson(postData)
            val jsonObject = jsonParser.parse(json).asJsonObject
            try {

            val response = createPostViewModel.createPost(jsonObject).execute()
                if(response.code()==201){
                    Log.d("svdvsds","vgvd");
                }
            if (response.isSuccessful) {
                return Result.success()
            } else {
                if (response.code() in (500..599)) {
                    // try again if there is a server error
                    return Result.Retry()
                }

                return Result.failure()
            }
            }catch (e: CancellationException){
                Log.d("CancellationException","tested")
            }finally {
                //Cleanup
            }
            if (isStopped) {
                Log.d("isStopped","Stopped")
                // perform cleanup/shutdown

                // this result is ignored
                return Result.success()
            }

            return Result.success()
        }

    }*/

}