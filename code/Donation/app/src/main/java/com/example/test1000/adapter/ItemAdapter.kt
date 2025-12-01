package com.example.test1000.adapter

import com.example.test1000.SocietyItemClickListener
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test1000.R
import com.example.test1000.R.layout.list_societies
import com.example.test1000.model.societies
import com.bumptech.glide.Glide

/**
 * Adapter for the [RecyclerView] in [Home]. Displays [societies] data object.
 */
class ItemAdapter(
    private val context: Context,
    private val dataset: List<societies>,
    private val itemClickListener: SocietyItemClickListener

) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just an Affirmation object.
    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.item_title)
        val text2 :TextView = view.findViewById(R.id.item_mail)
        val text3 : TextView = view.findViewById(R.id.goal)
        val imageView: ImageView = view.findViewById(R.id.item_image)
    }

    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(list_societies, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.textView.text = item.name
        holder.text2.text = item.email
        holder.text3.text = "Le But de cette société est de collecter " + item.goalsociety.toString() + " DH"
        Glide.with(holder.itemView.context)
            .load(item.image)
            .into(holder.imageView)
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(item)
        }
    }


    /**
     * Return the size of your dataset (invoked by the layout manager)
     */
    override fun getItemCount() = dataset.size

}