package com.hupisan.listener

import android.view.View

interface OnBottomSheetCallback {
    fun onStateChanged(bottomSheet: View, newState: Int)
}