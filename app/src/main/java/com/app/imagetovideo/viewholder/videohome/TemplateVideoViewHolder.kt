package com.app.imagetovideo.viewholder.videohome

import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.app.imagetovideo.aplication.ApplicationContext
import com.app.imagetovideo.base.handler.GlideHandler
import com.app.imagetovideo.data.model.Template
import com.app.imagetovideo.databinding.ItemTemplateVideoViewHolderBinding
import com.app.imagetovideo.utils.CommonUtils
import com.app.imagetovideo.utils.CommonUtils.getColor
import com.app.imagetovideo.utils.setSafeOnClickListener

class TemplateVideoViewHolder(
    private val binding: ItemTemplateVideoViewHolderBinding,
    private var onClickTemplate: (Int?, Template) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {
    init {
//        setupUIItemTemplateHome(itemView)
        binding.viewParent.setCardBackgroundColor(CommonUtils.randomColor())
    }

    fun bind(data : Template, position: Int) {
        binding.tvNameTemplate.text = data.name
        binding.viewParent.setCardBackgroundColor(
            data.colorCode?.getColor() ?: CommonUtils.randomColor()
        )
        binding.viewParent.post {
            GlideHandler.setImageFormDrawableResource(
                binding.imgVideoTemplate,
                data.colorCode?.getColor() ?: CommonUtils.randomColor()
            )
            binding.root.setSafeOnClickListener {
                onClickTemplate.invoke(position, data)
            }
        }
    }
}