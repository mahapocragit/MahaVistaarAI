package com.example.mhvui

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard.my_dashboard.MyDashboardFragment
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard.smart_farming.SmartFarmingFragment
import `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard.AgriServicesFragment

class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    private val fragments = listOf(
        MyDashboardFragment(),
        AgriServicesFragment(),
        SmartFarmingFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}