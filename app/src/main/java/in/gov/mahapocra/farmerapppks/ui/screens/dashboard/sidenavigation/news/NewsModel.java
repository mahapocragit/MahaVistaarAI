package in.gov.mahapocra.farmerapppks.ui.screens.dashboard.sidenavigation.news;

import org.json.JSONObject;

import in.co.appinventor.services_api.app_util.AppUtility;

public class NewsModel {

    private String id;
    private String newsHeading;
    private String date;
    private String news;
    private String headerImage;


    private JSONObject jsonObject;


    public NewsModel(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

//    public String getId() {
//        id = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "id");
//        return id;
//    }

    public String getHeaderImage() {
        headerImage = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "HeaderImage");
        return headerImage;
    }
    public String getNews() {
        news = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "News");
        return news;
    }

    public String getNewsHeading() {
        newsHeading = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "NewsHeading");
        return newsHeading;
    }

    public String getDate() {
        date = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "Date");
        return date;
    }



}
