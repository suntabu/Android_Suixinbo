package com.tencent.qcloud.suixinbo.presenters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tencent.TIMCallBack;
import com.tencent.TIMConversationType;
import com.tencent.TIMGroupManager;
import com.tencent.TIMManager;
import com.tencent.TIMValueCallBack;
import com.tencent.av.sdk.AVContext;
import com.tencent.av.sdk.AVRoom;
import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.qcloud.suixinbo.avcontrollers.AvConstants;
import com.tencent.qcloud.suixinbo.avcontrollers.QavsdkControl;
import com.tencent.qcloud.suixinbo.model.UserInfo;
import com.tencent.qcloud.suixinbo.presenters.viewinface.EnterQuiteRoomView;
import com.tencent.qcloud.suixinbo.utils.Constants;

import java.util.ArrayList;


/**
 * 进出房间Presenter
 */
public class EnterRoomAndQuiteRoomPresenter extends Presenter {
    private EnterQuiteRoomView mStepInOutView;
    private Context mContext;
    private static final String TAG = EnterRoomAndQuiteRoomPresenter.class.getSimpleName();
    private static boolean isInChatRoom = false;


    public EnterRoomAndQuiteRoomPresenter(Context context, EnterQuiteRoomView view) {
        mContext = context;
        mStepInOutView = view;
    }



    /**
     * 进入一个直播房间流程
     *
     * @param isHost true代表是直播
     */
    public void startEnterRoomByHost(int isHost) {
        if (isHost == Constants.HOST) {
            createLive();
        } else {
            joinLive();
        }

    }


    /**
     * 房间回调
     */
    private AVRoomMulti.Delegate mRoomDelegate = new AVRoomMulti.Delegate() {
        // 创建房间成功回调
        public void onEnterRoomComplete(int result) {
            Log.i(TAG, "createlive createAVRoom callback " + result);
            if (result == 0) {
                //只有进入房间后才能初始化AvView
                mStepInOutView.EnterRoomCB(UserInfo.getInstance().getIdStatus(), true);
            } else {
                mStepInOutView.EnterRoomCB(UserInfo.getInstance().getIdStatus(), false);
            }

        }

        // 离开房间成功回调
        public void onExitRoomComplete(int result) {
            Log.d(TAG, "WL_DEBUG mRoomDelegate.onExitRoomComplete result = " + result);
            mStepInOutView.QuiteRoomCB(UserInfo.getInstance().getIdStatus(), true);
        }

        //房间成员变化回调
        public void onEndpointsUpdateInfo(int eventid, String[] updateList) {
            Log.d(TAG, "WL_DEBUG onEndpointsUpdateInfo. eventid = " + eventid);
        }

        public void OnPrivilegeDiffNotify(int privilege) {
            Log.d(TAG, "OnPrivilegeDiffNotify. privilege = " + privilege);
        }
    };


    /**
     * 1_1 创建一个直播
     */
    private void createLive() {
        createIMChatRoom();

    }

    /**
     * 1_2创建一个IM聊天室
     */
    private void createIMChatRoom() {
        final ArrayList<String> list = new ArrayList<String>();
        final String roomName = "this is a  test";
        Log.i(TAG, "createlive createIMChatRoom " + UserInfo.getInstance().getMyRoomNum());
        TIMGroupManager.getInstance().createGroup("ChatRoom", list, roomName, "" + UserInfo.getInstance().getMyRoomNum(), new TIMValueCallBack<String>() {
            @Override
            public void onError(int i, String s) {
                Log.i(TAG, "onError " + i + "   " + s);
                //已在房间中,重复进入房间
                if (i == 10025) {
                    isInChatRoom = true;
                    createAVRoom(UserInfo.getInstance().getMyRoomNum());
                }
                // 创建IM房间失败，提示失败原因，并关闭等待对话框
                Toast.makeText(mContext, " chatroom  error " + s + "i " + i, Toast.LENGTH_SHORT).show();
//                QuiteRoomCB();
            }

            @Override
            public void onSuccess(String s) {
                isInChatRoom = true;
                //创建AV房间
                createAVRoom(UserInfo.getInstance().getMyRoomNum());

            }
        });

    }


    /**
     * 1_3创建一个AV房间
     */
    private void createAVRoom(int roomNum) {
        Log.i(TAG, "createlive createAVRoom " + roomNum);
        AVContext avContext = QavsdkControl.getInstance().getAVContext();
        byte[] authBuffer = null;//权限位加密串；TODO：请业务侧填上自己的加密串
        AVRoom.EnterRoomParam enterRoomParam = new AVRoomMulti.EnterRoomParam(roomNum, AvConstants.auth_bits, authBuffer, "", AvConstants.AUDIO_VOICE_CHAT_MODE, true);
        // create room
        avContext.enterRoom(AVRoom.AV_ROOM_MULTI, mRoomDelegate, enterRoomParam);

    }

    /**
     * 初始化Usr
     *
     */
    public void initAvUILayer(View avView) {
        //初始化AVSurfaceView
        if (QavsdkControl.getInstance().getAVContext() != null) {
            QavsdkControl.getInstance().initAvUILayer(mContext.getApplicationContext(), avView);
        }

    }

    public void OpenCameraAndMic(){

    }


    /**
     * 1_5上传直播封面
     */
    public void notifyServerCreateRoom() {
    }


    /**
     * 2_1加入一个房间
     */
    private void joinLive() {
        joinIMChatRoom();
    }

    /**
     * 2_2加入一个聊天室
     */
    private void joinIMChatRoom() {
        joinAVRoom();
    }

    /**
     * 2_2加入一个AV房间
     */
    private void joinAVRoom() {

    }


    /**
     * 退出房间
     */
    public void stepOutRoom() {
        //退出IM房间
        quiteIMChatRoom();
        //退出AV房间
        quiteAVRoom();
    }

    /**
     * 退出一个AV房间
     */
    private void quiteAVRoom() {
        Log.d(TAG, "WL_DEBUG exitRoom");
        AVContext avContext = QavsdkControl.getInstance().getAVContext();
        int result = avContext.exitRoom();
    }

    /**
     * 退出IM房间
     */
    private void quiteIMChatRoom() {
        if ((isInChatRoom == true)) {
            //主播解散群
            if (UserInfo.getInstance().getIdStatus() == Constants.HOST) {
                TIMGroupManager.getInstance().deleteGroup("" + UserInfo.getInstance().getMyRoomNum(), new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                    }

                    @Override
                    public void onSuccess() {
                        isInChatRoom = false;
                    }
                });
                TIMManager.getInstance().deleteConversation(TIMConversationType.Group, "" + UserInfo.getInstance().getMyRoomNum());
            } else {
                //成员退出群
                TIMGroupManager.getInstance().quitGroup("" + UserInfo.getInstance().getMyRoomNum(), new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {

                    }

                    @Override
                    public void onSuccess() {
                        isInChatRoom = false;
                    }
                });
            }

            //
        }
    }

    /**
     * 通知房间
     */
    private void notifyServerQuit() {

    }

}
