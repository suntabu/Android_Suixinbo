package com.tencent.qcloud.suixinbo.views;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.tencent.TIMUserProfile;
import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.model.MySelfInfo;
import com.tencent.qcloud.suixinbo.presenters.LoginHeloper;
import com.tencent.qcloud.suixinbo.presenters.ProfileInfoHelper;
import com.tencent.qcloud.suixinbo.presenters.viewinface.LogoutView;
import com.tencent.qcloud.suixinbo.presenters.viewinface.ProfileView;
import com.tencent.qcloud.suixinbo.utils.GlideCircleTransform;
import com.tencent.qcloud.suixinbo.utils.UIUtils;
import com.tencent.qcloud.suixinbo.views.customviews.LineControllerView;


/**
 * 视频和照片输入页面
 */
public class FragmentProfile extends Fragment implements View.OnClickListener, LogoutView, ProfileView {
    private static final String TAG = "FragmentLiveList";
    private TextView mID;
    private ImageView mAvatar;
    private TextView mProfileName;
    private TextView mProfileInfo;
    private LoginHeloper mLoginHeloper;
    private ProfileInfoHelper mProfileHelper;
    private LineControllerView mBtnLogout;

    public FragmentProfile() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.profileframent_layout, container, false);
        mID = (TextView) view.findViewById(R.id.name);
        mID.setText(MySelfInfo.getInstance().getId());
        mAvatar = (ImageView) view.findViewById(R.id.profile_avatar);
        mProfileName = (TextView) view.findViewById(R.id.profile_name);
        mProfileInfo = (TextView) view.findViewById(R.id.profile_info);
        mBtnLogout = (LineControllerView) view.findViewById(R.id.logout);
        mBtnLogout.setOnClickListener(this);

        mLoginHeloper = new LoginHeloper(getActivity().getApplicationContext(), this);
        mProfileHelper = new ProfileInfoHelper(this);
        mProfileHelper.getMyProfile();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.logout) {
            mLoginHeloper.imLogout();
        }
    }


    @Override
    public void LogoutSucc() {
        Toast.makeText(getContext(), "Logout and quite", Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }

    @Override
    public void LogoutFail() {

    }

    @Override
    public void updateProfileInfo(TIMUserProfile profile) {
        if (TextUtils.isEmpty(profile.getNickName())){
            MySelfInfo.getInstance().setNickName(profile.getIdentifier());
        }else{
            MySelfInfo.getInstance().setNickName(profile.getNickName());
        }
        mProfileName.setText(MySelfInfo.getInstance().getNickName());
        mProfileInfo.setText(profile.getRemark());
        if (TextUtils.isEmpty(profile.getFaceUrl())){
            Bitmap bitmap = BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.default_avatar);
            Bitmap cirBitMap = UIUtils.createCircleImage(bitmap, 0);
            mAvatar.setImageBitmap(cirBitMap);
        }else{
            Log.d(TAG, "profile avator: " + profile.getFaceUrl());
            MySelfInfo.getInstance().setAvatar(profile.getFaceUrl());
            RequestManager req = Glide.with(getActivity());
            req.load(profile.getFaceUrl()).transform(new GlideCircleTransform(getActivity())).into(mAvatar);
        }
        MySelfInfo.getInstance().writeToCache(getContext());
    }
}
