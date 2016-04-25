package com.tencent.qcloud.suixinbo.presenters;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.tencent.av.sdk.AVAudioCtrl;
import com.tencent.av.sdk.AVEndpoint;
import com.tencent.av.sdk.AVError;
import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.av.sdk.AVVideoCtrl;
import com.tencent.av.sdk.AVView;
import com.tencent.qcloud.suixinbo.avcontrollers.QavsdkControl;
import com.tencent.qcloud.suixinbo.presenters.viewinface.LiveView;

/**
 * 直播的控制类
 */
public class LiveControlPresenter extends Presenter  {
    public LiveView mLiveView;
    public Context mContext;
    private static final String TAG = LiveControlPresenter.class.getSimpleName();
    private static final int CAMERA_NONE = -1;
    private static final int FRONT_CAMERA = 0;
    private static final int BACK_CAMERA = 1;
    private static final int MAX_REQUEST_VIEW_COUNT = 3;//当前最大支持请求画面个数
    private static final boolean HOST = true;
    private static final boolean MEMBER =false;

    public LiveControlPresenter(Context context, LiveView liveview) {
        mContext = context;
        mLiveView =  liveview;
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
    public void OpenCameraAndMic(){
        enableCamera(FRONT_CAMERA, true);
        AVAudioCtrl avAudioCtrl =  QavsdkControl.getInstance().getAVContext().getAudioCtrl();//开启Mic
        avAudioCtrl.enableMic(true);

    }


    /**
     * 开启摄像头
     * @param camera
     * @param isEnable
     */
    private void enableCamera(int camera, boolean isEnable) {
        Log.i(TAG, "createlive enableCamera camera " + camera + "  isEnable " + isEnable);
        AVVideoCtrl avVideoCtrl = QavsdkControl.getInstance().getAVContext().getVideoCtrl();
        //打开摄像头
        int ret = avVideoCtrl.enableCamera(camera, isEnable, new AVVideoCtrl.EnableCameraCompleteCallback() {
            protected void onComplete(boolean enable, int result) {//开启摄像头回调
                super.onComplete(enable, result);
                Log.i(TAG, "createlive enableCamera result " + result);
                if (result == AVError.AV_OK) {//开启成功
//                    mIsEnableCamera = enable;
                    Log.i(TAG, "createlive enableCamera result " + result);
                    mLiveView.showVideoView(HOST);

                }
            }
        });

        Log.i(TAG, "enableCamera " + ret);

    }


    /**
     * AVSDK server请求主播数据
     *
     * @param identifier
     */
    public void requestView(String identifier) {
        Log.i(TAG, "requestView "+identifier);
        AVView mRequestViewList[] = new AVView[MAX_REQUEST_VIEW_COUNT];
        String mRequestIdentifierList[] = new String[MAX_REQUEST_VIEW_COUNT];
        AVEndpoint endpoint = ((AVRoomMulti) QavsdkControl.getInstance().getAVContext().getRoom()).getEndpointById(identifier);
        Log.d(TAG, "requestView hostIdentifier " + identifier + " endpoint " + endpoint);
        if (endpoint != null) {


            AVView view = new AVView();
            view.videoSrcType = AVView.VIDEO_SRC_TYPE_CAMERA;
            view.viewSizeType = AVView.VIEW_SIZE_TYPE_BIG;

            //界面数
            mRequestViewList[0] = view;
            mRequestIdentifierList[0] = identifier;
            mRequestViewList[0].viewSizeType = AVView.VIEW_SIZE_TYPE_BIG;
            AVEndpoint.requestViewList(mRequestIdentifierList, mRequestViewList, 1,mRequestViewListCompleteCallback);
            mLiveView.showVideoView(MEMBER);

        } else {
            Toast.makeText(mContext, "request remoteView empty !!!!! endpoint = null", Toast.LENGTH_SHORT).show();
        }
    }

    private AVEndpoint.RequestViewListCompleteCallback mRequestViewListCompleteCallback = new AVEndpoint.RequestViewListCompleteCallback() {
        protected void OnComplete(String identifierList[], int count, int result) {
            // TODO
            Log.d(TAG, "RequestViewListCompleteCallback.OnComplete");
        }
    };



}
