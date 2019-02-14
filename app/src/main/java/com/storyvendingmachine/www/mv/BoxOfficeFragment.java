package com.storyvendingmachine.www.mv;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.storyvendingmachine.www.mv.MainActivity.pb;


public class BoxOfficeFragment extends Fragment {

    ArrayList<String> BoxOfficeMovieTitle;
    ArrayList<String> BoxOfficeYoutubeUrl;

    int count ;
    LinearLayout box_office_layout;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BoxOfficeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BoxOfficeFragment newInstance(String param1, String param2) {
        BoxOfficeFragment fragment = new BoxOfficeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        BoxOfficeYoutubeUrl= new ArrayList<>();
        BoxOfficeMovieTitle = new ArrayList<>();
        count=0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_box_office, container, false);

        box_office_layout = (LinearLayout) rootview.findViewById(R.id.box_office_layout);
        getRecentMovieList(rootview);


        return  rootview;
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



                                box_office_layout.addView(recent_movie_container_list_2);//new addition

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


    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
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
                    box_office_layout.removeView(v);
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
}
