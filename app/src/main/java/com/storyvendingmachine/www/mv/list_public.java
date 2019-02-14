package com.storyvendingmachine.www.mv;

/**
 * Created by Administrator on 2018-03-22.
 */

public class list_public {
    String database_id;
    String kakao_id;
    String movie_title;
    String main_text;
    String w_date;
    String w_time;
    String w_year;
    String thumb_nail;
    String rotate;
    String hits;
    String user_type;
    String public_private;

    public String getPublic_private() {
        return public_private;
    }

    public void setPublic_private(String public_private) {
        this.public_private = public_private;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getDatabase_id() {
        return database_id;
    }

    public void setDatabase_id(String database_id) {
        this.database_id = database_id;
    }

    public String getHits() {
        return hits;
    }

    public void setHits(String hits) {
        this.hits = hits;
    }


    public String getKakao_id() {
        return kakao_id;
    }

    public void setKakao_id(String kakao_id) {
        this.kakao_id = kakao_id;
    }

    public String getMovie_title() {
        return movie_title;
    }

    public void setMovie_title(String movie_title) {
        this.movie_title = movie_title;
    }

    public String getMain_text() {
        return main_text;
    }

    public void setMain_text(String main_text) {
        this.main_text = main_text;
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

    public String getThumb_nail() {
        return thumb_nail;
    }

    public void setThumb_nail(String thumb_nail) {
        this.thumb_nail = thumb_nail;
    }

    public String getRotate() {
        return rotate;
    }

    public void setRotate(String rotate) {
        this.rotate = rotate;
    }

    public list_public(String database_id, String kakao_id, String movie_title, String main_text, String w_date, String w_time, String w_year, String thumb_nail, String rotate, String hits, String user_type, String public_private){
        this.database_id = database_id;
        this.kakao_id = kakao_id;
        this.movie_title = movie_title;
        this.main_text = main_text;
        this.w_date = w_date;
        this.w_time = w_time;
        this.w_year = w_year;
        this.thumb_nail = thumb_nail;
        this.rotate = rotate;
        this.hits = hits;
        this.user_type =user_type;
        this.public_private = public_private;
    }
}
