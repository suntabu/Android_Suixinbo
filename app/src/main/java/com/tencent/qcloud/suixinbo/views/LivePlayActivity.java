package com.tencent.qcloud.suixinbo.views;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.av.sdk.AVView;
import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.adapters.ChatMsgListAdapter;
import com.tencent.qcloud.suixinbo.avcontrollers.AvConstants;
import com.tencent.qcloud.suixinbo.avcontrollers.QavsdkControl;
import com.tencent.qcloud.suixinbo.model.ChatEntity;
import com.tencent.qcloud.suixinbo.model.LiveRoomInfo;
import com.tencent.qcloud.suixinbo.model.UserInfo;
import com.tencent.qcloud.suixinbo.presenters.EnterLiveHelper;
import com.tencent.qcloud.suixinbo.presenters.LiveControlHelper;
import com.tencent.qcloud.suixinbo.presenters.viewinface.EnterQuiteRoomView;
import com.tencent.qcloud.suixinbo.presenters.viewinface.LiveView;
import com.tencent.qcloud.suixinbo.utils.Constants;
import com.tencent.qcloud.suixinbo.views.customviews.InputTextMsgDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Live直播类
 */
public class LivePlayActivity extends Activity implements EnterQuiteRoomView, LiveView, View.OnClickListener {
    private EnterLiveHelper mEnterRoomProsscessHelper;
    private LiveControlHelper mLiveControlHelper;
    private static final String TAG = LivePlayActivity.class.getSimpleName();
    private View avView;
    private TextView BtnBack,BtnInput;
    private ListView mListViewMsgItems;
    private ArrayList<ChatEntity> mArrayListChatEntity;
    private ChatMsgListAdapter mChatMsgListAdapter;
    private LinearLayout mHostbottomLy,mMemberbottomLy;
    private static final int MINFRESHINTERVAL = 500;
    private boolean mBoolRefreshLock = false;
    private boolean mBoolNeedRefresh = false;
    private final Timer mTimer = new Timer();
    private ArrayList<ChatEntity> mTmpChatList= new ArrayList<ChatEntity>();//缓冲队列
    private TimerTask mTimerTask = null;
    private static final int REFRESH_LISTVIEW = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_live);

        //进出房间的协助类
        mEnterRoomProsscessHelper = new EnterLiveHelper(this, this);
        //房间内的交互协助类
        mLiveControlHelper = new LiveControlHelper(this, this);

        initView();
        registerReceiver();

        //进入房间流程
        mEnterRoomProsscessHelper.startEnterRoom();

//        QavsdkControl.getInstance().setCameraPreviewChangeCallback();

    }

    private static class MyHandler extends Handler {
        private final WeakReference<LivePlayActivity> mActivity;

        public MyHandler(LivePlayActivity activity) {
            mActivity = new WeakReference<LivePlayActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (null == mActivity.get()) {
                return;
            }
            mActivity.get().processInnerMsg(msg);
        }
    }


    private void processInnerMsg(Message msg) {
        switch (msg.what) {
            case REFRESH_LISTVIEW:
                doRefreshListView();
                break;
        }
        return;
    }



    private final MyHandler mHandler = new MyHandler(this);


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //AvSurfaceView 初始化成功
            if (action.equals(AvConstants.ACTION_SURFACE_CREATED)) {
                //打开摄像头
                if (UserInfo.getInstance().getIdStatus() == Constants.HOST) {
                    mLiveControlHelper.OpenCameraAndMic();
                } else {
                    //成员请求主播画面
                    String host = LiveRoomInfo.getHostID();
                    mLiveControlHelper.requestView(host);
                }

            }
            //主播数据OK
            if (action.equals(AvConstants.ACTION_HOST_ENTER)) {
                //主播上线才开始渲染视频
                mEnterRoomProsscessHelper.initAvUILayer(avView);
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
        mHostbottomLy = (LinearLayout)findViewById(R.id.host_bottom_layout);
        mMemberbottomLy = (LinearLayout)findViewById(R.id.member_bottom_layout);
        if(UserInfo.getInstance().getIdStatus()==Constants.HOST){
            mHostbottomLy.setVisibility(View.VISIBLE);
            mMemberbottomLy.setVisibility(View.GONE);
        }else{
            mMemberbottomLy.setVisibility(View.VISIBLE);
            mHostbottomLy.setVisibility(View.GONE);
        }


        avView = findViewById(R.id.av_video_layer_ui);
        BtnBack = (TextView) findViewById(R.id.btn_back);
        BtnBack.setOnClickListener(this);
        BtnInput = (TextView) findViewById(R.id.message_input);
        BtnInput.setOnClickListener(this);
        mListViewMsgItems = (ListView)findViewById(R.id.im_msg_listview);
        mArrayListChatEntity = new ArrayList<ChatEntity>();
        mChatMsgListAdapter = new ChatMsgListAdapter(this, mListViewMsgItems, mArrayListChatEntity);
        mListViewMsgItems.setAdapter(mChatMsgListAdapter);
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
     * 点击Back键
     */
    @Override
    public void onBackPressed() {
        mEnterRoomProsscessHelper.QuiteLive();

//        super.onBackPressed();
    }

    /**
     * 完成进出房间流程
     *
     * @param id_status
     * @param isSucc
     */
    @Override
    public void EnterRoomComplete(int id_status, boolean isSucc) {
        Toast.makeText(LivePlayActivity.this, "EnterRoomComplete " + id_status + " isSucc " + isSucc, Toast.LENGTH_SHORT).show();
        if (isSucc == true) {

            //IM初始化
            mLiveControlHelper.initTIMGroup(""+LiveRoomInfo.getRoomNum());

            if (id_status == Constants.HOST) {//主播方式加入房间成功
                //开启摄像头渲染画面
                Log.i(TAG, "createlive EnterRoomComplete isSucc" + isSucc);
                mEnterRoomProsscessHelper.initAvUILayer(avView);
            } else {//以成员方式加入房间成功

            }
        }
    }


    @Override
    public void QuiteRoomComplete(int id_status, boolean succ) {
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
            QavsdkControl.getInstance().setRemoteHasVideo(true, LiveRoomInfo.getHostID(), AVView.VIDEO_SRC_TYPE_CAMERA);
        }

    }

    @Override
    public void refreshText(String text, String name) {
        if (text != null) {
            refreshTextListView(name, text, Constants.TEXT_TYPE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                mEnterRoomProsscessHelper.QuiteLive();
                break;
            case R.id.message_input:
                inputMsgDialog();
                break;
        }
    }



    /**
     * 发消息弹出框
     */
    private void inputMsgDialog() {
        InputTextMsgDialog inputMsgDialog = new InputTextMsgDialog(this, R.style.inputdialog, mLiveControlHelper, this);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = inputMsgDialog.getWindow().getAttributes();

        lp.width = (int) (display.getWidth()); //设置宽度
        inputMsgDialog.getWindow().setAttributes(lp);
        inputMsgDialog.setCancelable(true);
        inputMsgDialog.show();
    }




    /**
     * 消息刷新显示
     *
     * @param name    发送者
     * @param context 内容
     * @param type    类型 （上线线消息和 聊天消息）
     */
    public void refreshTextListView(String name, String context, int type) {
        ChatEntity entity = new ChatEntity();
        entity.setSenderName(name);
        entity.setContext(context);
        entity.setType(type);
        //mArrayListChatEntity.add(entity);
        notifyRefreshListView(entity);
        //mChatMsgListAdapter.notifyDataSetChanged();

        mListViewMsgItems.setVisibility(View.VISIBLE);
        Log.d(TAG, "refreshTextListView height " + mListViewMsgItems.getHeight());

        if (mListViewMsgItems.getCount() > 1) {
            if (true)
                mListViewMsgItems.setSelection(0);
            else
                mListViewMsgItems.setSelection(mListViewMsgItems.getCount() - 1);
        }
    }


    /**
     * 通知刷新消息ListView
     */
    private void notifyRefreshListView(ChatEntity entity) {
        mBoolNeedRefresh = true;
        mTmpChatList.add(entity);
        if (mBoolRefreshLock) {
            return;
        } else {
            doRefreshListView();
        }
    }

    /**
     * 刷新ListView并重置状态
     */
    private void doRefreshListView() {
        if (mBoolNeedRefresh) {
            mBoolRefreshLock = true;
            mBoolNeedRefresh = false;
            mArrayListChatEntity.addAll(mTmpChatList);
            mTmpChatList.clear();
            mChatMsgListAdapter.notifyDataSetChanged();

            if (null != mTimerTask) {
                mTimerTask.cancel();
            }
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    Log.v(TAG, "doRefreshListView->task enter with need:" + mBoolNeedRefresh);
                    mHandler.sendEmptyMessage(REFRESH_LISTVIEW);
                }
            };
            //mTimer.cancel();
            mTimer.schedule(mTimerTask, MINFRESHINTERVAL);
        } else {
            mBoolRefreshLock = false;
        }
    }
}
