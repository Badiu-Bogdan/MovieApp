package com.example.smartestmovieapp.presentation.screens.notifications

import android.os.Bundle
import android.text.BoringLayout
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartestmovieapp.R
import com.example.smartestmovieapp.databinding.FragmentLoginScreenBinding
import com.example.smartestmovieapp.databinding.FragmentNotificationsBinding
import com.example.smartestmovieapp.presentation.screens.notifications.notificationsList.Notification
import com.example.smartestmovieapp.presentation.screens.notifications.notificationsList.NotificationsAdapter

class NotificationsFragment : Fragment() {

    private lateinit var binding: FragmentNotificationsBinding
    private var notificationList = ArrayList<Notification>()
    private lateinit var notificationAdapter: NotificationsAdapter
    private val args: NotificationsFragmentArgs by navArgs()
    private var internetConnection: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initNotificationList()

        internetConnection = arguments?.getBoolean("networkConnection") ?: false

        binding.recyclerViewNotifications.layoutManager = LinearLayoutManager(context)
        notificationAdapter = NotificationsAdapter(notificationList, R.layout.cardview_notification, internetConnection)
        binding.recyclerViewNotifications.adapter = notificationAdapter
    }


    private fun initNotificationList()
    {
        notificationList.add(Notification("Top Gun: Maverick", "https://image.tmdb.org/t/p/w500/62HCnUTziyWcpDaBO2i1DX17ljH.jpg"))
        notificationList.add(Notification("Minions: The Rise of Gru", "https://image.tmdb.org/t/p/w500/wKiOkZTN9lUUUNZLmtnwubZYONg.jpg"))
        notificationList.add(Notification("Doctor Strange in the Multiverse of Madness", "https://image.tmdb.org/t/p/w500/9Gtg2DzBhmYamXBS1hKAhiwbBKS.jpg"))
        notificationList.add(Notification("Jurassic World Dominion", "https://image.tmdb.org/t/p/w500/kAVRgw7GgK1CfYEJq8ME6EvRIgU.jpg"))
        notificationList.add(Notification("Luck", "https://image.tmdb.org/t/p/w500/1HOYvwGFioUFL58UVvDRG6beEDm.jpg"))
        notificationList.add(Notification("Thor: Love and Thunder", "https://image.tmdb.org/t/p/w500/pIkRyD18kl4FhoCNQuWxWu5cBLM.jpg"))
        notificationList.add(Notification("Spider-Man: No Way Home", "https://image.tmdb.org/t/p/w500/1g0dhYtq4irTY1GPXvft6k4YLjm.jpg"))

    }

}