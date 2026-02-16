package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.sidenavigation.costcalculator

import org.json.JSONObject

interface OnDeleteClick {

    fun onDeleteClick(cropId: Int, data: JSONObject = JSONObject())

}