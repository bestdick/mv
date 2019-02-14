package com.storyvendingmachine.www.mv;

import android.content.Intent;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.kakaotalk.response.KakaoTalkProfile;
import com.kakao.network.ErrorResult;

/**
 * Created by Administrator on 2018-03-18.
 */

public class requestKakaoStatus {

     protected void requestprofile(){
       com.kakao.kakaotalk.v2.KakaoTalkService.getInstance().requestProfile(new TalkResponseCallback<KakaoTalkProfile>() {
           @Override
           public void onNotKakaoTalkUser() {

           }

           @Override
           public void onSessionClosed(ErrorResult errorResult) {

           }

           @Override
           public void onNotSignedUp() {

           }

           @Override
           public void onSuccess(KakaoTalkProfile result) {

           }
       });
   }
}
