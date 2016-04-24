package com.tencent.qcloud.suixinbo.presenters;


import android.content.Context;
import android.util.Log;

import com.tencent.av.sdk.AVAudioCtrl;
import com.tencent.av.sdk.AVError;
import com.tencent.av.sdk.AVVideoCtrl;
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
        Log.i(TAG, "createlive enableCamera camera " + camera+"  isEnable "+isEnable);
        AVVideoCtrl avVideoCtrl = QavsdkControl.getInstance().getAVContext().getVideoCtrl();
        //打开摄像头
        int ret = avVideoCtrl.enableCamera(camera,isEnable, new AVVideoCtrl.EnableCameraCompleteCallback() {
            protected void onComplete(boolean enable, int result) {//开启摄像头回调
                super.onComplete(enable, result);
                Log.i(TAG, "createlive enableCamera result " + result);
                if (result == AVError.AV_OK) {//开启成功
//                    mIsEnableCamera = enable;
                    Log.i(TAG, "createlive enableCamera result " + result);
                    mLiveView.showVideoView();

                }
            }
        });

        Log.i(TAG, "enableCamera "+ret);

    }

}
