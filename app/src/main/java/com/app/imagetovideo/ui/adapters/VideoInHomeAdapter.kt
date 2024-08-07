package com.app.imagetovideo.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.imagetovideo.R
import com.app.imagetovideo.ads.nativeads.NativeAdsInHomeManager
import com.app.imagetovideo.data.model.ImageMadeByUser
import com.app.imagetovideo.data.model.Template
import com.app.imagetovideo.data.model.VideoMadeByUser
import com.app.imagetovideo.databinding.ItemNativeAdsVideoHomeBinding
import com.app.imagetovideo.databinding.ItemNewVideoHomeBinding
import com.app.imagetovideo.databinding.ItemTemplateVideoViewHolderBinding
import com.app.imagetovideo.databinding.ItemUserVideoHomeBinding
import com.app.imagetovideo.enums.VideoHomeType
import com.app.imagetovideo.model.Data
import com.app.imagetovideo.model.NativeAds
import com.app.imagetovideo.viewholder.videohome.*

class VideoInHomeAdapter(
    private var dataList: List<Data>,
    private var mContext : Context,
    private var onClickItemNewVideo: () -> Unit,
    private var onClickItemUserVideo: (Int?, VideoMadeByUser) -> Unit,
    private var onClickItemUserImage: (Int?, ImageMadeByUser) -> Unit,
    private var onClickTemplate: (Int?, Template) -> Unit,
    private val onClickDelete: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var nativeAdsInHomeManager: NativeAdsInHomeManager

    fun setNativeAdsManager(nativeAdsInHomeManager: NativeAdsInHomeManager) {
        this.nativeAdsInHomeManager = nativeAdsInHomeManager
    }

    override fun getItemViewType(position: Int): Int {
        return when (dataList[position]) {
            is VideoMadeByUser -> {
                VideoHomeType.USER_VIDEO.viewType
            }
            is ImageMadeByUser -> {
                VideoHomeType.IMAGE_USER.viewType
            }
            is Template ->{
                VideoHomeType.TEMPLATE.viewType
            }
            is NativeAds -> {
                VideoHomeType.NATIVE_ADS.viewType
            }
            else -> VideoHomeType.NEW_VIDEO.viewType
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        return when (viewType) {
            VideoHomeType.USER_VIDEO.viewType -> {
                val binding = DataBindingUtil.inflate(
                    inflate,
                    R.layout.item_user_video_home,
                    parent,
                    false
                ) as ItemUserVideoHomeBinding
                return UserVideoViewHolder(binding, onClickItemUserVideo, onClickDelete)
            }
            VideoHomeType.IMAGE_USER.viewType -> {
                val binding = DataBindingUtil.inflate(
                    inflate,
                    R.layout.item_user_video_home,
                    parent,
                    false
                ) as ItemUserVideoHomeBinding
                return UserImageViewHolder(binding, onClickItemUserImage, onClickDelete)
            }
            VideoHomeType.NATIVE_ADS.viewType -> {
                val binding = DataBindingUtil.inflate(
                    inflate,
                    R.layout.item_native_ads_video_home,
                    parent,
                    false
                ) as ItemNativeAdsVideoHomeBinding
                return NativeAdsViewHolder(binding,mContext)
            }
            VideoHomeType.TEMPLATE.viewType ->{
                val binding = DataBindingUtil.inflate(
                    inflate,
                    R.layout.item_template_video_view_holder,
                    parent,
                    false
                ) as ItemTemplateVideoViewHolderBinding
                return TemplateVideoViewHolder(binding, onClickTemplate)
            }
            else -> {
                val binding = DataBindingUtil.inflate(
                    inflate,
                    R.layout.item_new_video_home,
                    parent,
                    false
                ) as ItemNewVideoHomeBinding
                return NewVideoViewHolder(binding,onClickItemNewVideo)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.apply {
            when (holder) {
                is UserVideoViewHolder -> {
                    when (val dataInPosition = dataList[position]) {
                        is VideoMadeByUser -> {
                            holder.bind(dataInPosition, position)
                        }
                    }
                }
                is UserImageViewHolder -> {
                    when (val dataInPosition = dataList[position]) {
                        is ImageMadeByUser -> {
                            holder.bind(dataInPosition, position)
                        }
                    }
                }
                is NativeAdsViewHolder -> {
                    when (dataList[position]) {
                        is NativeAds -> {
                            nativeAdsInHomeManager.getNativeAd()?.nativeAd?.let { holder.bind(it) }
                        }
                    }
                }
                is TemplateVideoViewHolder ->{
                    val dataInPosition = dataList[position]
                    if(dataInPosition is Template){
                        holder.bind(dataInPosition, position)
                    }
                }
                is NewVideoViewHolder -> {}
                else -> {}
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}