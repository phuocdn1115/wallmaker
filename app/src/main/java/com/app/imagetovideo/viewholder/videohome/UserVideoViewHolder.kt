package com.app.imagetovideo.viewholder.videohome

import androidx.recyclerview.widget.RecyclerView
import com.app.imagetovideo.base.handler.GlideHandler
import com.app.imagetovideo.data.model.VideoMadeByUser
import com.app.imagetovideo.databinding.ItemUserVideoHomeBinding
import com.app.imagetovideo.utils.CommonUtils
import com.app.imagetovideo.utils.setSafeOnClickListener

class UserVideoViewHolder(
    private val binding: ItemUserVideoHomeBinding,
    private var onClickItemUserVideo: (Int?, VideoMadeByUser) -> Unit,
    private val onClickDelete: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        setupUIItemImgVideoHome(itemView)
    }

    fun bind(data: VideoMadeByUser?, position: Int){
        GlideHandler.setImageFormUrl(binding.imgUserVideo, data?.imageThumb)
        binding.tvName.text = data?.name
        binding.tvCreated.text = data?.createTime?.let { CommonUtils.milliSecondsToDate(it) }
        binding.root.setSafeOnClickListener {
            if (data != null) {
                onClickItemUserVideo.invoke(position, data)
            }
        }
        binding.icDelete.setSafeOnClickListener {
            onClickDelete.invoke(position)
        }
    }
}