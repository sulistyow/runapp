package com.liztstudio.runtime.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.liztstudio.runtime.databinding.ItemRunBinding
import com.liztstudio.runtime.source.local.RunEntity
import com.liztstudio.runtime.utils.TrackingUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RunAdapter : RecyclerView.Adapter<RunAdapter.RunViewHolder>() {


    private val diffCallback = object : DiffUtil.ItemCallback<RunEntity>() {
        override fun areItemsTheSame(oldItem: RunEntity, newItem: RunEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RunEntity, newItem: RunEntity): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitData(mData: List<RunEntity>) = differ.submitList(mData)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunAdapter.RunViewHolder {
        return RunViewHolder(
            ItemRunBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RunAdapter.RunViewHolder, position: Int) {
        val run = differ.currentList[position]
        holder.bind(run)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class RunViewHolder(private val bind: ItemRunBinding) :
        RecyclerView.ViewHolder(bind.root) {

        fun bind(run: RunEntity) {
            Glide.with(bind.root).load(run.img).into(bind.ivRunImage)

            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.timestamp
            }
            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())

            val avgSpeed = "${run.avgSpeedInKMH}km/h"
            val distanceKm = "${run.distanceInMeters / 1000f}km"
            val caloriesBurned = "${run.caloriesBurned}kcal"

            with(bind) {
                tvDate.text = dateFormat.format(calendar.time)
                tvAvgSpeed.text = avgSpeed
                tvDistance.text = distanceKm
                tvTime.text = TrackingUtils.getFormattedTime(run.timeInMillis)
                tvCalories.text = caloriesBurned
            }
        }
    }

}