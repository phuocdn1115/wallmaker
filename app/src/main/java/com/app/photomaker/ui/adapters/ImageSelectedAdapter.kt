package com.app.photomaker.ui.adapters

import android.content.Context
import android.view.View
import androidx.databinding.ViewDataBinding
import com.app.photomaker.R
import com.app.photomaker.base.BaseRecyclerAdapter
import com.app.photomaker.databinding.ItemImageSelectedViewHolderBinding
import com.app.photomaker.viewholder.ImageSelectedViewHolder
import com.app.photomaker.viewholder.ViewHolderLifecycle

class ImageSelectedAdapter(
    dataSet: MutableList<String?>,
    private val onRemoveImageListener: (Int, String?) -> Unit
) : BaseRecyclerAdapter<String, ImageSelectedViewHolder>(dataSet) {

    internal var context: Context? = null

    override fun getLayoutResourceItem(): Int {
        return R.layout.item_image_selected_view_holder
    }

    override fun setViewHolderLifeCircle(): ViewHolderLifecycle? {
        return null
    }

    override fun onCreateBasicViewHolder(binding: ViewDataBinding?): ImageSelectedViewHolder {
        context = binding?.root?.context
        return ImageSelectedViewHolder(
            binding as ItemImageSelectedViewHolderBinding,
            onRemoveImageListener
        )
    }

    override fun onBindBasicItemView(holder: ImageSelectedViewHolder, position: Int) {
        holder.bind(data = getDataSet()?.get(position), position)
    }

    fun removeImageSelected(image: String?, position: Int){
        notifyItemRemoved(position)
    }


    fun onScaleZoomOut(itemView: View){
        itemView.animate().scaleX(1.15f).scaleY(1.15f).setDuration(200).start()
    }

    fun onScaleZoomIn(itemView: View){
        itemView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
    }
}