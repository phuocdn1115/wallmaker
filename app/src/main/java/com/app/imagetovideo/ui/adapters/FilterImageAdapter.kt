package com.app.imagetovideo.ui.adapters

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.app.imagetovideo.R
import com.app.imagetovideo.base.BaseRecyclerAdapter
import com.app.imagetovideo.data.model.ImageFilter
import com.app.imagetovideo.databinding.LayoutItemTemplateBinding
import com.app.imagetovideo.utils.setSafeOnClickListener
import com.app.imagetovideo.viewholder.ViewHolderLifecycle

class FilterImageAdapter(
    private val dataSet: MutableList<ImageFilter?>,
    private val onClickFilter: (ImageFilter?, Int) -> Unit
) : BaseRecyclerAdapter<ImageFilter, FilterImageAdapter.FilterImageViewHolder>(dataSet) {

    override fun getLayoutResourceItem(): Int {
        return R.layout.layout_item_template
    }

    override fun setViewHolderLifeCircle(): ViewHolderLifecycle? {
        return null
    }

    override fun onCreateBasicViewHolder(binding: ViewDataBinding?): FilterImageViewHolder {
        return FilterImageViewHolder(
            binding as LayoutItemTemplateBinding,
            onClickFilter = onClickFilter
        )
    }

    override fun onBindBasicItemView(holder: FilterImageViewHolder, position: Int) {
        holder.bind(data = dataSet[position], position)
    }


    class FilterImageViewHolder(
        private val binding: LayoutItemTemplateBinding,
        private val onClickFilter: (ImageFilter?, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ImageFilter?, position: Int) {
            if (data?.isSelected == true) {
                binding.viewTemplateSelected.visibility = View.VISIBLE
            } else {
                binding.viewTemplateSelected.visibility = View.GONE
            }
            binding.imgImageSelected.setImageBitmap(data?.filterPreview)
            binding.tvName.text = data?.name
            binding.cardViewImageSelected.setSafeOnClickListener {
                onClickFilter.invoke(data, position)
            }
        }
    }

}