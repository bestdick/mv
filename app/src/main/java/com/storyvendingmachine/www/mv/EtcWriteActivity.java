package com.storyvendingmachine.www.mv;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class EtcWriteActivity extends AppCompatActivity {

    List<String> listview_items;
    ArrayAdapter<String> listview_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etc_write);
        toolbar();
        initializer();
    }
    public void toolbar(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("menu_button");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close);
        getSupportActionBar().setTitle("");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리
    }
    public void initializer(){
        TextView toolbar_title_textView = (TextView) findViewById(R.id.toolbar_title_textView);
        EditText title_editText = (EditText) findViewById(R.id.title_editText);
        EditText content_editText = (EditText) findViewById(R.id.content_editText);

        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        if(type.equals("free_board")){
            toolbar_title_textView.setText("#자유 게시판 글 작성");
        }else if (type.equals("error_board")){
            toolbar_title_textView.setText("#오류신고 게시판 글 작성");
        }else{
            // type.equals("suggestion")
            toolbar_title_textView.setText("#개선및건의 게시판 글 작성");
        }
    }


}