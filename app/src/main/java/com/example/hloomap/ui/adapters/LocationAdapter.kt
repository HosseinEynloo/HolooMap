package com.example.hloomap.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hloomap.data.models.LocationEntity
import com.example.hloomap.databinding.RowRecyclerviewBinding
import javax.inject.Inject

class LocationAdapter @Inject constructor() : RecyclerView.Adapter<LocationAdapter.ViewHolder>() {

    private lateinit var binding: RowRecyclerviewBinding
    private lateinit var context: Context
    private var locationList = emptyList<LocationEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = RowRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //getItem from PagingDataAdapter
        holder.bind(locationList[position])
        //Not duplicate items
        holder.setIsRecyclable(false)
    }

    override fun getItemCount() = locationList.size

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: LocationEntity) {
            binding.apply {
                titleLocation.text = item.title
                txtLatitude.text=item.latitude.toString()
                txtLongitude.text = item.longitude.toString()
                txtDes.text = item.description
            }
        }
    }

    private var onItemClickListener: ((LocationEntity, String) -> Unit)? = null

    fun setOnItemClickListener(listener: (LocationEntity, String) -> Unit) {
        onItemClickListener = listener
    }

    fun setData(data: List<LocationEntity>) {
        val moviesDiffUtil = LocationsDiffUtils(locationList, data)
        val diffUtils = DiffUtil.calculateDiff(moviesDiffUtil)
        locationList = data
        diffUtils.dispatchUpdatesTo(this)
    }

    class LocationsDiffUtils(private val oldItem: List<LocationEntity>, private val newItem: List<LocationEntity>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldItem.size
        }

        override fun getNewListSize(): Int {
            return newItem.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem[oldItemPosition] === newItem[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem[oldItemPosition] === newItem[newItemPosition]
        }
    }
}