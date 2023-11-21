package com.example.capstone.ui

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.example.capstone.R
import com.example.capstone.model.Place
import com.example.capstone.model.Response
import com.example.capstone.services.GeofenceService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap

import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MapsFragment : Fragment() {

    //Firebase initialization
    private val database = Firebase.database
    private val myRef = database.getReference("capstone").child("place")

    private val mutableLiveData = MutableLiveData<Response>()

    lateinit var mGoogleMap: GoogleMap
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    private val geofenceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "GEOFENCE_EVENT") {
                val geofenceName = intent.getStringExtra("geofenceName")
                val latLng = intent.getParcelableExtra<LatLng>("latlng")
                val entered = intent.getBooleanExtra("entered", true)
//                Toast.makeText(requireContext(), "RB: ${latLng.toString()} $entered", Toast.LENGTH_SHORT).show()
                val markerOptions = latLng?.let {
                    MarkerOptions()
                        .position(it)
                        .title(geofenceName)
                        .snippet(latLng.toString())
                }

                if (entered) {
                    // Geofence entered, add the marker
                    if (markerOptions != null) {
//                    Toast.makeText(requireContext(), "sdfs", Toast.LENGTH_SHORT).show()
                        mGoogleMap.addMarker(markerOptions)

                    }
                } else {
                    // Geofence entered, remove the marker
                    // TODO: can create a object of the addmarker and can delete that object to make it delete.
                    if (markerOptions != null) {
//                    Toast.makeText(requireContext(), "sdfs", Toast.LENGTH_SHORT).show()
//                        mGoogleMap.clear()
                    }
                }
            }
        }
    }

    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                val location = locationList.last()
                // Create a LatLng object for the current location
                val currentLatLng = LatLng(location!!.latitude, location!!.longitude)
                // Move the camera to the current location
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                mFusedLocationClient?.removeLocationUpdates(this)
                // Create a marker for the current location
//                    mGoogleMap.addMarker(
//                        MarkerOptions()
//                            .position(currentLatLng)
//                            .title("My Location")
//                    )

            }
        }
    }

    private val callback = OnMapReadyCallback { mMap ->

//       /* for(i in 0 until mutableLiveData.value?.list!!.size){
//            val markerData = mutableLiveData.value?.list!![i] // Replace with your marker data

//            val markerOptions = MarkerOptions()
//                .position(LatLng(43.47412575636098, -80.5332843170499))
//                .title("Home")
//                .snippet("this is my home")
//
//            mMap.addMarker(markerOptions)
//    }*/

        mGoogleMap = mMap
        mGoogleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        enableMyLocation()
    }

    private fun enableMyLocation() {
        if (checkPermissions()) {
            val locationRequest = LocationRequest.create()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    mFusedLocationClient?.requestLocationUpdates(
                        locationRequest,
                        mLocationCallback,
                        Looper.myLooper()
                    )
                    mGoogleMap.isMyLocationEnabled = true
                } else {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        PERMISSIONS_REQUEST_LOCATION
                    )
                }
            } else {
                mFusedLocationClient?.requestLocationUpdates(
                    locationRequest,
                    mLocationCallback,
                    Looper.myLooper()
                )
                mGoogleMap.isMyLocationEnabled = true
            }
        } else {
            // Handle permission denied
            // You can display a message or request permission again here
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        getResponseFromRealtimeDatabaseUsingLiveData()
        requireContext().startService(Intent(requireContext(), GeofenceService::class.java))
        val filter = IntentFilter("GEOFENCE_EVENT")
        requireContext().registerReceiver(geofenceReceiver, filter)

        //(activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        /*val fab = requireActivity().findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fab.setOnClickListener {
            childFragmentManager.beginTransaction()
                .replace(R.id.fragment_mapss, AddPlaceFragment())
                .addToBackStack(tag)

                .commit()
        }*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().stopService(Intent(requireContext(), GeofenceService::class.java))
        requireActivity().unregisterReceiver(geofenceReceiver)
    }

    private fun getResponseFromRealtimeDatabaseUsingLiveData() : MutableLiveData<Response> {

        myRef.child("place").get().addOnCompleteListener { task ->
            val response = Response()
            if (task.isSuccessful) {
                val result = task.result
                result?.let {
                    response.list = result.children.map { snapShot ->
                        snapShot.getValue(Place::class.java)!!
                    }
                }
            } else {
                response.exception = task.exception
            }
            mutableLiveData.value = response
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(callback)

        }
        return mutableLiveData
    }
    companion object {
        private const val PERMISSIONS_REQUEST_LOCATION = 123
    }

    private fun checkPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }
}