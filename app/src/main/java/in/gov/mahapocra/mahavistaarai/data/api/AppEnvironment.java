package in.gov.mahapocra.mahavistaarai.data.api;

public class AppEnvironment {
    private final String baseUrl;

    public AppEnvironment(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public static final AppEnvironment DBT = new AppEnvironment("https://dbt-api.mahapocra.gov.in/SharedAPI/");
    public static final AppEnvironment FARMER = new AppEnvironment("https://farmers-app-api.mahapocra.gov.in/");
    public static final AppEnvironment WOTR = new AppEnvironment("https://kisan.wotr.org.in/");
    public static final AppEnvironment GIS = new AppEnvironment("https://gis.mahapocra.gov.in/");
    public static final AppEnvironment SSO = new AppEnvironment("https://sso.mahapocra.gov.in/");
}
