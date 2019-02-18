package com.storyvendingmachine.www.mv;

import org.json.JSONArray;

/**
 * Created by Administrator on 2019-02-17.
 */

 class MovieList{
    String primary_key;
    String LoginType;
    String user_id;
    String user_nickname;
    String user_thumbnail;
    String movie_title;
    String content_alignment;
    String w_date;
    String w_time;
    String w_year;
    String hits;
    String public_private;
    JSONArray movie_content;
    JSONArray movie_images;
    JSONArray movie_images_rotation;
    JSONArray movie_images_width_height;


    public String getPrimary_key() {
        return primary_key;
    }

    public void setPrimary_key(String primary_key) {
        this.primary_key = primary_key;
    }

    public String getLoginType() {
        return LoginType;
    }

    public void setLoginType(String loginType) {
        LoginType = loginType;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getUser_thumbnail() {
        return user_thumbnail;
    }

    public void setUser_thumbnail(String user_thumbnail) {
        this.user_thumbnail = user_thumbnail;
    }

    public String getMovie_title() {
        return movie_title;
    }

    public void setMovie_title(String movie_title) {
        this.movie_title = movie_title;
    }

    public String getContent_alignment() {
        return content_alignment;
    }

    public void setContent_alignment(String content_alignment) {
        this.content_alignment = content_alignment;
    }

    public String getW_date() {
        return w_date;
    }

    public void setW_date(String w_date) {
        this.w_date = w_date;
    }

    public String getW_time() {
        return w_time;
    }

    public void setW_time(String w_time) {
        this.w_time = w_time;
    }

    public String getW_year() {
        return w_year;
    }

    public void setW_year(String w_year) {
        this.w_year = w_year;
    }

    public String getHits() {
        return hits;
    }

    public void setHits(String hits) {
        this.hits = hits;
    }

    public String getPublic_private() {
        return public_private;
    }

    public void setPublic_private(String public_private) {
        this.public_private = public_private;
    }

    public JSONArray getMovie_content() {
        return movie_content;
    }

    public void setMovie_content(JSONArray movie_content) {
        this.movie_content = movie_content;
    }

    public JSONArray getMovie_images() {
        return movie_images;
    }

    public void setMovie_images(JSONArray movie_images) {
        this.movie_images = movie_images;
    }

    public JSONArray getMovie_images_rotation() {
        return movie_images_rotation;
    }

    public void setMovie_images_rotation(JSONArray movie_images_rotation) {
        this.movie_images_rotation = movie_images_rotation;
    }

    public JSONArray getMovie_images_width_height() {
        return movie_images_width_height;
    }

    public void setMovie_images_width_height(JSONArray movie_images_width_height) {
        this.movie_images_width_height = movie_images_width_height;
    }

    public MovieList(String primary_key, String loginType, String user_id, String user_nickname, String user_thumbnail, String movie_title, String content_alignment, String w_date, String w_time, String w_year, String hits, String public_private, JSONArray movie_content, JSONArray movie_images, JSONArray movie_images_rotation, JSONArray movie_images_width_height) {
        this.primary_key = primary_key;
        LoginType = loginType;
        this.user_id = user_id;
        this.user_nickname = user_nickname;
        this.user_thumbnail = user_thumbnail;
        this.movie_title = movie_title;
        this.content_alignment = content_alignment;
        this.w_date = w_date;
        this.w_time = w_time;
        this.w_year = w_year;
        this.hits = hits;
        this.public_private = public_private;
        this.movie_content = movie_content;
        this.movie_images = movie_images;
        this.movie_images_rotation = movie_images_rotation;
        this.movie_images_width_height = movie_images_width_height;
    }
}
