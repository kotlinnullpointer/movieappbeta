package com.nagarro.poptvworkshop.ui.main.component.carousal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nagarro.poptvworkshop.databinding.RowCarousalPortraitBinding
import com.nagarro.poptvworkshop.databinding.RowCarousalRoundBinding
import com.nagarro.poptvworkshop.databinding.RowCarousalTallBinding
import com.nagarro.poptvworkshop.databinding.RowCarousalWideBinding
import com.nagarro.poptvworkshop.domain.CarousalItem
import com.nagarro.poptvworkshop.domain.CastItem
import com.nagarro.poptvworkshop.domain.MediaItem

/**
 * Diff Util Based implementation for the Recycler Adapter.
 */
class MediaCarousalAdapter(private val carousalType: CarousalType, private val onMediaClickListener: OnMediaClickListener) :
    ListAdapter<CarousalItem, MediaCarousalAdapter.BaseViewHolder>(DiffCallBack) {

    companion object DiffCallBack : DiffUtil.ItemCallback<CarousalItem>() {
        override fun areItemsTheSame(oldItem: CarousalItem, newItem: CarousalItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CarousalItem, newItem: CarousalItem): Boolean {
            return if (oldItem is MediaItem && newItem is MediaItem) {
                oldItem.id == newItem.id
            } else {
                (oldItem as CastItem).id == (newItem as CastItem).id
            }

        }

    }

    open class BaseViewHolder(binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        open fun bind(item: CarousalItem) {

        }
    }

    /*View holder Type 1*/
    class MediaWideViewHolder(private var binding: RowCarousalWideBinding) :
        BaseViewHolder(binding) {

        override fun bind(item: CarousalItem) {
            binding.item = item as MediaItem
            binding.executePendingBindings()
        }

        /*Returns a Viewholder for this View*/
        companion object {
            fun from(viewGroup: ViewGroup): MediaWideViewHolder {
                val inflater = LayoutInflater.from(viewGroup.context)
                val binding = RowCarousalWideBinding.inflate(inflater)
                return MediaWideViewHolder(binding)
            }
        }
    }

    /*View holder Type 2*/
    class MediaTallViewHolder(private var binding: RowCarousalTallBinding) :
        BaseViewHolder(binding) {

        override fun bind(item: CarousalItem) {
            binding.item = item as MediaItem
            binding.executePendingBindings()
        }

        /*Returns a Viewholder for this View*/
        companion object {
            fun from(viewGroup: ViewGroup): MediaTallViewHolder {
                val inflater = LayoutInflater.from(viewGroup.context)
                val binding = RowCarousalTallBinding.inflate(inflater)
                return MediaTallViewHolder(binding)
            }
        }
    }

    /*View holder Type 3*/
    class MediaPortraitViewHolder(private var binding: RowCarousalPortraitBinding) :
        BaseViewHolder(binding) {

        override fun bind(item: CarousalItem) {
            binding.item = item as MediaItem
            binding.executePendingBindings()
        }

        /*Returns a Viewholder for this View*/
        companion object {
            fun from(viewGroup: ViewGroup): MediaPortraitViewHolder {
                val inflater = LayoutInflater.from(viewGroup.context)
                val binding = RowCarousalPortraitBinding.inflate(inflater)
                return MediaPortraitViewHolder(binding)
            }
        }
    }

    /*View holder Type 4*/
    class MediaRoundedViewHolder(private var binding: RowCarousalRoundBinding) :
        BaseViewHolder(binding) {

        override fun bind(item: CarousalItem) {
            binding.item = item as CastItem
            binding.executePendingBindings()
        }

        companion object {
            fun from(viewGroup: ViewGroup): MediaRoundedViewHolder {
                val inflater = LayoutInflater.from(viewGroup.context)
                val binding = RowCarousalRoundBinding.inflate(inflater)
                return MediaRoundedViewHolder(binding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return carousalType.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            CarousalType.WIDE.ordinal -> {
                MediaWideViewHolder.from(parent)
            }
            CarousalType.TALL.ordinal -> {
                MediaTallViewHolder.from(parent)
            }
            CarousalType.PORTRAIT.ordinal -> {
                MediaPortraitViewHolder.from(parent)
            }
            else -> {
                MediaRoundedViewHolder.from(parent)
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener { onMediaClickListener.onMediaClicked(item) }
    }

}

class OnMediaClickListener(val clickListener: (item: CarousalItem) -> Unit) {
    fun onMediaClicked(mediaItem: CarousalItem) = clickListener(mediaItem)
}