package com.will.criminalintent.livedata

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

class SingleLiveData<T> : MutableLiveData<T>() {

    val mPending: AtomicBoolean = AtomicBoolean(false)
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (hasActiveObservers()) {
            Log.e("WillWolf", "Multiple observers registered but only one will be notified of changes")
        }
        super.observe(owner) {data ->

            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(data);
            }

        }
    }

    override fun setValue(value: T) {
        mPending.set(true)
        super.setValue(value)
    }
}