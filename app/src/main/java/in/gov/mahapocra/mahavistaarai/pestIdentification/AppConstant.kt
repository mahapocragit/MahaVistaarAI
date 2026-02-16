package `in`.gov.mahapocra.mahavistaarai.pestIdentification

import `in`.gov.mahapocra.mahavistaarai.BuildConfig

object AppConstant {

    const val PREDICT_URL = "https://ndksp-tih.mahapocra.gov.in/"

//    const val MHVA_URL = "https://stage-farmers-app-api.mahapocra.gov.in/"  // uat env
    const val MHVA_URL = BuildConfig.BASE_URL   // live env
}