package com.storyvendingmachine.www.mv;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.media.Image;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kakao.auth.Session;
import com.kakao.usermgmt.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static com.kakao.util.helper.Utility.getPackageInfo;

public class LoginActivity extends AppCompatActivity {

//    private SessionCallback callback;
//    String kakao_id;
        static SessionCallback callback;
        SharedPreferences login_remember;
        SharedPreferences.Editor editor;
        EditText input_email;
        EditText input_password;
         ProgressBar pb;
    /**
     * 로그인 버튼을 클릭 했을시 access token을 요청하도록 설정한다.
     *
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overridePendingTransition(R.anim.fade_in,0);

        getKeyHash();

        login_remember = getSharedPreferences("setting", 0);
        editor = login_remember.edit();

//****************kakao login **************************
        callback = new SessionCallback(this, "LoginActivity");
        callback.pb = (ProgressBar) findViewById(R.id.progressBar);

//        Session.getCurrentSession().clearCallbacks();
        Session.getCurrentSession().addCallback(callback);


//        String kakao_login = login_remember.getString("kakao", "true");
//        if(kakao_login.equals("true")){
//            Log.e("kakao ", "true");
//        }else{
//            Log.e("kakao ", "false");
//        }
//****************kakao login **************************


        input_email = (EditText) findViewById(R.id.input_id);
        input_password = (EditText) findViewById(R.id.input_PW);

        boolean login_success = login_remember.getBoolean("id_pw_match", true);
        if(login_success){
            input_email.setText(login_remember.getString("user_email", ""));
            input_password.setText(login_remember.getString("user_password", ""));
            pb = (ProgressBar) findViewById(R.id.progressBar);
            //////////항상 같이 다녀야합니다
            pb.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            loginButtonClicked();
        }




        Button loginButton = (Button) findViewById(R.id.login_button);
        Button joinButton = (Button) findViewById(R.id.join_button);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_left_bit); //
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pb = (ProgressBar) findViewById(R.id.progressBar);
                //////////항상 같이 다녀야합니다
                pb.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                //////////항상 같이 다녀야합니다
                if(input_email.length() == 0 || input_password.length() == 0){
                    String message = "아이디와 비밀번호를 입력해주세요";
                    notifier_retry(message);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    pb.setVisibility(View.INVISIBLE);
                }else{
                    Log.e("normal_login", "clicked");
                    loginButtonClicked();
                }

            }
        });

    }

    private void getKeyHash() {

        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.storyvendingmachine.www.mv", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }

    @Override
    public void onBackPressed() {
        Log.e("application close", " true");
        Session.getCurrentSession().removeCallback(callback);
        super.onBackPressed();  // optional depending on your needs
    }


    public void notifier_retry(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage(message)
                .setNegativeButton("다시시도", null)
                .create()
                .show();
    }

    private void loginButtonClicked(){
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
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
                                editor.putString("user_email", input_email.getText().toString());
                                editor.putString("user_password", input_password.getText().toString());
                                editor.commit();


                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("login_type", "normal");
                                intent.putExtra("user_email", user_email);
                                intent.putExtra("user_nickname", user_nickname);
                                intent.putExtra("user_thumbnail", user_thumbnail);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                pb.setVisibility(View.INVISIBLE);


                            }else if(login_success_fail.equals("login_fail")){
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                pb.setVisibility(View.INVISIBLE);

                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("존재하지 않는 사용자 입니다. \n 이메일과 비밀번호를 다시 확인해 주세요")
                                        .setNegativeButton("다시시도", null)
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
                params.put("email", input_email.getText().toString());
                params.put("password", input_password.getText().toString());
                return params;
            }
        };
        queue.add(stringRequest);

    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
//            return;
//        }
//
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        getCurrentSession().removeCallback(callback);
//    }

//    private class SessionCallback implements ISessionCallback {
//
//        @Override
//        public void onSessionOpened() {
//            //세션이 연결이 된 상태를 의미한다 -- 이곳에서 카카오톡의 정보들을 가져와야한다
//            Log.d("session connected", "connected");
//            requestMe();
//            //requestprofile();
//          //  redirectSignupActivity();
//        }
//
//        @Override
//        public void onSessionOpenFailed(KakaoException exception) {
//            if(exception != null) {
//                Logger.e(exception);
//            }
//        }
//    }
//
//    protected void redirectSignupActivity() {
//        final Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
//        finish();
//    }

//
//   protected void requestprofile(){
//       com.kakao.kakaotalk.v2.KakaoTalkService.getInstance().requestProfile(new TalkResponseCallback<KakaoTalkProfile>() {
//           @Override
//           public void onNotKakaoTalkUser() {
//
//           }
//
//           @Override
//           public void onSessionClosed(ErrorResult errorResult) {
//
//           }
//
//           @Override
//           public void onNotSignedUp() {
//
//           }
//
//           @Override
//           public void onSuccess(KakaoTalkProfile result) {
//               Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//               intent.putExtra("kakao_id", kakao_id);
//               intent.putExtra("thumb_nail", result.getThumbnailUrl().toString());
//               intent.putExtra("nickname", result.getNickName().toString());
//               final String thumb_nail = result.getThumbnailUrl().toString();
//               final String nickname = result.getNickName().toString();
//
//               RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
//               String url = "http://www.joonandhoon.com/jhmovienote/kakao_id_to_db.php";
//               StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                       new Response.Listener<String>() {
//                           @Override
//                           public void onResponse(String response) {
//
//
//                           }
//                       },
//                       new Response.ErrorListener() {
//                           @Override
//                           public void onErrorResponse(VolleyError error) {
//
//                           }
//                       }
//               ) {
//                   @Override
//                   protected Map<String, String> getParams(){
//                       Map<String, String> params = new HashMap<String, String>();
//                       params.put("existing_id", "true");
//                       params.put("kakao_id", kakao_id);
//                       params.put("thumb_nail", thumb_nail);
//                       params.put("nickname", nickname);
//                       return params;
//                   }
//               };
//
//
//               queue.add(stringRequest);
//
//
//               startActivity(intent);
//               finish();
//
//           }
//       });
//   }
//
//    protected void requestMe(){
//        UserManagement.getInstance().requestMe(new MeResponseCallback() {
//            @Override
//            public void onSessionClosed(ErrorResult errorResult) {
//                Log.d("closed", errorResult.toString());
//            }
//
//            @Override
//            public void onNotSignedUp() {
//                Log.d("onNotSignedup", "not signed up");
//            }
//            String email="";//if final declared then cannot change the value of this so make it global
//            @Override
//            public void onSuccess(final UserProfile result) {
//                Log.d("onsuccess", "on success");
//
//                kakao_id = Integer.toString((int) result.getId());
//                final boolean email_verified = result.getEmailVerified();
//                if(email_verified){
//                    email=result.getEmail().toString();
//                }else{
//                    email="";
//                }
//                final String thumb_nail = result.getThumbnailImagePath().toString();
//                final String nickname = result.getNickname().toString();
//
//                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
//                String url = "http://www.joonandhoon.com/jhmovienote/kakao_id_to_db.php";
//                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                        new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                Toast.makeText(LoginActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
//
//                                if (response.equals("exist")){
//                                    //이미 가입된 회원일때 프로파일 업데이트 하는 과정
//                                    Log.e("가입되어있음", "가입됨");
//                                    requestprofile();
//                                }else{
//                                    if(response.equals("add_success")){
//                                        //처음 로그인 했을때
//                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                        intent.putExtra("kakao_id", Integer.toString((int) result.getId()));
//                                        intent.putExtra("thumb_nail", result.getThumbnailImagePath().toString());
//                                        intent.putExtra("nickname", result.getNickname().toString());
//                                        Toast.makeText(LoginActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
//                                        Log.e("almost", "almost success");
//                                        startActivity(intent);
//                                        finish();
//                                    }else if(response.equals("add_fail")){
//                                        Toast.makeText(LoginActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
//                                    }
//
//                                }
//
//                            }
//                        },
//                        new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//
//                            }
//                        }
//                ) {
//                    @Override
//                    protected Map<String, String> getParams(){
//                        Map<String, String> params = new HashMap<String, String>();
//                        params.put("kakao_id", kakao_id);
//                        params.put("email", email);
//                        params.put("thumb_nail", thumb_nail);
//                        params.put("nickname", nickname);
//                        return params;
//                    }
//                };
//
//
//                queue.add(stringRequest);
//
//
//
//            }
//        });
//    }

}

