package com.example.test1000.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.test1000.R
import com.example.test1000.UserClickListener
import com.example.test1000.model.Events
import com.example.test1000.model.Users

/**
 * Adaptateur pour le [RecyclerView] dans [Home]. Affiche l'objet de données [Users].
 */

class UserAdapter (
    private val context: Context,
    private val dataset: List<Users>,
    private val itemClickListener: UserClickListener

    ) : RecyclerView.Adapter<UserAdapter.ItemViewHolder>() {

        // Fournit une référence aux vues pour chaque élément de données
        class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            val username: TextView = view.findViewById(R.id.username)
            val phone : TextView = view.findViewById(R.id.phone)
            val image : ImageView = view.findViewById(R.id.image)
            val email : TextView = view.findViewById(R.id.mail)
        }

        /**
         * Crée de nouvelles vues (invoqué par le gestionnaire de mise en page)
         */
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val adapterLayout = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_user, parent, false)
            return ItemViewHolder(adapterLayout)
        }
        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val item = dataset[position]
            holder.username.text = item.username
            holder.phone.text = item.phone
            holder.email.text= item.email
            Glide.with(holder.itemView.context)
                .load(item.image)
                .error(R.drawable.ic_user_placeholder) // Placeholder image for errors
                .into(holder.image)

            holder.itemView.setOnClickListener {
                itemClickListener.onItemClick(item)
            }
        }

        /**
         * Retourne la taille de votre ensemble de données (invoqué par le gestionnaire de mise en page)
         */
        override fun getItemCount() = dataset.size
}
