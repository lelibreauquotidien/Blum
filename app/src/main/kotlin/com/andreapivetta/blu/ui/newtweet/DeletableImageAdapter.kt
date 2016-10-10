package com.andreapivetta.blu.ui.newtweet

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import com.andreapivetta.blu.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.photo_deletable.view.*
import java.io.File

/**
 * Created by andrea on 29/05/16.
 */
class DeletableImageAdapter(val imageFiles: MutableList<File>) :
        RecyclerView.Adapter<DeletableImageAdapter.Companion.DeletableImageViewHolder>() {

    companion object {
        class DeletableImageViewHolder(root: View) : RecyclerView.ViewHolder(root) {
            val photoImageView: ImageView = root.tweetPhotoImageView
            val deleteButton: ImageButton = root.deleteButton
        }
    }

    override fun onBindViewHolder(holder: DeletableImageViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
                .load(imageFiles[position])
                .into(holder.photoImageView)
        holder.deleteButton.setOnClickListener {
            imageFiles.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = DeletableImageViewHolder(
            LayoutInflater.from(parent?.context).inflate(R.layout.photo_deletable, parent, false))

    override fun getItemCount() = imageFiles.size
}