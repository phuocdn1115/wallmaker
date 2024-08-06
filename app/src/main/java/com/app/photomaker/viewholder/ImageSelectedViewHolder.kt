package com.app.photomaker.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.app.photomaker.databinding.ItemImageSelectedViewHolderBinding
import com.bumptech.glide.Glide

class ImageSelectedViewHolder(private val binding : ItemImageSelectedViewHolderBinding, private val onRemoveImageListener: (Int, String?) -> Unit) : RecyclerView.ViewHolder(binding.root){
    init {
        if(absoluteAdapterPosition == 0)
            setupUIItemImageFromGallery(itemView)
    }

    fun bind(data: String?, position: Int) {
        Glide.with(binding.root.context)
            .load(data)
            .into(binding.imgPicker)
        binding.icRemovePickedImage.setOnClickListener {
            onRemoveImageListener.invoke(position, data)
        }
    }
}