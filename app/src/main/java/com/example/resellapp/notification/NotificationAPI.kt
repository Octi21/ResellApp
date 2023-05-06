package com.example.resellapp.notification

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.Response


interface NotificationAPI {

    @Headers("Authorization:key=AAAAeRYS3cE:APA91bEZASx7s3ex2CLEZKpX_WjYrsxIXspj0phmjWt6KbTVMyL07aul_seqmzmv8G-lwJu-eDMQvQspVdEXGADhyMKUPBhaziFOsQn12MHFeb1bl9VWaaDqAymDvFx88hv7ZKLgpNSr", "Content-Type:application/json")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}