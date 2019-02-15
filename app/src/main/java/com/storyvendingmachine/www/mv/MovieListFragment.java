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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.storyvendingmachine.www.mv.MainActivity.LoggedInUser_ID;
import static com.storyvendingmachine.www.mv.MainActivity.LoginType;

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
    listadapter_public adapter;
    ListView listView;



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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_movie_list, container, false);
        initializer(rootview);
        if(mParam1.equals("myList")){
            String list_selection = "my_list";
            swiperRefresh(rootview, list_selection);
            getListfromServer(rootview, list_selection);
        }else{
            //mParam1 == "sharedList
            String list_selection = "global_list";
            swiperRefresh(rootview, list_selection);
            getListfromServer(rootview, list_selection);

        }
        return rootview;
    }

    public void initializer(View rootView){
        listView = (ListView) rootView.findViewById(R.id.listFragment_container);
        list = new ArrayList<list_public>();
        adapter = new listadapter_public(getContext().getApplicationContext(), list);
        listView.setAdapter(adapter);
    }
    public void swiperRefresh(final View rootView, final String list_selection){
        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                item_get_num=0;
                list.clear();
                getListfromServer(rootView,list_selection);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void getListfromServer(final View rootview, final String list_selection){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://www.joonandhoon.com/jhmovienote/get_list.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("RESPONSE", list_selection+"::"+response);
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("response");
                            int count =0;
                            if(jsonArray.length()==0){
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
                                        startActivityForResult(intent, 10002);

                                    }
                                });
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


//                                    total_my_list = exam.getString("total_my_list");
//                                    total_global_list = exam.getString("total_global_list");

                                    list_public added_item = new list_public(database_id, author, movie_title, main_text, w_date, w_time, w_year, imageURL, img_rotate, hits, user_type, public_private);
                                    list.add(added_item);
                                    Log.e("count::", Integer.toString(count));
                                    count++;

                                }

                            }
                            adapter.notifyDataSetChanged();
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
}
