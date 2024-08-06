package com.app.photomaker.viewholder.videohome

import androidx.recyclerview.widget.RecyclerView
import com.app.photomaker.databinding.ItemNewVideoHomeBinding

class NewVideoViewHolder(
    itemBinding: ItemNewVideoHomeBinding,
    onClickItemNewVideo : () -> Unit
) : RecyclerView.ViewHolder(itemBinding.root) {
    val binding: ItemNewVideoHomeBinding
    val onClickItemNewVideo: () -> Unit
    init {
        this.binding = itemBinding
        this.onClickItemNewVideo = onClickItemNewVideo
        this.binding.cardViewNewVideo.setOnClickListener {
            onClickItemNewVideo.invoke()
        }
    }
}