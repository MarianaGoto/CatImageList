package com.marianagoto.catimagelist.ui.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

fun LifecycleOwner.lifecycleScopeRepeat(action: suspend () -> Unit){
    this.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            action()
        }
    }
}