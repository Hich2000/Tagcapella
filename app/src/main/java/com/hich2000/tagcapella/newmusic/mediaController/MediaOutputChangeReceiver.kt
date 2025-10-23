package com.hich2000.tagcapella.newmusic.mediaController

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import androidx.media3.session.MediaController
import javax.inject.Inject

class MediaOutputChangeReceiver @Inject constructor(
    private val application: Application
) {

    fun setGettingNoisyReceiver(mediaController: MediaController) {
        // Register the receiver to detect changes in audio output
        val audioOutputChangeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
                    mediaController.pause()
                }
            }
        }
        // Register the receiver
        val filter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        application.registerReceiver(audioOutputChangeReceiver, filter)
    }

}