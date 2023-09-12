package com.example.capstone.ui

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.capstone.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

//    val database = Firebase.database
//    val myRef = database.getReference("message")

    private val callback = OnMapReadyCallback { mMap ->

        // Add a marker in Sydney and move the camera
        val current = LatLng(43.450120, -80.516240)
        mMap.addMarker(
            MarkerOptions()
                .position(current)
                .title("Current location")
        )

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 16.0f))

        mMap.addMarker(
            MarkerOptions()
                .position(LatLng(43.452969,-80.495064))
                .title("Kitchener"),
        )
        mMap.setOnMarkerClickListener {
            // code inside onMarkerClick function
            if(it.title == "Current location"){
                Toast.makeText(requireActivity(),"Current loc", Toast.LENGTH_LONG).show()
            }else if(it.title == "Kitchener"){
                Toast.makeText(requireActivity(),"Kitchener", Toast.LENGTH_LONG).show()
            }
            true // return value of onMarkerClick function
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
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}