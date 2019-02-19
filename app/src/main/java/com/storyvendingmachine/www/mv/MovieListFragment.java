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
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import static com.storyvendingmachine.www.mv.MainActivity.fab;
import static com.storyvendingmachine.www.mv.MainActivity.pb;
import static com.storyvendingmachine.www.mv.REQUESTCODES.REQUEST_CODE_WRITE;

//
///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link MovieListFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link MovieListFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class MovieListFragment extends Fragment {


    List<list_public> list;
    List<MovieList> myMovieListList;
    listadapter_public adapter;
    MovieListAdapter movieListAdapter;
     ListView listView;
    int myPage, globalPage;

    int myListItemCount, globalListItemCount;
//    ProgressBar progressBar;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

//    private OnFragmentInteractionListener mListener;
//
//    public MovieListFragment() {
//        // Required empty public constructor
//    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MovieListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MovieListFragment newInstance(String param1, String param2) {
        MovieListFragment fragment = new MovieListFragment();
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
        myPage=0;
        globalPage = 0 ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_movie_list, container, false);

        if(mParam1.equals("myList")){
            String list_selection = "my_list";
            initializer(rootview, list_selection);
            View header_view = addHeader_MyList();
            getListfromServer(rootview, list_selection, myPage, header_view, null);
            View footer_view = scrollToBottomFetchMoreList(rootview, list_selection);
            swiperRefresh(rootview, list_selection, header_view, footer_view);
            OnItemSelectControl( rootview,  list_selection);
        }else{
            //mParam1 == "sharedList
            String list_selection = "global_list";
            initializer(rootview, list_selection);
            getListfromServer(rootview, list_selection, globalPage, null,null);
            View footer_view = scrollToBottomFetchMoreList(rootview, list_selection);
            swiperRefresh(rootview, list_selection, null, footer_view);
            OnItemSelectControl( rootview,  list_selection);
        }
        return rootview;
    }

    public void initializer(View rootView, String list_selection){
//        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        listView = (ListView) rootView.findViewById(R.id.listFragment_container);
//        if(list_selection.equals("my_list")){
            myMovieListList= new ArrayList<MovieList>();
            movieListAdapter = new MovieListAdapter(getActivity(), myMovieListList);
            listView.setAdapter(movieListAdapter);
//            fab.attachToListView(listView);
//        }else{
//            myMovieListList= new ArrayList<MovieList>();
//            movieListAdapter = new MovieListAdapter(getActivity(), myMovieListList);
//            listView.setAdapter(movieListAdapter);
//            fab.attachToListView(listView);
//            list = new ArrayList<list_public>();
//            adapter = new listadapter_public(getActivity(), list);
//            listView.setAdapter(adapter);
//        }
    }
    public void swiperRefresh(final View rootView, final String list_selection, final View header_view, final View footer_view){
        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                TextView see_more_textView = (TextView) footer_view.findViewById(R.id.see_more);
                see_more_textView.setText("더보기");
                if(list_selection.equals("my_list")){
                    myPage = 0;
                    myMovieListList.clear();
                    getListfromServer(rootView,list_selection, myPage, header_view, null);
                }else{
                    globalPage=0;
                    myMovieListList.clear();
                    getListfromServer(rootView,list_selection, globalPage, header_view, null);
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    public View addHeader_MyList(){
        View header = getLayoutInflater().inflate(R.layout.container_mylist_header, null);
        TextView write_textView = (TextView) header.findViewById(R.id.write_textView);
        write_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WriteMovieNoteActivity.class);
                getActivity().startActivityForResult(intent, REQUEST_CODE_WRITE);
            }
        });
        listView.addHeaderView(header);
        return header;
    }
    public View scrollToBottomFetchMoreList(final View rootview, final String list_selection){
        final View footer_progress_view = getLayoutInflater().inflate(R.layout.container_footer_progress, null);
        final TextView see_more_textView = (TextView) footer_progress_view.findViewById(R.id.see_more);
        listView.addFooterView(footer_progress_view);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE &&
                        (listView.getLastVisiblePosition() - listView.getHeaderViewsCount() - listView.getFooterViewsCount()) >= (movieListAdapter.getCount() - 1)) {
                    // Now your listview has hit the bottom
                    Log.e("scroll", "hit the bottom");
                    if(list_selection.equals("my_list")){
                        myPage+=20;
                        if(myListItemCount > myPage){
                            progressBar_visible();
                            getListfromServer(rootview, list_selection, myPage, null, footer_progress_view);
                        }else{
                            see_more_textView.setText("마지막 페이지");
                            Log.e("last page", " last_page");
                        }
                    }else{
                        globalPage+=20;
                        if(globalListItemCount > globalPage){
                            progressBar_visible();
                            getListfromServer(rootview, list_selection, globalPage, null, footer_progress_view);
                        }else{
                            see_more_textView.setText("마지막 페이지");
                            Log.e("last page", " last_page");
                        }
//                        screenUntouchable();
//                        getListfromServer(rootview, list_selection, page, null, footer_progress_view);
                    }

                }
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
        return footer_progress_view;
    }
    public void getListfromServer(final View rootview, final String list_selection, final int page, final View header_view, final View footer_view){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
//        String url = "";
//        if(list_selection.equals("my_list")){
//            url = "http://www.joonandhoon.com/jhmovienote/getMovieList.php";
//        }else{
//             url = "http://www.joonandhoon.com/jhmovienote/get_list.php";
//        }
        String url = "http://www.joonandhoon.com/jhmovienote/getMovieList.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("RESPONSE", list_selection+"::"+response);
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("response");
                            int count =0;
                            if(jsonArray.length()==0 && page==0){
                                Log.e("nothing", "nothing");
                                SwipeRefreshLayout swipe_layout = (SwipeRefreshLayout) rootview.findViewById(R.id.swipe_layout);
                                swipe_layout.setVisibility(View.GONE);
                                ConstraintLayout empty_conLayout = (ConstraintLayout) rootview.findViewById(R.id.empty_conLayout);
                                empty_conLayout.setVisibility(View.VISIBLE);
                                Button write_note_button = (Button) rootview.findViewById(R.id.write_note_button);
                                write_note_button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getActivity(), WriteMovieNoteActivity.class);
                                        getActivity().startActivityForResult(intent, 10002);
                                    }
                                });
                            }else {
                                if(list_selection.equals("my_list")){
                                    // 나의 리스트 갯수
                                    if(page == 0){
                                        String total_mylist_count = jsonObject.getString("count_total_mylist");
                                        myListItemCount = Integer.parseInt(total_mylist_count);
                                        TextView list_count_textView = (TextView) header_view.findViewById(R.id.list_count_textView);
                                        list_count_textView.setText(total_mylist_count);
                                    }
                                    // 나의 리스트 갯수
                                    for(int i = 0 ; i < jsonArray.length(); i++){
                                        String primary_key = jsonArray.getJSONObject(i).getString("primary_key");
                                        String login_type = jsonArray.getJSONObject(i).getString("login_type");
                                        String user_id = jsonArray.getJSONObject(i).getString("user_id");
                                        String user_nickname = jsonArray.getJSONObject(i).getString("user_nickname");
                                        String user_thumbnail = jsonArray.getJSONObject(i).getString("user_thumbnail");
                                        String movie_title = jsonArray.getJSONObject(i).getString("movie_title");
                                        String content_alignment = jsonArray.getJSONObject(i).getString("content_alignment");
                                        String w_date = jsonArray.getJSONObject(i).getString("w_date");
                                        String w_time = jsonArray.getJSONObject(i).getString("w_time");
                                        String w_year = jsonArray.getJSONObject(i).getString("w_year");
                                        String hits = jsonArray.getJSONObject(i).getString("hits");
                                        String public_private = jsonArray.getJSONObject(i).getString("public_private");
                                        JSONArray movie_content = jsonArray.getJSONObject(i).getJSONArray("movie_contents");
                                        JSONArray movie_images = jsonArray.getJSONObject(i).getJSONArray("movie_images");
                                        JSONArray movie_images_rotation = jsonArray.getJSONObject(i).getJSONArray("movie_images_rotation");
                                        JSONArray movie_images_width_height = jsonArray.getJSONObject(i).getJSONArray("movie_images_width_height");

                                        MovieList item = new MovieList(primary_key,
                                                login_type,
                                                user_id,
                                                user_nickname,
                                                user_thumbnail,
                                                movie_title,
                                                content_alignment,
                                                w_date,
                                                w_time,
                                                w_year,
                                                hits,
                                                public_private,
                                                movie_content,
                                                movie_images,
                                                movie_images_rotation,
                                                movie_images_width_height);
                                        myMovieListList.add(item);
                                    }
                                    int count_footer_views = listView.getFooterViewsCount();
                                    if(count_footer_views > 0){
                                        progressBar_invisible();
                                    }
                                    movieListAdapter.notifyDataSetChanged();
                                }else{
                                    String total_globallist_count = jsonObject.getString("count_total_globallist");
                                    globalListItemCount = Integer.parseInt(total_globallist_count);
                                    for(int i = 0 ; i < jsonArray.length(); i++){
                                        String primary_key = jsonArray.getJSONObject(i).getString("primary_key");
                                        String login_type = jsonArray.getJSONObject(i).getString("login_type");
                                        String user_id = jsonArray.getJSONObject(i).getString("user_id");
                                        String user_nickname = jsonArray.getJSONObject(i).getString("user_nickname");
                                        String user_thumbnail = jsonArray.getJSONObject(i).getString("user_thumbnail");
                                        String movie_title = jsonArray.getJSONObject(i).getString("movie_title");
                                        String content_alignment = jsonArray.getJSONObject(i).getString("content_alignment");
                                        String w_date = jsonArray.getJSONObject(i).getString("w_date");
                                        String w_time = jsonArray.getJSONObject(i).getString("w_time");
                                        String w_year = jsonArray.getJSONObject(i).getString("w_year");
                                        String hits = jsonArray.getJSONObject(i).getString("hits");
                                        String public_private = jsonArray.getJSONObject(i).getString("public_private");
                                        JSONArray movie_content = jsonArray.getJSONObject(i).getJSONArray("movie_contents");
                                        JSONArray movie_images = jsonArray.getJSONObject(i).getJSONArray("movie_images");
                                        JSONArray movie_images_rotation = jsonArray.getJSONObject(i).getJSONArray("movie_images_rotation");
                                        JSONArray movie_images_width_height = jsonArray.getJSONObject(i).getJSONArray("movie_images_width_height");

                                        MovieList item = new MovieList(primary_key,
                                                login_type,
                                                user_id,
                                                user_nickname,
                                                user_thumbnail,
                                                movie_title,
                                                content_alignment,
                                                w_date,
                                                w_time,
                                                w_year,
                                                hits,
                                                public_private,
                                                movie_content,
                                                movie_images,
                                                movie_images_rotation,
                                                movie_images_width_height);
                                        myMovieListList.add(item);
                                    }
                                    int count_footer_views = listView.getFooterViewsCount();
                                    if(count_footer_views > 0){
                                        progressBar_invisible();
                                    }
                                    movieListAdapter.notifyDataSetChanged();
//                                    temp(jsonArray);
//                                    adapter.notifyDataSetChanged();
                                }
                            }

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
                params.put("token", "jhmovienote");
                params.put("login_type", LoginType);
                params.put("user_id", LoggedInUser_ID);
                params.put("list_selection", list_selection);
                params.put("page", String.valueOf(page));
//                params.put("norm_user_email", LoggedInUser_ID);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void OnItemSelectControl(View rootview, String list_selection){
        if(list_selection.equals("my_list")){
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    int number = (i-1);
                    if(i == 0 || i==(movieListAdapter.getCount()+1)){ // header and footer
                        //****** make header and footer unclickable
                        Log.e("clicked item", "cannot click");
                    }else{
                        String primary_key = myMovieListList.get(number).getPrimary_key();
                        String LoginType = myMovieListList.get(number).getLoginType();
                        String user_id = myMovieListList.get(number).getUser_id();
                        Intent intent = new Intent(getActivity(), SelectViewActivity.class);
                        intent.putExtra("database_id", primary_key);
                        intent.putExtra("user_type", LoginType);
                        intent.putExtra("author", user_id);// 타입이 kakao 이면 카카오 아이디 반환 노멀이면 일반 아이디 반환
                        startActivity(intent);
                        Log.e("clicked item", String.valueOf(i));
                    }
                }
            });
        }else{
            //list_selection == global_list
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if(i==(movieListAdapter.getCount())){//footer
                        //****** make header and footer unclickable
                        Log.e("clicked item", "cannot click");
                    }else{
                        int number = i;
                        String primary_key = myMovieListList.get(number).getPrimary_key();
                        String LoginType = myMovieListList.get(number).getLoginType();
                        String user_id = myMovieListList.get(number).getUser_id();
                        Intent intent = new Intent(getActivity(), SelectViewActivity.class);
                        intent.putExtra("database_id", primary_key);
                        intent.putExtra("user_type", LoginType);
                        intent.putExtra("author", user_id);// 타입이 kakao 이면 카카오 아이디 반환 노멀이면 일반 아이디 반환
                        startActivity(intent);
                        Log.e("clicked item", String.valueOf(i));
                    }
                }
            });
        }
    }
    public void temp(JSONArray jsonArray) throws JSONException {
        for (int count = 0 ; count < jsonArray.length(); count++) {
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


//                                    total_my_list = exam.getString("total_my_list");
//                                    total_global_list = exam.getString("total_global_list");

            list_public added_item = new list_public(database_id, author, movie_title, main_text, w_date, w_time, w_year, imageURL, img_rotate, hits, user_type, public_private);
            list.add(added_item);
            Log.e("count::", Integer.toString(count));
        }
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











//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }

public void progressBar_visible(){
    pb.setVisibility(View.VISIBLE);
    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
}
public void progressBar_invisible(){
    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    pb.setVisibility(View.GONE);
}

}
