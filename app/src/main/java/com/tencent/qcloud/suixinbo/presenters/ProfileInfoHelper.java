package com.tencent.qcloud.suixinbo.presenters;

import android.util.Log;

import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.suixinbo.presenters.viewinface.ProfileView;

/**
 * 用户资料获取
 */
public class ProfileInfoHelper {
    private String TAG = getClass().getName();
    private ProfileView mView;

    public ProfileInfoHelper(ProfileView view){
        mView = view;
    }

    public void getMyProfile(){
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {
                Log.w(TAG, "getMyProfile->error:"+i+","+s);
            }

            @Override
            public void onSuccess(TIMUserProfile timUserProfile) {
                mView.updateProfileInfo(timUserProfile);
            }
        });
    }
}
