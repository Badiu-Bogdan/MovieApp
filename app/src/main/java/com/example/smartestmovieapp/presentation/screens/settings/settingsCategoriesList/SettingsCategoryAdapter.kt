package com.example.smartestmovieapp.presentation.screens.settings.settingsCategoriesList

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.smartestmovieapp.R

class SettingsCategoryAdapter(
    private val categoryList: List<SettingsCategory>,
    private val layoutId: Int,
    private val prefs: SharedPreferences
) : RecyclerView.Adapter<SettingsCategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categoryList[position])

    }

    override fun getItemCount() = categoryList.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var image: ImageView
        private lateinit var text: TextView
        private lateinit var checK: ImageView

        fun bind(categoryItem: SettingsCategory) {
            image = view.findViewById(R.id.imageViewPhoto)
            text = view.findViewById(R.id.textViewTitle)
            checK = view.findViewById(R.id.imageViewCheck)

            text.text = categoryItem.title
            image.setImageResource(categoryItem.idPhoto)
            if (categoryItem.active)
                checK.setImageResource(R.drawable.ic_circle_check)
            else
                checK.setImageResource(R.drawable.ic_add)

            checK.setOnClickListener {
                if (categoryItem.title != "Allow Notifications")
                    clickForCategory(categoryItem)
                else
                    clickForNotifications(categoryItem)
            }
        }

        private fun clickForNotifications(categoryItem: SettingsCategory) {
            val editor = prefs.edit()

            editor?.apply{
                putBoolean(categoryItem.title, !categoryItem.active)
                apply()
            }
            categoryList[absoluteAdapterPosition].active = !categoryItem.active
            notifyDataSetChanged()
        }

        private fun clickForCategory(categoryItem: SettingsCategory) {
            val editor = prefs.edit()

            editor?.apply{
                putBoolean(categoryItem.title, !categoryItem.active)
                apply()
            }
            categoryList[absoluteAdapterPosition].active = !categoryItem.active
            notifyDataSetChanged()
        }
    }
}