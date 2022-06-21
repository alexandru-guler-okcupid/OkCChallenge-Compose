package com.com.okcupidtakehome.util

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

fun <T : ViewBinding> ViewGroup.toBinding(
    creator: (inflater: LayoutInflater, root: ViewGroup, attachToRoot: Boolean) -> T
): T = creator(
    LayoutInflater.from(context),
    this,
    false
)

fun Context.dipToPx(dipValue: Int): Int = (dipValue * resources.displayMetrics.density).toInt()
