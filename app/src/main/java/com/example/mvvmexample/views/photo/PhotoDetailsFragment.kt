package com.example.mvvmexample.views.photo

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import coil.load
import com.example.mvvmexample.R
import com.example.mvvmexample.databinding.FragmentPhotoDetailsBinding
import com.example.mvvmexample.firebaseservice.analytics.ClickEventTracker
import com.example.mvvmexample.firebaseservice.analytics.ViewTracker
import com.example.mvvmexample.utils.Constants
import com.example.mvvmexample.utils.SessionManagement


class PhotoDetailsFragment : Fragment() {
    val TAG: String = PhotoDetailsFragment::class.java.simpleName
    var previousScreen = ""

    //For Screen tracking and engagement measurement
    var viewTracker: ViewTracker? = null
    /* companion object {
         @JvmStatic
         fun newInstance(data: PhotoData) = PhotoDetailsFragment().apply {
             arguments = Bundle().apply {
                 putParcelable(Constants.PHOTO_DETAILS_DATA, data)
             }
         }
     }*/
    //OR
    private var params: PhotoDetailsFragmentArgs? = null

    // private var photoInfo: PhotoData? = null
    private lateinit var mViewDataBinding: FragmentPhotoDetailsBinding
    var toolbar: Toolbar? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mViewDataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_photo_details, container, false
        )
        val mRootView = mViewDataBinding.root
        mViewDataBinding.lifecycleOwner = this
        return mRootView
    }


    /*override fun onAttach(context: Context) {
        super.onAttach(context)
        photoInfo = arguments?.getParcelable(Constants.PHOTO_DETAILS_DATA)
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        params = PhotoDetailsFragmentArgs.fromBundle(requireArguments())
        mViewDataBinding.photo = params?.photoData
        // mViewDataBinding.photo = photoInfo
        initViews()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initViews() {
        val sharedPreference = SessionManagement(requireActivity())
        if (sharedPreference.getValueString(Constants.PREVIOUS_SCREEN) != null) {
            previousScreen = sharedPreference.getValueString(Constants.PREVIOUS_SCREEN)!!
        }
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar?.visibility = View.VISIBLE
        toolbar?.navigationIcon = resources.getDrawable(R.drawable.ic_baseline_arrow_back_24)

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
                    currentScreen=PhotoDetailsFragment().TAG
                    val clickEventTracker = ClickEventTracker(drawerView)
                    clickEventTracker.setScreenValues(currentScreen)
            }
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
               // Log.d("onDrawerClosed: ", requireActivity().title.toString())
            }
        }
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        toggle.isDrawerIndicatorEnabled = false

        toolbar?.setNavigationOnClickListener(View.OnClickListener { v: View? ->
            // Handle the back button event
            requireActivity().onBackPressed()
        })

        mViewDataBinding.ivDetailImage.load(params?.photoData?.thumbnailUrl) {
            //crossfade(true)
            placeholder(R.drawable.ic_launcher_foreground)
            //transformations(CircleCropTransformation())
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
        viewTracker!!.setPhotoName(params!!.photoData.title)
        viewTracker!!.setPreviousScreen(previousScreen)
        viewTracker!!.stopTracking()
    }
}