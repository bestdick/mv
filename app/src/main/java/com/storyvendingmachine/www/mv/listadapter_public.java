package com.storyvendingmachine.www.mv;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.storyvendingmachine.www.mv.MainActivity.LoginType;

/**
 * Created by Administrator on 2018-03-22.
 */

public class listadapter_public extends BaseAdapter {
    private Context context;
    private List<list_public> list;


    public listadapter_public(Context context, List<list_public> list) {
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
        View v =View.inflate(context, R.layout.list_fragment_container, null);
        Log.e("are we here 4", "true");
        ImageView thumbnail = (ImageView) v.findViewById(R.id.thumbnail_imageView);
        TextView title_textView = (TextView) v.findViewById(R.id.read_title_textView);
        TextView main_textView = (TextView) v.findViewById(R.id.read_main_textView);
        TextView date_textView = (TextView) v.findViewById(R.id.read_date_textView);
        TextView hit_textView = (TextView) v.findViewById(R.id.read_hit_textView);
        TextView read_author_textView = (TextView) v.findViewById(R.id.read_author_textView);

        if(list.get(i).getThumb_nail().equals("image_not_exist")){

        }else{
            loadImageFromUrl(list.get(i).getThumb_nail(), thumbnail, list.get(i).getRotate());
        }
        title_textView.setText(list.get(i).getMovie_title());
        main_textView.setText(makePreviewMainText(list.get(i).getMain_text()));
        date_textView.setText(list.get(i).getW_date());
        hit_textView.setText(list.get(i).getHits());
        String login_type = list.get(i).getUser_type();

        String public_private = list.get(i).getPublic_private();

        if(public_private.equals("public")){
            //public 일때
            TextView tv = (TextView) v.findViewById(R.id.public_private_textview);
            tv.setText("공유됨");
        }else{
            //private 일때
            TextView tv = (TextView) v.findViewById(R.id.public_private_textview);
            tv.setText("나의 보관함");
        }


        backgroundTask bt = new backgroundTask();
        bt.id = list.get(i).getKakao_id();
        bt.tv = read_author_textView;
        bt.list_login_type = login_type;
        bt.execute();

        Log.e("are we here 5", "true");

        v.setTag(list.get(i));

        return v;
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


    protected  String makePreviewMainText(String main_text){
        int previewMainTextLength = 76;
        String return_string;
        String temp_text = main_text;
        String[] temp_array = temp_text.split("##\\$\\$##\\$\\$");
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < temp_array.length; i++){
            if(temp_array[i].equals("leave_it_empty")){
                stringBuilder.append("");
            }else{
                stringBuilder.append(temp_array[i]);
            }
        }

        if(stringBuilder.length()<=previewMainTextLength){
            return_string = stringBuilder.toString();
        }else{
            return_string = strCut(stringBuilder.toString(), previewMainTextLength);
        }


        return return_string;
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




    public class backgroundTask extends AsyncTask<Void, Void, String> {
        String list_login_type;
        String nn;
        String id; // input
        TextView tv;// input

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids){

            RequestQueue queue = Volley.newRequestQueue(context);
            String url = "http://www.joonandhoon.com/jhmovienote/nickname_by_kakao_id.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                tv.setText(jsonObject.getString("nickname"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams(){
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_type", list_login_type);
                    params.put("author", id);
                    return params;
                }
            };
            queue.add(stringRequest);
            return nn;
        }

        @Override
        protected void onProgressUpdate(Void... voids) {
            //** INPUT VALUSE IS THE MIDDLE VOID **
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }
}




}
