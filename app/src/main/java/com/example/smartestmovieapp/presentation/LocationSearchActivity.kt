package com.example.smartestmovieapp.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.smartestmovieapp.R
import com.example.smartestmovieapp.data.repository.LocationRepositoryImpl
import com.example.smartestmovieapp.presentation.screens.VM.LocationSearchVM
import dagger.android.AndroidInjection
import javax.inject.Inject

class LocationSearchActivity : AppCompatActivity() {

    @Inject
    lateinit var vm: LocationSearchVM

    @Inject
    lateinit var locationRepositoryImpl: LocationRepositoryImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_search)

        // COMENTAT
        /* vm.tradesLiveData.observe(this) {
             when (it) {
                 is Resource.Success -> {
                     Toast.makeText(this, "success: ${it.data}", Toast.LENGTH_LONG).show()
                 }
                 is Resource.Error -> {
                     Toast.makeText(this, "error: ${it.message}", Toast.LENGTH_LONG).show()
                 }
                 is Resource.Loading -> {
                     Toast.makeText(this, "loading", Toast.LENGTH_LONG).show()

                 }
             }
         }*/

        // Added
        /* supportFragmentManager.beginTransaction()
             .setReorderingAllowed(true)
             .add(R.id.fragment_container, RegisterFragment::class.java, null)
             .commit()*/

        window.statusBarColor = ContextCompat.getColor(baseContext, R.color.dark_blue)
    }


}