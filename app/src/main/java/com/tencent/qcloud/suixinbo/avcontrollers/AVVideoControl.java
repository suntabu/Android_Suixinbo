package com.tencent.qcloud.suixinbo.avcontrollers;

//test
/*
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
*/

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.tencent.av.sdk.AVError;
import com.tencent.av.sdk.AVVideoCtrl;
import com.tencent.av.sdk.AVVideoCtrl.CameraPreviewChangeCallback;
import com.tencent.av.sdk.AVVideoCtrl.EnableCameraCompleteCallback;
import com.tencent.av.sdk.AVVideoCtrl.EnableExternalCaptureCompleteCallback;
import com.tencent.av.sdk.AVVideoCtrl.LocalVideoPreProcessCallback;
import com.tencent.av.sdk.AVVideoCtrl.RemoteVideoPreviewCallback;
import com.tencent.av.sdk.AVVideoCtrl.SwitchCameraCompleteCallback;
import com.tencent.av.sdk.AVVideoCtrl.VideoFrame;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

public class AVVideoControl {
	private static final String TAG = "AVVideoControl";
	private Context mContext = null;
	private boolean mIsEnableCamera = false;
	private boolean mIsFrontCamera = true;
	private boolean mIsInOnOffCamera = false;
	private boolean mIsInSwitchCamera = false;
	private static final int CAMERA_NONE = -1;
	private static final int FRONT_CAMERA = 0;
	private static final int BACK_CAMERA = 1;
	private boolean mIsOnOffExternalCapture = false;
	private boolean mIsEnableExternalCapture = false;

	Vector<String> hasFileOpenIDList;

	private boolean isOpenBackCameraFirst = false;
	public void setIsOpenBackCameraFirst(boolean _isOpenBackCameraFirst) {
		isOpenBackCameraFirst = _isOpenBackCameraFirst;
	}

	private EnableCameraCompleteCallback mEnableCameraCompleteCallback = new EnableCameraCompleteCallback() {
		protected void onComplete(boolean enable, int result) {
			super.onComplete(enable, result);
			Log.d(TAG, "WL_DEBUG mEnableCameraCompleteCallback.onComplete enable = " + enable);
			Log.d(TAG, "WL_DEBUG mEnableCameraCompleteCallback.onComplete result = " + result);
			mIsInOnOffCamera = false;

			if (result == AVError.AV_OK) {
				mIsEnableCamera = enable;
			}
			Toast.makeText(mContext, "Open CameraOk !", Toast.LENGTH_SHORT).show();
			mContext.sendBroadcast(new Intent(AvConstants.ACTION_ENABLE_CAMERA_COMPLETE).putExtra(AvConstants.EXTRA_AV_ERROR_RESULT, result).putExtra(AvConstants.EXTRA_IS_ENABLE, enable));
			AVVideoCtrl avVideoCtrl = QavsdkControl.getInstance().getAVContext().getVideoCtrl();
			hasFileOpenIDList = new Vector<String>();
		
		}
	};
	
	private EnableExternalCaptureCompleteCallback mEnableExternalCaptureCompleteCallback = new EnableExternalCaptureCompleteCallback() {
			@Override
			protected void onComplete(boolean enable, int result) {
				super.onComplete(enable, result);
				Log.d(TAG, "WL_DEBUG mEnableExternalCaptureCompleteCallback.onComplete enable = " + enable);
				Log.d(TAG, "WL_DEBUG mEnableExternalCaptureCompleteCallback.onComplete result = " + result);
				mIsOnOffExternalCapture = false;
				
				if (result == AVError.AV_OK) {
					mIsEnableExternalCapture = enable;
				}
				
				mContext.sendBroadcast(new Intent(AvConstants.ACTION_ENABLE_EXTERNAL_CAPTURE_COMPLETE).putExtra(AvConstants.EXTRA_AV_ERROR_RESULT, result).putExtra(AvConstants.EXTRA_IS_ENABLE, enable));
				
			}
	};

	private SwitchCameraCompleteCallback mSwitchCameraCompleteCallback = new SwitchCameraCompleteCallback() {
		protected void onComplete(int cameraId, int result) {
			super.onComplete(cameraId, result);
			Log.d(TAG, "WL_DEBUG mSwitchCameraCompleteCallback.onComplete cameraId = " + cameraId);
			Log.d(TAG, "WL_DEBUG mSwitchCameraCompleteCallback.onComplete result = " + result);
			mIsInSwitchCamera = false;
			boolean isFront = cameraId == FRONT_CAMERA;

			if (result == AVError.AV_OK) {
				mIsFrontCamera = isFront;
			}

			mContext.sendBroadcast(new Intent(AvConstants.ACTION_SWITCH_CAMERA_COMPLETE).putExtra(AvConstants.EXTRA_AV_ERROR_RESULT, result).putExtra(AvConstants.EXTRA_IS_FRONT, isFront));
		}
	};
	
	
	
	private CameraPreviewChangeCallback mCameraPreviewChangeCallback = new CameraPreviewChangeCallback() {
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

	public AVVideoControl(Context context) {
		mContext = context;
	}

	int enableCamera(boolean isEnable) {
		int result = AVError.AV_OK;

		if (mIsEnableCamera != isEnable) {
			AVVideoCtrl avVideoCtrl = QavsdkControl.getInstance().getAVContext().getVideoCtrl();
			mIsInOnOffCamera = true;
			
			if (!isOpenBackCameraFirst) {
				mIsFrontCamera = true;
				result = avVideoCtrl.enableCamera(FRONT_CAMERA, isEnable, mEnableCameraCompleteCallback);
				Log.i(TAG, "enableCamera result "+result);
			} else {
				mIsFrontCamera = false;
				result = avVideoCtrl.enableCamera(BACK_CAMERA, isEnable, mEnableCameraCompleteCallback);
			}
		}
		Log.d(TAG, "WL_DEBUG enableCamera isEnable = " + isEnable);
		Log.d(TAG, "WL_DEBUG enableCamera result = " + result);
		return result;
	}



	int enableExternalCapture(boolean isEnable) {
		int result = AVError.AV_OK;
		
		if (mIsEnableExternalCapture != isEnable) {
			AVVideoCtrl avVideoCtrl = QavsdkControl.getInstance().getAVContext().getVideoCtrl();
			mIsOnOffExternalCapture = true;
			result = avVideoCtrl.enableExternalCapture(isEnable, mEnableExternalCaptureCompleteCallback);
		}
		
		Log.d(TAG, "WL_DEBUG enableExternalCapture isEnable = " + isEnable);
		Log.d(TAG, "WL_DEBUG enableExternalCapture result = " + result);
		return result;
	}

	int switchCamera(boolean isFront) {
		int result = AVError.AV_OK;

		if (mIsFrontCamera != isFront) {
			AVVideoCtrl avVideoCtrl = QavsdkControl.getInstance().getAVContext().getVideoCtrl();
			mIsInSwitchCamera = true;
			result = avVideoCtrl.switchCamera(isFront ? FRONT_CAMERA : BACK_CAMERA, mSwitchCameraCompleteCallback);
		}
		Log.d(TAG, "WL_DEBUG switchCamera isFront = " + isFront);
		Log.d(TAG, "WL_DEBUG switchCamera result = " + result);
		return result;
	}
	
	void setRotation(int rotation) {
		AVVideoCtrl avVideoCtrl = QavsdkControl.getInstance().getAVContext().getVideoCtrl();
		avVideoCtrl.setRotation(rotation);
		Log.e(TAG, "WL_DEBUG setRotation rotation = " + rotation);

	}
	String getQualityTips() {
		AVVideoCtrl avVideoCtrl = QavsdkControl.getInstance().getAVContext().getVideoCtrl();
		return avVideoCtrl.getQualityTips();
	}	

	int toggleEnableCamera() {
		return enableCamera(!mIsEnableCamera);
	}
	
	RemoteVideoPreviewCallback remoteVideoPreviewCallback = new RemoteVideoPreviewCallback() {
		public void onFrameReceive(VideoFrame videoFrame) {
		
//			Log.d(TAG, "real RemoteVideoPreviewCallback.onFrameReceive");
//			Log.d(TAG, "len: " + videoFrame.dataLen);
//			Log.d(TAG, "identifier: " + videoFrame.identifier);
//			Log.d(TAG, "videoFormat: " + videoFrame.videoFormat);
//			Log.d(TAG, "width: " + videoFrame.width);
//			Log.d(TAG, "height: " + videoFrame.height);
			
			String printTxtPath =   AvConstants.outputYuvFilePath + "/" + videoFrame.height + "_" + videoFrame.width + "_" + videoFrame.identifier +".yuv" ;
			Log.d("test", "printTxtPath: " + printTxtPath);
			byte[] b = videoFrame.data;
			DataOutputStream d;
			
			try {
				if (!hasFileOpenIDList.contains(videoFrame.identifier))
				{
					d = new DataOutputStream(new FileOutputStream(
							printTxtPath, false));
					hasFileOpenIDList.add(videoFrame.identifier);
				} else {
					d = new DataOutputStream(new FileOutputStream(
						printTxtPath, true));
				}
				d.write(b);
				d.flush();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    	

		};
	};
	
	LocalVideoPreProcessCallback localVideoPreProcessCallback = new LocalVideoPreProcessCallback() {
		public void onFrameReceive(VideoFrame videoFrame) {
		
//			Log.d(TAG, "len: " + videoFrame.dataLen);
//			Log.d(TAG, "identifier: " + videoFrame.identifier);
//			Log.d(TAG, "videoFormat: " + videoFrame.videoFormat);
//			Log.d(TAG, "width: " + videoFrame.width);
//			Log.d(TAG, "height: " + videoFrame.height);
			
			//简单的摄像头前处理函数
			String printTxtPath =   AvConstants.outputYuvFilePath + "/" + videoFrame.height + "_" + videoFrame.width + "_" + videoFrame.identifier +".yuv" ;
			Log.d("test", "printTxtPath: " + printTxtPath);
			byte[] b = videoFrame.data;
			
			if (b != null && b.length > 50000) {
				for (int i=0; i<50000; i++)
					b[i] = 0;
			}
	    
		};
	};
	
	public boolean StartRecordingVideo() {
		AVVideoCtrl avVideoCtrl = QavsdkControl.getInstance().getAVContext().getVideoCtrl();
		hasFileOpenIDList = new Vector<String>();
		
		return avVideoCtrl.setRemoteVideoPreviewCallback(remoteVideoPreviewCallback);		
	}
	
	public boolean enableLocalPreProcess(boolean enable) {
		AVVideoCtrl avVideoCtrl = QavsdkControl.getInstance().getAVContext().getVideoCtrl();
		if (enable) {
			return avVideoCtrl.setLocalVideoPreProcessCallback(localVideoPreProcessCallback);	
		} else {
			return avVideoCtrl.setLocalVideoPreProcessCallback(null);	
		}
	}

	int toggleSwitchCamera() {
		return switchCamera(!mIsFrontCamera);
	}

	boolean getIsInOnOffCamera() {
		return mIsInOnOffCamera;
	}

	boolean getIsInSwitchCamera() {
		return mIsInSwitchCamera;
	}
	
	public void setIsInSwitchCamera(boolean isInSwitchCamera) {
		this.mIsInSwitchCamera = isInSwitchCamera;
	}

	boolean getIsEnableCamera() {
		return mIsEnableCamera;
	}
	
	boolean getIsInOnOffExternalCapture() {
		return mIsOnOffExternalCapture;
	}
	
	boolean getIsEnableExternalCapture() {
		return mIsEnableExternalCapture;
	}
	
	public void setIsInOnOffCamera(boolean isInOnOffCamera) {
		this.mIsInOnOffCamera = isInOnOffCamera;
	}
	
	public void setIsOnOffExternalCapture(boolean isOnOffExternalCapture) {
		this.mIsOnOffExternalCapture = isOnOffExternalCapture;
	}

	boolean getIsFrontCamera() {
		return mIsFrontCamera;
	}

	void initAVVideoSettings() {
		mIsEnableCamera = false;
		mIsFrontCamera = true;
		mIsInOnOffCamera = false;
		mIsInSwitchCamera = false;
		mIsEnableExternalCapture = false;
		mIsOnOffExternalCapture = false;
	}
}