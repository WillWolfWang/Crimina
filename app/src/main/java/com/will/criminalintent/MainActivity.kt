package com.will.criminalintent

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.will.criminalintent.fragment.CrimeFragment
import com.will.criminalintent.fragment.CrimeListFragment
import java.util.UUID

class MainActivity : AppCompatActivity(), CrimeListFragment.Callbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        if (currentFragment == null) {
//            val fragment = CrimeListFragment.newInstance()
//        }
    }

    override fun onCrimeSelected(crimeId: UUID) {
        Log.e("WillWolf", "MainActivity.onCrimeSelected: $crimeId")
//        val fragment = CrimeFragment.newInstance(crimeId)
        // 添加一个回退栈，否则按返回按钮，activity 会退出
//        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
    }
}