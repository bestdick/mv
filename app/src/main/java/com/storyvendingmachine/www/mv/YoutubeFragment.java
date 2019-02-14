package com.storyvendingmachine.www.mv;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

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
import java.util.Random;

import static com.storyvendingmachine.www.mv.MainActivity.screen;


public class YoutubeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    YouTubePlayerSupportFragment youTubePlayerFragment;
    YouTubePlayer.OnInitializedListener listener;
    String youtuber;
    ArrayList<String> youtubeUrlList;
    ArrayList<String> youtubeTitleList;
    ArrayList<String> youtubeDescriptionList;





    ArrayList<String> youtubeThumbnail;
    int rand_number;
    Random r;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment YoutubeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YoutubeFragment newInstance(String param1, String param2) {
        YoutubeFragment fragment = new YoutubeFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_youtube, container, false);
        youtuber="";
        youtubeUrlList = new ArrayList<>();
        youtubeTitleList = new ArrayList<>();
        youtubeDescriptionList = new ArrayList<>();

        youtubeThumbnail = new ArrayList<>();
        r = new Random();
        getYoutubeAddress(rootview);
        return rootview;
    }


    public void youtube(final int rand_num, final View rootview){
//        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        youTubePlayerFragment.initialize("AIzaSyCtICWDaIimwYlVC6tkiUxa9d7ZswS0zP4", new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.e("Name of Screen", Integer.toString(screen));
                TextView youtuber_youtube_textView = (TextView) rootview.findViewById(R.id.youtuber_youtube_textView);
                TextView youtubeTitleTextView = (TextView) rootview.findViewById(R.id.title_youtube_textView);
                TextView youtubeDescriptionTextView = (TextView) rootview.findViewById(R.id.description_youtube_textView);
                youtuber_youtube_textView.setText(youtuber);
                youtubeTitleTextView.setText("[유튜브 영상 제목]" + youtubeTitleList.get(rand_num));
//                Spanned description = Html.fromHtml(youtubeDescriptionList.get(rand_num));
                youtubeDescriptionTextView.setText(youtubeDescriptionList.get(rand_num));
                youTubePlayer.setShowFullscreenButton(false);
//                youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
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
            String target ="https://www.googleapis.com/youtube/v3/activities?part=snippet,contentDetails&channelId="+channel_url+"&key="+api_key+"&maxResults=5";
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
                    youtubeDescriptionList.add(temp1.getJSONObject("snippet").getString("description"));
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
                            String url = temp.getString("url");


                            youtube_channel_list get_youtube_channel_list = new youtube_channel_list();
                            get_youtube_channel_list.rootview =rootview;
                            get_youtube_channel_list.channel_url=url;
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

}
