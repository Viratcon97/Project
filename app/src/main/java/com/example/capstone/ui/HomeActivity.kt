package com.example.capstone.ui

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.capstone.R
import com.example.capstone.utils.writeLogToFile
import com.google.android.material.bottomnavigation.BottomNavigationView


class HomeActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private val BACKGROUND_LOCATION_PERMISSION_CODE: Int = 888
    private val LOCATION_PERMISSION_CODE: Int = 999
    private val TAG = HomeActivity::class.simpleName.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //Fetch Data
        val intent = intent
        val email = intent.getStringExtra("Email")
        val name = intent.getStringExtra("Name")

        Log.d("Email", email.toString())
        Log.d("Name", name.toString())

        //Default Fragment as Home
        loadFragment(EventFragment())
        checkPermission()
        bottomNav = findViewById(R.id.bottomNav)
        bottomNav.itemIconTintList = null
        bottomNav.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.home -> {
                    loadFragment(EventFragment())
                    true
                }

                R.id.map -> {
                    loadFragment(MapsFragment())
                    true
                }

                R.id.profile -> {
                    loadFragment(ProfileFragment())
                    true
                }

                R.id.place -> {
                    loadFragment(AddPlaceFragment())
                    true
                }

                R.id.event -> {
                    loadFragment(AddEventFragment())
                    true
                }

                else -> {
                    false
                }
            }
        }
    }
    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this@HomeActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Fine Location permission is granted
            // Check if current android version >= 11, if >= 11 check for Background Location permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (ContextCompat.checkSelfPermission(
                        this@HomeActivity,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    // Background Location Permission is granted so do your work here
//                    startService(Intent(this, GeofenceService::class.java))
                } else {
                    // Ask for Background Location Permission
                    askPermissionForBackgroundUsage()
                }
            }
        } else {
            // Fine Location Permission is not granted so ask for permission
            askForLocationPermission()
        }
    }

    private fun askForLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@HomeActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            AlertDialog.Builder(this)
                .setTitle("Permission Needed!")
                .setMessage("Location Permission Needed!")
                .setPositiveButton(
                    "OK"
                ) { _, _ ->
                    ActivityCompat.requestPermissions(
                        this@HomeActivity, arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ), LOCATION_PERMISSION_CODE
                    )
                }
                .setNegativeButton("CANCEL") { _, _ ->
                    // Permission is denied by the user
                }
                .create().show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
        }
    }

    private fun askPermissionForBackgroundUsage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@HomeActivity,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        ) {
            writeLogToFile(TAG, "Background Location Permission Needed... Showing alert dialog")
            AlertDialog.Builder(this)
                .setTitle("Permission Needed!")
                .setMessage("Background Location Permission Needed!, tap \"Allow all time in the next screen\"")
                .setPositiveButton(
                    "OK"
                ) { _, _ ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        writeLogToFile(TAG, "Launching App Info page for getting background location permission")
                        ActivityCompat.requestPermissions(
                            this@HomeActivity, arrayOf(
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            ), BACKGROUND_LOCATION_PERMISSION_CODE
                        )
                    }
                }
                .setNegativeButton("CANCEL", DialogInterface.OnClickListener { dialog, which ->
                    // User declined for Background Location Permission.
                })
                .create().show()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    BACKGROUND_LOCATION_PERMISSION_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()) {
            if (requestCode == LOCATION_PERMISSION_CODE) {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // User granted location permission
                    // Now check if android version >= 11, if >= 11 check for Background Location Permission
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (ContextCompat.checkSelfPermission(
                                this@HomeActivity,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            // Background Location Permission is granted so do your work here
//                            startService(Intent(this, GeofenceService::class.java))
                        } else {
                            // Ask for Background Location Permission
                            askPermissionForBackgroundUsage()
                        }
                    }
                } else {
                    // User denied location permission
                }
            } else if (requestCode == BACKGROUND_LOCATION_PERMISSION_CODE) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // User granted for Background Location Permission.
//                    startService(Intent(this, GeofenceService::class.java))
                } else {
                    // User declined for Background Location Permission.
                    askPermissionForBackgroundUsage()
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }
}