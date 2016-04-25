package com.tencent.qcloud.suixinbo.views;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.model.LiveRoomInfo;
import com.tencent.qcloud.suixinbo.model.UserInfo;
import com.tencent.qcloud.suixinbo.utils.Constants;


/**
 * 视频和照片输入页面
 */
public class FragmentLiveList extends Fragment implements View.OnClickListener {
    private static final String TAG = "FragmentLiveList";
    private ImageButton mBtn_videoCreate, mBtn_JoinRoom;
    private EditText hostText;
    private TextView joinLive;

    public FragmentLiveList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.liveframent_layout, container, false);
//        hostText =(EditText)view.findViewById(R.id.testHostId);
        joinLive = (TextView) view.findViewById(R.id.JoinLive);
        joinLive.setOnClickListener(this);
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
        if (view.getId() == R.id.JoinLive) {
            Intent intent = new Intent(getActivity(),LivePlayActivity.class);
            intent.putExtra(Constants.ID_STATUS,Constants.MEMBER);
            UserInfo.getInstance().setIdStatus(Constants.MEMBER);
            LiveRoomInfo.getInstance().setHostID("willguo");
            LiveRoomInfo.getInstance().setRoomNum(54321);
            startActivity(intent);
        }
    }


}
