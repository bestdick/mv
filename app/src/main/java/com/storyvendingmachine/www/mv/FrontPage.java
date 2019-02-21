package com.storyvendingmachine.www.mv;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.MobileAds;
import com.kakao.auth.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import static com.storyvendingmachine.www.mv.LoginActivity.callback;
public class FrontPage extends AppCompatActivity {
    ProgressBar progressBar;
    SharedPreferences login_remember;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);
        MobileAds.initialize(this, getResources().getString(R.string.admob_id));

        login_remember = getSharedPreferences("setting", 0);
        editor = login_remember.edit();

        new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
            @Override
            public void run() {
                // 실행할 동작 코딩
                //front_cover.setVisibility(View.GONE);
                boolean login_success = login_remember.getBoolean("id_pw_match", true);
                if(login_success){
                    String user_email = login_remember.getString("user_email", "");
                    String user_password = login_remember.getString("user_password", "");
                    loginButtonClicked(user_email, user_password);
                }else{
                     String isKakao = login_remember.getString("kakao", "");
                     if(isKakao.equals("true")){
                         callback = new SessionCallback(FrontPage.this, "FrontPage");
                         callback.pb = (ProgressBar) findViewById(R.id.progressBar);

                         Session.getCurrentSession().addCallback(callback);
                         if (Session.getCurrentSession().checkAndImplicitOpen()) {
                             // 액세스토큰 유효하거나 리프레시 토큰으로 액세스 토큰 갱신을 시도할 수 있는 경우
                         } else {
                             // 무조건 재로그인을 시켜야 하는 경우
                             //            Session.getCurrentSession().clearCallbacks();
                         }
                         Log.e("kakao login", "this is kakao login");
                     }else{
                         Intent intent = new Intent(FrontPage.this, LoginActivity.class);
                         startActivity(intent);
                         finish();
                         overridePendingTransition(0, R.anim.fade_out);
                     }
                }
            }
        }, 800);
    }


    private void loginButtonClicked(final String input_user_email, final String input_user_password){

        RequestQueue queue = Volley.newRequestQueue(FrontPage.this);
        String url = "http://www.joonandhoon.com/jhmovienote/normal_login_join_check.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("response");
                            JSONObject temp = jsonArray.getJSONObject(0);

                            String login_success_fail = temp.getString("call");
                            if(login_success_fail.equals("login_success")){
                                String user_email = temp.getString("user_email");
                                String user_nickname = temp.getString("user_nickname");
                                String user_thumbnail = temp.getString("user_thumbnail");

                                editor.putBoolean("id_pw_match", true);
                                editor.putString("user_email", input_user_email);
                                editor.putString("user_password", input_user_password);
                                editor.commit();


                                Intent intent = new Intent(FrontPage.this, MainActivity.class);
                                intent.putExtra("login_type", "normal");
                                intent.putExtra("user_email", user_email);
                                intent.putExtra("user_nickname", user_nickname);
                                intent.putExtra("user_thumbnail", user_thumbnail);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();



                            }else if(login_success_fail.equals("login_fail")){

                                AlertDialog.Builder builder = new AlertDialog.Builder(FrontPage.this);
                                builder.setMessage("존재하지 않는 사용자 입니다.\n로그인 페이지로 이동합니다")
                                        .setPositiveButton("이동", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                editor.putBoolean("id_pw_match", false);
                                                editor.putString("user_email", "");
                                                editor.putString("user_password", "");
                                                editor.commit();
                                                Intent intent = new Intent(FrontPage.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();

                                            }
                                        })
                                        .create()
                                        .show();

                            }else{
                                Log.e("알수 없는 에러 발생", "php 를 켜서 확인 하세요");
                            }

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
                params.put("input_type", "login");
                params.put("email", input_user_email);
                params.put("password", input_user_password);
                return params;
            }
        };
        queue.add(stringRequest);

    }

    @Override
    public void onBackPressed()
    {
        Log.e("application close", " true");
        super.onBackPressed();  // optional depending on your needs
        finish();
    }


}
