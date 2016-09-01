package com.etna.gunzbu_a.alldj;

/**
 * Created by SuChi on 01/09/2016.
 */
public class Video {

    private String Title;
    private String videoId;
    private String ChannelTitle;
    private String ThumbnailUrl;

    public String getTitle() {
        return Title;
    }
    public String getVideoId() {
        return videoId;
    }
    public String getChannelTitle() {
        return ChannelTitle;
    }
    public String getThumbnailUrl() {
        return ThumbnailUrl;
    }

    public void setTitle(String title) {
        this.Title = title;
    }
    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
    public void setChannelTitle(String ChannelTitle) {
        this.ChannelTitle = ChannelTitle;
    }
    public void setThumbnailUrl(String ThumbnailUrl) {
        this.ThumbnailUrl = ThumbnailUrl;
    }

    public Video (String Title, String videoId, String ChannelTitle, String ThumbnailUrl){
        this.Title = Title;
        this.videoId = videoId;
        this.ChannelTitle = ChannelTitle;
        this.ThumbnailUrl = ThumbnailUrl;
    }

}