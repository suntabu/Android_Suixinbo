package com.tencent.qcloud.suixinbo.presenters;


import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMGroupSystemElem;
import com.tencent.TIMGroupSystemElemType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMTextElem;
import com.tencent.TIMValueCallBack;
import com.tencent.av.sdk.AVAudioCtrl;
import com.tencent.av.sdk.AVEndpoint;
import com.tencent.av.sdk.AVError;
import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.av.sdk.AVVideoCtrl;
import com.tencent.av.sdk.AVView;
import com.tencent.qcloud.suixinbo.avcontrollers.QavsdkControl;
import com.tencent.qcloud.suixinbo.model.MemberInfo;
import com.tencent.qcloud.suixinbo.model.MyCurrentLiveInfo;
import com.tencent.qcloud.suixinbo.model.MySelfInfo;
import com.tencent.qcloud.suixinbo.presenters.viewinface.LiveView;
import com.tencent.qcloud.suixinbo.presenters.viewinface.MembersDialogView;
import com.tencent.qcloud.suixinbo.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 直播的控制类
 */
public class LiveHelper extends Presenter {
    private LiveView mLiveView;
    private MembersDialogView mMembersDialogView;
    public Context mContext;
    private static final String TAG = LiveHelper.class.getSimpleName();
    private static final int CAMERA_NONE = -1;
    private static final int FRONT_CAMERA = 0;
    private static final int BACK_CAMERA = 1;
    private static final int MAX_REQUEST_VIEW_COUNT = 3;//当前最大支持请求画面个数
    private static final boolean LOCAL = true;
    private static final boolean REMOTE = false;
    private TIMConversation mConversation;
    private TIMConversation mC2CConversation;
    private boolean isMicOpen = true;
    private static final String UNREAD = "0";
    private ArrayList<String> mCurrentVideoMembers;
    private ArrayList<MemberInfo> mDialogMembers = new ArrayList<MemberInfo>();
    private int requestCount = 1;
    private AVView mRequestViewList[] = new AVView[MAX_REQUEST_VIEW_COUNT];
    private String mRequestIdentifierList[] = new String[MAX_REQUEST_VIEW_COUNT];


    public LiveHelper(Context context, LiveView liveview) {
        mContext = context;
        mLiveView = liveview;
    }

    public LiveHelper(Context context) {
        mContext = context;
    }

    public LiveHelper(Context context, MembersDialogView dialogView) {
        mContext = context;
        mMembersDialogView = dialogView;
    }


    private AVVideoCtrl.CameraPreviewChangeCallback mCameraPreviewChangeCallback = new AVVideoCtrl.CameraPreviewChangeCallback() {
        @Override
        public void onCameraPreviewChangeCallback(int cameraId) {
            Log.d(TAG, "WL_DEBUG mCameraPreviewChangeCallback.onCameraPreviewChangeCallback cameraId = " + cameraId);

            QavsdkControl.getInstance().setMirror(FRONT_CAMERA == cameraId);
        }
    };

    public void setCameraPreviewChangeCallback() {
        AVVideoCtrl avVideoCtrl = QavsdkControl.getInstance().getAVContext().getVideoCtrl();
        avVideoCtrl.setCameraPreviewChangeCallback(mCameraPreviewChangeCallback);
    }

    /**
     * 开启摄像头和MIC
     */
    public void OpenCameraAndMic() {
        enableCamera(FRONT_CAMERA, true);
        AVAudioCtrl avAudioCtrl = QavsdkControl.getInstance().getAVContext().getAudioCtrl();//开启Mic
        avAudioCtrl.enableMic(true);
        isMicOpen = true;

    }


    /**
     * 开启摄像头
     *
     * @param camera
     * @param isEnable
     */
    private void enableCamera(final int camera, boolean isEnable) {
        Log.i(TAG, "createlive enableCamera camera " + camera + "  isEnable " + isEnable);
        AVVideoCtrl avVideoCtrl = QavsdkControl.getInstance().getAVContext().getVideoCtrl();
        //打开摄像头
        int ret = avVideoCtrl.enableCamera(camera, isEnable, new AVVideoCtrl.EnableCameraCompleteCallback() {
            protected void onComplete(boolean enable, int result) {//开启摄像头回调
                super.onComplete(enable, result);
                Log.i(TAG, "createlive enableCamera result " + result);
                if (result == AVError.AV_OK) {//开启成功
//                    mIsEnableCamera = enable;
                    if (camera == FRONT_CAMERA) {
                        mIsFrontCamera = true;
                    } else {
                        mIsFrontCamera = false;
                    }

                    //如果是主播直接本地渲染
                    if (MySelfInfo.getInstance().getIdStatus() == Constants.HOST)
                        mLiveView.showVideoView(LOCAL, MyCurrentLiveInfo.getHostID());

                }
            }
        });

        Log.i(TAG, "enableCamera " + ret);

    }


    /**
     * AVSDK 请求主播数据
     *
     * @param identifier 主播ID
     */
    public void RequestView(String identifier, int viewindex, int requestCount, int type, int size) {
        Log.i(TAG, "RequestView " + identifier);

        AVEndpoint endpoint = ((AVRoomMulti) QavsdkControl.getInstance().getAVContext().getRoom()).getEndpointById(identifier);
        Log.d(TAG, "RequestView hostIdentifier " + identifier + " endpoint " + endpoint);
        if (endpoint != null) {

//
//            AVView view = new AVView();
//            view.videoSrcType = AVView.VIDEO_SRC_TYPE_CAMERA;
//            view.viewSizeType = AVView.VIEW_SIZE_TYPE_BIG;

            AVView view = new AVView();
            view.videoSrcType = type;
            view.viewSizeType = size;

            //界面数
            mRequestViewList[viewindex] = view;
            mRequestIdentifierList[viewindex] = identifier;
            int ret = AVEndpoint.requestViewList(mRequestIdentifierList, mRequestViewList, requestCount, mRequestViewListCompleteCallback);



            mLiveView.showVideoView(REMOTE, identifier);

        } else {
            Toast.makeText(mContext, "request remoteView empty !!!!! endpoint = null", Toast.LENGTH_SHORT).show();
        }
    }



    private AVEndpoint.RequestViewListCompleteCallback mRequestViewListCompleteCallback = new AVEndpoint.RequestViewListCompleteCallback() {
        protected void OnComplete(String identifierList[], AVView viewList[], int count, int result) {
            // TODO
            Log.d(TAG, "RequestViewListCompleteCallback.OnComplete");
        }
    };

    public void sendText(TIMMessage Nmsg) {
        if (mConversation != null)
            mConversation.sendMessage(Nmsg, new TIMValueCallBack<TIMMessage>() {
                @Override
                public void onError(int i, String s) {
                    if (i == 85) { //消息体太长
                        Toast.makeText(mContext, "Text too long ", Toast.LENGTH_SHORT).show();
                    } else if (i == 6011) {//群主不存在
                        Toast.makeText(mContext, "Host don't exit ", Toast.LENGTH_SHORT).show();
                    }
                    Log.e(TAG, "send message failed. code: " + i + " errmsg: " + s);
                }

                @Override
                public void onSuccess(TIMMessage timMessage) {
                    //发送成回显示消息内容
                    for (int j = 0; j < timMessage.getElementCount(); j++) {
                        TIMElem elem = (TIMElem) timMessage.getElement(0);
                        String sendId = timMessage.getSender();
                        handleTextMessage(elem, sendId);
                    }
                    Log.i(TAG, "Send text Msg ok");

                }
            });
    }

    /**
     * 初始化聊天室  设置监听器
     */
    public void initTIMGroup(String chatRoomId) {
        Log.v(TAG, "initTIMGroup->current room id: " + chatRoomId);
        mConversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, chatRoomId);
        TIMManager.getInstance().addMessageListener(msgListener);
        mC2CConversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, chatRoomId);
    }


    /**
     * 群消息回调
     */
    private TIMMessageListener msgListener = new TIMMessageListener() {
        @Override
        public boolean onNewMessages(List<TIMMessage> list) {
            //Log.d(TAG, "onNewMessages readMessage " + list.size());
            //解析TIM推送消息
            parseIMMessage(list);
            return false;
        }
    };

    /**
     * 解析消息回调
     *
     * @param list 消息列表
     */
    private void parseIMMessage(List<TIMMessage> list) {
        List<TIMMessage> tlist = list;


        if (tlist.size() > 0) {
            if (mConversation != null)
                mConversation.setReadMessage(tlist.get(0));
            Log.d(TAG, "parseIMMessage readMessage " + tlist.get(0).timestamp());
        }
//        if (!bNeverLoadMore && (tlist.size() < mLoadMsgNum))
//            bMore = false;

        for (int i = tlist.size() - 1; i >= 0; i--) {
            TIMMessage currMsg = tlist.get(i);
//


            for (int j = 0; j < currMsg.getElementCount(); j++) {
                if (currMsg.getElement(j) == null)
                    continue;
                TIMElem elem = currMsg.getElement(j);
                TIMElemType type = elem.getType();
                String sendId = currMsg.getSender();
                //系统消息
                if (type == TIMElemType.GroupSystem) {
                    if (TIMGroupSystemElemType.TIM_GROUP_SYSTEM_DELETE_GROUP_TYPE == ((TIMGroupSystemElem) elem).getSubtype()) {
//                        mUIhandler.sendEmptyMessage(IM_HOST_LEAVE);
//                        LEVAE_MODE = true;
                    }

                }
                //定制消息
                if (type == TIMElemType.Custom) {
                    handleCustomMsg(elem, sendId);
                    continue;
                }

                //其他群消息过滤
                if (!MyCurrentLiveInfo.getChatRoomId().equals(currMsg.getConversation().getPeer())) {
                    continue;
                }

                //最后处理文本消息
                if (type == TIMElemType.Text) {
                    String sendid = currMsg.getSender();
                    handleTextMessage(elem, sendid);
                }


            }
        }

    }

    /**
     * 处理文本消息解析
     *
     * @param elem
     * @param sendId
     */
    private void handleTextMessage(TIMElem elem, String sendId) {
        TIMTextElem textElem = (TIMTextElem) elem;
//        Toast.makeText(mContext, "" + textElem.getText(), Toast.LENGTH_SHORT).show();

        mLiveView.refreshText(textElem.getText(), sendId);
//        sendToUIThread(REFRESH_TEXT, textElem.getText(), sendId);
    }


    /**
     * 处理定制消息 赞 关注 取消关注
     *
     * @param elem
     */
    private void handleCustomMsg(TIMElem elem, String sendId) {
        try {
            String customText = new String(((TIMCustomElem) elem).getData(), "UTF-8");
            Log.i(TAG, "cumstom msg  " + customText);

            JSONTokener jsonParser = new JSONTokener(customText);
            // 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
            // 如果此时的读取位置在"name" : 了，那么nextValue就是"yuanzhifei89"（String）
            JSONObject json = (JSONObject) jsonParser.nextValue();
            int action = json.getInt("userAction");
            switch (action) {
                case Constants.AVIMCMD_MUlTI_HOST_INVITE:
                    mLiveView.showInviteDialog();
                    break;
                case Constants.AVIMCMD_MUlTI_JOIN:
                    Toast.makeText(mContext, sendId + " accepted !", Toast.LENGTH_SHORT).show();
//                    requestMu(sendId);
                    break;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException ex) {
            // 异常处理代码
        }
    }


    public boolean isFrontCamera() {
        return mIsFrontCamera;
    }

    private boolean mIsFrontCamera = true;

    /**
     * 转换前后摄像头
     *
     * @return
     */
    public int switchCamera() {
        AVVideoCtrl avVideoCtrl = QavsdkControl.getInstance().getAVContext().getVideoCtrl();
        int result = avVideoCtrl.switchCamera(mIsFrontCamera ? BACK_CAMERA : FRONT_CAMERA, mSwitchCameraCompleteCallback);
        return result;
    }


    /**
     * 装换摄像头回调
     */
    private AVVideoCtrl.SwitchCameraCompleteCallback mSwitchCameraCompleteCallback = new AVVideoCtrl.SwitchCameraCompleteCallback() {
        protected void onComplete(int cameraId, int result) {
            super.onComplete(cameraId, result);

            if (result == AVError.AV_OK) {
                mIsFrontCamera = !mIsFrontCamera;
            }

        }
    };

    public boolean isMicOpen() {
        return isMicOpen;
    }

    /**
     * 开启Mic
     */
    public void openMic() {
        AVAudioCtrl avAudioCtrl = QavsdkControl.getInstance().getAVContext().getAudioCtrl();//开启Mic
        avAudioCtrl.enableMic(true);
        isMicOpen = true;
    }

    /**
     * 关闭Mic
     */
    public void muteMic() {
        AVAudioCtrl avAudioCtrl = QavsdkControl.getInstance().getAVContext().getAudioCtrl();//关闭Mic
        avAudioCtrl.enableMic(false);
        isMicOpen = false;
    }


    /**
     * 开关闪光灯
     */
    private boolean flashLgihtStatus = false;

    public void toggleFlashLight() {
        AVVideoCtrl videoCtrl = QavsdkControl.getInstance().getAVContext().getVideoCtrl();
        if (null == videoCtrl) {
            return;
        }

        final Object cam = videoCtrl.getCamera();
        if ((cam == null) || (!(cam instanceof Camera))) {
            return;
        }
        final Camera.Parameters camParam = ((Camera) cam).getParameters();
        if (null == camParam) {
            return;
        }

        Object camHandler = videoCtrl.getCameraHandler();
        if ((camHandler == null) || (!(camHandler instanceof Handler))) {
            return;
        }

        //对摄像头的操作放在摄像头线程
        if (flashLgihtStatus == false) {
            ((Handler) camHandler).post(new Runnable() {
                public void run() {
                    try {
                        camParam.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        ((Camera) cam).setParameters(camParam);
                        flashLgihtStatus = true;
                    } catch (RuntimeException e) {
                        Log.d("setParameters", "RuntimeException");
                    }
                }
            });
        } else {
            ((Handler) camHandler).post(new Runnable() {
                public void run() {
                    try {
                        camParam.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        ((Camera) cam).setParameters(camParam);
                        flashLgihtStatus = false;
                    } catch (RuntimeException e) {
                        Log.d("setParameters", "RuntimeException");
                    }

                }
            });
        }
    }

    /**
     * 拉取成员列表 成功返回ID列表
     */
    public void getMemberList() {
        TIMGroupManager.getInstance().getGroupMembers("" + MySelfInfo.getInstance().getMyRoomNum(), new TIMValueCallBack<List<TIMGroupMemberInfo>>() {
            @Override
            public void onError(int i, String s) {
                Log.i(TAG, "get MemberList ");
            }

            @Override
            public void onSuccess(List<TIMGroupMemberInfo> timGroupMemberInfos) {
                Log.i(TAG, "get MemberList ");
                getMemberListInfo(timGroupMemberInfos);

            }
        });
    }


    /**
     * 拉取成员列表信息
     *
     * @param timGroupMemberInfos
     */
    private void getMemberListInfo(List<TIMGroupMemberInfo> timGroupMemberInfos) {
        mDialogMembers.clear();
        for (TIMGroupMemberInfo item : timGroupMemberInfos) {
            if (item.getUser().equals(MySelfInfo.getInstance().getId())) {
                continue;
            }
            MemberInfo member = new MemberInfo();
            member.setUserId(item.getUser());

            mDialogMembers.add(member);
            //判断是否在视频中
//            for (String id : videoMembers) {
//                if (id.equals(item.getUser())) ;
//                member.setIsOnVideoChat(true);
//            }
        }

        mMembersDialogView.showMembersList(mDialogMembers);

    }


    public void sendC2CMessage(int cmd, String id) {
        JSONObject inviteCmd = new JSONObject();
        try {
            inviteCmd.put("userAction", cmd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String cmds = inviteCmd.toString();
        Log.i(TAG, "send cmd : " + cmd);
        TIMMessage msg = new TIMMessage();
        TIMCustomElem elem = new TIMCustomElem();
        elem.setData(cmds.getBytes());
        msg.addElement(elem);
        mC2CConversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, id);
        mC2CConversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int i, String s) {
                Log.e(TAG, "enter error" + i + ": " + s);
            }

            @Override
            public void onSuccess(TIMMessage timMessage) {
                Log.i(TAG, "send praise succ !");
            }
        });
    }


}
