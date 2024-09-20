package com.will.criminalintent.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.will.criminalintent.R

class TitleFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_title, container, false)
        view.findViewById<TextView>(R.id.tv_title).setOnClickListener {
            findNavController().navigate(R.id.action_titleFragment_to_aboutFragment)
        }
        return view
    }
}