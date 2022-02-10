package com.example.polyenergy.ui.favorite

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.polyenergy.R
import com.example.polyenergy.domain.AddressInfo
import com.example.polyenergy.domain.ChargeInfo

class ChargeViewHolder(private val view: View, private val onDeleteClickListener: OnDeleteClickListener, private val onClickListener: OnClickListener): RecyclerView.ViewHolder(view) {


    private val name: TextView = view.findViewById(R.id.text_title)
    private val address: TextView = view.findViewById(R.id.text_address)
    private val button: ImageButton = view.findViewById(R.id.favorite_button)

    fun bind(item: ChargeInfo) {
        name.text = item.addressInfo.title
        address.text = item.addressInfo.addressLine1
        button.setOnClickListener {
            item.let { it1 -> onDeleteClickListener(it1) }
        }
        view.setOnClickListener {
            item.let { it1 -> onClickListener(it1) }
        }
    }
}