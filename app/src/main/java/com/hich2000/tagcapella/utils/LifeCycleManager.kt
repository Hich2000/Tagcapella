package com.hich2000.tagcapella.utils

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.hich2000.tagcapella.newmusic.mediaController.MediaPlayerCoordinator
import javax.inject.Inject

class LifeCycleManager @Inject constructor(
    private val mediaPlayerCoordinator: MediaPlayerCoordinator
): DefaultLifecycleObserver {
    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        mediaPlayerCoordinator.cleanup()
    }
}