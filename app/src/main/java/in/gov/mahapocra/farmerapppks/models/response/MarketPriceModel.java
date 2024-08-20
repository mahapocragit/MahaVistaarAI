package in.gov.mahapocra.farmerapppks.models.response;

public class MarketPriceModel {
    String CorpName;
    int imageResourceId;
    public String getCorpName() {
        return CorpName;
    }

    public void setCorpName(String corpName) {
        CorpName = corpName;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

}
