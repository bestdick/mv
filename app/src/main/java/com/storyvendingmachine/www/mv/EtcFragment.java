package com.storyvendingmachine.www.mv;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.storyvendingmachine.www.mv.MainActivity.LoggedInUser_ID;
import static com.storyvendingmachine.www.mv.MainActivity.LoginType;


public class EtcFragment extends Fragment {
    ListView listView;
    List<EtcList> etcList;
    EtcListAdapter etcListAdapter;
    SwipeRefreshLayout swiper;
    String type_for_swiper;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static EtcFragment newInstance(String param1, String param2) {
        EtcFragment fragment = new EtcFragment();
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
        View rootview = inflater.inflate(R.layout.fragment_etc, container, false);
        initializer(rootview);
        subMenuSelector(rootview);
        swipeRefresher(rootview);
        return rootview;
    }
    public void initializer(View rootview){
        listView = (ListView) rootview.findViewById(R.id.listView);
        etcList = new ArrayList<EtcList>();
        etcListAdapter = new EtcListAdapter(getActivity(), etcList);
        listView.setAdapter(etcListAdapter);
        String type = "news_announcement_update";
        type_for_swiper = "news_announcement_update";
        View headerView = getLayoutInflater().inflate(R.layout.container_etc_header, null);
        TextView title_textView = (TextView) headerView.findViewById(R.id.title_textView);
        TextView write_textView = (TextView) headerView.findViewById(R.id.write_textView);
        title_textView.setText("#공지 및 업데이트 게시판");
        write_textView.setVisibility(View.GONE);
        listView.addHeaderView(headerView);
        getEtc(type);

    }
    public void swipeRefresher(View rootview){
        swiper = (SwipeRefreshLayout) rootview.findViewById(R.id.swipeRefresher);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                etcList.clear();
                addHeader(type_for_swiper);
                getEtc(type_for_swiper);
                swiper.setRefreshing(false);
            }
        });
    }
    public void subMenuSelector(final View rootview){
        final TextView announcement_notifier_list_textView = (TextView) rootview.findViewById(R.id.announcement_notifier_list_textView);
        announcement_notifier_list_textView.setTag(0);
        final TextView free_list_textView = (TextView) rootview.findViewById(R.id.free_list_textView);
        free_list_textView.setTag(1);
        final TextView error_list_textView = (TextView) rootview.findViewById(R.id.error_list_textView);
        error_list_textView.setTag(2);
        final TextView develop_list_textView = (TextView) rootview.findViewById(R.id.develop_list_textView);
        develop_list_textView.setTag(3);

        announcement_notifier_list_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textColorChangeToGrey(rootview);
                announcement_notifier_list_textView.setTextColor(getResources().getColor(R.color.colorCrimsonRed));
                etcList.clear();
                String type = "news_announcement_update";
                type_for_swiper="news_announcement_update";
                addHeader(type);
                getEtc(type);
            }
        });
        free_list_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textColorChangeToGrey(rootview);
                free_list_textView.setTextColor(getResources().getColor(R.color.colorCrimsonRed));
                etcList.clear();
                String type = "free_board";
                type_for_swiper="free_board";
                addHeader(type);
                getEtc(type);
            }
        });
        error_list_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textColorChangeToGrey(rootview);
                error_list_textView.setTextColor(getResources().getColor(R.color.colorCrimsonRed));
                etcList.clear();
                String type = "error_board";
                type_for_swiper="error_board";
                addHeader(type);
                getEtc(type);
            }
        });
        develop_list_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textColorChangeToGrey(rootview);
                develop_list_textView.setTextColor(getResources().getColor(R.color.colorCrimsonRed));
                etcList.clear();
                String type = "suggestion_board";
                type_for_swiper="suggestion_board";
                addHeader(type);
                getEtc(type);
            }
        });
    }
    public void getEtc(final String type){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://www.joonandhoon.com/jhmovienote/getEtcList.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("etc response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("response1");
                            for(int i = 0; i < jsonArray.length(); i++){
                                String primary_key = jsonArray.getJSONObject(i).getString("primary_key");
                                String login_type = jsonArray.getJSONObject(i).getString("login_type");
                                String user_id = jsonArray.getJSONObject(i).getString("user_id");
                                String user_nickname = jsonArray.getJSONObject(i).getString("user_nickname");
                                String type = jsonArray.getJSONObject(i).getString("type");
                                String title = jsonArray.getJSONObject(i).getString("title");
                                String content = jsonArray.getJSONObject(i).getString("content");
                                String upload_date = jsonArray.getJSONObject(i).getString("upload_date");
                                String upload_time = jsonArray.getJSONObject(i).getString("upload_time");
                                String isNew = jsonArray.getJSONObject(i).getString("isNew");

                                EtcList item = new EtcList(primary_key, login_type, user_id, user_nickname, type, title, content, upload_date, upload_time, isNew);
                                etcList.add(item);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        etcListAdapter.notifyDataSetChanged();
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("are we here 6", error.toString());
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
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void addHeader(String type){
        listView.removeHeaderView(listView.getChildAt(0));
        View headerView = getLayoutInflater().inflate(R.layout.container_etc_header, null);
        TextView title_textView = (TextView) headerView.findViewById(R.id.title_textView);
        TextView write_textView = (TextView) headerView.findViewById(R.id.write_textView);
        listView.addHeaderView(headerView);
        if(type.equals("news_announcement_update")){
            title_textView.setText("#공지 및 업데이트 게시판");
            write_textView.setVisibility(View.GONE);
        }else if(type.equals("free_board")){
            title_textView.setText("#자유 게시판");
            write_textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), EtcWriteActivity.class);
                    intent.putExtra("type", "free_board");
                    startActivity(intent);
                }
            });
        }else if (type.equals("error_board")){
            title_textView.setText("#오류신고 게시판");
            write_textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), EtcWriteActivity.class);
                    intent.putExtra("type", "error_board");
                    startActivity(intent);
                }
            });
        }else{
            //suggestion_board
            title_textView.setText("#건의 및 개선 게시판");
            write_textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), EtcWriteActivity.class);
                    intent.putExtra("type", "suggestion_board");
                    startActivity(intent);
                }
            });
        }
    }
    public void OnItemClickControl(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }
    private void textColorChangeToGrey(View rootview){
        for(int i = 0 ; i < 4; i++){
            TextView textView= (TextView) rootview.findViewWithTag(i);
            textView.setTextColor(getResources().getColor(R.color.colorGrey));
        }
    }
}
