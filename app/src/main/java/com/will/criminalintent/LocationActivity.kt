package com.will.criminalintent

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class LocationActivity: AppCompatActivity() {

    var permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions(), object : ActivityResultCallback<Map<String, Boolean>> {
        override fun onActivityResult(result: Map<String, Boolean>) {
            var isAllPermission = true
            result.forEach {entry: Map.Entry<String, Boolean> ->
                if (!entry.value) {
                    Toast.makeText(this@LocationActivity, "请授权：${entry.key}", Toast.LENGTH_SHORT).show()
                    isAllPermission = false
                }
            }

            if (isAllPermission) {
                // 开始定位
                startLocation()
            }
        }

    })

    lateinit var gpsReceiver: BroadcastReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        gpsReceiver = GpsBroadcastReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(gpsAction)
        registerReceiver(gpsReceiver, intentFilter)
        // 请求获取 location 权限
        checkPermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManger?.removeUpdates(listener)
        unregisterReceiver(gpsReceiver)
    }

    val permissions = arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION)
    private fun checkPermission() {
        val list = mutableListOf<String>()
        for (permission in permissions) {
            // 没有授予权限
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
                list.add(permission)
            }
        }
        Log.e("WillWolf", "checkPermission-->" + list)
        if (list.isNotEmpty()) {
            permissionLauncher.launch(list.toTypedArray())
        } else {
            // 开始定位
            startLocation()
        }
    }

    //
    private var locationManger: LocationManager? = null
    @SuppressLint("MissingPermission")
    private fun startLocation() {
        locationManger = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val list = locationManger?.getProviders(true)
        Log.e("WillWolf", "list-->$list")
        if (list?.contains(LocationManager.GPS_PROVIDER) == true) {
            locationManger?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 0f, listener)
        }
        if (list?.contains(LocationManager.NETWORK_PROVIDER) == true) {
            locationManger?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 0f, listener)
        }
    }

    private val listener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.e("WillWolf", "provider-->" + location.provider)
            Log.e("WillWolf", "locationChanged-->" + location.longitude + ", " + location.latitude)
        }
    }

    val gpsAction = "android.location.PROVIDERS_CHANGED"
    inner class GpsBroadcastReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == gpsAction) {
                val locationManager =
                    context!!.getSystemService(LOCATION_SERVICE) as LocationManager
                val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                Log.e("WillWolf", "isGpsEnable-->" + isGpsEnabled)
            }
        }

    }
}