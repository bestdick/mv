package com.storyvendingmachine.www.mv;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Administrator on 2019-02-17.
 */

public class MovieListAdapter extends BaseAdapter{

    private Context context;
    private List<MovieList> list;


    public MovieListAdapter(Context context, List<MovieList> list) {
        this.context = context;
        this.list= list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v =View.inflate(context, R.layout.container_list_element, null);
        Log.e("are we here 4", "true");
        ImageView thumbnail_imageView = (ImageView) v.findViewById(R.id.thumbnail_imageView);
        TextView title_textView = (TextView) v.findViewById(R.id.title_textView);
        TextView genre_textView = (TextView) v.findViewById(R.id.genre_textview);
        TextView content_textView = (TextView) v.findViewById(R.id.content_textView);
        TextView upload_date = (TextView) v.findViewById(R.id.upload_date_textView);

        ImageView author_thumbnail_imageView = (ImageView) v.findViewById(R.id.author_thumbnail_imageView);
        TextView author_nickname_textView = (TextView) v.findViewById(R.id.author_nickname_textView);
        TextView view_count_textView = (TextView) v.findViewById(R.id.count_view_textView);

        String primary_key = list.get(i).getPrimary_key();
        String LoginType = list.get(i).getLoginType();
        String user_id = list.get(i).getUser_id();
        String user_nickname = list.get(i).getUser_nickname();
        String user_thumbnail = list.get(i).getUser_thumbnail();
        String movie_title = list.get(i).getMovie_title();
        String content_alignment = list.get(i).getContent_alignment();
        String w_date = list.get(i).getW_date();
        String w_time = list.get(i).getW_time();
        String w_year = list.get(i).getW_year();
        String hits = list.get(i).getHits();
        String public_private = list.get(i).getPublic_private();
        JSONArray movie_content = list.get(i).getMovie_content();
        JSONArray movie_images = list.get(i).getMovie_images();
        JSONArray movie_images_rotation = list.get(i).getMovie_images_rotation();
        JSONArray movie_images_width_height = list.get(i).getMovie_images_width_height();

        title_textView.setText(movie_title);
        try {
            String[] thumbnail_image_address = isImageExist(movie_images, movie_images_rotation);// 0 address , 1 rotation of picture
            if(thumbnail_image_address[0].equals("image_not_exist") || thumbnail_image_address[1].equals("image_not_exist")){

            }else{
                load_movie_thumbnail(thumbnail_image_address[0], thumbnail_imageView,  thumbnail_image_address[1]);
            }
            content_textView.setText(makePreviewMainText(movie_content, 26));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        load_author_thumbnail(author_thumbnail_imageView, user_thumbnail);
        author_nickname_textView.setText(user_nickname);
        upload_date.setText(w_date);
        view_count_textView.setText("뷰 " + hits);

        v.setTag(list.get(i));
        return v;
    }


    public String[] isImageExist(JSONArray jsonArray, JSONArray jsonArray2) throws JSONException{
        String[] return_str = new String[2];
        return_str[0] = "image_not_exist";
        return_str[1] = "image_not_exist";
        for(int i = 0 ; i < jsonArray.length(); i++){
            String image_address = jsonArray.getString(i);
            String rotation = jsonArray2.getString(i);
            if(!image_address.equals("image_not_exist")){
                return_str[0] = image_address;
                return_str[1] = rotation;
                return return_str;
            }
        }
        return return_str;
    }
    public void load_author_thumbnail(ImageView imageView, String url){
            Picasso.with(context)
                    .load(url)
                    .transform(new CircleTransform())
                    .into(imageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }
                        @Override
                        public void onError() {

                        }
                    });
    }
    public void load_movie_thumbnail(String url, ImageView view, String rotation){
        int degree = Integer.parseInt(rotation);
        if(degree <=360){
            Log.e("current degree :", Integer.toString(degree));
            //         height = rotate_image_size(degree);
        }else{
            degree = degree%360;
            Log.e("current degree :", Integer.toString(degree));
            //        height = rotate_image_size(degree);
        }
        if(url == null) {
            Log.e("no image url", "url empty");
        }else if(url.equals("")){
            Log.e("no image url", "url empty");
        }else {
            Picasso.with(context)
                    .load(url)
                    .rotate(degree)
                    .resize(80, 100)
                    .centerCrop()
                    .into(view, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    Log.e("loadImageFromUrl", "success");
                }

                @Override
                public void onError() {
                    Log.e("load image", "fail to load images ");
                }
            });
        }
    }
        private void loadImageFromUrl(String url, ImageView view, String rotation) {
        // url은 array 이로 존재하며 ##$$##$$ 로 구분 되어져있다
        // url 의 첫번째 element 는 url 이며  두번째 element 는 rotate 할 것으 몇번째 인가를 나타낸다
        String[] URL = url.split("##\\$\\$##\\$\\$");
        int count = Integer.parseInt(URL[1]);
        String[] rot_temp = rotation.split("##\\$\\$##\\$\\$");

        int degree = Integer.parseInt(rot_temp[count]);
        if(degree <=360){
            Log.e("current degree :", Integer.toString(degree));
            //         height = rotate_image_size(degree);
        }else{
            degree = degree%360;
            Log.e("current degree :", Integer.toString(degree));
            //        height = rotate_image_size(degree);
        }
        if(url == null) {
            Log.e("no image url", "url empty");
        }else if(url.equals("")){
            Log.e("no image url", "url empty");
        }else {
            Picasso.with(context).load(URL[0]).rotate(degree).resize(80, 100).centerCrop().into(view, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    Log.e("loadImageFromUrl", "success");
                }

                @Override
                public void onError() {
                    Log.e("load image", "fail to load images ");
                }
            });
        }
    }
    protected  String makePreviewMainText(JSONArray jsonArray, int maxText) throws JSONException {
        String return_string;
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < jsonArray.length(); i++){
            String temp_str = jsonArray.getString(i);
            if(temp_str.equals("leave_it_empty")){
                stringBuilder.append("");
            }else{
                stringBuilder.append(temp_str);
            }
        }
        if(stringBuilder.length()<=maxText){
            return_string = stringBuilder.toString();
        }else{
            return_string = strCut(stringBuilder.toString(), maxText);
        }


        return return_string.trim();
    }
    protected String strCut(String szText, int nLength) { // 문자열 자르기
        String r_val = szText;
        int oF = 0, oL = 0, rF = 0, rL = 0;
        int nLengthPrev = 0;
        try {
            byte[] bytes = r_val.getBytes("UTF-8"); // 바이트로 보관
            // x부터 y길이만큼 잘라낸다. 한글안깨지게.
            int j = 0;
            if (nLengthPrev > 0)
                while (j < bytes.length) {
                    if ((bytes[j] & 0x80) != 0) {
                        oF += 2;
                        rF += 3;
                        if (oF + 2 > nLengthPrev) {
                            break;
                        }
                        j += 3;
                    } else {
                        if (oF + 1 > nLengthPrev) {
                            break;
                        }
                        ++oF;
                        ++rF;
                        ++j;
                    }
                }
            j = rF;
            while (j < bytes.length) {
                if ((bytes[j] & 0x80) != 0) {
                    if (oL + 2 > nLength) {
                        break;
                    }
                    oL += 2;
                    rL += 3;
                    j += 3;
                } else {
                    if (oL + 1 > nLength) {
                        break;
                    }
                    ++oL;
                    ++rL;
                    ++j;
                }
            }
            r_val = new String(bytes, rF, rL, "UTF-8"); // charset 옵션
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return r_val;
    }
}
