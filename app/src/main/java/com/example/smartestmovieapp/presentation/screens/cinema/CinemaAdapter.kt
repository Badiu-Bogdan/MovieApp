package com.example.smartestmovieapp.presentation.screens.cinema

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smartestmovieapp.R
import com.example.smartestmovieapp.data.local.model.cinema.CinemaEntity

class CinemaAdapter(
    var cinemaList: ArrayList<CinemaEntity>,
    val cinemaCardListener: onCinemaCardClicked,
    val seeInMapListener: onCinemaCardClicked?,
    val layoutId: Int
) :
    RecyclerView.Adapter<CinemaAdapter.ViewHolder>() {

    fun updateList(newCinemaList: List<CinemaEntity>) {
        this.cinemaList = newCinemaList as ArrayList<CinemaEntity>
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CinemaAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CinemaAdapter.ViewHolder, position: Int) {
        holder.bind(cinemaList.get(position))
    }

    override fun getItemCount(): Int = cinemaList.size


    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var image: ImageView
        private lateinit var name: TextView
        private var address: TextView? = null
        private var see_in_map: View? = null

        fun bind(cinema: CinemaEntity) {
            image = view.findViewById(R.id.image)
            name = view.findViewById(R.id.title)
            address = view.findViewById(R.id.address)
            see_in_map = view.findViewById(R.id.see_in_map)

            Glide.with(view)
                .load(cinema.imageUrl)
                .placeholder(R.drawable.default_movie_image)
                .into(image)
            name.text = cinema.name

            address?.text = cinema.address
            see_in_map?.setOnClickListener { seeInMapListener?.onClick(cinema) }

            view.setOnClickListener { cinemaCardListener.onClick(cinema) }
        }
    }

    interface onCinemaCardClicked {
        fun onClick(cinema: CinemaEntity)
    }


}
