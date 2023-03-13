package com.example.hloomap.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hloomap.R
import com.example.hloomap.databinding.LocationListBottomsheetBinding
import com.example.hloomap.ui.adapters.LocationAdapter
import com.example.hloomap.viewmodel.MapActivityViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LocationListBSh @Inject constructor(): BottomSheetDialogFragment() {

    private val viewModel: MapActivityViewModel by activityViewModels()

     private lateinit var binding:LocationListBottomsheetBinding

     @Inject
     lateinit var adapterLocationList: LocationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= LocationListBottomsheetBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAllLocations()

        //Get All Locations
        viewModel.locationsList.observe(this, androidx.lifecycle.Observer {
            adapterLocationList.setData(it.data!!)
            binding.locationRecyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
                adapter = adapterLocationList
            }
        })

    }


    override fun dismiss() {
        super.dismiss()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
    }
}