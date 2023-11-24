package com.example.capstone.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.capstone.databinding.FragmentEventDetailsBinding
import com.example.capstone.model.Events
import com.squareup.picasso.Picasso


//Todo - Events details after clicking on specific event
class EventDetailsFragment : Fragment() {

    private var fragmentEventDetailsBinding: FragmentEventDetailsBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        fragmentEventDetailsBinding =
            FragmentEventDetailsBinding.inflate(inflater, container, false)
        return fragmentEventDetailsBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("event", Events::class.java)
        } else {
            arguments?.getParcelable<Events>("event")
        }
        Picasso.get().load(data?.pictures).into(fragmentEventDetailsBinding?.eventPoster)
        fragmentEventDetailsBinding?.eventTitle?.text = data?.eventName
        fragmentEventDetailsBinding?.eventDate?.text = data?.eventStartDate
        fragmentEventDetailsBinding?.eventTime?.text = data?.eventTime
        fragmentEventDetailsBinding?.eventLocation?.text = data?.eventLocation
        fragmentEventDetailsBinding?.eventDescription?.text = data?.eventDescription
    }
}