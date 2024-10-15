package com.will.criminalintent

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener

class TestActivity: AppCompatActivity() {
    private lateinit var navView : BottomNavigationView

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        navView = findViewById(R.id.nav_view)
        // 将 底部导航栏 和 中间的 fragment 关联起来，就可以自由切换了
        val fragmentContainerView =  supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = fragmentContainerView.navController
        navView.setupWithNavController(navController)

        // 将 actionBar 和 中间的 fragment 关联起来
        appBarConfiguration = AppBarConfiguration.Builder(navController.graph).build()
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
    }

    // 给 Activity 创建 menu 菜单
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_about, menu)
        return true
    }

    // 可以使用 NavigationUI 自动导航，因为 menu 的 itemId 和 fragment 的 id 一样
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return  NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item)
    }

    // actionBar 中 左侧返回按钮的事件处理
    override fun onSupportNavigateUp(): Boolean {

        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}