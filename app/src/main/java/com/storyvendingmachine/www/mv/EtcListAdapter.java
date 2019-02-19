package com.storyvendingmachine.www.mv;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

public class EtcListAdapter extends BaseAdapter {

    private Context context;
    private List<EtcList> list;
    public EtcListAdapter(Context context, List<EtcList> list){
        this.context = context;
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v =View.inflate(context, R.layout.container_etc_list_element, null);
        ImageView isNew_imageView = (ImageView) v.findViewById(R.id.isNew_imageView);
        TextView type_textView = (TextView) v.findViewById(R.id.type_textView);
        TextView title_textView = (TextView) v.findViewById(R.id.title_textView);
        TextView author_textView = (TextView) v.findViewById(R.id.author_textView);
        TextView date_textView = (TextView) v.findViewById(R.id.date_textView);

        String isNew = list.get(i).getIsNew();
        String type = list.get(i).getType();
        String title = list.get(i).getTitle();
        String author = list.get(i).getUser_nickname();
        String upload_date = list.get(i).getUpload_date();
        String uploda_time = list.get(i).getUpload_time();

        if(isNew.equals("new")){

        }else{
            isNew_imageView.setVisibility(View.GONE);
        }
        type_textView.setText(type_to_korean(type));
        title_textView.setText(title);
        author_textView.setText(author);
        date_textView.setText(upload_date);

        return v;
    }

    public String type_to_korean(String input_type){
        switch (input_type){
            case  "news":
                return "[뉴스]";
            case "update":
                return "[업데이트]";
            case "announcement":
                return "[공지]";
            case "error":
                return "[오류신고]";
            case "free":
                return "[자유게시판]";
            default:
                return "[개선및건의]";
        }
    }
}
