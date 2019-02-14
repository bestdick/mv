package com.storyvendingmachine.www.mv;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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

/**
 * Created by Administrator on 2018-04-11.
 */

public class youtube_channel_list  extends AsyncTask<Void, Void, String> {
    private static final String api_key ="AIzaSyB-pAa9jDHKaodHZdJjvUFA13bx-VMalP4";
    Context context;


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
            for(int i = 0; i<jsonArray.length(); i++){
                JSONObject temp1 = jsonArray.getJSONObject(i);
                JSONObject temp2 = temp1.getJSONObject("contentDetails");
                if(temp2.isNull("upload")){
                    JSONObject temp3 = temp2.getJSONObject("playlistItem");
                    JSONObject temp4 = temp3.getJSONObject("resourceId");
                    Log.e("videoid" , temp4.getString("videoId"));
                }else{
                    JSONObject temp3 = temp2.getJSONObject("upload");
                    Log.e("videoId", temp3.getString("videoId"));
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


}
