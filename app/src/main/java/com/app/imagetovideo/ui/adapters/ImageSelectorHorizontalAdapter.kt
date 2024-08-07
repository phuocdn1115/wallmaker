package com.app.imagetovideo.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.imagetovideo.R
import com.app.imagetovideo.databinding.ItemAddImageBinding
import com.app.imagetovideo.databinding.ItemImageSelectedHorizontalBinding
import com.app.imagetovideo.model.ImageSelected
import com.app.imagetovideo.utils.AnimationAppUtils.pushDownClickAnimation
import com.bumptech.glide.Glide

class ImageSelectorHorizontalAdapter(
    private var dataSet: MutableList<ImageSelected?>,
    private val onClickImageSelected: (Int, ImageSelected?) -> Unit,
    private val onClickAddImage: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    internal var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when(viewType){
            IMAGE_SELECTED ->{
                val binding = DataBindingUtil.inflate(layoutInflater,
                    R.layout.item_image_selected_horizontal, parent, false) as ItemImageSelectedHorizontalBinding
                ImageSelectedViewHolder(binding)
            }
            else ->{
                val binding = DataBindingUtil.inflate(layoutInflater,
                    R.layout.item_add_image, parent, false) as ItemAddImageBinding
                ItemAddImageViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.apply {
            when(holder){
                is ImageSelectedViewHolder ->{
                    holder.bind(dataSet[position], position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun getItemViewType(position: Int): Int {
        return if(dataSet[position]?.uriInput.isNullOrEmpty())
            ADD_IMAGE
        else IMAGE_SELECTED
    }

    inner class ItemAddImageViewHolder(binding: ItemAddImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
            init {
                binding.addImage.setOnClickListener {
                    onClickAddImage.invoke()
                }
            }
    }

    inner class ImageSelectedViewHolder(private val binding: ItemImageSelectedHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ResourceAsColor")
        fun bind(data: ImageSelected?, position: Int) {
            if(data?.uriResultCutImageInCache != null){
                Glide.with(binding.root.context)
                    .load(data.uriResultCutImageInCache)
                    .into(binding.imgImageSelected)
            }
            else {
                Glide.with(binding.root.context)
                .load(data?.uriInput)
                .into(binding.imgImageSelected)
            }

            if (data?.isSelected == true) {
                binding.cardViewImageSelected.setCardBackgroundColor(
                    binding.root.context.resources.getColor(
                        R.color.white,
                        null
                    )
                )
            } else {
                binding.cardViewImageSelected.setCardBackgroundColor(Color.TRANSPARENT)
            }
            pushDownClickAnimation(0.95F, binding.root) {
                onClickImageSelected.invoke(position, data)
            }
        }
    }

    companion object{
        const val IMAGE_SELECTED = 1
        const val ADD_IMAGE = 2
    }
}