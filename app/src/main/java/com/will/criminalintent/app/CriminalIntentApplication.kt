package com.will.criminalintent.app

import android.app.Application
import com.will.criminalintent.database.CrimeRepository

class CriminalIntentApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}