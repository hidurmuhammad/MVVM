package com.example.mvvmexample.views.photo.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation

import com.example.mvvmexample.R
import com.example.mvvmexample.databinding.PhotoListItemsBinding
import com.example.mvvmexample.model.PhotoData
import com.example.mvvmexample.views.photo.interfaces.PhotoClickListener
import kotlinx.android.synthetic.main.photo_list_items.view.*


class PhotoListAdapter (val context: Context?,
                        val clickListener: PhotoClickListener) : RecyclerView.Adapter<PhotoListAdapter.ViewHolder>() {

    var photoList: List<PhotoData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding: PhotoListItemsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.photo_list_items, parent, false
        )
        return ViewHolder(viewBinding)
    }


    override fun getItemCount(): Int {
        return photoList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)

        holder.itemView.iv_image.load(photoList.get(position).thumbnailUrl) {
            //crossfade(true)
            placeholder(R.drawable.ic_launcher_foreground)
            //transformations(CircleCropTransformation())
        }
    }

    fun setPhotos(photos: List<PhotoData>) {
        this.photoList = photos
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val viewBinding: PhotoListItemsBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun onBind(position: Int) {
            val row = photoList[position]
            viewBinding.photos = row
            viewBinding.photoClickInterface = clickListener
        }
    }

}