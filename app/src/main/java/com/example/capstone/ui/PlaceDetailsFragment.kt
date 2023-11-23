package com.example.capstone.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.capstone.R
import com.example.capstone.databinding.FragmentPlaceDetailsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PlaceDetailsFragment : Fragment() {

    // Firebase initialization
//    private val database = Firebase.database
//    private val myRef = database.getReference("capstone").child("place")

//    private lateinit var textViewDetails: TextView

    private var fragmentPlaceDetailsBinding: FragmentPlaceDetailsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        fragmentPlaceDetailsBinding =
            FragmentPlaceDetailsBinding.inflate(inflater, container, false)
        return fragmentPlaceDetailsBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        textViewDetails = view.findViewById(R.id.placeDetailsId)
        val placeName = arguments?.getString("placeName")

        Toast.makeText(requireContext(),placeName, Toast.LENGTH_SHORT).show()

//        if (placeName != null) {
//            fetchPlaceDetails(placeName)
//        }
    }

//    private fun fetchPlaceDetails(placeName: String) {
//        myRef.child(placeName).addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // Check if the place exists
//                if (dataSnapshot.exists()) {
//                    val latitude = dataSnapshot.child("latitude").getValue(Double::class.java) ?: 0.0
//                    val longitude = dataSnapshot.child("longitude").getValue(Double::class.java) ?: 0.0
//                    val placeDescription = dataSnapshot.child("placeDescription").getValue(String::class.java) ?: ""
//
//                    // Handle other details as needed
//                    // For example, set them to TextViews or other UI components
//                    textViewDetails.setText("Name: $placeName\nLatitude: $latitude\nLongitude: $longitude\nDescription: $placeDescription")
//                } else {
//                    // Handle case where the place doesn't exist
//                    textViewDetails.setText("Place not found")
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Handle errors
//                textViewDetails.text = "Error fetching place details"
//            }
//        })
//    }
}
