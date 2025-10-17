package com.hich2000.tagcapella.utils

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.hich2000.tagcapella.music.MediaControllerManager
import javax.inject.Inject

class LifeCycleManager @Inject constructor(
    private val mediaControllerManager: MediaControllerManager,
): DefaultLifecycleObserver {
    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        mediaControllerManager.cleanUpResources()
    }
}