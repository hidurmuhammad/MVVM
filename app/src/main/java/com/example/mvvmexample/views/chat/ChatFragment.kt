package com.example.mvvmexample.views.chat


import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvmexample.R
import com.example.mvvmexample.databinding.FragmentChatBinding
import com.example.mvvmexample.model.chat.ChatData
import com.example.mvvmexample.utils.TAG
import com.example.mvvmexample.views.chat.adapter.ChatMessageAdapter
import com.example.mvvmexample.views.login.LoginActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage


class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private lateinit var manager: LinearLayoutManager
    private val mFirebaseRef: DatabaseReference? = null

    // Firebase instance variables
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var adapter: ChatMessageAdapter
    private var userIds: String? = null
    private var params: ChatFragmentArgs? = null
    var toolbar: Toolbar? = null

    private val openDocument = registerForActivityResult(MyOpenDocumentContract()) { uri ->
        onImageSelected(uri)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        params = ChatFragmentArgs.fromBundle(requireArguments())
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar?.visibility = View.VISIBLE
        toolbar?.navigationIcon = resources.getDrawable(R.drawable.ic_baseline_arrow_back_24)
        val drawer: DrawerLayout = requireActivity().findViewById(R.id.drawer_layout)
        val toggle = object : ActionBarDrawerToggle(
            activity,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {}
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        toggle.isDrawerIndicatorEnabled = false

        toolbar?.title = params!!.userData.name
        toolbar?.setNavigationOnClickListener { v: View? ->
            // Handle the back button event
            requireActivity().onBackPressed()
        }
        // binding.chat = params?.userData
        // Initialize Firebase Auth and check if the user is signed in
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(Intent(requireActivity(), LoginActivity::class.java))
            requireActivity().finish()
            return
        } else {

            userIds = auth.currentUser!!.uid
        }

        // Initialize Realtime Database
        db = FirebaseDatabase.getInstance()
        val messagesRef = db.getReference(MESSAGES_CHILD)
        // The FirebaseRecyclerAdapter class and options come from the FirebaseUI library
        val options = FirebaseRecyclerOptions.Builder<ChatData>()
            .setQuery(messagesRef, ChatData::class.java)
            .build()
        adapter = ChatMessageAdapter(options,getUserName())
        binding.progressBar.visibility = ProgressBar.INVISIBLE
        manager = LinearLayoutManager(requireActivity())
        manager.stackFromEnd = true
        // adapter.notifyDataSetChanged()

        binding.messageRecyclerView.layoutManager = manager
        binding.messageRecyclerView.adapter = adapter


        // Scroll down when a new message arrives
        // See MyScrollToBottomObserver for details
        adapter.registerAdapterDataObserver(
            MyScrollToBottomObserver(binding.messageRecyclerView, adapter, manager)
        )

        // Disable the send button when there's no text in the input field
        // See MyButtonObserver for details
        binding.messageEditText.addTextChangedListener(SendButtonObserver(binding.sendButton))

        // When the send button is clicked, send a text message
        binding.sendButton.setOnClickListener {
            val friendlyMessage = ChatData(
                binding.messageEditText.text.toString(),
                getUserName(),
                getPhotoUrl(),
                null, userIds.toString()
            )
            db.reference.child(MESSAGES_CHILD).push().setValue(friendlyMessage)
            binding.messageEditText.setText("")
        }

        // When the image button is clicked, launch the image picker
        binding.addMessageImageView.setOnClickListener {
            openDocument.launch(arrayOf("image/*"))
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in.
        if (auth.currentUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(Intent(requireActivity(), LoginActivity::class.java))
            requireActivity().finish()
            return
        }
        adapter.setChats()
    }

    override fun onPause() {
        adapter.stopListening()
        super.onPause()
        adapter.setChats()
    }

    override fun onResume() {
        adapter.startListening()
        super.onResume()
        adapter.setChats()
    }


    private fun onImageSelected(uri: Uri) {
        Log.d(Companion.TAG, "Uri:" + uri.toString())
        val user = auth.currentUser
        val tempMessage =
            ChatData(null, getUserName(), getPhotoUrl(), LOADING_IMAGE_URL, userIds.toString())
        db.reference.child(MESSAGES_CHILD).push().setValue(
            tempMessage,
            DatabaseReference.CompletionListener { databaseError, databaseReference ->
                if (databaseError != null) {
                    Log.d(
                        TAG, "Unable to write message to database.",
                        databaseError.toException()
                    )
                    return@CompletionListener
                }

                // Build a StorageReference and then upload the file
                val key = databaseReference.key
                val storageReference = Firebase.storage
                    .getReference(user!!.uid)
                    .child(key!!)
                    .child(uri.lastPathSegment!!)
                putImageInStorage(storageReference, uri, key)

            })
    }

    private fun putImageInStorage(storageReference: StorageReference, uri: Uri, key: String?) {
        // First upload the image to Cloud Storage
        binding.progressBar.visibility = ProgressBar.VISIBLE
        storageReference.putFile(uri).addOnSuccessListener(requireActivity()) { taskSnapshot ->
            // After the image loads, get a public downloadUrl for the image
            // and add it to the message.
            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                    binding.progressBar.visibility = ProgressBar.INVISIBLE
                    Log.d("url_value", uri.toString())
                    val friendlyMessage =
                        ChatData(
                            null,
                            getUserName(),
                            getPhotoUrl(),
                            uri.toString(),
                            userIds.toString()
                        )
                    db.reference.child(MESSAGES_CHILD)
                        .child(key!!)
                        .setValue(friendlyMessage)
                }

        }.addOnFailureListener(requireActivity()) { e ->
            binding.progressBar.visibility = ProgressBar.INVISIBLE
            Log.d(
                Companion.TAG,
                "Image upload task was unsuccessful.", e
            )
        }
    }

    private fun getPhotoUrl(): String? {
        val user = auth.currentUser
        return user?.photoUrl?.toString()
    }

    private fun getUserName(): String? {
        val user = auth.currentUser
        return if (user != null) {
            user.displayName
        } else ANONYMOUS
    }

    companion object {
        private const val TAG = "MainActivity"
        const val MESSAGES_CHILD = "messages"
        const val ANONYMOUS = "anonymous"
        private const val LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif"
    }

    private fun signOut() {
        AuthUI.getInstance().signOut(requireActivity())
    }
}