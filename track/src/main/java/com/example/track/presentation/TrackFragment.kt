package com.example.track.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.track.R
import com.example.track.databinding.FragmentTrackBinding
import com.example.track.domain.LocationTracker
import com.example.track.data.LocationRepository
import com.example.track.data.WorkerScheduler

class TrackFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentTrackBinding
    private lateinit var locationTracker: LocationTracker

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrackBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btStart.setOnClickListener(this)

        val locationRepository = LocationRepository(requireActivity())
        locationTracker = LocationTracker(requireActivity(), viewLifecycleOwner.lifecycle, locationRepository)



        return view
    }

    override fun onStop() {
        super.onStop()
        locationTracker.onDestroy(viewLifecycleOwner)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btStart -> {
                if (binding.btStart.text == getString(R.string.start)) {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvTracker.setText(R.string.tracker_on)
                    binding.btStart.setText(R.string.stop)
                    binding.btStart.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.stop_bt))
                    locationTracker.startTracking()
                    WorkerScheduler.scheduleSendLocationWorker(requireContext())
                } else {
                    binding.progressBar.visibility = View.GONE
                    binding.tvTracker.setText(R.string.tracker_off)
                    binding.btStart.setText(R.string.start)
                    binding.btStart.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray_bt))
                    locationTracker.stopTracking()
                    Toast.makeText(activity, "Tracker has been stopped", Toast.LENGTH_LONG).show()
                }
            }
        }
        val lastLocation = locationTracker.getLastLocation()
        if (lastLocation != null) {
            Toast.makeText(requireContext(), "Last location: ${lastLocation.latitude}, ${lastLocation.longitude}", Toast.LENGTH_LONG).show()
        }
    }
}