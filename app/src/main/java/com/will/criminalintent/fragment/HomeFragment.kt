package com.will.criminalintent.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.will.criminalintent.R

class HomeFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        view.findViewById<TextView>(R.id.tv_home).setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                findNavController().navigate(R.id.action_homeFragment_to_titleFragment)
            }

        })
        return view
    }
}