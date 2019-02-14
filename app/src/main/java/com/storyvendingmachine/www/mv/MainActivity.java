package com.storyvendingmachine.www.mv;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kakao.auth.Session;
import com.squareup.picasso.Picasso;

import static android.view.KeyEvent.KEYCODE_VOLUME_DOWN;
import static android.view.KeyEvent.KEYCODE_VOLUME_UP;
import static com.storyvendingmachine.www.mv.LoginActivity.callback;
import static com.storyvendingmachine.www.mv.mainFragment.volumeFlag;
import static com.storyvendingmachine.www.mv.mainFragment.volume_mute;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    static String LoginType;
    static String LoggedInUser_ID;
    static String LoggedInUser_nickname;
    static String LoggedInUser_thumbnail;

    static ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;

    //private ViewPager mViewPager;

    // 추가된 소스
    Toolbar myToolbar;

    DrawerLayout drawer;

    static int screen = 0;

    static ProgressBar pb;
    SharedPreferences login_remember;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = (ProgressBar) findViewById(R.id.mainactivity_pb);

                //permission
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 0);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 0);

        Intent intent = getIntent();
        LoginType = intent.getStringExtra("login_type");
        if(LoginType.equals("kakao")){
           // Toast.makeText(MainActivity.this, "kakao login", Toast.LENGTH_LONG).show();
            LoggedInUser_ID=intent.getStringExtra("kakao_id");//카카오 아이디를 int 에서 string 으로 바꾸었다
            LoggedInUser_nickname =intent.getStringExtra("nickname");
            LoggedInUser_thumbnail = intent.getStringExtra("thumb_nail");


        }else if(LoginType.equals("normal")){
            //Toast.makeText(MainActivity.this, "normal login", Toast.LENGTH_LONG).show();
            LoggedInUser_ID = intent.getStringExtra("user_email");
            LoggedInUser_nickname = intent.getStringExtra("user_nickname");
            LoggedInUser_thumbnail = null;

        }else{
            Toast.makeText(MainActivity.this, "else login", Toast.LENGTH_LONG).show();
        }



        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOffscreenPageLimit(5);



//drawer navigation 초기 설정
        drawer = (DrawerLayout) findViewById(R.id.drawer);
//drawer navigation 초기 설정

        //handling navigation view item event
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        toolbar();

}

    public void toolbar(){
        // 추가된 소스, Toolbar를 생성한다.
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    //toolbox setting -[----------
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("menu_button");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.button);
//        getSupportActionBar().setTitle("");  //해당 액티비티의 툴바에 있는 타이틀을 공백으로 처리
        ImageView thumbnail_imageView = findViewById(R.id.toolbar_thumbnail_imageView);
        getImage(thumbnail_imageView, LoggedInUser_thumbnail);
    }

    public void getImage(ImageView imageView, String url){
        Picasso.with(this)
                .load(url)
                .transform(new CircleTransform())
                .into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    public void moveToUserInfoActivity(){
        Intent intent = new Intent(MainActivity.this, UserInfoSetting.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_left_bit);
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item_1){
            mViewPager.setCurrentItem(0);
        }else if(id == R.id.item_2){
            mViewPager.setCurrentItem(1);
        }else if (id == R.id.item_3){
            mViewPager.setCurrentItem(2);
        }else if(id == R.id.item_4) {
            drawer.closeDrawer(GravityCompat.START);
            moveToUserInfoActivity();
        }else if(id == R.id.item_5){
            login_remember = getSharedPreferences("setting", 0);
            editor = login_remember.edit();
            editor.putBoolean("id_pw_match", false);
            editor.putString("user_email", "");
            editor.putString("user_password", "");
            editor.commit();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            editor.putString("kakao", "false");
            Session.getCurrentSession().removeCallback(callback);
            //아래 2개는 완전히 콜백을 삭제시킨다.
            Session.getCurrentSession().clearCallbacks();
            Session.getCurrentSession().close();
            finish();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //추가된 소스, ToolBar에 menu.xml을 인플레이트함
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
//        final MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.menu, menu);
        Log.e("toolbar action control", "controlling");
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
            Log.e("option item ::", Integer.toString(id));
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            //drawer is open
            drawer.closeDrawer(Gravity.START);
        } else {
            //drawer is closed
            drawer.openDrawer(Gravity.START);
        }

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        // 휴대폰 외부의 물리적 볼륨 컨트롤 키를 눌렀을때 실행되는 메소드이다
        if(keyCode == KEYCODE_VOLUME_UP || keyCode == KEYCODE_VOLUME_DOWN){
            volume_mute.setImageDrawable(null);
            volume_mute.setBackgroundResource(R.drawable.volume_on_icon);
            volumeFlag=1;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        //this is only needed if you have specific things
        //that you want to do when the user presses the back button.
        /* your specific things...*/
        Session.getCurrentSession().removeCallback(callback);
        super.onBackPressed();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus == true) {
            // 해당 화면 보임
            if(screen == 0){

            }else{
                mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                // Set up the ViewPager with the sections adapter.
                mViewPager = (ViewPager) findViewById(R.id.container);
                mViewPager.setAdapter(mViewPagerAdapter);
                mViewPager.setOffscreenPageLimit(3);
                screen = 0;
            }



        } else {
            // 화면 안보임

        }
    }



}
