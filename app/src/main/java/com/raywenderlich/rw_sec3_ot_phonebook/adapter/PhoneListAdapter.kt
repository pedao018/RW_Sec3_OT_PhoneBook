package com.raywenderlich.rw_sec3_ot_phonebook.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.rw_sec3_ot_phonebook.databinding.PhoneItemLayoutBinding
import com.raywenderlich.rw_sec3_ot_phonebook.viewmodel.MainViewModel

class PhoneListAdapter(
    private var listData: List<MainViewModel.PhoneView>?,
    private val context: Context,
    private var onItemClick: ((itemObj: MainViewModel.PhoneView) -> Unit)
) : RecyclerView.Adapter<PhoneListAdapter.ViewHolder>() {

    fun setListData(listData: List<MainViewModel.PhoneView>) {
        this.listData = listData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = PhoneItemLayoutBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding, context, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        listData?.let { list ->
            val itemObj = list[position]
            holder.binding.root.tag = itemObj
            holder.binding.phoneViewData = itemObj
            //holder.binding.bookmarkIcon.setImageResource(R.drawable.ic_other)
            val bitmap = itemObj.getImage(context)
            bitmap?.let {
                holder.binding.phoneImg.setImageBitmap(it)
            }
        }
    }

    override fun getItemCount(): Int = listData?.size ?: 0

    class ViewHolder(
        val binding: PhoneItemLayoutBinding,
        private val context: Context,
        private var onItemClick: ((itemObj: MainViewModel.PhoneView) -> Unit)
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val itemObj = itemView.tag as MainViewModel.PhoneView
                //Toast.makeText(context, "Click Item " + itemObj.info, Toast.LENGTH_SHORT).show()
                onItemClick.invoke(itemObj)
            }
        }
    }
}