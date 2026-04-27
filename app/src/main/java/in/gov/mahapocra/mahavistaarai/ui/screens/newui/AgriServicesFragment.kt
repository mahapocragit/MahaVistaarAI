package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import `in`.gov.mahapocra.mahavistaarai.databinding.FragmentAgriServicesBinding
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.costcalculator.CostCalculatorDashboardActivity


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
        binding.costCalculatorCard.setOnClickListener {
            startActivity(Intent(context, CostCalculatorDashboardActivity::class.java))
        }
    }
}