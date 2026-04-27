package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import `in`.gov.mahapocra.mahavistaarai.R
import `in`.gov.mahapocra.mahavistaarai.databinding.FragmentAgriServicesBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.chc.CHCenterActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.FertilizerCalculatorActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.Warehouse
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.marketprice.MarketPrice
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.pestIdentification.ui.PestIdentificationActivity
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.costcalculator.CostCalculatorDashboardActivity
import `in`.gov.mahapocra.mahavistaarai.util.AppConstants.TAG
import `in`.gov.mahapocra.mahavistaarai.util.helpers.CryptoHelper


class AgriServicesFragment : Fragment() {
    private var _binding: FragmentAgriServicesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAgriServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setUpListeners()
    }

    private fun setUpListeners() {

        binding.marketPriceLayout.containerIcon.setImageDrawable(context?.let {
            ContextCompat.getDrawable(
                it,
                R.drawable.market_bubble_as
            )
        })

        binding.takePictureButton.setOnClickListener {
            startActivity(Intent(context, PestIdentificationActivity::class.java))
        }

        binding.marketPriceLayout.txtTitle.text = "Real-Time Market Prices"
        binding.marketPriceLayout.shortDescriptionTextView.text = "Live rates from 200+ Mandis"
        binding.marketPriceLayout.root.setOnClickListener {
            startActivity(Intent(context, MarketPrice::class.java))
        }

        binding.warehouseLayout.containerIcon.setImageDrawable(context?.let {
            ContextCompat.getDrawable(
                it,
                R.drawable.warehouse_bubble_as
            )
        })
        binding.warehouseLayout.txtTitle.text = "Warehouse Availability"
        binding.warehouseLayout.shortDescriptionTextView.text = "Find storage space near you"
        binding.warehouseLayout.root.setOnClickListener {
            startActivity(Intent(context, Warehouse::class.java))
        }

        binding.chcCentreLayout.containerIcon.setImageDrawable(context?.let {
            ContextCompat.getDrawable(
                it,
                R.drawable.chc_bubble_as
            )
        })
        binding.chcCentreLayout.txtTitle.text = "CHC Center"
        binding.chcCentreLayout.shortDescriptionTextView.text = "Find local trading hubs"
        binding.chcCentreLayout.root.setOnClickListener {
            startActivity(Intent(context, CHCenterActivity::class.java))
        }

        binding.costCalculatorCard.setOnClickListener {
            startActivity(Intent(context, CostCalculatorDashboardActivity::class.java))
        }

        binding.fertilizerCalculatorCard.setOnClickListener {
            startActivity(Intent(context, FertilizerCalculatorActivity::class.java))
        }
    }
}