package com.example.mvvmexample.views.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmexample.R
import com.example.mvvmexample.databinding.FragmentUserListBinding
import com.example.mvvmexample.model.userInfo.UserInfos
import com.example.mvvmexample.views.chat.adapter.UserListAdapter
import com.example.mvvmexample.views.chat.interfaces.UserItemClick
import com.example.mvvmexample.views.photo.PhotoListFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class UserItemListFragment : Fragment(),UserItemClick {
    private lateinit var binding: FragmentUserListBinding
    var rvUsers: RecyclerView? = null
    var toolbar: Toolbar? = null
    private lateinit var useListAdapter: UserListAdapter
    var userList: ArrayList<UserInfos> = ArrayList()
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_user_list, container, false
        )
        val mRootView = binding.root
        binding.lifecycleOwner = this
        return mRootView
        /*binding = FragmentUserListBinding.inflate(inflater, container, false)
        return binding.root*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        firebaseAuth = FirebaseAuth.getInstance()
        getAllUsers()
    }

    private fun initViews() {
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar?.visibility = View.VISIBLE
        toolbar?.title = "Users"

        rvUsers=requireActivity().findViewById(R.id.rv_users)

        useListAdapter = UserListAdapter(context, this)
        val layoutManagers = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rvUsers!!.layoutManager = layoutManagers
        rvUsers!!.adapter = useListAdapter
        rvUsers!!.isNestedScrollingEnabled = false
    }

    private fun getAllUsers() {
      val firebaseUser=firebaseAuth.currentUser
        val reference = FirebaseDatabase.getInstance().getReference("/users")
        Log.d("reference",reference.toString())
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userList.clear()
                for (dataSnapshot1 in dataSnapshot.children) {

                    val modelUsers: UserInfos? = dataSnapshot1.getValue(UserInfos::class.java)

                    //Avoid current login user
                    if (modelUsers!!.uid != null && !modelUsers.uid.equals(firebaseUser!!.uid)) {
                        userList.add(modelUsers)
                    }

                    useListAdapter.setUsers(userList)
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("databaseError",databaseError.message.toString())
            }
        })
    }

    override fun userItemClick(view: View, data: UserInfos) {
        try {
            navController().navigate(
                UserItemListFragmentDirections.actionUserListFragmentToChatFragment(data)
            )
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }
    private fun navController() = findNavController()
}