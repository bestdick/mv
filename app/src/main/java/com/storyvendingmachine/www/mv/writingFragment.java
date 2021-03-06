package com.storyvendingmachine.www.mv;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.storyvendingmachine.www.mv.MainActivity.LoggedInUser_ID;
import static com.storyvendingmachine.www.mv.MainActivity.LoginType;
import static java.lang.Integer.parseInt;


public class writingFragment extends Fragment {

    LinearLayout linearLayout;

    int count=0;



    String clicked_image_name;
    String clicked_rotate_btn;

//upload file 에 필요한 변수
    ProgressDialog dialog;
    int serverResponseCode = 0;
    //String upLoadServerUri = "http://joonandhoon.dothome.co.kr/mn/php/upload_image_request.php";
    String upLoadServerUri = "http://joonandhoon.dothome.co.kr/jhmovienote/upload_request_for_write_frag.php";
//upload file 에 필요한 변수

    //String imgPath;
    String name_Str;
    int REQ_CODE_SELECT_IMAGE;

    //**********************이미지의 이름//패스// 로테이션//텍스트 리스트***********************

//    ArrayList<String> imgPath_list = new ArrayList<>();
//    ArrayList<String> imgName_list =new ArrayList<>();
//    ArrayList<Integer> imgRotate_list =new ArrayList<>();
//    ArrayList<String> textarea_list = new ArrayList<>();
//    ArrayList<Bitmap> imageList = new ArrayList<>();

    ArrayList<String> imgPath_list;
    ArrayList<String> imgName_list;
    ArrayList<Integer> imgRotate_list;
    ArrayList<String> textarea_list;
    ArrayList<Bitmap> imageList;

    ArrayList<EditText> text_list;
    ArrayList<String> img_width_height;//저장값은 항상 width and then height
//    ArrayList<String> encoded_data = new ArrayList<>();

    //***********************alignment**********************
    String alignment;
    //***********************alignment**********************

    EditText first_text_area;

    EditText title;

    private void closeKeyboard(){
        View view = getActivity().getCurrentFocus();
        if(view !=null){
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public String IntToString(int input){
        String output = Integer.toString(input);
        return output;
    }
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static writingFragment newInstance() {
        writingFragment fragment = new writingFragment();
        Bundle args = new Bundle();
        return fragment;
    }
    @Override
    public void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        if(LoginType.equals("kakao")){
            Log.e("loginType", "kakao");

        }else if(LoginType.equals("normal")){
            Log.e("loginType", "normal");

        }else{

        }
    }

    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_writing, container, false);
        imgPath_list = new ArrayList<>();
        imgName_list =new ArrayList<>();
        imgRotate_list =new ArrayList<>();
        textarea_list = new ArrayList<>();
        imageList = new ArrayList<>();
        text_list = new ArrayList<>();
        img_width_height = new ArrayList<>();
        dialog = null;
        REQ_CODE_SELECT_IMAGE=100;
        clicked_image_name=new String();
        clicked_rotate_btn = new String();
        alignment="left";

        title = (EditText) rootView.findViewById(R.id.title_textarea);
        first_text_area = (EditText) rootView.findViewById(R.id.first_text_area);
        final ImageView keyboard_hide = (ImageView)rootView.findViewById(R.id.keyboardHide_button);
        keyboard_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
            }
        });



        //*****************************업로드 프로세스
                Button upload_button =(Button) rootView.findViewById(R.id.upload_btn);
                upload_button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                         closeKeyboard();

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("작성하신 내용을 '나의보관함'에만 저장하거나  또는 '공유'하시겠습니까?\n*나의보관함에 보관하면 공유되지 않습니다")
                                .setPositiveButton("나의보관함", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        upload_process("private");
                                    }
                                })
                                .setNegativeButton("나의보관함+공유", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        upload_process("public");
                                    }
                                })
                                .setCancelable(true)
                                .create()
                                .show();

                    }
                });
        //*****************************TExt 업로드 프로세스********************************


        //***************************버튼을 클릭했을때 새로운 edittext area 와 imagview 가 생성되는 프로세스 *****************
        ImageButton button = (ImageButton) rootView.findViewById(R.id.button_write);
        //final LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.write_container);
         linearLayout = (LinearLayout) rootView.findViewById(R.id.write_container);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final View textView = getLayoutInflater().inflate(R.layout.write_edittext, null);
                textView.setTag("layout_"+Integer.toString(count));
                //EditText et = (EditText) textView.findViewById(R.id.edittextView_for_write);

                final EditText textarea = (EditText) textView.findViewById(R.id.edittextView_for_write);//inflated layout에 있는 edittext
                ImageView iv_add =(ImageView) textView.findViewById(R.id.imageadd_and_view);
                TextView img_name_text = (TextView) textView.findViewById(R.id.img_name);
                final ImageView delete_button = (ImageView) textView.findViewById(R.id.delete_button);

                textarea.setTag("textarea_"+Integer.toString(count)+"");
                switch (alignment){
                    case "right":
                        textarea.setGravity(Gravity.RIGHT);
                        break;
                    case "left":
                        textarea.setGravity(Gravity.LEFT);
                        break;
                    case "center":
                        textarea.setGravity(Gravity.CENTER);
                        break;
                    default:
                        break;
                }
                iv_add.setTag("button_"+Integer.toString(count)+"");
                img_name_text.setTag("textview_"+Integer.toString(count)+"");
                delete_button.setTag("del_"+Integer.toString(count)+"");

                imgName_list.add("");
                imgPath_list.add("");
                imgRotate_list.add(0);
                textarea_list.add("");
                imageList.add(null);
                text_list.add(textarea);
                img_width_height.add("");


                delete_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String[] tag_num = delete_button.getTag().toString().split("_");// tag_num[1] -->실재 사용할 번호
                        View temp_view = rootView.findViewWithTag("layout_"+tag_num[1]);
                        temp_view.setVisibility(View.GONE);
                        imgName_list.set(parseInt(tag_num[1]), "image_not_exist");
                        imgPath_list.set(parseInt(tag_num[1]), "image_not_exist");
                        textarea_list.set(parseInt(tag_num[1]), "leave_it_empty");
                    }
                });


                //***새롭게 생성된 edittext 에 key listener 을 달아서 쓰여진 글을 textarea_list에 넣어주는 작업을 한다.



             //   clicked_image_name = "button"+Integer.toString(count)+"";
                Log.e("button clicked", Integer.toString(count));

                // 추가된 레이아웃의 로테이트 버튼을 눌렀을때 추가되는 것들
                final ImageView rotate_iv = textView.findViewById(R.id.rotate_button);
                rotate_iv.setTag("rotate_"+Integer.toString(count)+"");

                rotate_iv.setOnClickListener(new View.OnClickListener() {
                    int degree= 0;
                    @Override
                    public void onClick(View view) {
                    Log.e("Rotate 할 이미지", "클릭티드");
                    clicked_rotate_btn = String.valueOf(rotate_iv.getTag());
                    String[] divider_for_num = clicked_rotate_btn.split("_");

                    ImageView temp_image = linearLayout.findViewWithTag("button_"+divider_for_num[1]);// 실재 추가한 사진이 들어있는 이미지 뷰

                    BitmapDrawable drawable = (BitmapDrawable) temp_image.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    degree = degree+90;
                    temp_image.setImageBitmap(rotateImage(bitmap, 90));
                    imgRotate_list.set(parseInt(divider_for_num[1]), degree);
                    Log.e("rotate", Integer.toString(imgRotate_list.get(parseInt(divider_for_num[1]))));
                    Log.e("rotation height:", Integer.toString(temp_image.getHeight()));
                    Log.e("rotation width:", Integer.toString(temp_image.getWidth()));
                    }
                });

// 추가한 이미지를 클릭했을때 동작되는 행동들 -------
                final ImageView temp_iv = textView.findViewWithTag("button_"+Integer.toString(count)+"");
                temp_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("해당 뷰의 테그 이름", String.valueOf(temp_iv.getTag()));
                        clicked_image_name=String.valueOf(temp_iv.getTag());
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);

                    }
                });

                count++;
                linearLayout.addView(textView);
            }
        });


                setAlignment(rootView);

        return rootView;
    }//OncreateView 마지막


    public void upload_process(final String public_private){
        dialog = ProgressDialog.show(getActivity(), "", "업로드 중...", true);

        final String whole_text = textsToOne(title.getText().toString().trim().length(), first_text_area.getText().toString().trim().length(), first_text_area.getText().toString());
        final String image_names = imageNameToString();
        final String img_rotate_list= imgRotateToStringList();
        final String img_width_and_height = imgWidthHeightList();

        Log.e("Confirm ::", whole_text+"////"+image_names+"////"+img_rotate_list);
        Log.e("how many images in list", Integer.toString(imageList.size()));


//**********************************upload image with volley by string********************
        JSONObject jsonObject = new JSONObject();
//                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ArrayList<String> encoded_data = new ArrayList<>();
        for(int i = 0; i<imageList.size(); i++){
//                        imageList.get(i).compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//                        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            if(imageList.get(i) == null){
                encoded_data.add("no_image");
            }else{
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageList.get(i).compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                encoded_data.add(encodedImage);
            }

        }
        Log.e("imageList.size() ::", Integer.toString(imageList.size()));
        Log.e("encodedimage list size:", Integer.toString(encoded_data.size()));
        JSONArray jsonArray = new JSONArray();
        for(int i = 0; i<encoded_data.size(); i++){
            jsonArray.put(encoded_data.get(i));
        }
        Log.e("jsonArray count:", IntToString(jsonArray.length()));

        try {
            jsonObject.put("name", image_names);
            jsonObject.put("image", jsonArray);
        } catch (JSONException e) {
            Log.e("JSONObject Here", e.toString());
        }

        String url = "http://joonandhoon.dothome.co.kr/jhmovienote/upload_test.php";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e("Message from server_1", jsonObject.toString());

                //*****************************TExt 업로드 프로세스********************************
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e("response :", response);
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){
                                Log.e("upload : ", "success");
                                title.setText("");
                                first_text_area.setText("");

                                dialog.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("작성하신 글을 업로드 하였습니다.")
                                        .setPositiveButton("확인", null)
                                        .create()
                                        .show();
                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.detach(writingFragment.this).attach(writingFragment.this).commit();
                                    ft.replace(R.id.writing_fragment, writingFragment.newInstance());

                            }else{
                                Log.e("upload : ", "fail");
                                Log.e("message:", jsonResponse.toString());// 에러뜸!
                            }
                        }
                        catch(JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                };

                uploadRequest_ext_writefragment writeRequest = new uploadRequest_ext_writefragment(LoggedInUser_ID, LoggedInUser_ID,
                        title.getText().toString(), whole_text, image_names, img_rotate_list, alignment, img_width_and_height, public_private, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                queue.add(writeRequest);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Message from server_2", volleyError.toString());
                dialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("작성하신 글을 업로드 하지 못했습니다.")
                        .setNegativeButton("다시시도", null)
                        .create()
                        .show();
            }
        });
        ;
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getActivity()).add(jsonObjectRequest);


        //**********************************upload image with volley by string********************

    }

    public void setAlignment(View rootView){
        ImageButton align_left = (ImageButton) rootView.findViewById(R.id.align_left);
        align_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alignment="left";
                first_text_area.setGravity(Gravity.LEFT);
                findAlltextareaToSetAlign(3);
            }
        });

        ImageButton align_center = (ImageButton) rootView.findViewById(R.id.align_center);
        align_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alignment="center";
                first_text_area.setGravity(Gravity.CENTER);
                findAlltextareaToSetAlign(17);
            }
        });
        ImageButton align_right = (ImageButton) rootView.findViewById(R.id.align_right);
        align_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alignment="right";
                first_text_area.setGravity(Gravity.RIGHT);
                findAlltextareaToSetAlign(5);
            }
        });
    }

    public void findAlltextareaToSetAlign(int input){
        //center = 17
        //left = 3
        //right =5
        int align = input;
        for(int i =0; i<text_list.size(); i++){
            EditText temp = text_list.get(i);
            temp.setGravity(align);
        }
    }

    public String textsToOne(int title, int first_text, String first_text_string){

        StringBuilder total_story = new StringBuilder();
        if(title > 0){
            //*************타이틀 텍스트 에디터가 empty가 아닐때 **************
            Log.e("is title empty?", "no... something is in the title area");
            Log.e("textarealist count : ", Integer.toString(textarea_list.size()));
            if(first_text>0){
                //****************첫번째 텍스트 에디터가 empty가 아닐때
                total_story.append(first_text_string+"##$$##$$");
                for(int i =0; i<textarea_list.size(); i++){
                    EditText edittext = (EditText) linearLayout.findViewWithTag("textarea_"+i);//edittext 가져오기
                    if(i == textarea_list.size()-1){
                        if(edittext.getText().toString().length()>0){
                            // if(textarea_list.get(i).toString().trim().length()>0){
                            //만약 각각의 텍스트리스트가 empty 가 아닐때
                            if(textarea_list.get(i).equals("leave_it_empty")){
                                total_story.append("leave_it_empty");
                            }else{
                                total_story.append(edittext.getText().toString());
                            }
                            //total_story.append(edittext.getText().toString());
                            //total_story.append(textarea_list.get(i).toString()+"##$$##$$");
                        }else{
                            //만약 각각의 텍스트리스트가 empty 일때
                            total_story.append("leave_it_empty");
                        }
                    }else{
                        if(edittext.getText().toString().length()>0){
                            // if(textarea_list.get(i).toString().trim().length()>0){
                            //만약 각각의 텍스트리스트가 empty 가 아닐때

                            if(textarea_list.get(i).equals("leave_it_empty")){
                                total_story.append("leave_it_empty##$$##$$");
                            }else{
                                total_story.append(edittext.getText().toString()+"##$$##$$");
                            }
//                            total_story.append(edittext.getText().toString()+"##$$##$$");
                            //total_story.append(textarea_list.get(i).toString()+"##$$##$$");
                        }else{
                            //만약 각각의 텍스트리스트가 empty 일때
                            total_story.append("leave_it_empty##$$##$$");
                        }
                    }
                }
            }else {
                //****************첫번째 텍스트 에디터가 empty일때
                total_story.append("leave_it_empty##$$##$$");
                for(int i =0; i<textarea_list.size(); i++){
                    EditText edittext = (EditText) linearLayout.findViewWithTag("textarea_"+i);//edittext 가져오기

                    if(i == textarea_list.size()-1){
                        if(edittext.getText().toString().length()>0){
                            // if(textarea_list.get(i).toString().trim().length()>0){
                            //만약 각각의 텍스트리스트가 empty 가 아닐때

                            if(textarea_list.get(i).equals("leave_it_empty")){
                                total_story.append("leave_it_empty");
                            }else{
                                total_story.append(edittext.getText().toString());
                            }

                            //total_story.append(textarea_list.get(i).toString()+"##$$##$$");
                        }else{
                            //만약 각각의 텍스트리스트가 empty 일때
                            total_story.append("leave_it_empty");
                        }
                    }else{
                        if(edittext.getText().toString().length()>0){
                            // if(textarea_list.get(i).toString().trim().length()>0){
                            //만약 각각의 텍스트리스트가 empty 가 아닐때
                            if(textarea_list.get(i).equals("leave_it_empty")){
                                total_story.append("leave_it_empty##$$##$$");
                            }else{
                                total_story.append(edittext.getText().toString()+"##$$##$$");
                            }

                            //total_story.append(textarea_list.get(i).toString()+"##$$##$$");
                        }else{
                            //만약 각각의 텍스트리스트가 empty 일때
                            total_story.append("leave_it_empty##$$##$$");
                        }
                    }

                }

            }
      //      Toast.makeText(getActivity(), total_story.toString(), Toast.LENGTH_LONG).show();
        }else{
            //*************타이틀 텍스트 에디터가 empty일때 **************
            Log.e("is title empty?", "yes... it is empty");
        }
        return total_story.toString();
    }
    public String imgRotateToStringList(){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i= 0; i<imgRotate_list.size(); i++){
            if(i==imgRotate_list.size()-1){
                //마지막일때
                 stringBuilder.append(Integer.toString(imgRotate_list.get(i)));
            }else{
                stringBuilder.append(Integer.toString(imgRotate_list.get(i))+"##$$##$$");
            }
        }
            return stringBuilder.toString();
    }
    public String imageNameToString(){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i<imgName_list.size();i++){

            if(i==imgName_list.size()-1){
                //마지막 라인
                if(imgName_list.get(i).trim().length()>0){
                    // if image exist
                    stringBuilder.append(imgName_list.get(i).toString());
                }else{
                    // if image does not exist
                    stringBuilder.append("image_not_exist");
                }
            }else{
                if(imgName_list.get(i).trim().length()>0){
                    stringBuilder.append(imgName_list.get(i).toString()+"##$$##$$");
                }else{
                    stringBuilder.append("image_not_exist##$$##$$");
                }

            }

        }
        return stringBuilder.toString();
}
    public String imgWidthHeightList(){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < img_width_height.size(); i++){
            if(i == img_width_height.size()-1){
                stringBuilder.append(img_width_height.get(i).toString());
            }else{
                stringBuilder.append(img_width_height.get(i).toString()+"##$$##$$");
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public void onActivityResult(int requestcode, int resultcode, Intent data){

        if(resultcode == getActivity().RESULT_CANCELED){
            // only if nothing selected
        }else if(resultcode == getActivity().RESULT_OK){
            Log.e("Request code ", Integer.toString(requestcode) +"::"+Integer.toString(resultcode));
            String imgPath= getImageNameToUri(data.getData());
            name_Str = imgPath.substring(imgPath.lastIndexOf("/")+1);// 이미지의 이름
            String new_name = make_image_name_unique(name_Str);

            //**************put imgpath and name of the image in arraylist *****************
            String[] temp_num = clicked_image_name.split("_");
            int string_to_numb = parseInt(temp_num[1]);
            imgPath_list.set(string_to_numb, imgPath);
            imgName_list.set(string_to_numb, new_name);
            Log.e("size of path list", Integer.toString(imgPath_list.size()));
            Log.e("size of name list", Integer.toString(imgName_list.size()));

            //String[] temp = clicked_image_name.split("_");
            TextView img_name_text = linearLayout.findViewWithTag("textview_"+temp_num[1]);
            img_name_text.setText(new_name);

            //**************put imgpath and name of the image in arraylist *****************
            Bitmap image_bitmap = null;
            try {
                image_bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                Log.e("real image height", Integer.toString(image_bitmap.getHeight()));
                Log.e("real image width", Integer.toString(image_bitmap.getWidth()));
                img_width_height.set(string_to_numb, Integer.toString(image_bitmap.getHeight())+"/"+Integer.toString(image_bitmap.getWidth()));

                Bitmap resized = getResizedBitmap(image_bitmap, getScreenWidth(),getScreenHeight());
                Log.e("resized image height", Integer.toString(resized.getHeight()));
                Log.e("resized image width", Integer.toString(resized.getWidth()));
                imageList.set(string_to_numb, resized);
                ImageView iv = linearLayout.findViewWithTag(clicked_image_name);

                iv.setImageBitmap(resized);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    ///**********************to make image name unique *****************
    public String make_image_name_unique(String ori_name){
        String[] temp = ori_name.split("\\.");
        String without_extension = temp[0];
        String extension = temp[1];
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();

        String new_name;
        if(LoginType.equals("kakao")){
            new_name = LoggedInUser_ID+"_"+dateFormat.format(date).toString()+"."+extension;
        }else if(LoginType.equals("normal")){
            String[] temp_norm =LoggedInUser_ID.split("@");
            new_name = temp_norm[0]+"_"+dateFormat.format(date).toString()+"."+extension;
        }else{
            new_name = "";
        }
        Log.e("new name of image : ",new_name);
        return new_name;
    }

//이미지 url 가져오기
    public String getImageNameToUri(Uri data) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String imgPath = cursor.getString(column_index);  // === >>> 이놈이 이미지 path 실재 이미지가 저장되어있는놈.
        File temp_img1 = new File(imgPath);
        Log.e("오리지널 파일", imgPath+"////"+String.valueOf(temp_img1.length()/1024));
//        File temp_img = new File(imgPath);
//        File result = saveBitmapToFile(temp_img);
//        Log.e("결과", result.getAbsolutePath()+"/////"+String.valueOf(result.length()/1024));

        return imgPath;
    }

    //*********************************이미지 크기 줄이기 -----------------------------------
    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) { // 가장 중요한것 ..... input 할때 height 와  width 의 자리를 바꾸서 인풋한다
        int width = bm.getWidth();
        int height = bm.getHeight();
        Bitmap resizedBitmap;
        if(newHeight>=height || newWidth>=width){
            resizedBitmap = bm;
        }else{
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // create a matrix for the manipulation
            Matrix matrix = new Matrix();
            // resize the bit map
            matrix.postScale(scaleWidth, scaleHeight);
            // recreate the new Bitmap
            resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        }

        return resizedBitmap;
    }

    //*******************image rotate
    public Bitmap rotateImage(Bitmap src, float degree) {
            // Matrix 객체 생성
            Matrix matrix = new Matrix();
            // 회전 각도 셋팅
            matrix.postRotate(degree);
            // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
            return Bitmap.createBitmap(src, 0, 0, src.getWidth(),src.getHeight(), matrix, true);
        }


    //*****************************convert bitmap to file ********************
    public File saveBitmapToFile(File file){
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }



//*********************** file uploader ******************************** OUTDATED --> ALTERNATIVE --> VOLLEY
//    public int uploadFile(String sourceFileUri, String image_Name) {
//        String fileName = sourceFileUri;
//        String imageName = image_Name;
//        HttpURLConnection conn = null;
//        DataOutputStream dos = null;
//        String lineEnd = "\r\n";
//        String twoHyphens = "--";
//        String boundary = "*****";
//        int bytesRead, bytesAvailable, bufferSize;
//        byte[] buffer;
//        int maxBufferSize = 100 * 1024 * 1024;
//        File sourceFile = new File(sourceFileUri);
//
//    //    File sourceFile = saveBitmapToFile(sourceFile_temp);
//        Log.e("결과", sourceFile.getAbsolutePath()+"/////"+String.valueOf(sourceFile.length()/1024));
//
//        if (!sourceFile.isFile()) {
//            dialog.dismiss();
//            getActivity().runOnUiThread(new Runnable() {
//                public void run() {
//                    Log.e("if file is not a file", "it is not a file");
//                }
//            });
//            return 0;
//        }else {
//            try {
//                // open a URL connection to the Servlet
//
//                FileInputStream fileInputStream = new FileInputStream(sourceFile);
//                URL url = new URL(upLoadServerUri);
//                // Open a HTTP  connection to  the URL
//                conn = (HttpURLConnection) url.openConnection();
//                conn.setDoInput(true); // Allow Inputs
//                conn.setDoOutput(true); // Allow Outputs
//                conn.setUseCaches(false); // Don't use a Cached Copy
//                conn.setRequestMethod("POST");
//                conn.setRequestProperty("Connection", "Keep-Alive");
//                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
//                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
//                conn.setRequestProperty("uploaded_file", fileName);
//                conn.setRequestProperty("imageName", imageName);
//
//                dos = new DataOutputStream(conn.getOutputStream());
//
//
//                dos.writeBytes(twoHyphens + boundary + lineEnd);
//                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);
//                dos.writeBytes(lineEnd);
//                //  위는 파일
//
//                // create a buffer of  maximum size
//                bytesAvailable = fileInputStream.available();
//                bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                buffer = new byte[bufferSize];
//                // read file and write it into form...
//                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//                while (bytesRead > 0) {
//                    dos.write(buffer, 0, bufferSize);
//                    bytesAvailable = fileInputStream.available();
//                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//                }
//                // send multipart form data necesssary after file data...
//                dos.writeBytes(lineEnd);
//
//
//                dos.writeBytes(twoHyphens + boundary + lineEnd);
//                dos.writeBytes("Content-Disposition: form-data; name=\"imageName\"" + lineEnd);
//                dos.writeBytes(lineEnd);
//                dos.writeBytes(imageName);
//                dos.writeBytes(lineEnd);
//                // 위는 text
//
//
//
//
//                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//                // Responses from the server (code and message)
//                serverResponseCode = conn.getResponseCode();
//                String serverResponseMessage = conn.getResponseMessage();
//                Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
//                if(serverResponseCode == 200){
//                    getActivity().runOnUiThread(new Runnable() {
//                        public void run() {
//                            //************this is when upload successfully completed
//
//                 Log.e("uploaded file status", "file upload successfully");
//
//                        }
//                    });
//                }
//
//                //close the streams //
//                fileInputStream.close();
//                dos.flush();
//                dos.close();
//            } catch (MalformedURLException ex) {
//                dialog.dismiss();
//                ex.printStackTrace();
//                getActivity().runOnUiThread(new Runnable() {
//                    public void run() {
//
//                        // 잘못된 url 일때
////                        TextView welcometext = (TextView) findViewById(R.id.welcomeText);
////                        welcometext.setText("MalformedURLException Exception : check script url.");
////                        Toast.makeText(WriteActivity.this, "MalformedURLException",
////                                Toast.LENGTH_SHORT).show();
//                    }
//                });
//                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
//            } catch (final Exception e) {
//                dialog.dismiss();
//                e.printStackTrace();
//                getActivity().runOnUiThread(new Runnable() {
//                    public void run() {
//                        //***************got exception see logcat *********************
////                        messageText.setText("Got Exception : see logcat ");
////                        Toast.makeText(WriteActivity.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
////                        TextView welcometext = (TextView) findViewById(R.id.welcomeText);
////
////                        welcometext.setText(e.getStackTrace().toString());
//                        //Toast.makeText(WriteActivity.this, e.getMessage().toString() + e.getStackTrace().toString(), Toast.LENGTH_LONG).show();
//                    }
//                });
//                Log.e("Exception", "Exception : " + e.getMessage(), e);
//            }
//            dialog.dismiss();
//            return serverResponseCode;
//        } // End else block
//
//    }

}
