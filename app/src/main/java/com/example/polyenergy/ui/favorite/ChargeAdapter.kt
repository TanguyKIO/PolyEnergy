package com.example.polyenergy.ui.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.polyenergy.R
import com.example.polyenergy.domain.AddressInfo
import com.example.polyenergy.domain.ChargeInfo

class ChargeAdapter(
    var items: List<ChargeInfo>,
    private val onDeleteClickListener: OnDeleteClickListener,
    private val onClickListener: OnClickListener
) : ListAdapter<AddressInfo, ChargeViewHolder>(ItemDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChargeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_fav, parent, false)
        return ChargeViewHolder(view, onDeleteClickListener, onClickListener)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ChargeViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }
}

class ItemDiffUtil : DiffUtil.ItemCallback<AddressInfo>() {
    override fun areItemsTheSame(oldItem: AddressInfo, newItem: AddressInfo) = false

    override fun areContentsTheSame(oldItem: AddressInfo, newItem: AddressInfo) = false
}

typealias OnDeleteClickListener = (ChargeInfo) -> Unit
typealias OnClickListener = (ChargeInfo) -> Unit