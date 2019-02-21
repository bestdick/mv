package com.storyvendingmachine.www.mv;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.storyvendingmachine.www.mv.MainActivity.LoggedInUser_ID;
import static com.storyvendingmachine.www.mv.MainActivity.LoginType;

public class EtcWriteActivity extends AppCompatActivity {

    List<String> listview_items;
    ArrayAdapter<String> listview_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etc_write);
        toolbar();
        initializer();
    }
    public void toolbar(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("menu_button");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close);
        getSupportActionBar().setTitle("");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리
    }
    public void initializer(){
        TextView title_textView = (TextView) findViewById(R.id.title_textView);
        EditText title_editText = (EditText) findViewById(R.id.title_editText);
        EditText content_editText = (EditText) findViewById(R.id.content_editText);
        Button submit_button = (Button) findViewById(R.id.submit_button);

        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        if(type.equals("free_board")){
            title_textView.setText("#지유게시판");
        }else if (type.equals("error_board")){
            title_textView.setText("#오류게시판");
        }else{
            // type.equals("suggestion_board")
            title_textView.setText("#건의및개선게시판");
        }
        submitButtonClicked(type, title_editText, content_editText, submit_button);
    }
    public void submitButtonClicked(final String type, final EditText title_editText, final EditText content_editText, Button submit_button){
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp_title_str = title_editText.getText().toString();
                String temp_content_str = content_editText.getText().toString();
                if(temp_title_str.isEmpty() || temp_title_str.length() <= 1){
                    Toast.makeText(EtcWriteActivity.this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    if(temp_content_str.isEmpty() || temp_content_str.length() <=1){
                        Toast.makeText(EtcWriteActivity.this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }else{
                        String upload_title_str = custom_encode(temp_title_str);
                        String upload_content_str = custom_encode(temp_content_str);
                        uploadProcess(type, upload_title_str, upload_content_str);
                    }
                }

            }
        });
    }

    public void uploadProcess(final String type, final String title, final String content){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://www.joonandhoon.com/jhmovienote/uploadEtc.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Upload RESPONSE", response.toString());
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String isSuccess = jsonObject.getString("response1");
                            if(isSuccess.equals("success")){
                                onBackPressed();
                            }

                        }
                        catch(Exception e){

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
                params.put("token", "jhmovienote");
                params.put("login_type", LoginType);
                params.put("user_id", LoggedInUser_ID);
                params.put("type", type);
                params.put("title", title);
                params.put("content", content);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public String custom_encode(String str){
        String temp = str.replace("\n", "<br>");
        String htmlEncode_str  = TextUtils.htmlEncode(temp);
        return htmlEncode_str;
    }
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//            Log.e("option item ::", Integer.toString(id));
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            //drawer is open
//            drawer.closeDrawer(Gravity.START);
//        } else {
//            //drawer is closed
//            drawer.openDrawer(Gravity.START);
//        }
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
