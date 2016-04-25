package com.tencent.qcloud.suixinbo.views;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.avcontrollers.AvConstants;
import com.tencent.qcloud.suixinbo.avcontrollers.QavsdkControl;
import com.tencent.qcloud.suixinbo.model.LiveRoomInfo;
import com.tencent.qcloud.suixinbo.model.UserInfo;
import com.tencent.qcloud.suixinbo.presenters.EnterRoomAndQuiteRoomPresenter;
import com.tencent.qcloud.suixinbo.presenters.LiveControlPresenter;
import com.tencent.qcloud.suixinbo.presenters.viewinface.EnterQuiteRoomView;
import com.tencent.qcloud.suixinbo.presenters.viewinface.LiveView;
import com.tencent.qcloud.suixinbo.utils.Constants;


/**
 * Live直播类
 */
public class LivePlayActivity extends Activity implements EnterQuiteRoomView, LiveView, View.OnClickListener {
    private static int IDStatus = -1;
    private EnterRoomAndQuiteRoomPresenter mEnterRoomProsscessHelper;
    private LiveControlPresenter mLiveControlHelper;
    private static final String TAG = LivePlayActivity.class.getSimpleName();
    private View avView;
    private TextView BtnBack;
    private boolean SurfaceViewCreate = false;
    private boolean HostEnter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_live);


        mEnterRoomProsscessHelper = new EnterRoomAndQuiteRoomPresenter(this, this);
        mLiveControlHelper = new LiveControlPresenter(this, this);
        SurfaceViewCreate = false;
        HostEnter = false;
        initView();
        registerReceiver();

        //获取进入身份
        IDStatus = UserInfo.getInstance().getIdStatus();
        //进入房间流程
        mEnterRoomProsscessHelper.startEnterRoom(IDStatus);


//        QavsdkControl.getInstance().setCameraPreviewChangeCallback();

    }


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();


            //AvSurfaceView 初始化成功
            if (action.equals(AvConstants.ACTION_SURFACE_CREATED)) {
                SurfaceViewCreate = true;
                //打开摄像头
                if (UserInfo.getInstance().getIdStatus() == Constants.HOST) {
                    mLiveControlHelper.OpenCameraAndMic();
                } else {

//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
                    String host = LiveRoomInfo.getHostID();
//                    mLiveControlHelper.requestView(host);
                    if (SurfaceViewCreate == true && HostEnter == true) {
                        Log.i(TAG, "yes yes yes surface into ");
                        mLiveControlHelper.requestView(host);
                    }

                }


            }
            //主播数据OK
            if (action.equals(AvConstants.ACTION_HOST_ENTER)) {
                HostEnter = true;
                String host = LiveRoomInfo.getHostID();
                if (SurfaceViewCreate == true && HostEnter == true) {
                    Log.i(TAG, "yes yes yes member into ");

                    mLiveControlHelper.requestView(host);
                }
            }

        }
    };

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AvConstants.ACTION_SURFACE_CREATED);
        intentFilter.addAction(AvConstants.ACTION_HOST_ENTER);
        registerReceiver(mBroadcastReceiver, intentFilter);

    }

    private void unregisterReceiver() {
        unregisterReceiver(mBroadcastReceiver);
    }

    /**
     * 初始化UI
     */
    private void initView() {
        avView = findViewById(R.id.av_video_layer_ui);
        BtnBack = (TextView) findViewById(R.id.btn_back);
        BtnBack.setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        QavsdkControl.getInstance().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        QavsdkControl.getInstance().onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
        QavsdkControl.getInstance().onDestroy();
    }

    /**
     * 进入房间回调
     *
     * @param id_status
     * @param isSucc
     */
    @Override
    public void EnterRoomCB(int id_status, boolean isSucc) {
        if (isSucc == true) {
            if (id_status == Constants.HOST) {//主播方式加入房间成功
                //开启摄像头渲染画面
                Log.i(TAG, "createlive EnterRoomCB isSucc" + isSucc);
                mEnterRoomProsscessHelper.initAvUILayer(avView);
                Toast.makeText(LivePlayActivity.this, "Host Enter Live Room Succ ", Toast.LENGTH_SHORT).show();
            } else {//以成员方式加入房间成功
                Toast.makeText(LivePlayActivity.this, "Member Enter Live Room Succ ", Toast.LENGTH_SHORT).show();
                mEnterRoomProsscessHelper.initAvUILayer(avView);
                //等待zhuzhi

            }
        }
    }


    @Override
    public void QuiteRoomCB(int id_status, boolean succ) {
        finish();
    }

    /**
     * 开启本地渲染
     */
    @Override
    public void showVideoView(boolean isHost) {
        //渲染本地界面
        if (isHost == true) {

            Log.i(TAG, "showVideoView host :" + UserInfo.getInstance().getId());
            QavsdkControl.getInstance().setSelfId(UserInfo.getInstance().getId());
            QavsdkControl.getInstance().setLocalHasVideo(true, UserInfo.getInstance().getId());
            //通知用户服务器
            mEnterRoomProsscessHelper.notifyServerCreateRoom();
        } else {
            String host = LiveRoomInfo.getHostID();
            QavsdkControl.getInstance().setRemoteHasVideo(true, LiveRoomInfo.getHostID(), Constants.BackGroundViewIndex);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                mEnterRoomProsscessHelper.stepOutRoom();
                break;
        }
    }
}
