package com.com.okcupidtakehome.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

inline fun <reified T : ViewBinding> Fragment.bindingLifecycle(): ReadWriteProperty<Fragment, T> =
    object : ReadWriteProperty<Fragment, T>, LifecycleEventObserver {

        private val TAG = "bindingLifecycle"
        private var binding: T? = null

        init {
            lifecycle.addObserver(this)
        }

        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            // null out binding when OnDestroyView is called
            if (event == Lifecycle.Event.ON_DESTROY) {
                binding = null
            }
        }

        override fun getValue(thisRef: Fragment, property: KProperty<*>): T = binding!!

        override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
            binding = value
        }
    }
