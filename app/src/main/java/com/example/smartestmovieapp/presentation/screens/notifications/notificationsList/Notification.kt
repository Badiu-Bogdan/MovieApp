package com.example.smartestmovieapp.presentation.screens.notifications.notificationsList

data class Notification(
    var title: String,
    var imageUrl: String,
    var description: String = "Check out this movie that will arrive in the cinemas around you in the next weeks.",
)