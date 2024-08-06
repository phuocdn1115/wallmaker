package com.app.photomaker.viewholder.videohome

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.app.photomaker.aplication.ApplicationContext
import com.app.photomaker.base.handler.GlideHandler
import com.app.photomaker.data.model.Template
import com.app.photomaker.databinding.ItemTemplateVideoViewHolderBinding
import com.app.photomaker.utils.CommonUtils
import com.app.photomaker.utils.CommonUtils.getColor
import com.app.photomaker.utils.setSafeOnClickListener

class TemplateVideoViewHolder(
    private val binding: ItemTemplateVideoViewHolderBinding,
    private var onClickTemplate: (Int?, Template) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {
    init {
        setupUIItemImgVideoHome(itemView)
        binding.viewParent.setCardBackgroundColor(CommonUtils.randomColor())
    }

    fun bind(data : Template, position: Int){
        binding.tvNameTemplate.text = data.name
        binding.viewParent.setCardBackgroundColor(data.colorCode?.getColor() ?: CommonUtils.randomColor())
        binding.viewParent.post{
            GlideHandler.setImageFormUrlWithCallBack(binding.imgVideoTemplate, "${ApplicationContext.getNetworkContext().videoURL}${data.thumbUrlImageString()}"){
                binding.imgVideoTemplate.scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }
        binding.root.setSafeOnClickListener {
            onClickTemplate.invoke(position, data)
        }
    }

}