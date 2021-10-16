package com.example.mvvmexample.views.post

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.mvvmexample.R
import com.example.mvvmexample.databinding.FragmentPostListBinding
import com.example.mvvmexample.firebaseservice.analytics.ClickEventTracker
import com.example.mvvmexample.firebaseservice.analytics.ViewTracker
import com.example.mvvmexample.utils.Constants
import com.example.mvvmexample.utils.SessionManagement
import com.example.mvvmexample.viewmodel.PostListViewModel
import com.example.mvvmexample.views.photo.PhotoListFragment
import com.example.mvvmexample.views.post.adapter.PostListAdapter
import kotlinx.android.synthetic.main.fragment_post_list.*
import org.koin.android.viewmodel.ext.android.viewModel


class PostListFragment : Fragment(), View.OnClickListener {
    val TAG: String = PostListFragment::class.java.simpleName

    private val postListViewModel by viewModel<PostListViewModel>()
    private lateinit var postListAdapter: PostListAdapter
    private lateinit var mViewDataBinding: FragmentPostListBinding
    var toolbar: Toolbar? = null

    private var isOpen: Boolean = false
    private lateinit var fabOpenAnimation: Animation
    private lateinit var fabCloseAnimation: Animation
    private lateinit var fabClockAnimation: Animation
    private lateinit var fabAntiClockAnimation: Animation
    var previousScreen=""
    //For Screen tracking and engagement measurement
    var viewTracker: ViewTracker? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mViewDataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_post_list, container, false
        )
        val mRootView = mViewDataBinding.root
        mViewDataBinding.lifecycleOwner = this
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setView()
        fabAnimation()
        mViewDataBinding.postListViewModel = postListViewModel
        postListViewModel.getAllPosts(1)
        postListViewModel.postList.observe(viewLifecycleOwner, Observer {
            if (it!!.isNotEmpty()) {
                Log.d("@posts", it.size.toString())
                postListAdapter.setPosts(it)
            }
        })
    }


    private fun setView() {
        postListAdapter = PostListAdapter(context)
        rv_posts.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rv_posts.adapter = postListAdapter
        rv_posts.isNestedScrollingEnabled = false
        (rv_posts.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    private fun initViews() {
        val sharedPreference = SessionManagement(requireActivity())
        if (sharedPreference.getValueString(Constants.PREVIOUS_SCREEN) != null) {
            previousScreen = sharedPreference.getValueString(Constants.PREVIOUS_SCREEN)!!
        }
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar?.visibility = View.VISIBLE
        toolbar?.title = getString(R.string.app_name)

        val drawer: DrawerLayout = requireActivity().findViewById(R.id.drawer_layout)
        val toggle = object :ActionBarDrawerToggle(
            activity,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ){
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                var currentScreen = ""
                currentScreen=PostListFragment().TAG
                val clickEventTracker = ClickEventTracker(drawerView)
                clickEventTracker.setScreenValues(currentScreen)
            }
            override fun onDrawerClosed(drawerView: View) {
            super.onDrawerClosed(drawerView)
           }
        }
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        toggle.isDrawerIndicatorEnabled = true
    }


    private fun fabAnimation() {
        //Fab button animation
        fabCloseAnimation = AnimationUtils.loadAnimation(activity, R.anim.fab_close)
        fabOpenAnimation = AnimationUtils.loadAnimation(activity, R.anim.fab_open)
        fabClockAnimation = AnimationUtils.loadAnimation(activity, R.anim.fab_rotate_clock)
        fabAntiClockAnimation = AnimationUtils.loadAnimation(activity, R.anim.fab_rotate_anticlock)

        mViewDataBinding.fabCreatePost.setOnClickListener {
            if (isOpen) {
                mViewDataBinding.fabCreatePost.startAnimation(fabAntiClockAnimation)
                mViewDataBinding.fabCreate.startAnimation(fabCloseAnimation)
                val drawableIcon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_menu_24)
                mViewDataBinding.fabCreatePost.icon = drawableIcon
                //   mViewDataBinding.fabCreate.isClickable = false
                isOpen = false
            } else {
                mViewDataBinding.fabCreatePost.startAnimation(fabClockAnimation)
                val drawableIcon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_add_24)
                mViewDataBinding.fabCreatePost.icon = drawableIcon
                mViewDataBinding.fabCreate.startAnimation(fabOpenAnimation)
                mViewDataBinding.fabCreate.isClickable = true
                isOpen = true
            }
        }
        mViewDataBinding.fabCreate.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        if (view==mViewDataBinding.fabCreate){
            try {
                navController().navigate(
                    PostListFragmentDirections.actionPostListFragmentToCreatePostFragment()
                )
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun navController() = findNavController()

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

}