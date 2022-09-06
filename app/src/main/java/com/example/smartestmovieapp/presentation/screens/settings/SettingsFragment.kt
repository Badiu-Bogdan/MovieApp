package com.example.smartestmovieapp.presentation.screens.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartestmovieapp.R
import com.example.smartestmovieapp.databinding.FragmentSettingsBinding
import com.example.smartestmovieapp.presentation.screens.authentication.AuthenticationActivity
import com.example.smartestmovieapp.presentation.screens.settings.settingsCategoriesList.SettingsCategory
import com.example.smartestmovieapp.presentation.screens.settings.settingsCategoriesList.SettingsCategoryAdapter
import com.example.smartestmovieapp.presentation.screens.settings.settingsThemesList.Theme
import com.example.smartestmovieapp.presentation.screens.settings.settingsThemesList.ThemeAdapter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth


class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private var themesList = ArrayList<Theme>()
    private var categoryList = ArrayList<SettingsCategory>()
    private var notificationList = ArrayList<SettingsCategory>()
    private lateinit var themeAdapter: ThemeAdapter
    private lateinit var discoverAdapter: SettingsCategoryAdapter
    private lateinit var notificationsAdapter: SettingsCategoryAdapter
    private lateinit var themePrefs: SharedPreferences
    private var defaultThemeChosen: Boolean = true
    private lateinit var themeEditor: SharedPreferences.Editor
    private lateinit var prefs: SharedPreferences

    private val spinnerListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            val selectedItem: String = parent.getItemAtPosition(position).toString()

            if (selectedItem.contains("Imperial")) {
                themeEditor.apply {
                    putBoolean("Default", true)
                    apply()
                }
                if (!defaultThemeChosen) {
                    defaultThemeChosen = true
                    setTheme()
                }

            } else {
                themeEditor.apply {
                    putBoolean("Default", false)
                    apply()
                }
                if (defaultThemeChosen) {
                    defaultThemeChosen = false
                    setTheme()
                }
            }
            binding.spinnerThemes.setSelection(0)
            updateList()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //Initializez shared preferences
        prefs = requireContext().getSharedPreferences("activeCategories", Context.MODE_PRIVATE)
        themePrefs = requireContext().getSharedPreferences("chosenTheme", Context.MODE_PRIVATE)

        //Initializez listele pentru adaptoare
        initThemesList()
        initCategory(prefs)
        initNotifications(prefs)

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        themeEditor = themePrefs.edit()
        val spinner = binding.spinnerThemes
        themeAdapter = ThemeAdapter(requireContext(), themesList)
        spinner.adapter = themeAdapter
        spinner.onItemSelectedListener = spinnerListener


        binding.reciclerViewDiscoverSettings.layoutManager = LinearLayoutManager(context)
        discoverAdapter = SettingsCategoryAdapter(categoryList, R.layout.cardview_settings, prefs)
        binding.reciclerViewDiscoverSettings.adapter = discoverAdapter


        binding.reciclerViewNotificationsSettings.layoutManager = LinearLayoutManager(context)
        notificationsAdapter =
            SettingsCategoryAdapter(notificationList, R.layout.cardview_settings, prefs)
        binding.reciclerViewNotificationsSettings.adapter = notificationsAdapter

        connectActions()
    }

    private fun connectActions()
    {
        binding.textViewLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val googleSignInOptions =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

            val googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions)
            googleSignInClient.signOut()

            themeEditor.apply {
                putBoolean("Default", true)
                apply()
            }
            val intent = Intent(context, AuthenticationActivity::class.java)
            val fragmentManager = requireActivity().supportFragmentManager
            for (item in 1..fragmentManager.backStackEntryCount)
                fragmentManager.popBackStack()
            startActivity(intent)
            requireActivity().finish()
        }

        binding.imageViewBell.setOnClickListener{
            val bundle = Bundle()
            bundle.putBoolean("networkConnection", true)
            Navigation.findNavController(binding.root)
                .navigate(R.id.fromSettingsToNotifications, bundle)
        }
    }

    private fun initCategory(prefs:SharedPreferences) {
        val trailers = prefs.getBoolean("Trailers", true)
        val popular = prefs.getBoolean("Popular", true)
        val topRated = prefs.getBoolean("Top Rated", true)
        val upComing = prefs.getBoolean("Upcoming", true)

        categoryList.add(SettingsCategory("Trailers", R.drawable.ic_trailer_with_gradient, trailers))
        categoryList.add(SettingsCategory("Popular", R.drawable.ic_popular_with_gradient, popular))
        categoryList.add(SettingsCategory("Top Rated", R.drawable.ic_top_rated_with_gradient, topRated))
        categoryList.add(SettingsCategory("Upcoming", R.drawable.ic_upcoming_with_gradient, upComing))

    }

    private fun initNotifications(prefs: SharedPreferences) {
        val allowNotifications = prefs.getBoolean("Allow Notifications", true)

        notificationList.add(
            SettingsCategory(
                "Allow Notifications",
                R.drawable.ic_notification_with_gradient,
                allowNotifications
            )
        )
    }

    private fun updateList() {
        themesList.clear()

        if (defaultThemeChosen) {
            themesList.add(Theme("Imperial Theme", R.drawable.circle_gradient_imperial_theme))
            themesList.add(Theme("Emerald Theme", R.drawable.circle_gradient_second_theme))
        } else {
            themesList.add(Theme("Emerald Theme", R.drawable.circle_gradient_second_theme))
            themesList.add(Theme("Imperial Theme", R.drawable.circle_gradient_imperial_theme))
        }

        themeAdapter.notifyDataSetChanged()

    }

    private fun initThemesList() {
        defaultThemeChosen = themePrefs.getBoolean("Default", true)

        if (defaultThemeChosen) {
            themesList.add(Theme("Imperial Theme", R.drawable.circle_gradient_imperial_theme))
            themesList.add(Theme("Emerald Theme", R.drawable.circle_gradient_second_theme))
        } else {
            themesList.add(Theme("Emerald Theme", R.drawable.circle_gradient_second_theme))
            themesList.add(Theme("Imperial Theme", R.drawable.circle_gradient_imperial_theme))
        }
    }

    private fun setTheme() {
        if (defaultThemeChosen) {
            requireActivity().setTheme(R.style.Theme_SmartestMovieApp)
        } else {
            requireActivity().setTheme(R.style.Theme_SmartestMovieApp_SecondTheme)
        }
        requireActivity().recreate()
    }
}