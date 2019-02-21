package com.storyvendingmachine.www.mv;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.storyvendingmachine.www.mv.MainActivity.LoggedInUser_ID;
import static com.storyvendingmachine.www.mv.MainActivity.LoggedInUser_nickname;
import static com.storyvendingmachine.www.mv.MainActivity.LoggedInUser_thumbnail;
import static com.storyvendingmachine.www.mv.MainActivity.LoginType;
import static com.storyvendingmachine.www.mv.MainActivity.RESULTCODE_CUSTOME_LOGOUT;
import static com.storyvendingmachine.www.mv.MainActivity.mViewPager;
import static com.storyvendingmachine.www.mv.VERSION.THIS_DEVICE_VERSION;

public class UserInfoSetting extends AppCompatActivity {
    Toolbar myToolbar;

    TextView login_type_textView;
    ImageView user_thumbnail;
    TextView user_id;
    TextView user_nickname;

    TextView total_num_1;//내가 쓴 글의 갯수
    TextView total_num_2;// 미정
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_setting);

        ad();
        total_num_1 = (TextView) findViewById(R.id.total_num_1);
        total_num_2 = (TextView) findViewById(R.id.total_num_2);
        get_toolbar();
        get_info();
        logoutControl();

    }
    public void get_toolbar(){
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("menu_button");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close);
        getSupportActionBar().setTitle("");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리
    }
    public void get_info(){
        if(LoginType.equals("kakao")){
            login_type_textView = (TextView) findViewById(R.id.login_type_textView);
            login_type_textView.setText("카카오 계정");
            user_id = (TextView) findViewById(R.id.user_id_textview);
            user_id.setVisibility(View.INVISIBLE);
            user_nickname = (TextView) findViewById(R.id.user_nickname_textview);
            user_nickname.setText(LoggedInUser_nickname);
            user_thumbnail =(ImageView) findViewById(R.id.user_thumbnail_imageview);
            user_thumbnail.setScaleType(ImageView.ScaleType.FIT_XY);
            loadImageFromUrl(LoggedInUser_thumbnail, user_thumbnail);


            getUserListTotal gULT = new getUserListTotal();
            gULT.total_my_list_textView = total_num_1;
            gULT.total_my_comment_textView = total_num_2;
            gULT.execute();
        }else if(LoginType.equals("normal")){
            login_type_textView = (TextView) findViewById(R.id.login_type_textView);
            login_type_textView.setText("일반 계정");
            user_id = (TextView) findViewById(R.id.user_id_textview);
            user_id.setText(LoggedInUser_ID);
            user_nickname = (TextView) findViewById(R.id.user_nickname_textview);
            user_nickname.setText(LoggedInUser_nickname);
            user_thumbnail =(ImageView) findViewById(R.id.user_thumbnail_imageview);
            user_thumbnail.setScaleType(ImageView.ScaleType.FIT_XY);
            loadImageFromUrl(LoggedInUser_thumbnail, user_thumbnail);

            total_num_1 = (TextView) findViewById(R.id.total_num_1);
            getUserListTotal gULT = new getUserListTotal();
            gULT.total_my_list_textView = total_num_1;
            gULT.total_my_comment_textView = total_num_2;
            gULT.execute();
        }else{}

    }
    public void logoutControl(){
        Button logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "정말 로그아웃 하시겠습니까?";
                String positive_message = "로그아웃";
                String negative_message = "취소";
                notifier_logout(message, positive_message, negative_message);
            }
        });
    }
    public void notifier_logout(String message, String positive_message, String negative_message){
        AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoSetting.this);
        builder.setMessage(message)
                .setPositiveButton(positive_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setResult(RESULTCODE_CUSTOME_LOGOUT);
                        finish();
                    }
                })
                .setNegativeButton(negative_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
    }
    private void loadImageFromUrl(String url, ImageView view) {
        Picasso.with(this)
                .load(url)
                .transform(new CircleTransform())
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
    public class getUserListTotal extends AsyncTask<Void, Void, String> {
        TextView total_my_list_textView;
        TextView total_my_comment_textView;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids){
            RequestQueue queue = Volley.newRequestQueue(UserInfoSetting.this);
            String url = "http://www.joonandhoon.com/jhmovienote/get_my_total.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("user info version", response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("response");
                                JSONObject temp = jsonArray.getJSONObject(0);

                                String total_my_list = temp.getString("total_my_list");
                                String total_my_comment = temp.getString("total_my_comment");


                                Log.e("total_my_comment", total_my_comment);
                                total_my_list_textView.setText(total_my_list);
                                total_my_comment_textView.setText(total_my_comment);

                                String new_version  = jsonObject.getString("response_version");
                                version_check(new_version);


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
                    params.put("login_type", LoginType);
                    params.put("kakao_id", LoggedInUser_ID);
                    params.put("norm_user_email", LoggedInUser_ID);
                    return params;
                }
            };
            queue.add(stringRequest);
            return null;
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
    public void version_check(String new_version){
        TextView this_device_version_textView = (TextView) findViewById(R.id.this_device_version_textView);
        TextView new_version_textView = (TextView) findViewById(R.id.new_version_textView);
        this_device_version_textView.setText("현재 기기 버전 : " + THIS_DEVICE_VERSION);
        new_version_textView.setText("새 버전 : "+new_version);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        //this is only needed if you have specific things
        //that you want to do when the user presses the back button.
        /* your specific things...*/
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_right_bit,R.anim.slide_out);// first entering // second exiting
    }
    public void ad(){
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.e("ad load fail", String.valueOf(errorCode));
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });
    }
}
