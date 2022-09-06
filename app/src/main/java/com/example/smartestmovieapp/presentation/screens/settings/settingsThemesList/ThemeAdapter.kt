package com.example.smartestmovieapp.presentation.screens.settings.settingsThemesList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.smartestmovieapp.R


class ThemeAdapter(context: Context, themeList: List<Theme>) :
    ArrayAdapter<Theme>(context, 0, themeList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return myView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return myView(position, convertView, parent)
    }

    private fun myView(position: Int, convertView: View?, parent: ViewGroup): View {

        val list = getItem(position)
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.custom_spinner_item, parent, false)

        list.let {
            val textView = view.findViewById<TextView>(R.id.textViewThemeSpinner)
            val imageView = view.findViewById<ImageView>(R.id.imageViewThemeSpinner)

            textView.text = list?.name
            if (list != null) {
                imageView.setImageResource(list.circleDrawableId)
            }
        }

        return view
    }
}