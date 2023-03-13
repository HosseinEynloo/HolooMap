package com.example.hloomap.ui

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.hloomap.R
import com.example.hloomap.data.models.LocationEntity
import com.example.hloomap.databinding.BottomSheetLocationBinding
import com.example.hloomap.utils.Constants.LATITUDE
import com.example.hloomap.utils.Constants.LONGITUDE
import com.example.hloomap.utils.HolooResponse
import com.example.hloomap.viewmodel.MapActivityViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchDestinationBSh @Inject constructor() : BottomSheetDialogFragment() {

    private val viewModel: MapActivityViewModel by activityViewModels()

    @Inject
    lateinit var locationEntity: LocationEntity

    private lateinit var binding:BottomSheetLocationBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomSheetDialogStyle)
    }

    // setup FullScreen BottomSheet
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            if (false) {
                setupFullHeight(bottomSheetDialog)
            }
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding= BottomSheetLocationBinding.inflate(layoutInflater,container,false)
//        val view: View = inflater.inflate(R.layout.bottom_sheet_location, container, false);
        return binding.root
    }

//    var latArg:Double = 0.0
//    var longArg:Double = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        latArg= this.requireArguments().getDouble(LATITUDE)
//        longArg= this.requireArguments().getDouble(LONGITUDE)





//        val btn: MaterialButton = view.findViewById(R.id.searchLoc)
//        val latEditText: TextInputEditText = view.findViewById(R.id.lat_editText)
//        latEditText.setText(latArg.toString())
//        val longEditText: TextInputEditText = view.findViewById(R.id.long_editText)
//        longEditText.setText(longArg.toString())
//        val locationName: TextInputEditText = view.findViewById(R.id.locationName_editText)
//        val locationDes: TextInputEditText = view.findViewById(R.id.locationDes_editText)
//        val saveCheckBox: CheckBox = view.findViewById(R.id.checkboxSaveLocation)



        viewModel.locationMarked.observe(this, Observer { latLng->
            binding.latEditText.setText(latLng.latitude.toString())
            binding.longEditText.setText(latLng.longitude.toString())
        })

        binding.apply {

        searchLoc.setOnClickListener {
            dismiss()
            val latitude = latEditText.text.toString()
            val longitude = longEditText.text.toString()
            var locationTitle=locationNameEditText.text.toString()
            var locationDescription=locationDesEditText.text.toString()
            if (locationTitle.equals(null)){
                locationTitle=""
            }
            if (locationDescription.equals(null)){
                locationDescription=""
            }
            if (!latitude.equals("") && !longitude.equals("")) {
                if (checkboxSaveLocation.isChecked){
                    // Add items To Entity For Save
                    locationEntity.latitude=latitude.toDouble()
                    locationEntity.longitude=longitude.toDouble()
                    locationEntity.title=locationTitle
                    locationEntity.description=locationDescription
                    //Save Location
                    viewModel.saveLocation(locationEntity)
                    //Find Location On Map
                    viewModel.loadDestination(locationEntity.latitude, locationEntity.longitude)
                }else{
                    //Find Location On Map
                    viewModel.loadDestination(latitude.toDouble(), longitude.toDouble())
                }
            } else {
                viewModel.destinationLocation.postValue(HolooResponse.error("Please check the entries!"))
            }

        }
        }
    }




    override fun dismiss() {
        super.dismiss()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
    }

    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
//        val bottomSheet =
//            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
//        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
//        val layoutParams = bottomSheet!!.layoutParams
//        val windowHeight = windowHeight
//        if (layoutParams != null) {
//            layoutParams.height = windowHeight
//        }
//        bottomSheet.layoutParams = layoutParams
//        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    // Calculate window height for fullscreen use
    private val windowHeight: Int
        private get() {
            // Calculate window height for fullscreen use
            val displayMetrics = DisplayMetrics()
            (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.heightPixels
        }


}