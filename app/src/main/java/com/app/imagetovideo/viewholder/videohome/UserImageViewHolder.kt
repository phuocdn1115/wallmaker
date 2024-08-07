package com.app.imagetovideo.viewholder.videohome

import androidx.recyclerview.widget.RecyclerView
import com.app.imagetovideo.base.handler.GlideHandler
import com.app.imagetovideo.data.model.ImageMadeByUser
import com.app.imagetovideo.databinding.ItemUserVideoHomeBinding
import com.app.imagetovideo.utils.CommonUtils
import com.app.imagetovideo.utils.setSafeOnClickListener

class UserImageViewHolder(
    private val binding: ItemUserVideoHomeBinding,
    private var onClickItemUserImage: (Int?, ImageMadeByUser) -> Unit,
    private val onClickDelete: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        setupUIItemImgVideoHome(itemView)
    }

    fun bind(data: ImageMadeByUser?, position: Int){
        GlideHandler.setImageFormUrl(binding.imgUserVideo, data?.imageThumb)
        binding.tvName.text = data?.name
        binding.tvCreated.text = data?.createTime?.let { CommonUtils.milliSecondsToDate(it) }
        binding.root.setSafeOnClickListener {
            if (data != null) {
                onClickItemUserImage.invoke(position, data)
            }
        }
        binding.icDelete.setSafeOnClickListener {
            onClickDelete.invoke(position)
        }
    }
}