package `in`.gov.mahapocra.mahavistaarai.util.app_util

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class SideNavMenuHelper private constructor() {
    val menuOption: JSONArray
        get() {
            val jsonArray = JSONArray()

            try {
                val profileObject = JSONObject()
                profileObject.put("id", 0)
                profileObject.put("name", "My Profile")
                profileObject.put("icon", "myprofile")

                val aboutObject = JSONObject()
                aboutObject.put("id", 1)
                aboutObject.put("name", "About")
                aboutObject.put("icon", "about_ic")

                val expertsObject = JSONObject()
                expertsObject.put("id", 2)
                expertsObject.put("name", "Experts Corner")
                expertsObject.put("icon", "experts_ic")

                val partnerObject = JSONObject()
                partnerObject.put("id", 3)
                partnerObject.put("name", "Credits")
                partnerObject.put("icon", "partners_ic")

                val newsObject = JSONObject()
                newsObject.put("id", 4)
                newsObject.put("name", "News")
                newsObject.put("icon", "news_ic")

                val costCalculatorObject = JSONObject()
                costCalculatorObject.put("id", 5)
                costCalculatorObject.put("name", "Cost Calculator")
                costCalculatorObject.put("icon", "cost_calculator_ic")

                val leaderboardObject = JSONObject()
                leaderboardObject.put("id", 8)
                leaderboardObject.put("name", "Leaderboard")
                leaderboardObject.put("icon", "leaderboard_icon")

                val logoutObject = JSONObject()
                logoutObject.put("id", 7)
                logoutObject.put("name", "Logout")
                logoutObject.put("icon", "logout")

                jsonArray.put(profileObject)
//                jsonArray.put(expertsObject)
//                jsonArray.put(newsObject)
                jsonArray.put(leaderboardObject)
                jsonArray.put(costCalculatorObject)
                jsonArray.put(partnerObject)
                jsonArray.put(aboutObject)
                jsonArray.put(logoutObject)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return jsonArray
        }

    val menuOptionMarathi: JSONArray
        get() {
            val jsonArray = JSONArray()
            try {
                val profileObject = JSONObject()
                profileObject.put("id", 0)
                profileObject.put("name", "माझी प्रोफाईल")
                profileObject.put("icon", "myprofile")

                val aboutObject = JSONObject()
                aboutObject.put("id", 1)
                aboutObject.put("name", "आमच्या विषयी")
                aboutObject.put("icon", "about_ic")

                val expertsObject = JSONObject()
                expertsObject.put("id", 2)
                expertsObject.put("name", "Experts Corner")
                expertsObject.put("icon", "experts_ic")

                val partnerObject = JSONObject()
                partnerObject.put("id", 3)
                partnerObject.put("name", "भागीदार")
                partnerObject.put("icon", "partners_ic")

                val newsObject = JSONObject()
                newsObject.put("id", 4)
                newsObject.put("name", "बातम्या")
                newsObject.put("icon", "news_ic")

                val costCalculatorObject = JSONObject()
                costCalculatorObject.put("id", 5)
                costCalculatorObject.put("name", "खर्च गणक")
                costCalculatorObject.put("icon", "cost_calculator_ic")

                val leaderboardObject = JSONObject()
                leaderboardObject.put("id", 8)
                leaderboardObject.put("name", "लीडरबोर्ड")
                leaderboardObject.put("icon", "leaderboard_icon")

                val logoutObject = JSONObject()
                logoutObject.put("id", 7)
                logoutObject.put("name", "बाहेर पडा")
                logoutObject.put("icon", "logout")

                jsonArray.put(profileObject)
//                jsonArray.put(expertsObject)
//                jsonArray.put(newsObject)
                jsonArray.put(leaderboardObject)
                jsonArray.put(costCalculatorObject)
                jsonArray.put(partnerObject)
                jsonArray.put(aboutObject)
                jsonArray.put(logoutObject)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return jsonArray
        }

    val forGuestOption: JSONArray
        get() {
            val jsonArray = JSONArray()

            try {
                val aboutObject = JSONObject()
                aboutObject.put("id", 1)
                aboutObject.put("name", "About")
                aboutObject.put("icon", "about_ic")

                val expertsObject = JSONObject()
                expertsObject.put("id", 2)
                expertsObject.put("name", "Experts Corner")
                expertsObject.put("icon", "experts_ic")

                val creditObject = JSONObject()
                creditObject.put("id", 3)
                creditObject.put("name", "Credits")
                creditObject.put("icon", "partners_ic")

                val newsObject = JSONObject()
                newsObject.put("id", 4)
                newsObject.put("name", "News")
                newsObject.put("icon", "news_ic")

                val costCalculatorObject = JSONObject()
                costCalculatorObject.put("id", 5)
                costCalculatorObject.put("name", "Cost Calculator")
                costCalculatorObject.put("icon", "cost_calculator_ic")

                val profileObject = JSONObject()
                profileObject.put("id", 6)
                profileObject.put("name", "Login/Registration")
                profileObject.put("icon", "myprofile")

                val leaderboardObject = JSONObject()
                leaderboardObject.put("id", 8)
                leaderboardObject.put("name", "Leaderboard")
                leaderboardObject.put("icon", "leaderboard_icon")

                jsonArray.put(profileObject)
//                jsonArray.put(expertsObject)
//                jsonArray.put(newsObject)
                jsonArray.put(leaderboardObject)
                jsonArray.put(costCalculatorObject)
                jsonArray.put(creditObject)
                jsonArray.put(aboutObject)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return jsonArray
        }

    val menuOptionForGuestMarathi: JSONArray
        get() {
            val jsonArray = JSONArray()
            try {
                val aboutObject = JSONObject()
                aboutObject.put("id", 1)
                aboutObject.put("name", "आमच्या विषयी")
                aboutObject.put("icon", "about_ic")

                val expertsObject = JSONObject()
                expertsObject.put("id", 2)
                expertsObject.put("name", "Experts Corner")
                expertsObject.put("icon", "experts_ic")

                val creditObject = JSONObject()
                creditObject.put("id", 3)
                creditObject.put("name", "भागीदार")
                creditObject.put("icon", "partners_ic")

                val newsObject = JSONObject()
                newsObject.put("id", 4)
                newsObject.put("name", "बातम्या")
                newsObject.put("icon", "news_ic")

                val costCalculatorObject = JSONObject()
                costCalculatorObject.put("id", 5)
                costCalculatorObject.put("name", "खर्च गणक")
                costCalculatorObject.put("icon", "cost_calculator_ic")

                val profileObject = JSONObject()
                profileObject.put("id", 6)
                profileObject.put("name", "लॉगइन/नोंदणी")
                profileObject.put("icon", "myprofile")

                val leaderboardObject = JSONObject()
                leaderboardObject.put("id", 8)
                leaderboardObject.put("name", "लीडरबोर्ड")
                leaderboardObject.put("icon", "leaderboard_icon")

                jsonArray.put(profileObject)
//                jsonArray.put(expertsObject)
//                jsonArray.put(newsObject)
                jsonArray.put(leaderboardObject)
                jsonArray.put(costCalculatorObject)
                jsonArray.put(creditObject)
                jsonArray.put(aboutObject)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return jsonArray
        }

    companion object {
        val instance: SideNavMenuHelper = SideNavMenuHelper()
    }
}
