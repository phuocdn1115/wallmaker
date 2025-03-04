package com.app.photomaker.ui.adapters

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.app.photomaker.R
import com.app.photomaker.base.BaseRecyclerAdapter
import com.app.photomaker.base.handler.GlideHandler
import com.app.photomaker.data.model.TemplateVideo
import com.app.photomaker.databinding.LayoutItemTemplateBinding
import com.app.photomaker.utils.setSafeOnClickListener
import com.app.photomaker.viewholder.ViewHolderLifecycle

class TemplateVideoAdapter(
    dataSet: MutableList<TemplateVideo?>,
    private val onClickItemTemplate: (Int) -> Unit
) : BaseRecyclerAdapter<TemplateVideo, TemplateVideoAdapter.TemplateViewHolder>(dataSet) {

    override fun getLayoutResourceItem(): Int {
        return R.layout.layout_item_template
    }

    override fun setViewHolderLifeCircle(): ViewHolderLifecycle? {
        return null
    }

    override fun onCreateBasicViewHolder(binding: ViewDataBinding?): TemplateViewHolder {
        return TemplateViewHolder(binding as LayoutItemTemplateBinding, onClickItemTemplate)
    }

    override fun onBindBasicItemView(holder: TemplateViewHolder, position: Int) {
        holder.bind(position, getDataSet()?.get(position))
    }

    inner class TemplateViewHolder(
        private val binding: LayoutItemTemplateBinding,
        private val onClickItemTemplate: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, data: TemplateVideo?) {
            GlideHandler.setImageFormDrawableResource(binding.imgImageSelected, data?.image)
            if (data?.isSelected == true) {
                binding.viewTemplateSelected.visibility = View.VISIBLE
            } else {
                binding.viewTemplateSelected.visibility = View.GONE
            }
            binding.cardViewImageSelected.setSafeOnClickListener {
                onClickItemTemplate.invoke(position)
            }
        }
    }
}