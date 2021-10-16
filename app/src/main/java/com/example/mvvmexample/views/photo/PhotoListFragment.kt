package com.example.mvvmexample.views.photo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmexample.R
import com.example.mvvmexample.databinding.FragmentPhotoListBinding
import com.example.mvvmexample.firebaseservice.analytics.ClickEventTracker
import com.example.mvvmexample.firebaseservice.analytics.ViewTracker
import com.example.mvvmexample.model.PhotoData
import com.example.mvvmexample.utils.Constants
import com.example.mvvmexample.utils.SessionManagement
import com.example.mvvmexample.viewmodel.PhotoListViewModel
import com.example.mvvmexample.views.photo.adapter.PhotoListAdapter
import com.example.mvvmexample.views.photo.interfaces.PhotoClickListener
import kotlinx.android.synthetic.main.fragment_photo_list.*
import org.koin.android.viewmodel.ext.android.viewModel


class PhotoListFragment : Fragment(), PhotoClickListener {
    val TAG: String = PhotoListFragment::class.java.simpleName

    private val photoListViewModel by viewModel<PhotoListViewModel>()
    private lateinit var photoListAdapter: PhotoListAdapter
    private lateinit var photoViewDataBinding: FragmentPhotoListBinding
    var toolbar: Toolbar? = null

    //For Screen tracking and engagement measurement
    var viewTracker: ViewTracker? = null

    var rv_Photo:RecyclerView?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        photoViewDataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_photo_list, container, false
        )
        val mRootView = photoViewDataBinding.root
        photoViewDataBinding.lifecycleOwner = this
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                requireActivity().onBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
        initViews()
        setView()
        photoViewDataBinding.viewModel = photoListViewModel
        photoListViewModel.getAllPhotos()
        photoListViewModel.photosList.observe(viewLifecycleOwner, Observer {
            if (it!!.isNotEmpty()) {
                Log.d("@Photos", it.size.toString())
                photoListAdapter.setPhotos(it)
            }
        })
    }

    private fun initViews() {
        val sharedPreference = SessionManagement(requireActivity())
        sharedPreference.save(Constants.PREVIOUS_SCREEN,TAG)

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
                    currentScreen=PhotoListFragment().TAG
                    val clickEventTracker = ClickEventTracker(drawerView)
                    clickEventTracker.setScreenValues(currentScreen)
            }
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
              //  Log.d("onDrawerClosed: ", requireActivity().title.toString())
            }
        }
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        toggle.isDrawerIndicatorEnabled = true

        rv_Photo=requireActivity().findViewById(R.id.rv_photos)
    }

    private fun setView() {
        photoListAdapter = PhotoListAdapter(context, this)
         val layoutManagers = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rv_Photo!!.layoutManager = layoutManagers
        rv_Photo!!.adapter = photoListAdapter
        rv_Photo!!.isNestedScrollingEnabled = false
        rv_Photo!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                var lastVisibleItemPosition = 0
                val totalItemCount = layoutManagers.itemCount
                lastVisibleItemPosition = layoutManagers.findLastVisibleItemPosition()
                val endHasBeenReached: Boolean = lastVisibleItemPosition + 5 >= totalItemCount
                if (totalItemCount > 0 && endHasBeenReached) {
                    //you have reached to the bottom of your recycler view
                    Log.d("Reached at Bottom","Success")
                }
            }
        })

    }

    override fun onItemClick(view: View,data: PhotoData) {
        /*(activity as MainActivity).clearBackStack()
         (activity as MainActivity).replaceFragment(PhotoDetailsFragment.newInstance(data),
            R.id.container, "photoDetailsFragment")    }*/
       val currentScreen=PhotoListFragment().TAG
        val clickEventTracker = ClickEventTracker(view)
        clickEventTracker.setScreenValues(currentScreen)
        clickEventTracker.setPhotoName(data.title!!)
        try {
            navController().navigate(
                PhotoListFragmentDirections.actionPhotoListFragmentToPhotoDetailsFragment(
                    data
                )
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
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
        viewTracker!!.stopTracking()
        viewTracker = null
    }
}
