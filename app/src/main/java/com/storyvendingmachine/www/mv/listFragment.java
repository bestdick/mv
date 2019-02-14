package com.storyvendingmachine.www.mv;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import static com.storyvendingmachine.www.mv.MainActivity.LoggedInUser_ID;
import static com.storyvendingmachine.www.mv.MainActivity.LoginType;


public class listFragment extends Fragment{



    ProgressDialog dialog;

    List<list_public> list;
    listadapter_public adapter;

    ListView listView;

    int item_get_num = 0;//한번 로딩할때마다  가져올 아이템의 갯수

    String list_selection;//선택한 리스트  ex) global list or my list   --> default: mylist

    String total_my_list;
    String total_global_list;

    int checker; // 0이면 total_my_list ,,   1이면 total_global_list


    TextView selectionTextView_1, selectionCountTextView_1,selectionTextView_2, selectionCountTextView_2;


    public static listFragment newInstance() {
        listFragment fragment = new listFragment();
        Bundle args = new Bundle();
        return fragment;
    }
    @Override
    public void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); //fragment 에서 toolbar menu 를 사용하기 위해서는 꼭 필수적으로해야한다

        list_selection = "my_list";
        total_my_list ="";
        total_global_list="";
        checker = 0;

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_list, container, false);


         selectionTextView_1 = (TextView) rootView.findViewById(R.id.selection_textView_1);
         selectionCountTextView_1 = (TextView) rootView.findViewById(R.id.selection_count_textView_1);
        selectionTextView_2 = (TextView) rootView.findViewById(R.id.selection_textView_2);
        selectionCountTextView_2 = (TextView) rootView.findViewById(R.id.selection_count_textView_2);

        listView = (ListView) rootView.findViewById(R.id.listFragment_container);

        dialog = null;


        list = new ArrayList<list_public>();
        adapter = new listadapter_public(getContext().getApplicationContext(), list);
        listView.setAdapter(adapter);
        Log.e("are we here 1", "true");
        getListfromServer(rootView);// 처음 면이 표시될때 리스트를 가져오는 작업을 한다
        Log.e("are we here 2", "true");
        selectionTextView_1.setText("나의 보관함");//defualt 가 mylist
        selectionCountTextView_1.setText(total_my_list); //total_list를 위 getListfromServer() 메소드에서 처음으로 호출하므로 그 뒤에 해당 코드를 넣어야한다
        selectionTextView_2.setText("사람들의 추천");
        selectionCountTextView_2.setText(total_global_list); //total_list를 위 getListfromServer() 메소드에서 처음으로 호출하므로 그 뒤에 해당 코드를 넣어야한다

        selectionTextView_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = 0;
                list_selection = "my_list";
                item_get_num=0;
                list.clear();
                getListfromServer(rootView);
                ImageView option_one =(ImageView) rootView.findViewById(R.id.option_one_image);
                option_one.setImageDrawable(null);
                option_one.setBackgroundResource(R.drawable.bookmark_icon);
                ImageView option_two =(ImageView) rootView.findViewById(R.id.option_two_image);
                option_two.setImageDrawable(null);
                option_two.setBackgroundResource(R.drawable.bookmark_off_icon);

            }
        });
        selectionTextView_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = 1;
                list_selection = "global_list";
                item_get_num=0;
                list.clear();
                getListfromServer(rootView);
                ImageView option_one =(ImageView) rootView.findViewById(R.id.option_one_image);
                option_one.setImageDrawable(null);
                option_one.setBackgroundResource(R.drawable.bookmark_off_icon);
                ImageView option_two =(ImageView) rootView.findViewById(R.id.option_two_image);
                option_two.setImageDrawable(null);
                option_two.setBackgroundResource(R.drawable.bookmark_icon);
            }
        });
        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                item_get_num=0;
                list.clear();
                getListfromServer(rootView);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        final Handler mHandler = new Handler() {
            public void handleMessage(Message msg) {  // 실행이 끝난후 확인 가능

            }
        };

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if(listView.getLastVisiblePosition() == listView.getAdapter().getCount()-1 &&
                        listView.getChildAt(listView.getChildCount()-1).getBottom() <= listView.getHeight()){


                    item_get_num = item_get_num +20;
                    String total_list = new String();
                    if(checker == 0){
                        total_list = total_my_list;
                    }else if(checker ==1){
                        total_list = total_global_list;
                    }
                    if(Integer.parseInt(total_list) >= item_get_num){
                        dialog = ProgressDialog.show(getActivity(), "", "페이지 가져오는 중...", true);
                        getAdditionalListfromServer(item_get_num);
                        Log.e("scroll reach end::", "true"+Integer.toString(item_get_num));


                        new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                            @Override
                            public void run() {
                                // 실행할 동작 코딩
                                dialog.dismiss();
                                mHandler.sendEmptyMessage(0);	// 실행이 끝난후 알림
                            }
                        }, 1000);

                    }else{
                        Toast.makeText(getActivity(), "마지막 페이지 입니다.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Log.e("scroll reach end::", "false");
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.e("num of item ", Integer.toString(position));
                Intent intent = new Intent(getContext(), SelectViewActivity.class);
                intent.putExtra("database_id", list.get(position).getDatabase_id());
                intent.putExtra("user_type", list.get(position).getUser_type());
                intent.putExtra("author", list.get(position).getKakao_id());// 타입이 kakao 이면 카카오 아이디 반환 노멀이면 일반 아이디 반환


                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_left_bit);
            }
        });

        return rootView;
    }


    public void getAdditionalListfromServer(final int input){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://www.joonandhoon.com/jhmovienote/get_list.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("RESPONSE", response.toString());
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("response");
                            int count =0;
                            while (count < jsonArray.length()) {
                                JSONObject exam = jsonArray.getJSONObject(count);
                                String database_id = exam.getString("database_id");
                                String author = exam.getString("author");
                                String movie_title = exam.getString("movie_title");
                                String main_text = exam.getString("main_text");
                                String w_date = exam.getString("w_date");
                                String w_time = exam.getString("w_time");
                                String w_year = exam.getString("w_year");
                                String img_name =exam.getString("img_name");
                                String imageURL = imgURL(img_name);//image full url
                                String img_rotate = exam.getString("img_rotate");
                                String hits = exam.getString("hits");
                                String user_type = exam.getString("user_type");
                                String public_private =exam.getString("public_private");
//                                total_list = exam.getString("temp");



                                list_public added_item = new list_public(database_id, author, movie_title, main_text, w_date, w_time, w_year, imageURL, img_rotate, hits, user_type, public_private);
                                list.add(added_item);
                                count++;
                            }
                              adapter.notifyDataSetChanged();
//                            dialog.dismiss();

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
                params.put("login_type", LoginType);
                params.put("get_start_num", Integer.toString(input));
                params.put("list_selection", list_selection);
                params.put("kakao_id", LoggedInUser_ID);
                params.put("norm_user_email", LoggedInUser_ID);
                return params;
            }
        };
        queue.add(stringRequest);
    }



    public void getListfromServer(final View rootview){
        final LinearLayout empty_view = (LinearLayout) rootview.findViewById(R.id.empty_layout);

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://www.joonandhoon.com/jhmovienote/get_list.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("RESPONSE", response.toString());
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("response");
                            int count =0;
                            if(jsonArray.length()==0){
                                Log.e("nothing", "nothing");
                                View empty = getLayoutInflater().inflate(R.layout.user_empty_list_container, null);
                                empty_view.removeAllViews();

                                empty_view.addView(empty);
                            }else {

                                while (count < jsonArray.length()) {
                                    JSONObject exam = jsonArray.getJSONObject(count);
                                    String database_id = exam.getString("database_id");
                                    String author = exam.getString("author");
                                    String movie_title = exam.getString("movie_title");
                                    String main_text = exam.getString("main_text");
                                    String w_date = exam.getString("w_date");
                                    String w_time = exam.getString("w_time");
                                    String w_year = exam.getString("w_year");
                                    String img_name = exam.getString("img_name");
                                    String imageURL = imgURL(img_name);//image full url
                                    String img_rotate = exam.getString("img_rotate");
                                    String hits = exam.getString("hits");
                                    String user_type = exam.getString("user_type");
                                    String public_private = exam.getString("public_private");


                                    total_my_list = exam.getString("total_my_list");
                                    total_global_list = exam.getString("total_global_list");

                                    list_public added_item = new list_public(database_id, author, movie_title, main_text, w_date, w_time, w_year, imageURL, img_rotate, hits, user_type, public_private);
                                    list.add(added_item);
                                    Log.e("count::", Integer.toString(count));
                                    count++;

                                }

                            }
                            adapter.notifyDataSetChanged();
                            selectionCountTextView_1.setText(total_my_list);
                            selectionCountTextView_2.setText(total_global_list);
                        }
                        catch(Exception e){

                        }
                    }
                },
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
                params.put("login_type", LoginType);
                params.put("list_selection", list_selection);
                params.put("kakao_id", LoggedInUser_ID);
                params.put("norm_user_email", LoggedInUser_ID);
                return params;
            }
        };
        queue.add(stringRequest);

    }


    public String imgURL(String image_name){
        String baseURL = "http://www.joonandhoon.com/jhmovienote/uploads/";
        String[] temp = image_name.split("##\\$\\$##\\$\\$");
        StringBuilder imageURL = new StringBuilder();
        Log.e("갯수", Integer.toString(temp.length));
            for(int i =0; i<temp.length; i++) {
                if (temp[i].equals("image_not_exist") && temp.length == 1) {
                    Log.e("image not exist", "on" + Integer.toString(i));
                    imageURL.append("image_not_exist");
                    break;
                }else if(temp[temp.length-1].equals("image_not_exist") && temp.length-1 == i){
                    imageURL.append("image_not_exist");
                    break;
                }else if(temp[i].equals("image_not_exist")) {
                    Log.e("image not exist", "on" + Integer.toString(i));
                }else{
                    imageURL.append(baseURL+temp[i]+"##$$##$$"+Integer.toString(i));
                    Log.e("stop at", imageURL.toString());
                    break;
                }
            }

        Log.e("name of url ::", imageURL.toString());
        return imageURL.toString();
    }


}
