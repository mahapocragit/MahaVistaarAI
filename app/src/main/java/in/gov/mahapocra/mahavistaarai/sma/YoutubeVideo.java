package in.gov.mahapocra.mahavistaarai.sma;

//public class YoutubeVideo {
//    String videoId;
//
//    public YoutubeVideo(String videoId) {
//        this.videoId = videoId;
//    }
//
//    public String getVideoId() {
//        return videoId;
//    }
//}
public class YoutubeVideo {
    String videoId, title, channel, views;

    public YoutubeVideo(String videoId, String title, String channel, String views) {
        this.videoId = videoId;
        this.title = title;
        this.channel = channel;
        this.views = views;
    }
    public String getVideoId() { return videoId; }
    public String getTitle() { return title; }
    public String getChannel() { return channel; }
    public String getViews() { return views; }
}
