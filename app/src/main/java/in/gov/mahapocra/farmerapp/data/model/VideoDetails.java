package in.gov.mahapocra.farmerapp.data.model;

import java.util.ArrayList;
public class VideoDetails {



    private int id;
    private String titles;
    private ArrayList<CropsCategName> moviesImagesList;

    public VideoDetails(int id, String titles, ArrayList<CropsCategName> moviesImagesList) {
        this.id = id;
        this.titles = titles;
        this.moviesImagesList = moviesImagesList;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitles(String titles) {
        this.titles = titles;
    }

    public void setMoviesImagesList(ArrayList<CropsCategName> moviesImagesList) {
        this.moviesImagesList = moviesImagesList;
    }

    public int getId() {
        return id;
    }

    public String getTitles() {
        return titles;
    }

    public ArrayList<CropsCategName> getMoviesImagesList() {
        return moviesImagesList;
    }
}
