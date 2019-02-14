package com.storyvendingmachine.www.mv;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static com.storyvendingmachine.www.mv.MainActivity.LoginType;

/**
 * Created by Administrator on 2018-03-15.
 */

public class uploadRequest_ext_writefragment extends StringRequest{
    final static private String URL = "http://joonandhoon.dothome.co.kr/jhmovienote/gettext_and_save.php";
    private Map<String, String> parameter;

    public uploadRequest_ext_writefragment(String kakao_id, String norm_user_email, String title, String main_text, String img_name, String img_rotate, String alignment, String width_and_height, String public_private, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameter= new HashMap<>();
        parameter.put("login_type", LoginType);
        parameter.put("kakao_id", kakao_id);
        parameter.put("norm_user_email", norm_user_email);
        parameter.put("movie_title", title);
        parameter.put("main_text", main_text);
        parameter.put("img_name", img_name);
        parameter.put("img_rotate", img_rotate);
        parameter.put("txt_alignment", alignment);
        parameter.put("width_and_height", width_and_height);
        parameter.put("public_private", public_private);
    }
    @Override
    public Map<String, String> getParams(){
        return parameter;
    }
}
