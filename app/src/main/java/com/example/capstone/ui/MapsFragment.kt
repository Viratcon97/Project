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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
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
import com.google.android.gms.maps.model.Marker
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

    lateinit var marker: MarkerOptions

    private val geofenceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "GEOFENCE_EVENT") {
                val geofenceId = intent.getStringExtra("geofenceId")
                val entered = intent.getBooleanExtra("entered", true)

                fetchGeofenceDataFromFirebase(geofenceId, entered)
            }
        }
    }

    private fun fetchGeofenceDataFromFirebase(geofenceId: String?, entered: Boolean) {
        myRef.child(geofenceId ?: "").get().addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                val dataSnapShot = task.result
                if (dataSnapShot != null && dataSnapShot.exists()) {
                    val lat = dataSnapShot.child("latitude").getValue(Double::class.java) ?: 0.0
                    val lng = dataSnapShot.child("longitude").getValue(Double::class.java)  ?: 0.0
                    val latlng = LatLng(lat,lng)

                    // Example of adding markers
                    val markerWithRating = CustomMarker(
                        name = dataSnapShot.child("placeName").getValue(String::class.java).toString(),
                        description = dataSnapShot.child("placeDescription").getValue(String::class.java).toString(),
                        category = dataSnapShot.child("category").getValue(String::class.java).toString(),
                        iconRes = R.drawable.bottom_navigation_icon_event,
                        imageResList = listOf(R.drawable.bottom_navigation_icon_event, R.drawable.bottom_navigation_icon_event, R.drawable.bottom_navigation_icon_event),
                    )
                    val markerOptions = latlng?.let {
                        MarkerOptions()
                            .position(it)
                            .title(dataSnapShot.child("placeName").getValue(String::class.java))
                            .snippet(dataSnapShot.child("placeDescription").getValue(String::class.java))
                    }

                    if (entered) {
                        // Geofence entered, add the marker
                        if (markerOptions != null) {
//                    Toast.makeText(requireContext(), "sdfs", Toast.LENGTH_SHORT).show()
                            val marker = mGoogleMap.addMarker(markerOptions)
                            marker?.tag = markerWithRating
                        }
                    } else {
                        // Geofence entered, remove the marker
                        // TODO: can create a object of the addmarker and can delete that object to make it delete.
                        if (markerOptions != null) {
//                    Toast.makeText(requireContext(), "sdfs", Toast.LENGTH_SHORT).show()
//                        mGoogleMap.clear()
                            markerOptions.visible(false)
                        }
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
        // Set custom InfoWindowAdapter
        mGoogleMap?.setInfoWindowAdapter(CustomInfoWindowAdapter())
    }

    // Custom InfoWindowAdapter class
    inner class CustomInfoWindowAdapter : GoogleMap.InfoWindowAdapter {

        private val mInflater: LayoutInflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getInfoContents(p0: Marker): View? {
            val customView = mInflater.inflate(R.layout.custom_marker_option, null)

            // Get data from the marker (assuming you have a custom Marker class)
            val customMarker = p0?.tag as? CustomMarker// Your custom marker class
            if (customMarker != null){
                val nameTextView: TextView = customView.findViewById(R.id.textViewName)
                val descriptionTextView: TextView = customView.findViewById(R.id.textViewDescription)
                val categoryTextView: TextView = customView.findViewById(R.id.textViewCategory)
                val imageSliderLayout: LinearLayout = customView.findViewById(R.id.imageSliderLayout)

                nameTextView.text = customMarker.name
                descriptionTextView.text = customMarker.description
                categoryTextView.text = customMarker.category

                for (imageResId in customMarker.imageResList) {
                    val imageView = ImageView(requireContext())
                    imageView.layoutParams = LinearLayout.LayoutParams(
                        400,
                        400
                    )
                    imageView.setImageResource(imageResId)
                    imageSliderLayout.addView(imageView)
                }
                customView.setOnClickListener {
                    // Handle click event here
                    // You can perform any action when the info window is clicked
                    Toast.makeText(requireContext(), "Info window clicked for ${customMarker.name}", Toast.LENGTH_SHORT).show()
                }
            }



            return customView
        }

        override fun getInfoWindow(p0: Marker): View? {
            return null
        }
    }
    data class CustomMarker(
        val name: String,
        val description: String,
        val category: String,
        val iconRes: Int,
        val imageResList: List<Int>
    )

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