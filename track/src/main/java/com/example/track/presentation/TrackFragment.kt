package com.example.track.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.track.R
import com.example.track.databinding.FragmentTrackBinding
import com.example.track.domain.LocationTracker
import com.example.track.data.LocationRepository

class TrackFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentTrackBinding
    private lateinit var trackViewModel: TrackViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrackBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btStart.setOnClickListener(this)

        trackViewModel = ViewModelProvider(
            this,
            TrackViewModelFactory(requireActivity())
        ).get(TrackViewModel::class.java)

        val locationRepository = LocationRepository(requireActivity())
        val locationTracker =
            LocationTracker(requireContext(), locationRepository, viewLifecycleOwner.lifecycle)
        trackViewModel.setLocationTracker(locationTracker)

        trackViewModel.isTracking.observe(viewLifecycleOwner, Observer { isTracking ->
            if (isTracking) {
                binding.progressBar.visibility = View.VISIBLE
                binding.tvTracker.setText(R.string.tracker_on)
                binding.btStart.setText(R.string.stop)
                binding.btStart.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.stop_bt
                    )
                )
            } else {
                binding.progressBar.visibility = View.GONE
                binding.tvTracker.setText(R.string.tracker_off)
                binding.btStart.setText(R.string.start)
                binding.btStart.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.gray_bt
                    )
                )
            }
        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isTracking = trackViewModel.loadTrackingState()
        trackViewModel.isTracking.value = isTracking

        trackViewModel.startOrStopService()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btStart -> {
                if (trackViewModel.isTracking.value == false) {
                    trackViewModel.startTracking()
                } else {
                    trackViewModel.stopTracking()
                }
            }
        }
    }
}