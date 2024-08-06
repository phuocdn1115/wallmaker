package com.app.photomaker.ui.adapters

import android.content.Context
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.app.photomaker.R
import com.app.photomaker.base.BaseRecyclerAdapter
import com.app.photomaker.databinding.ItemImagePickerViewHolderBinding
import com.app.photomaker.utils.ToastUtil
import com.app.photomaker.utils.setSafeOnClickListener
import com.app.photomaker.viewholder.ViewHolderLifecycle
import com.app.photomaker.viewholder.setupUIItemImageFromGallery
import com.bumptech.glide.Glide

class ImagePickerAdapter(
    dataSet: MutableList<String?>,
    private val onSelectedImageListener: (Int, String?) -> Unit
) : BaseRecyclerAdapter<String, ImagePickerAdapter.ImagePickerViewHolder>(dataSet) {
    internal val selectedImageList: MutableList<String?> = mutableListOf()
    internal var context: Context? = null

    override fun getLayoutResourceItem(): Int {
        return R.layout.item_image_picker_view_holder
    }

    override fun setViewHolderLifeCircle(): ViewHolderLifecycle? {
        return null
    }

    override fun onCreateBasicViewHolder(binding: ViewDataBinding?): ImagePickerViewHolder {
        context = binding?.root?.context
        return ImagePickerViewHolder(binding as ItemImagePickerViewHolderBinding)
    }

    override fun onBindBasicItemView(holder: ImagePickerViewHolder, position: Int) {
        holder.bind(data = getDataSet()?.get(position), position)
    }

    fun toggleImageSelect(image: String?,position: Int?= null){
        var positionHasChanged = position
        if(position == null){
            positionHasChanged = getDataSet()?.indexOfFirst { item -> item == image}
        }
        if (positionHasChanged != null) {
            if (selectedImageList.contains(image)) {
                removeSelectedImage(image, positionHasChanged)
                onSelectedImageListener.invoke(positionHasChanged, image)
            } else
                addSelectedImage(image, positionHasChanged)
        }
    }

    private fun removeSelectedImage(image: String?, position: Int){
        selectedImageList.remove(image)
        refreshSelectedView(position)
    }

    private fun addSelectedImage(image: String?, position: Int){
        if(selectedImageList.size == 5){
            ToastUtil.showToast("Bạn chỉ có thể chọn tối đa 5 ảnh", context)
        }else{
            onSelectedImageListener.invoke(position, image)
            selectedImageList.add(image)
            refreshSelectedView(position)
        }
    }

    private fun refreshSelectedView(position: Int){
        notifyItemChanged(position)
    }

    inner class ImagePickerViewHolder(private val binding : ItemImagePickerViewHolderBinding) : RecyclerView.ViewHolder(binding.root){
        init {
            setupUIItemImageFromGallery(itemView)
        }
        fun bind(data: String?,position: Int){
            if(selectedImageList.contains(data))
                binding.icSelectedImagePicker.setImageResource(R.drawable.ic_selected_image_picker)
            else binding.icSelectedImagePicker.setImageResource(R.drawable.ic_normal_image_picker)
            Glide.with(binding.root.context)
                .load(data)
                .into(binding.imgPicker)
            binding.root.setSafeOnClickListener {
                toggleImageSelect(data, position)
            }
        }
    }

}
