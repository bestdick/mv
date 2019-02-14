package com.storyvendingmachine.www.mv;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kakao.auth.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.storyvendingmachine.www.mv.LoginActivity.callback;

public class JoinActivity extends AppCompatActivity {
    Toolbar myToolbar;

    EditText user_email;
    TextView email_check;
    EditText user_password;
    EditText user_password_confirm;
    TextView password_check;
    EditText user_name;
    EditText user_nickname;
    TextView nickname_check;
    Button register_button;

    Boolean user_email_boolean = false;
    Boolean user_password_check_boolean = false;
    Boolean nickname_check_boolean =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        toolbar();

        user_email = (EditText) findViewById(R.id.r_userEmailTextbox);
        email_check = (TextView) findViewById(R.id.r_emailcheck);
        user_password = (EditText) findViewById(R.id.r_userPasswordTextbox);
        user_password_confirm = (EditText) findViewById(R.id.r_userPasswordConfirm);
        password_check = (TextView) findViewById(R.id.r_passwordCheck);
        user_name = (EditText) findViewById(R.id.r_userName);
        user_nickname = (EditText) findViewById(R.id.r_userNickname);
        nickname_check = (TextView) findViewById(R.id.r_nicknamecheck);
        register_button = (Button) findViewById(R.id.r_register);


        user_email_text_listener();
        user_email_text_focus_listener();
        password_text_listener();
        nickname_listener();
        register_button_clicked();
    }


    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if(view !=null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void notifier_retry(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setNegativeButton("다시시도", null)
                .create()
                .show();
    }

    public void notifier_success(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .create()
                .show();
    }


    public void register_button_clicked(){
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                if(user_email_boolean && user_password_check_boolean && nickname_check_boolean){
                    RequestQueue queue = Volley.newRequestQueue(JoinActivity.this);
                    String url = "http://www.joonandhoon.com/jhmovienote/normal_register.php";
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        JSONArray jsonArray = jsonObject.getJSONArray("return");
                                        JSONObject temp = jsonArray.getJSONObject(0);
                                        if(temp.getBoolean("response")){
                                            //가입 성공
                                            String message = "Joon & Hoon Movie Note 가입을 환영합니다 로그인 해주세요";
                                            notifier_success(message);
                                        }else{
                                            //가입 실패 (이미 존재하는 계정입니다)
                                            String message = "이미 존재 하는 계정 입니다. 다시 시도해주세요";
                                            notifier_retry(message);
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
                            params.put("user_email", user_email.getText().toString().trim());
                            params.put("user_password", user_password.getText().toString().trim());
                            params.put("user_name", user_name.getText().toString().trim());
                            params.put("user_nickname", user_nickname.getText().toString().trim());
                            return params;
                        }
                    };
                    queue.add(stringRequest);
                }else{
                        Log.e("checker", "something is false");
                }
            }
        });

    }




    public void nickname_listener(){
        user_nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String nickname = charSequence.toString();
                BackgroundTask_nickname bt_nickname =new BackgroundTask_nickname();
                bt_nickname.String("http://www.joonandhoon.com/jhmovienote/available_nickname_checker.php?nickname=" + nickname);
                bt_nickname.execute();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void password_text_listener(){
        user_password_confirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(user_password.getText().toString().equals(charSequence.toString())){
                    password_check.setText("비밀번호가 일치합니다");
                    password_check.setTextColor(getResources().getColor(R.color.colorGreen));
                    user_password_check_boolean = true;
                }else{
                    password_check.setText("비밀번호가 일치하지 않습니다");
                    password_check.setTextColor(getResources().getColor(R.color.colorRed));
                    user_password_check_boolean = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(user_password.getText().toString().equals(editable.toString())){
                    password_check.setText("비밀번호가 일치합니다");
                    password_check.setTextColor(getResources().getColor(R.color.colorGreen));
                    user_password_check_boolean = true;
                }else{
                    password_check.setText("비밀번호가 일치하지 않습니다");
                    password_check.setTextColor(getResources().getColor(R.color.colorRed));
                    user_password_check_boolean = false;
                }
            }
        });
    }
public void user_email_text_focus_listener(){
    user_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            user_email.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if(charSequence.length() > 0 || charSequence.toString().contains("@")) {
                        //String target = "http://joonandhoon.dothome.co.kr/mn/php/available_email_checker.php?user_email=" + charSequence.toString();
                        final String written_user_email = charSequence.toString();
                        BackgroundTask bt =new BackgroundTask();
                        bt.String("http://www.joonandhoon.com/jhmovienote/available_email_checker.php?user_email=" + written_user_email);
                        bt.execute();
                    }else if(charSequence.length() == 0){
                        email_check.setText("이메일을 입력해주세요");
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
        }
    });
}
public void user_email_text_listener(){
        user_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() > 8 || charSequence.toString().contains("@")) {
                    //String target = "http://joonandhoon.dothome.co.kr/mn/php/available_email_checker.php?user_email=" + charSequence.toString();
                    final String written_user_email = charSequence.toString();
                    BackgroundTask bt =new BackgroundTask();
                    bt.String("http://www.joonandhoon.com/jhmovienote/available_email_checker.php?user_email=" + written_user_email);
                    bt.execute();
                }else if(charSequence.length() == 0){
                    email_check.setText("이메일을 입력해주세요");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }





    public void toolbar(){
        //toolbar back 버튼 생성
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("menu_button");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setTitle("");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리
//toolbar back 버튼 생성
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String target;
        TextView email_check = (TextView) findViewById(R.id.r_emailcheck);
        public void String(String string) {
            target = string;
        }
        @Override
        protected void onPreExecute(){ // 특정 php 타일을 초기화하는 상태
            //target = "http://joonandhoon.dothome.co.kr/mn/php/available_email_checker.php?user_email=" + written_user_email;
        }
        @Override
        protected String doInBackground(Void... voids){
            try{
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while((temp =bufferedReader.readLine())!=null){
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        public void onProgressUpdate(Void... values){
            super.onProgressUpdate(values);
        }
        @Override
        public void onPostExecute (String result){
            Log.e("result", result);
            if (result.toString().equals("usable")) {
                //사용 가능 한 이메일 일때
                email_check.setText("사용 가능한 이메일 입니다");
                email_check.setTextColor(getResources().getColor(R.color.colorGreen));
                user_email_boolean = true;
            }else if(result.toString().equals("taken")) {
                //이미 사용중인 이메일 일때
                email_check.setText("이미 사용중인 이메일 입니다");
                email_check.setTextColor(getResources().getColor(R.color.colorRed));
                user_email_boolean = false;
            }else if(result.toString().equals("notformatted")){
                email_check.setText("잘못된 이메일 형식 입니다");
                email_check.setTextColor(getResources().getColor(R.color.colorRed));
                user_email_boolean = false;
            }

        }
    }
    class BackgroundTask_nickname extends AsyncTask<Void, Void, String> {
        String target;
        TextView email_check = (TextView) findViewById(R.id.r_emailcheck);
        public void String(String string) {
            target = string;
        }
        @Override
        protected void onPreExecute(){ // 특정 php 타일을 초기화하는 상태
            //target = "http://joonandhoon.dothome.co.kr/mn/php/available_email_checker.php?user_email=" + written_user_email;
        }
        @Override
        protected String doInBackground(Void... voids){
            try{
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while((temp =bufferedReader.readLine())!=null){
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        public void onProgressUpdate(Void... values){
            super.onProgressUpdate(values);
        }
        @Override
        public void onPostExecute (String result){
            Log.e("result", result);
            if (result.toString().equals("usable")) {
                //사용 가능 한 이메일 일때
                nickname_check.setText("사용 가능한 닉네임 입니다");
                nickname_check.setTextColor(getResources().getColor(R.color.colorGreen));
                nickname_check_boolean = true;
            }else if(result.toString().equals("taken")) {
                //이미 사용중인 이메일 일때
                nickname_check.setText("이미 사용중인 닉네임 입니다");
                nickname_check.setTextColor(getResources().getColor(R.color.colorRed));
                nickname_check_boolean = false;
            }else if(result.toString().equals("notformatted")){
                nickname_check.setText("사용 불가능한 닉네임 입니다");
                nickname_check.setTextColor(getResources().getColor(R.color.colorRed));
                nickname_check_boolean = false;
            }

        }
    }
}
