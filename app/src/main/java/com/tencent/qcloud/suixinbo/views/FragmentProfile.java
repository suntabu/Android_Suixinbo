package com.tencent.qcloud.suixinbo.views;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.model.UserInfo;
import com.tencent.qcloud.suixinbo.presenters.LoginHeloper;
import com.tencent.qcloud.suixinbo.presenters.OKhttpHelper;
import com.tencent.qcloud.suixinbo.presenters.viewinface.LogoutView;


/**
 * 视频和照片输入页面
 */
public class FragmentProfile extends Fragment implements View.OnClickListener, LogoutView {
    private static final String TAG = "FragmentLiveList";
    private TextView mID, mBtnLogout, mTestHttp,mTestHttp2;
    private LoginHeloper mLoginHeloper;


    public FragmentProfile() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.profileframent_layout, container, false);
        mID = (TextView) view.findViewById(R.id.name);
        mID.setText(UserInfo.getInstance().getId());
        mBtnLogout = (TextView) view.findViewById(R.id.logout);
        mBtnLogout.setOnClickListener(this);
        mTestHttp = (TextView) view.findViewById(R.id.startlive);
        mTestHttp.setOnClickListener(this);
        mTestHttp2 = (TextView) view.findViewById(R.id.stoplive);
        mTestHttp2.setOnClickListener(this);

        mLoginHeloper = new LoginHeloper(getActivity().getApplicationContext(), this);
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

        if (view.getId() == R.id.startlive) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
////                    OKhttpHelper.getInstance().notifyServerNewLiveInfo();
//                }
//            }).start();
        }

        if (view.getId() == R.id.stoplive) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OKhttpHelper.getInstance().getLiveList(0,10);
                }
            }).start();
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


}
