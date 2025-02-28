package in.gov.mahapocra.farmerapppks.data.model;



public class ClimateGridModel {

    private String climate_name;
    private String climate_image;
    private String webUrl;

    public ClimateGridModel(String climate_name, String climate_image,String webUrl ) {
        this.climate_name = climate_name;
        this.climate_image = climate_image;
        this.webUrl = webUrl;
    }
    public String getWebUrl() {
        return webUrl;
    }
    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

        public String getClimate_name() {
            return climate_name;
        }
        public void setClimate_name(String climate_name) {
            this.climate_name = climate_name;
        }

    public String getClimate_image() {
        return climate_image;
    }
    public void setClimate_image(String climate_image) {
        this.climate_image = climate_image;
    }
}
