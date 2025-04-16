package `in`.gov.mahapocra.mahavistaarai.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.weather.FirstFragment
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.weather.FourthFragment
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.weather.SecondFragment
import `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.weather.ThirdFragment

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private val fragments = listOf(
        FirstFragment(),
        SecondFragment(),
        ThirdFragment(),
        FourthFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}