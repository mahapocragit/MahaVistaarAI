package in.gov.mahapocra.mahavistaarai.data.api;

public class AppEnvironment {
    private final String baseUrl;

    public AppEnvironment(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public static final AppEnvironment UAT_DBT = new AppEnvironment("https://uat-dbt.mahapocra.gov.in:8026/");
    public static final AppEnvironment FARMER = new AppEnvironment("https://farmers-app-api.mahapocra.gov.in/"); //For Production
//    public static final AppEnvironment FARMER = new AppEnvironment("https://stage-farmers-app-api.mahapocra.gov.in/"); //For Internal Testing
    public static final AppEnvironment WOTR = new AppEnvironment("https://kisan.wotr.org.in/");
    public static final AppEnvironment GIS = new AppEnvironment("https://gis.mahapocra.gov.in/");
    public static final AppEnvironment PANI_FOUNDATION = new AppEnvironment("https://ianm-preprod.wadhwaniai.org/");
    public static final AppEnvironment VISTAAR = new AppEnvironment("https://vistaar.maharashtra.gov.in/");
}
