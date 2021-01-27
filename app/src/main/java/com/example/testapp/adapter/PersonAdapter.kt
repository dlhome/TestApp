package com.example.testapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testapp.R
import com.example.testapp.databinding.ItemPersonLayoutBinding
import com.example.testapp.model.PersonData
import com.example.testapp.ui.dateToStr

class PersonAdapter(private val clickListener: ItemClickListener, private val data: List<PersonData>) :
    RecyclerView.Adapter<PersonAdapter.ViewHolder>() {
        private var dataSet = data

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemPersonLayoutBinding.bind(view)
        fun bind(data: PersonData) {
            with(binding) {
                Glide.with(photo).load(data.image).centerInside().into(photo)
                val st = data.weight.toString() + if (data.isKgUnits) "kg" else "lb"
                weightValue.text = st
                dobValue.text = dateToStr( data.dob)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_person_layout, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(dataSet[position])
        viewHolder.binding.editText.setOnClickListener { clickListener.onItemClickListener(position) }
    }

    override fun getItemCount() = dataSet.size

    fun update(list: List<PersonData>){
        dataSet = list
    }
}

interface ItemClickListener {
    fun onItemClickListener(pos : Int)
}