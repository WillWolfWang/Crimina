package com.will.criminalintent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.will.criminalintent.databinding.ActivityCoroutineBinding
import kotlinx.coroutines.runBlocking

class CoroutineActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCoroutineBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnTest.setOnClickListener() {
            runBlocking {

            }
        }

    }
}