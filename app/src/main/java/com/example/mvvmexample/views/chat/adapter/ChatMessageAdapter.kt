package com.example.mvvmexample.views.chat.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.mvvmexample.R
import com.example.mvvmexample.databinding.ImageMessageBinding
import com.example.mvvmexample.databinding.MessageBinding
import com.example.mvvmexample.model.chat.ChatData
import com.example.mvvmexample.model.todos.TodosData
import com.example.mvvmexample.views.chat.ChatFragment.Companion.ANONYMOUS
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ChatMessageAdapter(
    private val options: FirebaseRecyclerOptions<ChatData>,
    private val currentUserName: String?
) :
    FirebaseRecyclerAdapter<ChatData, RecyclerView.ViewHolder>(options) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_TEXT) {
            val view = inflater.inflate(R.layout.message, parent, false)
            val binding = MessageBinding.bind(view)
            MessageViewHolder(binding)
        } else {
            val view = inflater.inflate(R.layout.image_message, parent, false)
            val binding = ImageMessageBinding.bind(view)
            ImageMessageViewHolder(binding)
        }
    }
    fun setChats() {
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: ChatData) {

                if (!options.snapshots[position].equals("")) {
                    if (options.snapshots[position].text != null) {
                        (holder as MessageViewHolder).bind(model)
                    } else {
                        (holder as ImageMessageViewHolder).bind(model)
                    }
                }

    }

    override fun getItemViewType(position: Int): Int {
        return if (options.snapshots[position].text != null) VIEW_TYPE_TEXT else VIEW_TYPE_IMAGE
    }

    inner class MessageViewHolder(private val binding: MessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatData) {
            if (!item.equals("")) {
                binding.messageTextView.text = item.text
                setTextColor(item.name, binding.messageTextView)

                binding.messengerTextView.text = if (item.name == null) ANONYMOUS else item.name
                if (item.photoUrl != null) {
                    loadImageIntoView(binding.messengerImageView, item.photoUrl!!)
                } else {
                    binding.messengerImageView.setImageResource(R.drawable.ic_baseline_account_circle_24)
                }
            }
        }

        private fun setTextColor(userName: String?, textView: TextView) {
            if (userName != ANONYMOUS && currentUserName == userName && userName != null) {
                textView.setBackgroundResource(R.drawable.rounded_message_blue)
                textView.setTextColor(Color.WHITE)
            } else {
                textView.setBackgroundResource(R.drawable.rounded_message_gray)
                textView.setTextColor(Color.BLACK)
            }
        }
    }

    inner class ImageMessageViewHolder(private val binding: ImageMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatData) {
            if (!item.equals("")) {
                if (item.imageUrl != null) {
                    loadImageIntoView(binding.messageImageView, item.imageUrl!!)
                }
                binding.messengerTextView.text = if (item.name == null) ANONYMOUS else item.name
                if (item.photoUrl != null) {
                    loadImageIntoView(binding.messengerImageView, item.photoUrl!!)
                } else {
                    binding.messengerImageView.setImageResource(R.drawable.ic_baseline_account_circle_24)
                }
            }
        }
    }

    private fun loadImageIntoView(view: ImageView, url: String) {
        if (url.startsWith("gs://")) {
            val storageReference = Firebase.storage.getReferenceFromUrl(url)
            storageReference.downloadUrl
                .addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    view.load(downloadUrl) {
                        //crossfade(true)
                        //placeholder(R.drawable.ic_launcher_foreground)
                        //transformations(CircleCropTransformation())
                    }
                    /*  Glide.with(view.context)
                          .load(downloadUrl)
                          .into(view)*/
                }
                .addOnFailureListener { e ->
                    Log.d(
                        TAG,
                        "Getting download url was not successful.",
                        e
                    )
                }
        } else {
            view.load(url) {
                //crossfade(true)
                // placeholder(R.drawable.ic_launcher_foreground)
                //transformations(CircleCropTransformation())
            }
            //Glide.with(view.context).load(url).into(view)
        }

    }

    companion object {
        const val TAG = "MessageAdapter"
        const val VIEW_TYPE_TEXT = 1
        const val VIEW_TYPE_IMAGE = 2
    }
}