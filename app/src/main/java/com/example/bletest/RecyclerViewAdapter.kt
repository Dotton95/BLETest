package com.example.bletest

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(private val myDataset: ArrayList<BluetoothDevice>): RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder > () {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val name :TextView = itemView.findViewById(R.id.item_name)
        private val address:TextView = itemView.findViewById(R.id.item_address)
        @SuppressLint("MissingPermission")
        fun bind(item:BluetoothDevice){
            name.text = item.name.toString()
            address.text =item.address.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int):RecyclerViewAdapter.MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item, parent,false))
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(myDataset[position])
    }
    override fun getItemCount() = myDataset.size
}