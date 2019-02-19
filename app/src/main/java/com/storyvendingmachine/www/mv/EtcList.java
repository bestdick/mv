package com.storyvendingmachine.www.mv;

public class EtcList {
    String primary_key;
    String login_type;
    String user_id;
    String user_nickname;
    String type;
    String title;
    String content;
    String upload_date;
    String upload_time;
    String isNew;

    public String getPrimary_key() {
        return primary_key;
    }

    public void setPrimary_key(String primary_key) {
        this.primary_key = primary_key;
    }

    public String getLogin_type() {
        return login_type;
    }

    public void setLogin_type(String login_type) {
        this.login_type = login_type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUpload_date() {
        return upload_date;
    }

    public void setUpload_date(String upload_date) {
        this.upload_date = upload_date;
    }

    public String getUpload_time() {
        return upload_time;
    }

    public void setUpload_time(String upload_time) {
        this.upload_time = upload_time;
    }

    public String getIsNew() {
        return isNew;
    }

    public void setIsNew(String isNew) {
        this.isNew = isNew;
    }

    public EtcList(String primary_key, String login_type, String user_id, String user_nickname, String type, String title, String content, String upload_date, String upload_time, String isNew) {
        this.primary_key = primary_key;
        this.login_type = login_type;
        this.user_id = user_id;
        this.user_nickname = user_nickname;
        this.type = type;
        this.title = title;
        this.content = content;
        this.upload_date = upload_date;
        this.upload_time = upload_time;
        this.isNew = isNew;
    }
}
