package com.will.criminalintent.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.will.criminalintent.R
import com.will.criminalintent.data.Crime

class CrimeFragment: Fragment() {
    private lateinit var crime: Crime
    // fragment 的 onCreate 函数是 public 的
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
    }

    // 该函数会实例化 fragment 视图的布局，然后将实例化的 View 返回给托管的 activity
    override fun onCreateView(
        inflater: LayoutInflater,//
        container: ViewGroup?, // LayoutInflater 和 ViewGroup 是实例化布局的必要参数
        savedInstanceState: Bundle? // 用来存储恢复数据
    ): View? {
        // 第三个参数告诉布局生成器是否立即将生成的视图添加到父视图。因为 fragment 的视图由 activity 的容器
        // 视图托管，所以这里传入 false
        val view = inflater.inflate(R.layout.fragment_crime, container, false)
        return view
    }
}