package com.appaspect.news.androidnewslibrary.data;

public class RssFeedModel {

    public String title;
    public String link;
    public String description;
    public String pubDate;
    public String img_url;

    public RssFeedModel(String title, String link, String description,String pubDate,String img_url) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.pubDate = pubDate;
        this.img_url = img_url;
    }
}
