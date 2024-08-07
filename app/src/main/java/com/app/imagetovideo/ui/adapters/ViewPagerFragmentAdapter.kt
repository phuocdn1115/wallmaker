package com.app.imagetovideo.ui.adapters

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerFragmentAdapter (
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    private val fragmentList: MutableList<Fragment> = ArrayList()
    private val itemIds: MutableList<Long> = ArrayList()

    override fun getItemId(position: Int): Long {
        return fragmentList[position].hashCode().toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return itemIds.contains(itemId)
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
        itemIds.add(fragment.hashCode().toLong())
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeFragmentByPosition(position: Int) {
        fragmentList.removeAt(position)
        itemIds.removeAt(position)
        notifyItemRemoved(position)
        notifyItemChanged(position, itemCount)
    }

    fun clearAllData(){
        fragmentList.clear()
    }

    fun getFragmentByPosition(position: Int) = fragmentList[position]
}

