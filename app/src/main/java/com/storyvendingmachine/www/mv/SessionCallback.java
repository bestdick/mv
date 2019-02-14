package com.storyvendingmachine.www.mv;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
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
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.kakaotalk.response.KakaoTalkProfile;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

/**
 * Created by Administrator on 2018-03-18.
 */

public class SessionCallback implements ISessionCallback {
private Context context;
String from;

String kakao_id;
String email;

ProgressDialog dialog;

ProgressBar pb;


    SharedPreferences login_remember;
    SharedPreferences.Editor editor;

    public SessionCallback(Context context, String from){
            this.context=context;
            this.from = from;
        }

    @Override
    public void onSessionOpened() {
        login_remember = context.getSharedPreferences("setting", 0);
        editor = login_remember.edit();

        pb.setVisibility(View.VISIBLE);
        //dialog = ProgressDialog.show(context, "", "로그인 중...", true);
        UserManagement.getInstance().requestMe(new MeResponseCallback() {

            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Log.e("onFailure", "fail");
                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    //에러로 인한 로그인 실패
//                        finish();
                } else {
                    //redirectMainActivity();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.e("onSessionClosed", "fail");
                Session.getCurrentSession().close();
            }

            @Override
            public void onNotSignedUp() {
                Log.e("onNotsignedUp", "fail");

            }

            @Override
            public void onSuccess(final UserProfile userProfile) {
                Log.e("Session opened ::", "true");
                //로그인에 성공하면 로그인한 사용자의 일련번호, 닉네임, 이미지url등을 리턴합니다.
                //사용자 ID는 보안상의 문제로 제공하지 않고 일련번호는 제공합니다.

                Log.e("UserProfile", userProfile.toString());

                long number = userProfile.getId();

                kakao_id = Integer.toString((int) userProfile.getId());
                Log.e("카카오 아이디", kakao_id);
                final boolean email_verified = userProfile.getEmailVerified();
                if(email_verified){
                    email=userProfile.getEmail();
                }else{
                    email="";
                }
                Log.e("이메일 주소", email);
                final String thumb_nail;
                if(userProfile.getThumbnailImagePath() == null){
                    thumb_nail="thumb_nail_empty";
                }else{
                    thumb_nail = userProfile.getThumbnailImagePath().toString();
                }
                Log.e("썸내일 주소", thumb_nail);
                final String nickname = userProfile.getNickname().toString();
                Log.e("닉네임", nickname);

                RequestQueue queue = Volley.newRequestQueue(context);
                String url = "http://www.joonandhoon.com/jhmovienote/kakao_id_to_db.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                if (response.equals("exist")){
                                    //이미 가입된 회원일때 프로파일 업데이트 하는 과정
                                    Log.e("가입되어있음", "가입됨");
                                    requestprofile();
                                }else{
                                    if(response.equals("add_success")){
                                        //처음 로그인 했을때
                                        Intent intent = new Intent(context, MainActivity.class);
                                        intent.putExtra("login_type", "kakao");
                                        intent.putExtra("kakao_id", Integer.toString((int) userProfile.getId()));
                                        intent.putExtra("thumb_nail", userProfile.getThumbnailImagePath());
                                        intent.putExtra("nickname", userProfile.getNickname());
                                        //Toast.makeText(LoginActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                                        Log.e("almost", "almost success");
                                        context.startActivity(intent);
                                        //dialog.dismiss();

                                        editor.putString("kakao", "true");
                                        editor.commit();

                                        if(from.equals("LoginActivity")){
                                            ((LoginActivity)context).finish();
                                            ((LoginActivity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        }else{
                                            ((FrontPage)context).finish();
                                            ((FrontPage) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        }


                                        pb.setVisibility(View.INVISIBLE);
                                    }else if(response.equals("add_fail")){
                                        Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();
                                    }

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
                        params.put("kakao_id", kakao_id);
                        params.put("email", email);
                        params.put("thumb_nail", thumb_nail);
                        params.put("nickname", nickname);
                        return params;
                    }
                };
                queue.add(stringRequest);
            }
        });

    }

    @Override
    public void onSessionOpenFailed(KakaoException exception) {
        // 세션 연결이 실패했을때
        // 어쩔때 실패되는지는 테스트를 안해보았음 ㅜㅜw

        Log.e("onSessionOpenFailed", "fail" + exception);

    }




    protected void requestprofile(){
        com.kakao.kakaotalk.v2.KakaoTalkService.getInstance().requestProfile(new TalkResponseCallback<KakaoTalkProfile>() {
            @Override
            public void onNotKakaoTalkUser() {
                Log.e("kakao session", "not kakao user");
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.e("kakao session", "close");
            }

            @Override
            public void onNotSignedUp() {
                Log.e("kakao session", "not kakao not sign up");
            }

            @Override
            public void onSuccess(KakaoTalkProfile result) {
                final Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("login_type", "kakao");
                intent.putExtra("kakao_id", kakao_id);
                intent.putExtra("thumb_nail", result.getThumbnailUrl().toString());
                intent.putExtra("nickname", result.getNickName().toString());
                final String thumb_nail = result.getThumbnailUrl().toString();
                final String nickname = result.getNickName().toString();

                final RequestQueue queue = Volley.newRequestQueue(context);
                String url = "http://www.joonandhoon.com/jhmovienote/kakao_id_to_db.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                            Log.e("are we here?","이미 가입되있는 사람들의 리스판스");

                                editor.putString("kakao", "true");
                                editor.commit();

                            //dialog.dismiss();
                                context.startActivity(intent);
//                                ((LoginActivity)context).finish();
//                                ((LoginActivity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                if(from.equals("LoginActivity")){
                                    ((LoginActivity)context).finish();
                                    ((LoginActivity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                }else{
                                    ((FrontPage)context).finish();
                                    ((FrontPage) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                }
                                pb.setVisibility(View.INVISIBLE);
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
                        params.put("existing_id", "true");
                        params.put("kakao_id", kakao_id);
                        params.put("thumb_nail", thumb_nail);
                        params.put("nickname", nickname);
                        return params;
                    }
                };
                queue.add(stringRequest);




            }
        });
    }




  // last line of class
}
