package com.example.test1000.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.test1000.databinding.ItemFavoriteSocietyBinding
import com.example.test1000.model.FavoriteSociety

class FavoriteSocietyAdapter(private val societies: List<FavoriteSociety>) :
    RecyclerView.Adapter<FavoriteSocietyAdapter.FavoriteSocietyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteSocietyViewHolder {
        val binding = ItemFavoriteSocietyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteSocietyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteSocietyViewHolder, position: Int) {
        holder.bind(societies[position])
    }

    override fun getItemCount(): Int = societies.size

    inner class FavoriteSocietyViewHolder(private val binding: ItemFavoriteSocietyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(society: FavoriteSociety) {
            binding.nom.text = society.name
            binding.description.text = society.desc
            Glide.with(binding.root.context)
                .load(society.image)
                .into(binding.image)
        }
    }
}
