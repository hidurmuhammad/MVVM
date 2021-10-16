package com.example.mvvmexample.views.todos

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvmexample.R
import com.example.mvvmexample.databinding.FragmentTodosListBinding
import com.example.mvvmexample.firebaseservice.analytics.ClickEventTracker
import com.example.mvvmexample.firebaseservice.analytics.ViewTracker
import com.example.mvvmexample.model.todos.TodosData
import com.example.mvvmexample.utils.Constants
import com.example.mvvmexample.utils.SessionManagement
import com.example.mvvmexample.viewmodel.TodosListViewModel
import com.example.mvvmexample.views.photo.PhotoListFragment
import com.example.mvvmexample.views.todos.adapter.TodosAdapter
import com.example.mvvmexample.views.todos.interfaces.TodosClickListener
import kotlinx.android.synthetic.main.fragment_todos_list.*
import org.koin.android.viewmodel.ext.android.viewModel

class TodosListFragment : Fragment(), TodosClickListener, View.OnClickListener {
    val TAG: String = TodosListFragment::class.java.simpleName

    private val todoListViewModel by viewModel<TodosListViewModel>()
    private lateinit var todoAdapter: TodosAdapter
    private lateinit var mViewDataBinding: FragmentTodosListBinding
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
            R.layout.fragment_todos_list, container, false
        )
        val mRootView = mViewDataBinding.root
        mViewDataBinding.lifecycleOwner = this
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setView()
        mViewDataBinding.viewModel = todoListViewModel
        todoListViewModel.getAllTodos()
        todoListViewModel.todosList.observe(viewLifecycleOwner, Observer {
            if (it!!.isNotEmpty()) {
                Log.d("@todo", it.size.toString())
                todoAdapter.setTodos(it)
            }
        })
    }

    private fun initViews() {
        val sharedPreference = SessionManagement(requireActivity())
        if (sharedPreference.getValueString(Constants.PREVIOUS_SCREEN) != null) {
            previousScreen = sharedPreference.getValueString(Constants.PREVIOUS_SCREEN)!!
        }
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar?.visibility = View.VISIBLE

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
                currentScreen = TodosListFragment().TAG
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

    private fun setView() {
        todoAdapter = TodosAdapter(context, this)
        rv_todos.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rv_todos.adapter = todoAdapter
        rv_todos.isNestedScrollingEnabled = false
    }

    override fun onItemClick(data: TodosData) {

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
}