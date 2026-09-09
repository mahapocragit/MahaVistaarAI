package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mahanidan.vcccore.common.i18n.NativeUiConfig
import com.mahanidan.vcccore.my_farms.view.MyFarmsUi
import `in`.gov.mahapocra.mahavistaarai.R

class FarmObservationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(
            R.layout.fragment_farm_observation,
            container,
            false
        )
        return view
    }
}