package com.storyvendingmachine.www.mv;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

import static com.storyvendingmachine.www.mv.MainActivity.LoggedInUser_ID;
import static com.storyvendingmachine.www.mv.MainActivity.LoggedInUser_nickname;
import static com.storyvendingmachine.www.mv.MainActivity.LoginType;

public class SelectViewActivity extends AppCompatActivity {
    Toolbar myToolbar;//back button

    String db_selection;
    String user_type;
    String author;

    LinearLayout linearLayout;
    LinearLayout linearLayout_2;
    LinearLayout linearLayout_3;

    EditText commentEditText;
    Button commentButton;

    String public_or_private;


    String selected_item_author;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_view);
        // *********************** 초기화 *********************
        selected_item_author="";
        // *********************** 초기화 *********************
        linearLayout = (LinearLayout) findViewById(R.id.select_view_container);
        linearLayout_2 = (LinearLayout) findViewById(R.id.select_view_container_2);
        linearLayout_3 = (LinearLayout) findViewById(R.id.select_view_container_3);

        commentEditText = (EditText) findViewById(R.id.comment_editText);
        commentButton =(Button) findViewById(R.id.comment_upload_btn);

        toolbar();
        Intent intent = getIntent();
        db_selection = intent.getStringExtra("database_id");
        user_type = intent.getStringExtra("user_type");
        author = intent.getStringExtra("author");

        getSelectionText(db_selection);



        whenUpLoadButtonClicked();
        commentsGetTask GetComments = new commentsGetTask();
        GetComments.execute();
    }



    public void notifier_retry(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setNegativeButton("다시시도", null)
                .create()
                .show();
    }

    public void notifier_success(String message){
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        commentEditText.setText("");
                        linearLayout_3.removeAllViews();
                        commentsGetTask GetComments = new commentsGetTask();
                        GetComments.execute();
                    }
                })
                .create()
                .show();
    }

    public void whenUpLoadButtonClicked(){
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                String comment = commentEditText.getText().toString();
                if(comment.length()<=0){
                    String message = "댓글을 입력해 주세요";
                    notifier_retry(message);
                }else{
                    commentUpLoadTask(comment);
                }

            }
        });
    }
    public void commentUpLoadTask(final String comment){
        final RequestQueue queue = Volley.newRequestQueue(SelectViewActivity.this);
        String url = "http://www.joonandhoon.com/jhmovienote/commentUpLoadTask.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("success")){
                            String message = "업로드 완료";
                            notifier_success(message);
                        Log.e("Comment Response", response);
                        }else{
                            String message = "업로드 실패";
                            notifier_retry(message);
                            Log.e("Comment Response", response);
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
                params.put("LoggedInUser_ID", LoggedInUser_ID);
                params.put("user_id", author);
                params.put("db_selection", db_selection);
                params.put("comment", comment);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public class commentsGetTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids){

            RequestQueue queue = Volley.newRequestQueue(SelectViewActivity.this);
            String url = "http://www.joonandhoon.com/jhmovienote/commentGetTask.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("response");
                                for(int i =0; i< jsonArray.length(); i++){
                                    View commentContainer = getLayoutInflater().inflate(R.layout.comments_container, null);
                                    JSONObject temp = jsonArray.getJSONObject(i);
                                    String user_type = temp.getString("user_type");
                                    String nickname = temp.getString("nickname");
                                    String index_jhmn_movie_db = temp.getString("index_jhmn_movie_db");
                                    String comment = temp.getString("comment");
                                    String c_date = temp.getString("c_date");
                                    String c_time = temp.getString("c_time");
                                    String c_year = temp.getString("c_year");

                                    TextView commenterID = (TextView) commentContainer.findViewById(R.id.commenterIDTextView);
                                    TextView commentTextView = (TextView) commentContainer.findViewById(R.id.commentTextView);
                                    TextView commentDate = (TextView) commentContainer.findViewById(R.id.commentDate);

                                    commenterID.setText(nickname);
                                    commentTextView.setText(comment);
                                    commentDate.setText(c_date);

                                    linearLayout_3.addView(commentContainer);
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
                    params.put("db_selection", db_selection);
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

    protected void getSelectionText(String input){
        final String db_id = input; // iNTENT  로 가져온 DB 의 ID 값


        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://www.joonandhoon.com/jhmovienote/SelectViewActivity_php.php";

         StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("response", response);
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("response");
                            JSONObject temp = jsonArray.getJSONObject(0);
                            String movie_title = temp.getString("movie_title");
                            String[] main_text = temp.getString("main_text").split("##\\$\\$##\\$\\$");
                            String w_date = temp.getString("w_date");
                            String w_time = temp.getString("w_time");
                            String[] img_name = temp.getString("img_name").split("##\\$\\$##\\$\\$");
                            String[] img_rotate = temp.getString("img_rotate").split("##\\$\\$##\\$\\$");
                            String txt_alignment = temp.getString("txt_alignment");
                            String hit = temp.getString("hit");
//                            String nickname = temp.getString("nickname");
                            selected_item_author = temp.getString("nickname");
                            String[] width_height = temp.getString("width_height").split("##\\$\\$##\\$\\$");
                            public_or_private = temp.getString("public_private");

                            View SV_container_info = getLayoutInflater().inflate(R.layout.select_view_main_info_container, null);

                            TextView title = (TextView) SV_container_info.findViewById(R.id.SV_container_title);
                            title.setText(movie_title.trim());
                            TextView author = (TextView) SV_container_info.findViewById(R.id.SV_container_author);
                            author.setText(selected_item_author);
                            TextView hit_tv = (TextView) SV_container_info.findViewById(R.id.SV_container_hit);
                            hit_tv.setText(hit);
                            TextView date = (TextView) SV_container_info.findViewById(R.id.SV_container_date);
                            date.setText(w_date+"  "+w_time);

                            linearLayout.addView(SV_container_info);
//                            TextView title = (TextView) findViewById(R.id.select_view_title);
//                            title.setText(movie_title);
//                            title.setTextSize(20);
//                            title.setTypeface(Typeface.DEFAULT_BOLD);


                            int paddingDp = pixelToDp(15);


                            //LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            //LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(getScreenWidth()*1.7));
                            for(int i=0; i<main_text.length; i++){
                                if(main_text[i].equals("leave_it_empty")){

                                }else{
                                    TextView text = new TextView(SelectViewActivity.this);
                                    //text.setLayoutParams(layoutParams);
                                    text.setTag("text_"+Integer.toString(i));
                                    text.setText(main_text[i]);
                                    text.setPadding(paddingDp,5,paddingDp,5);
                                    switch (txt_alignment){
                                        case "right":
                                            text.setGravity(Gravity.RIGHT);
                                            break;
                                        case "left":
                                            text.setGravity(Gravity.LEFT);
                                            break;
                                        case "center":
                                            text.setGravity(Gravity.CENTER);
                                            break;
                                    }
                                    linearLayout_2.addView(text);
                                }



                                if(i == main_text.length-1){
                                    Log.e("error", " text is more than image");
                                }else{
                                    if(img_name[i].equals("image_not_exist")){
                                        Log.e("empty : " , img_name[i]);
                                    }else{
                                        String WH = width_height[i];
                                        int temp_height = rotate_image_size(Integer.parseInt(img_rotate[i]), WH);
                                        int height = pixelToDp(temp_height);

                                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getScreenWidth(), temp_height);
                                        ImageView imageView = new ImageView(SelectViewActivity.this);
                                        imageView.setPadding(0,10, 0, 10);
                                        imageView.setTag("imageView_"+Integer.toString(i));
                                        loadImageFromUrl(img_name[i], imageView, img_rotate[i], width_height[i]);
                                        imageView.setLayoutParams(layoutParams);
                                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                        Log.e("Width and Height", Integer.toString(imageView.getWidth())+"//"+Integer.toString(imageView.getHeight()));
                                        linearLayout_2.addView(imageView);
                                    }
                                }

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("json faile", e.toString());
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("listener error", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("selection_db_id", db_id);
                return params;
            }
        };
        queue.add(stringRequest);


    }
    public int pixelToDp(int input){
    int paddingpixel =input;
    float density = getApplicationContext().getResources().getDisplayMetrics().density;
    int paddingDp = (int)(paddingpixel*density);
    return paddingDp;
}
    private ArrayList<String> mainTextToArrayList(String[] input){
        ArrayList<String> temp = new ArrayList<>();
        for(int i = 0; i<input.length; i++){
            temp.add(input[i]);
        }

        return temp;
    }

    public String imgURL(String image_name){
        String baseURL = "http://www.joonandhoon.com/jhmovienote/uploads/";
        return baseURL+image_name;
    }


    private void loadImageFromUrl(String url, final ImageView view, String rotation, String width_height) {
        String imageFinalUrl = imgURL(url);
        int degree = Integer.parseInt(rotation);
        if(degree <=360){
            Log.e("current degree :", Integer.toString(degree));

        }else{
            degree = degree%360;
            Log.e("current degree :", Integer.toString(degree));

        }

        int temp_height = rotate_image_size(degree, width_height);
        int height = pixelToDp(temp_height);

        backgroundTask_image bt_i = new backgroundTask_image();
        bt_i.context= getApplicationContext();
        bt_i.imageFinalUrl= imageFinalUrl;
        bt_i.execute();

        if(url == null) {
            Log.e("no image url", "url empty");
        }else if(url.equals("")){
            Log.e("no image url", "url empty");
        }else {
            Picasso.with(this)
                    .load(imageFinalUrl)
                    .rotate(degree)
                    .into(view, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            Log.e("width :", Integer.toString(view.getWidth()));
                            Log.e("height :", Integer.toString(view.getHeight()));
                            Log.e("loadImageFromUrl", "success");
                        }

                        @Override
                        public void onError() {
                            Log.e("load image", "fail to load images ");
                        }
                    });
        }
    }

    public int rotate_image_size(int degree, String width_height){//회전 각도 마다 이미지 사이즈를 지정해주는 것이다
        String[] temp = width_height.split("/");
        int height = Integer.parseInt(temp[0]);
        int width = Integer.parseInt(temp[1]);

        int return_number = 0;
        switch (degree){
            case 0:
                return_number = (int)((getScreenWidth()*height)/width);
                break;
            case 90:
                return_number = (int)((getScreenWidth()*width)/height);
                break;
            case 180:
                return_number = (int)((getScreenWidth()*height)/width);
                break;
            case 270:
                return_number = (int)((getScreenWidth()*width)/height);
                break;
            case 360:
                return_number = (int)((getScreenWidth()*height)/width);
                break;
        }
        Log.e("return number", Integer.toString(return_number));
        return return_number;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }


    private class backgroundTask_image extends AsyncTask<Void, Void, String> {
//        String nn;
//        String id; // input
//        TextView tv;// input
            String imageFinalUrl;
            Context context;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids){
            try {
                Bitmap picasso_image = Picasso.with(context).load(imageFinalUrl).get();
                Log.e("picasso image width", Integer.toString(picasso_image.getWidth()));
                Log.e("picasso image height", Integer.toString(picasso_image.getHeight()));

            } catch (IOException e) {
                e.printStackTrace();
            }

//            RequestQueue queue = Volley.newRequestQueue(getBaseContext());
//            String url = "http://www.joonandhoon.com/jhmovienote/nickname_by_kakao_id.php";
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//
//                            tv.setText(response);
//
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//
//                        }
//                    }
//            ) {
//                @Override
//                protected Map<String, String> getParams(){
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put("kakao_id", id);
//                    return params;
//                }
//            };
//            queue.add(stringRequest);
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
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.selection_view_menu, menu);


        Log.e("toolbar action control", "controlling");


        return super.onCreateOptionsMenu(menu);
    }


    public void positive_cancel_notifier(String message, final String pos_message, final int menu_id){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(pos_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(menu_id == 1){
                            fixPublicPrivate(Integer.toString(menu_id));
                        }else if(menu_id == 2){

                        }else if(menu_id ==3){
                            fixPublicPrivate(Integer.toString(menu_id));
                        }else{
                            onBackPressed();
                        }

                    }
                })
                .setNegativeButton("취소", null)
                .create()
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.s_item_1){ //공유 버튼 누른것

            if(selected_item_author.equals(LoggedInUser_nickname)){
                    ///선택한 아이템이 자신이 쓴 글이 맞을때 ....
                if(public_or_private.equals("public")){
                    String message = "현재 \'공유된\' 상태입니다. 개인 보관으로 바꾸시겠습니까?";
                    String pos_message = "개인 보관";
                    positive_cancel_notifier(message, pos_message, 1);
//                Toast.makeText(SelectViewActivity.this, "public", Toast.LENGTH_SHORT).show();

                }else if(public_or_private.equals("private")){
                    String message = "현재 \'개인보관\' 상태입니다. 공유하 시겠습니까?";
                    String pos_message = "공유";
                    positive_cancel_notifier(message, pos_message, 1);
//                Toast.makeText(SelectViewActivity.this, "private", Toast.LENGTH_SHORT).show();
                }else{
                    // false 일때
                    Toast.makeText(SelectViewActivity.this, "error", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "회원님이 작성한 글이 아닙니다",Toast.LENGTH_SHORT).show();
            }

        }else if(id == R.id.s_item_2){ // 수정
            Toast.makeText(SelectViewActivity.this, "(수정) 준비중...", Toast.LENGTH_SHORT).show();
        }else if(id==R.id.s_item_3){ // 삭제
            if(selected_item_author.equals(LoggedInUser_nickname)){
                ///선택한 아이템이 자신이 쓴 글이 맞을때 ....
                    String message = "현재 글을 삭제 하시겠습니까?";
                    String pos_message = "삭제";
                    positive_cancel_notifier(message, pos_message, 3);
            }else{
                Toast.makeText(this, "회원님이 작성한 글이 아닙니다",Toast.LENGTH_SHORT).show();
            }
        }else{
            onBackPressed();
        }

        return true;
    }


    public void fixPublicPrivate(final String menu_selection){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://www.joonandhoon.com/jhmovienote/identify_public_private.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
Log.e("response : ", response);
                        if(response.equals("success")){
                            Toast.makeText(SelectViewActivity.this, "공유 상태가 변경되었습니다", Toast.LENGTH_SHORT).show();
                        }else if(response.equals("delete_success")){
                            Toast.makeText(SelectViewActivity.this, "해당 글을 성공적으로 삭제하였습니다", Toast.LENGTH_SHORT).show();
                            String message = "해당 글을 성공적으로 삭제하였습니다";
                            String pos_message = "확인";
                            positive_cancel_notifier(message, pos_message, -1);
                        }else{
                            Toast.makeText(SelectViewActivity.this, "공유 상태를 변경하지 못 했습니다", Toast.LENGTH_SHORT).show();
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
//                params.put("login_type", LoginType);
                params.put("logged_in_user", LoggedInUser_ID);
                params.put("db_selection", db_selection);// 데이터베이스의 고유값
                params.put("menu_selection", menu_selection);// 데이터베이스의 고유값


                return params;
            }
        };
        queue.add(stringRequest);

    }

    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if(view !=null){
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_right_bit,R.anim.slide_out);// first entering // second exiting
    }
}
