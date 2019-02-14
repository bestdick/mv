package com.storyvendingmachine.www.mv;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
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
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.app.Activity.RESULT_OK;
import static android.view.KeyEvent.KEYCODE_VOLUME_DOWN;
import static android.view.KeyEvent.KEYCODE_VOLUME_UP;

import static com.storyvendingmachine.www.mv.MainActivity.LoggedInUser_ID;
import static com.storyvendingmachine.www.mv.MainActivity.LoggedInUser_nickname;
import static com.storyvendingmachine.www.mv.MainActivity.LoggedInUser_thumbnail;
import static com.storyvendingmachine.www.mv.MainActivity.LoginType;
import static com.storyvendingmachine.www.mv.MainActivity.mViewPager;
import static com.storyvendingmachine.www.mv.MainActivity.pb;
import static com.storyvendingmachine.www.mv.MainActivity.screen;


public class mainFragment extends Fragment{
//********************************youtube api key
//
//                AIzaSyCtICWDaIimwYlVC6tkiUxa9d7ZswS0zP4
//
//********************************youtube api key



    static ImageView volume_mute;

    ProgressDialog dialog;

    YouTubePlayer.OnInitializedListener listener;

    String youtuber;

    ArrayList<String> BoxOfficeMovieTitle;
    ArrayList<String> BoxOfficeYoutubeUrl;

    ArrayList<String> youtubeUrlList;
    ArrayList<String> youtubeTitleList;
    ArrayList<String> youtubeThumbnail;

    int rand_number;
    Random r;

    static int volumeFlag =0;

    int count ;



    LinearLayout recent_movie_container_2;

    public static mainFragment newInstance() {
        mainFragment fragment = new mainFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        youtuber="";
        BoxOfficeYoutubeUrl= new ArrayList<>();
        BoxOfficeMovieTitle = new ArrayList<>();
        youtubeUrlList = new ArrayList<>();
        youtubeTitleList = new ArrayList<>();
        youtubeThumbnail = new ArrayList<>();
        r = new Random();

        MuteAudio();
        volumeFlag=0;
        count=0;



    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        recent_movie_container_2 = (LinearLayout) rootView.findViewById(R.id.recent_movie_container_2);
//        focus_listener(rootView);// focuse listener

        pb.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        //dialog = ProgressDialog.show(getActivity(), "", "페이지 불러오는 중...", true);
        TextView tv1 = (TextView) rootView.findViewById(R.id.textView1);
        TextView tv2 = (TextView) rootView.findViewById(R.id.textView2);
        final ImageView iv = (ImageView) rootView.findViewById(R.id.thumbnail);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);

        if(LoginType.equals("kakao")){
            tv1.setText(LoggedInUser_nickname + "님 안녕하세요");
            //********************GET USER TOTAL LIST**********************
            getUserListTotal userlistTotal = new getUserListTotal();
            userlistTotal.textView=tv2;
            userlistTotal.execute();
            // ********************GET USER TOTAL LIST**********************
        }else if(LoginType.equals("normal")){
            Intent intent = getActivity().getIntent();
            tv1.setText(LoggedInUser_nickname + "님 안녕하세요");
            //********************GET USER TOTAL LIST**********************
            getUserListTotal userlistTotal = new getUserListTotal();
            userlistTotal.textView=tv2;
            userlistTotal.execute();
            // ********************GET USER TOTAL LIST**********************
        }else{

        }

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToUserInfoActivity();

            }
        });

        volume_mute = (ImageView) rootView.findViewById(R.id.volume_mute_control_button);
        volume_mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(volumeFlag == 0){
                    volume_mute.setImageDrawable(null);
                    volume_mute.setBackgroundResource(R.drawable.volume_on_icon);
                    UnMuteAudio();
                    volumeFlag=1;
                }else{
                    volume_mute.setImageDrawable(null);
                    volume_mute.setBackgroundResource(R.drawable.volume_off_icon);
                    MuteAudio();
                    volumeFlag=0;
                }

            }
        });

        loadImageFromUrl(LoggedInUser_thumbnail, iv, 0);// 마지막 변수가 0 이면 썸네일을 뜻하는 것이다


//----------최신 영화 목록 가져오는 기능 ----------------------
        getYoutubeAddress(rootView);
        getRecentMovieList(rootView);
        return rootView;
    }


    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public void getRecentMovieList(final View rootView){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://www.joonandhoon.com/jhmovienote/test01.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //final LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.recent_movie_container);
//                        final GridLayout ll = (GridLayout) rootView.findViewById(R.id.recent_movie_container);

//                        LinearLayout recent_movie_container_2 = (LinearLayout) rootView.findViewById(R.id.recent_movie_container_2);

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(((getScreenWidth() - 58) / 3), (int) Math.round(((getScreenWidth() - 58) / 3) * 1.4));

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("response");
//                            int count = 0;
                            while (count < jsonArray.length()) {

                                final View recent_movie_container_list_2 = getLayoutInflater().inflate(R.layout.recent_movie_container_2, null);//new addition
                                recent_movie_container_list_2.setTag(count);

                                Log.e("teload tester", Integer.toString(count)+"is reloaded?");
                                View listview = getLayoutInflater().inflate(R.layout.recent_movie_container, null);

                                String temp_title = jsonArray.getJSONObject(count).getString("title");
                                String mv_title = Html.fromHtml(temp_title).toString();//사용해야지 <br> 과 같은 테그가 사라진다
                                BoxOfficeMovieTitle.add(mv_title);

                                String temp_image_url = jsonArray.getJSONObject(count).getString("image_url");
                                final String mv_image_url = Html.fromHtml(temp_image_url).toString();//사용해야지 <br> 과 같은 테그가 사라진다


                                String temp_director = jsonArray.getJSONObject(count).getString("director");
                                String mv_director = Html.fromHtml(temp_director).toString();

                                String temp_hyperlink = jsonArray.getJSONObject(count).getString("hyperlink");
                                final String mv_hyperlink = Html.fromHtml(temp_hyperlink).toString();


                                String temp_pubdate = jsonArray.getJSONObject(count).getString("pubdate");
                                String mv_pubdate = Html.fromHtml(temp_pubdate).toString();

                                String temp_user_rate = jsonArray.getJSONObject(count).getString("user_rate");
                                String mv_user_rate = Html.fromHtml(temp_user_rate).toString();

                                String temp_actor = jsonArray.getJSONObject(count).getString("actor");
                                String mv_actor = Html.fromHtml(temp_actor).toString();

                                String temp_rank = jsonArray.getJSONObject(count).getString("rank");
                                final String mv_rank = Html.fromHtml(temp_rank).toString();

                                String temp_audiAcc = jsonArray.getJSONObject(count).getString("movie_audiAcc");
                                String mv_audiAcc = Html.fromHtml(temp_audiAcc).toString();

                                String temp_openDt = jsonArray.getJSONObject(count).getString("openDt");
                                String mv_openDt = Html.fromHtml(temp_openDt).toString();

                                String temp_update_date = jsonArray.getJSONObject(count).getString("updated_date");
                                String mv_update_date = Html.fromHtml(temp_update_date).toString();


                                ImageView m_imageView = (ImageView) recent_movie_container_list_2.findViewById(R.id.m_thumbnail_imageView);
                                m_imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                loadImageFromUrl(mv_image_url, m_imageView, 1); //마지막 변수가 1이면  썸네일이 아니라는 뜻이다

                                TextView m_rank_textView = (TextView) recent_movie_container_list_2.findViewById(R.id.m_rank_textView);
                                m_rank_textView.setText("No. "+ mv_rank);

                                TextView m_title_textView = (TextView) recent_movie_container_list_2.findViewById(R.id.m_title_textView);
                                m_title_textView.setText("영화 제목 : "+ mv_title);

                                TextView m_director_textView = (TextView) recent_movie_container_list_2.findViewById(R.id.m_director_textView);
                                m_director_textView.setText("감독 : "+ mv_director);

                                TextView m_rate_textView = (TextView) recent_movie_container_list_2.findViewById(R.id.m_rate_textView);
                                m_rate_textView.setText("평점 : "+ mv_user_rate);

                                TextView m_openDt_textView = (TextView) recent_movie_container_list_2.findViewById(R.id.m_openDt_textView);
                                m_openDt_textView.setText("개봉일 : "+ mv_openDt);

                                TextView m_actor_textView = (TextView) recent_movie_container_list_2.findViewById(R.id.m_actor_textView);
                                m_actor_textView.setText("배우 : "+ mv_actor);

                                TextView m_audiAcc_textView = (TextView) recent_movie_container_list_2.findViewById(R.id.m_audiAcc_textView);

                                m_audiAcc_textView.setText("누적 관객 수 : "+ audiAcc_Reduce(mv_audiAcc));

                                TextView m_update_date = (TextView) recent_movie_container_list_2.findViewById(R.id.m_updated_date);
                                m_update_date.setText("업데이트 날짜 : "+ mv_update_date);


                                ImageView preview = (ImageView) recent_movie_container_list_2.findViewById(R.id.m_youtube_thumbnail);
                                preview.setScaleType(ImageView.ScaleType.FIT_XY);
                                thumbnail_url tu = new thumbnail_url();
                                tu.title = mv_title;
                                tu.imageView = preview;
                                tu.count= count;
                                tu.execute();

                                final ConstraintLayout constraintLayout = (ConstraintLayout) recent_movie_container_list_2.findViewById(R.id.clicker_constraint_layout);
                                constraintLayout.setTag("list_"+count);
                                constraintLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String temp_tag_name = constraintLayout.getTag().toString();
                                        String tag_sufix = temp_tag_name.split("_")[1];


                                        Intent intent = new Intent(getActivity(), BoxOfficeActivity.class);
                                        intent.putExtra("rank", mv_rank);
                                        intent.putExtra("title", BoxOfficeMovieTitle.get(Integer.parseInt(tag_sufix)));
                                        intent.putExtra("youtube_url", BoxOfficeYoutubeUrl.get(Integer.parseInt(tag_sufix)));
                                        intent.putExtra("hyperlink", mv_hyperlink);
                                        startActivity(intent);
                                        getActivity().overridePendingTransition(R.anim.slide_up, R.anim.slide_up_bit);
                                    }
                                });



                                recent_movie_container_2.addView(recent_movie_container_list_2);//new addition

                                count++;
                            }
                            //박스오피스 영화를 다 가지고 온다음에 실행되어야 할 것


//                            dialog.dismiss();
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            pb.setVisibility(View.INVISIBLE);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(" json exception", e.toString());
                            Toast.makeText(getContext(), "조금만 더 기다려주세요...;;", Toast.LENGTH_LONG).show();
//                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                            pb.setVisibility(View.INVISIBLE);
                            getRecentMovieList(rootView);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "거의다...;;", Toast.LENGTH_LONG).show();
//                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                        pb.setVisibility(View.INVISIBLE);
                        getRecentMovieList(rootView);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("exam_code_list", "passpop");
                return params;
            }
        };
        queue.add(stringRequest);
    }




    public class thumbnail_url  extends AsyncTask<Void, Void, String> {
        private static final String api_key ="AIzaSyB-pAa9jDHKaodHZdJjvUFA13bx-VMalP4";
        String title;
        ImageView imageView;
        int count;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... voids) {
            String query_title=title+" 예고편";
            String encoded_title = Uri.encode(query_title);
            String target ="https://www.googleapis.com/youtube/v3/search?part=snippet&q="+encoded_title+"&key=AIzaSyB-pAa9jDHKaodHZdJjvUFA13bx-VMalP4&maxResults=1&type=video&regionCode=KR";
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject check_if_exist = jsonObject.getJSONObject("pageInfo");
                String totalResults = check_if_exist.getString("totalResults");
                Log.e(" number_of result", totalResults);
                if(Integer.parseInt(totalResults)  <= 1){
                    Log.e("result : ", "0");
                    View v = getView().findViewWithTag(count);
                    recent_movie_container_2.removeView(v);
                }else{

                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    JSONObject j_temp = jsonArray.getJSONObject(0);


                    String preview_video_id = j_temp.getJSONObject("id").getString("videoId");
                    String preview_thumbnail_url = j_temp.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("high").getString("url");


                    BoxOfficeYoutubeUrl.add(preview_video_id);

                    Log.e("items inarray: ", Integer.toString(jsonArray.length()));

                    Log.e("preview url", preview_thumbnail_url);


                    loadImageFromUrl(preview_thumbnail_url, imageView, 1);// 마지막 변수가 1이면 썸네일이 아니라는뜻이다
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }




    private String audiAcc_Reduce(String audiAcc){
        String return_value = "";
        switch (audiAcc.length()){
            case 0:
                return_value = audiAcc;
                break;
            case 1:
                return_value = audiAcc;
                break;
            case 2:
                return_value = audiAcc;
                break;
            case 3:
                return_value = audiAcc;
                break;
            case 4:
                return_value = audiAcc;
                break;
            case 5:
                return_value = audiAcc.substring(0,1)+"만 명";
                break;
            case 6:
                return_value = audiAcc.substring(0,2)+"만 명";
                break;
            case 7:
                return_value = audiAcc.substring(0,3)+"만 명";
                break;
            case 8:
                return_value = audiAcc.substring(0,4)+"만 명";
                break;

        }

        return return_value;
    }

    public void moveToUserInfoActivity(){
        Intent intent = new Intent(getActivity(), UserInfoSetting.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_left_bit);

    }

    public void MuteAudio(){
        AudioManager mAlramMAnager = (AudioManager) getActivity().getSystemService(getContext().AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
            //mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0);
        } else {
            //mAlramMAnager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            //mAlramMAnager.setStreamMute(AudioManager.STREAM_RING, true);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        }
    }

    public void UnMuteAudio(){
        AudioManager mAlramMAnager = (AudioManager) getActivity().getSystemService(getContext().AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE,0);
            //mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0);
        } else {
            //mAlramMAnager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            //mAlramMAnager.setStreamMute(AudioManager.STREAM_RING, false);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
        }
    }

    private void loadImageFromUrl(String url, ImageView view, int which_imageView) {
if(which_imageView == 0){
    // 0 일때는 썸네일을 뜻한다
    if(url == null) {
        Log.e("no image url", "url empty");
    }else if(url.equals("")){
        Log.e("no image url", "url empty");
    }else{
        Picasso.with(getContext()).load(url).into(view, new com.squareup.picasso.Callback() {
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
}else if(which_imageView == 1){
    //1은 그 외의 것들을 뜻한다
    if(url == null) {
        Log.e("no image url", "url empty");
        view.setBackground(getResources().getDrawable(R.drawable.empty_image));
    }else if(url.equals("")){
        Log.e("no image url", "url empty");
        view.setBackground(getResources().getDrawable(R.drawable.empty_image));
    }else{
        Picasso.with(getContext()).load(url).into(view, new com.squareup.picasso.Callback() {
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


    }

    public class youtube_channel_list  extends AsyncTask<Void, Void, String> {
        private static final String api_key ="AIzaSyB-pAa9jDHKaodHZdJjvUFA13bx-VMalP4";
        View rootview;
        String channel_url;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... voids) {
            String target ="https://www.googleapis.com/youtube/v3/activities?part=snippet,contentDetails&channelId="+channel_url+"&key="+api_key+"&maxResults=50";
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                rand_number = r.nextInt(jsonArray.length());
                for(int i = 0; i<jsonArray.length(); i++){
                    JSONObject temp1 = jsonArray.getJSONObject(i);
                    youtubeTitleList.add(temp1.getJSONObject("snippet").getString("title"));

                    //*************************  youtube video url ******************
                    JSONObject temp2 = temp1.getJSONObject("contentDetails");
                    if(temp2.isNull("upload")){
                        JSONObject temp3 = temp2.getJSONObject("playlistItem");
                        JSONObject temp4 = temp3.getJSONObject("resourceId");
                        youtubeUrlList.add(temp4.getString("videoId"));
                        Log.e("videoid" , temp4.getString("videoId"));
                    }else{
                        JSONObject temp3 = temp2.getJSONObject("upload");
                        youtubeUrlList.add(temp3.getString("videoId"));
                        Log.e("videoId", temp3.getString("videoId"));
                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        youtube(rand_number, rootview);

        }


    }
    YouTubePlayerSupportFragment youTubePlayerFragment;
    public void youtube(final int rand_num, final View rootview){
//        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
         youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        youTubePlayerFragment.initialize("AIzaSyCtICWDaIimwYlVC6tkiUxa9d7ZswS0zP4", new YouTubePlayer.OnInitializedListener() {
        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
            Log.e("Name of Screen", Integer.toString(screen));
            TextView youtubeTitleTextView = (TextView) rootview.findViewById(R.id.youtube_titleText);
            youtubeTitleTextView.setText("[유튜브 영상 제목]" + youtubeTitleList.get(rand_num));
            youTubePlayer.setShowFullscreenButton(false);
            youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
            youTubePlayer.loadVideo(youtubeUrlList.get(rand_num));
            youTubePlayer.play();

        }

        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
            Log.e("Name of Screen", "초기화 실패");

        }
    });
    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
    transaction.add(R.id.youtube_fragment, youTubePlayerFragment).commit();

}

    public class getUserListTotal extends AsyncTask<Void, Void, String> {
        TextView textView;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids){

            RequestQueue queue = Volley.newRequestQueue(getContext());
            String url = "http://www.joonandhoon.com/jhmovienote/get_my_total.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("response");
                                JSONObject temp = jsonArray.getJSONObject(0);

                                String total_my_list = temp.getString("total_my_list");
                                String total_my_comment = temp.getString("total_my_comment");

                                textView.setText("총 " + total_my_list +" 개의 영화를 보셨습니다");
                                textView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mViewPager.setCurrentItem(1);
                                    }
                                });


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

    public void getYoutubeAddress(final View rootview){

        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "http://www.joonandhoon.com/jhmovienote/youtube_url_list.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("response");
                            JSONObject temp = jsonArray.getJSONObject(0);
                            youtuber=temp.getString("author");


                            youtube_channel_list get_youtube_channel_list = new youtube_channel_list();
                            get_youtube_channel_list.rootview =rootview;
                            get_youtube_channel_list.channel_url=temp.getString("url");
                            get_youtube_channel_list.execute();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("youtube_url", e.toString());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("volley error", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }
        };
        queue.add(stringRequest);

    }


//    public void focus_listener(final View rootView){
//        rootView.getViewTreeObserver().addOnWindowFocusChangeListener(new ViewTreeObserver.OnWindowFocusChangeListener() {
//            @Override
//            public void onWindowFocusChanged(final boolean hasFocus) {
//                // do your stuff
//
//                if (hasFocus == true) {
//                    // 해당 화면 보임
//                    if(screen == 0){
//
//                    }else{
////                        FragmentTransaction ft = getFragmentManager().beginTransaction();
////                        ft.detach(mainFragment.this).attach(mainFragment.this).commit();
////                        ft.replace(R.id.main_fragment, mainFragment.newInstance());
//
//                        youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
//                        getYoutubeAddress(rootView);
//
//                        screen = 0;
//                    }
//
//
//
//                } else {
//                    // 화면 안보임
//
//                }
//            }
//        });
//    }

}
