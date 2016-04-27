package com.tencent.qcloud.suixinbo.views;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.adapters.LiveShowAdapter;
import com.tencent.qcloud.suixinbo.model.LiveRoomInfo;
import com.tencent.qcloud.suixinbo.model.LiveShowModel;
import com.tencent.qcloud.suixinbo.model.UserInfo;
import com.tencent.qcloud.suixinbo.utils.Constants;

import java.util.ArrayList;
import java.util.List;


/**
 * 视频和照片输入页面
 */
public class FragmentLiveList extends Fragment implements View.OnClickListener {
    private static final String TAG = "FragmentLiveList";
    private ListView mLiveList;
    private List<LiveShowModel> liveList = new ArrayList<>();
    private LiveShowAdapter adapter;

    public FragmentLiveList() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.liveframent_layout, container, false);
        mLiveList = (ListView) view.findViewById(R.id.live_list);
        Display display = getActivity().getWindowManager().getDefaultDisplay();

        LiveShowModel test1 = new LiveShowModel();
        test1.setCover("12312");
        test1.setHostName("axing");
        test1.setTitle("This is a test");
        test1.setHostUid(54321);
        test1.setCover("");
        test1.setAdmireCount(1231);
        test1.setHostAvatar("");

        LiveShowModel test2 = new LiveShowModel();
        test2.setCover("12312");
        test2.setHostName("axing");
        test2.setHostUid(54321);
        test2.setCover("");
        test2.setAdmireCount(1231);
        test1.setHostAvatar("");


        liveList.add(test1);
        liveList.add(test2);

        adapter = new LiveShowAdapter(this.getActivity(), R.layout.item_liveshow, liveList);
        mLiveList.setAdapter(adapter);
        mLiveList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), "12312", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), LivePlayActivity.class);
                intent.putExtra(Constants.ID_STATUS, Constants.MEMBER);
                UserInfo.getInstance().setIdStatus(Constants.MEMBER);
                LiveRoomInfo.getInstance().setHostID("willguo");
                LiveRoomInfo.getInstance().setRoomNum(54321);
                startActivity(intent);
            }
        });

//        joinLive.setOnClickListener(this);
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
    }


}
