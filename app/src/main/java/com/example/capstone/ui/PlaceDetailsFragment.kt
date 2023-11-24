package com.example.capstone.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.capstone.R
import com.example.capstone.databinding.FragmentPlaceDetailsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class PlaceDetailsFragment : Fragment() {

    private val database = Firebase.database
    private val myRef = database.getReference("capstone").child("place")

    private lateinit var textViewTitle: TextView
    private lateinit var textViewDescription: TextView
    private lateinit var textViewCategory: TextView
    private lateinit var imageViewCategoryIcon: ImageView
    private lateinit var imagesLayout: LinearLayout

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

        textViewTitle = view.findViewById(R.id.textViewTitle)
        textViewDescription = view.findViewById(R.id.textViewDescription)
        textViewCategory = view.findViewById(R.id.textViewCategory)
        imageViewCategoryIcon = view.findViewById(R.id.imageViewCategoryIcon)
        imagesLayout = view.findViewById(R.id.imagesLayout)

        val placeName = arguments?.getString("placeName")

        if (placeName != null) {
            fetchPlaceDetails(placeName)
        }
    }

    private fun fetchPlaceDetails(placeName: String) {
        myRef.orderByChild("placeName").equalTo(placeName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (placeSnapshot in dataSnapshot.children) {
                            val title =
                                placeSnapshot.child("placeName").getValue(String::class.java) ?: ""
                            val description =
                                placeSnapshot.child("placeDescription").getValue(String::class.java)
                                    ?: ""
                            val category =
                                placeSnapshot.child("category").getValue(String::class.java) ?: ""
                            val categoryIconRes = getCategoryIconRes(category)
                            val contentUris = getImageUrls(placeSnapshot) // Updated function

                            // Set the values to UI components
                            textViewTitle.text = title
                            textViewDescription.text = description
                            textViewCategory.text = category
                            imageViewCategoryIcon.setImageResource(categoryIconRes)
                            loadImageUrls(contentUris)
                        }
                    } else {
                        // Handle case where the place doesn't exist
                        textViewTitle.text = "Place not found"
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                    textViewTitle.text = "Error fetching place details"
                }
            })
    }

    private fun getCategoryIconRes(category: String): Int {
        return when (category.lowercase()) {
            "fun & games" -> R.drawable.game_controller
            "hiking trails & parks" -> R.drawable.hiking
            "point of interest & landmark" -> R.drawable.point_of_interest
            "food & drinks" -> R.drawable.drink
            "shopping malls & antique shops" -> R.drawable.online_shopping
            // Add more categories as needed
            else -> R.drawable.point_of_interest
        }
    }

    private fun getImageUrls(placeSnapshot: DataSnapshot): List<String> {
        val imageUrls = mutableListOf<String>()
        for (imageSnapshot in placeSnapshot.child("images").children) {
            val imageUrl = imageSnapshot.getValue(String::class.java)
            if (imageUrl != null) {
                imageUrls.add(imageUrl)
            }
        }
        return imageUrls
    }

    private fun loadImageUrls(contentUris: List<String>) {
        // Clear existing images
        //imagesLayout.removeAllViews()

        // Load new images using Picasso
        for (contentUri in contentUris) {
//            val imageView = ImageView(requireContext())
//            imageView.layoutParams = LinearLayout.LayoutParams(
//                400,
//                400
//            )

            // Use Picasso to load the image from content URI
            Picasso.get().load(Uri.parse(contentUri)).into(fragmentPlaceDetailsBinding?.placeImage)

            //imagesLayout.addView(imageView)
        }
    }
}
