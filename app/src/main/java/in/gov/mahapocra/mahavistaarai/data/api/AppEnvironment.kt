package `in`.gov.mahapocra.mahavistaarai.data.api

class AppEnvironment(val baseUrl: String?) {
    companion object {
        val UAT_DBT: AppEnvironment = AppEnvironment("https://uat-dbt.mahapocra.gov.in:8026/")

//        val FARMER: AppEnvironment = AppEnvironment("https://farmers-app-api.mahapocra.gov.in/"); //For Production
        val FARMER: AppEnvironment = AppEnvironment("https://stage-farmers-app-api.mahapocra.gov.in/") //For Internal Testing
        val WOTR: AppEnvironment = AppEnvironment("https://kisan.wotr.org.in/")
        val GIS: AppEnvironment = AppEnvironment("https://gis.mahapocra.gov.in/")
        val PANI_FOUNDATION: AppEnvironment = AppEnvironment("https://ianm-preprod.wadhwaniai.org/")
        val VISTAAR: AppEnvironment = AppEnvironment("https://vistaar.maharashtra.gov.in/")
        val BOT_URL: AppEnvironment = AppEnvironment("https://prodaskvistaar.mahapocra.gov.in/?token=")
    }
}
