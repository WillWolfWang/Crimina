package com.will.criminalintent

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener

class TestActivity: AppCompatActivity() {
    private lateinit var navView : BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        navView = findViewById(R.id.nav_view)
        // 将 底部导航栏 和 中间的 fragment 关联起来，就可以自由切换了
        val fragmentContainerView =  supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        val navController = fragmentContainerView.navController
        navView.setupWithNavController(navController)
    }
}