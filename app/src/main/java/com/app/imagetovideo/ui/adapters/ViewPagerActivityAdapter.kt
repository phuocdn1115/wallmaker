package com.app.imagetovideo.ui.adapters

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerActivityAdapter (
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    private val myListFragment: MutableList<Fragment> = ArrayList()
    private val itemIds: MutableList<Long> = ArrayList()

    override fun getItemId(position: Int): Long {
        return myListFragment[position].hashCode().toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return itemIds.contains(itemId)
    }

    override fun getItemCount(): Int {
        return myListFragment.size
    }

    override fun createFragment(position: Int): Fragment {
        return myListFragment[position]
    }

    fun addFragment(fragment: Fragment) {
        myListFragment.add(fragment)
        itemIds.add(fragment.hashCode().toLong())
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeFragmentByPosition(position: Int) {
        myListFragment.removeAt(position)
        itemIds.removeAt(position)
        notifyItemRemoved(position)
        notifyItemChanged(position, itemCount)
    }

    fun clearAllData(){
        myListFragment.clear()
    }

    fun getFragmentByPosition(position: Int) = myListFragment[position]
}


