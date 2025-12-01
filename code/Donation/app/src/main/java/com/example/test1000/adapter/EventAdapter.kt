package com.example.test1000.adapter

import com.example.test1000.EventClickListener
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.test1000.R
import com.example.test1000.model.Events
import com.example.test1000.R.layout.list_events

/**
 * Adaptateur pour le [RecyclerView] dans [Home]. Affiche l'objet de données [Events].
 */
class EventAdapter(
    private val context: Context,
    private val dataset: List<Events>,
    private val itemClickListener: EventClickListener

) : RecyclerView.Adapter<EventAdapter.ItemViewHolder>() {

    // Fournit une référence aux vues pour chaque élément de données
    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.even_name)
        val date : TextView = view.findViewById(R.id.date)
    }

    /**
     * Crée de nouvelles vues (invoqué par le gestionnaire de mise en page)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // crée une nouvelle vue
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(list_events, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    /**
     * Remplace le contenu d'une vue (invoqué par le gestionnaire de mise en page)
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.name.text = item.name
        holder.date.text = item.date
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(item)
        }
    }

    /**
     * Retourne la taille de votre ensemble de données (invoqué par le gestionnaire de mise en page)
     */
    override fun getItemCount() = dataset.size
}
