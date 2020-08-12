package com.shorman.shoppinglist.models

data class PushNotification(
    val data:NotificationData,
    val to:String
)