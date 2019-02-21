package com.storyvendingmachine.www.mv;

import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import static com.storyvendingmachine.www.mv.MainActivity.screen;
import static com.storyvendingmachine.www.mv.mainFragment.volumeFlag;
import static com.storyvendingmachine.www.mv.mainFragment.volume_mute;

public class BoxOfficeActivity extends AppCompatActivity {
    Toolbar myToolbar;

    YouTubePlayerSupportFragment youTubePlayerFragment;
    FragmentTransaction transaction;

//    ImageView volume_mute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_office);
        screen = 1;

//        MuteAudio();
//        volumeFlag=0;
//
//        volume_mute = (ImageView) findViewById(R.id.volume_mute_control_button);
//        volume_mute.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(volumeFlag == 0){
//                    volume_mute.setImageDrawable(null);
//                    volume_mute.setBackgroundResource(R.drawable.volume_on_icon);
//                    UnMuteAudio();
//                    volumeFlag=1;
//                }else{
//                    volume_mute.setImageDrawable(null);
//                    volume_mute.setBackgroundResource(R.drawable.volume_off_icon);
//                    MuteAudio();
//                    volumeFlag=0;
//                }
//
//            }
//        });


        Intent intent = getIntent();
        String from = intent.getStringExtra("from");
        if (from.equals("boxOffice")) {

        ConstraintLayout youtube_description_conLayout = (ConstraintLayout) findViewById(R.id.youtube_description_conLayout);
        youtube_description_conLayout.setVisibility(View.GONE);
        String rank = intent.getStringExtra("rank");
        final String webview_url = intent.getStringExtra("hyperlink");//네이버 하이퍼 링크
        String title = intent.getStringExtra("title");
        String youtube_url = intent.getStringExtra("youtube_url");

        ImageButton naverHyper = (ImageButton) findViewById(R.id.naver_hyperlink);
        naverHyper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(webview_url));
                startActivity(browserIntent);
            }
        });

        when_first_created(title);
        toolbar("  Box Office No." + rank);
        youtube(youtube_url);
        }else{
            //from.equal("youtube");
            String title = intent.getStringExtra("video_title");
            String description = intent.getStringExtra("video_description");
            String video_url = intent.getStringExtra("video_url");

            ConstraintLayout movie_info_conLayout = (ConstraintLayout) findViewById(R.id.movie_info_conLayout);
            movie_info_conLayout.setVisibility(View.GONE);
            TextView description_textView = (TextView) findViewById(R.id.description_textView);

            when_first_created(title);
            description_textView.setText(description);
            youtube(video_url);
            toolbar("");

        }
    }

    public void when_first_created(String title){
        TextView title_textView = (TextView) findViewById(R.id.bo_movie_title_textView);
        title_textView.setText(title);
    }
    public void youtube(final String url){
        youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        youTubePlayerFragment.initialize("AIzaSyCtICWDaIimwYlVC6tkiUxa9d7ZswS0zP4", new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.setShowFullscreenButton(false);
                youTubePlayer.loadVideo(url);
                 youTubePlayer.play();
//                youTubePlayer.release();
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }

        });
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.youtube_fragment, youTubePlayerFragment).commit();
    }

    public void toolbar(String rank){
        //toolbar back 버튼 생성
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("menu_button");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setTitle(rank);  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_down_bit,R.anim.slide_down);// first entering // second exiting
    }

    public void MuteAudio(){
        AudioManager mAlramMAnager = (AudioManager) getSystemService(this.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
            //mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0);
        } else {
            //mAlramMAnager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            //mAlramMAnager.setStreamMute(AudioManager.STREAM_RING, true);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        }
    }

    public void UnMuteAudio(){
        AudioManager mAlramMAnager = (AudioManager) getSystemService(this.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE,0);
            //mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0);
        } else {
            //mAlramMAnager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            //mAlramMAnager.setStreamMute(AudioManager.STREAM_RING, false);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
        }
    }

}
