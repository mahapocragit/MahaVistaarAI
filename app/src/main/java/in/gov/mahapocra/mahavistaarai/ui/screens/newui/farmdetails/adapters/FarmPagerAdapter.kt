package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails.fragments.FarmDetailsFragment
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails.fragments.FarmHistoryFragment
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.farmdetails.fragments.FarmObservationFragment

class FarmPagerAdapter(activity: FragmentActivity, private var farmData: String) : FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FarmDetailsFragment.newInstance(farmData)
            1 -> FarmObservationFragment()
            2 -> FarmHistoryFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

    override fun getItemCount(): Int { return 3 }
}