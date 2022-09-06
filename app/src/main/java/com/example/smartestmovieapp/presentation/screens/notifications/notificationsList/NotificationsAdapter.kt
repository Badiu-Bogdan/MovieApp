package com.example.smartestmovieapp.presentation.screens.notifications.notificationsList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smartestmovieapp.R

class NotificationsAdapter(
    val notificationList: List<Notification>,
    val layoutId: Int,
    val internetConnection: Boolean
) : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notificationList[position])
    }

    override fun getItemCount() = notificationList.size


    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var title: TextView
        private lateinit var description: TextView
        private lateinit var image: ImageView
        private lateinit var dots: TextView
        private lateinit var spinner: Spinner

        fun bind(notificationItem: Notification) {
            title = view.findViewById(R.id.textViewNotificationTitle)
            description = view.findViewById(R.id.textViewNotificationDescription)
            image = view.findViewById(R.id.notificationPhoto)
            dots = view.findViewById(R.id.textViewDots)

            title.text = notificationItem.title
            description.text = notificationItem.description
            Glide
                .with(view)
                .load(notificationItem.imageUrl)
                .placeholder(R.drawable.default_movie_image)
                .into(image)

            dots.setOnClickListener(object : View.OnClickListener{
                override fun onClick(p0: View?) {
                    val popupMenu = PopupMenu(p0?.context, p0)
                    popupMenu.inflate(R.menu.popup_menu)
                    popupMenu.show()
                }

            })


        }
    }
}