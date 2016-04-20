package com.tencent.qcloud.suixinbo.avcontrollers;

import android.content.Context;
import android.content.Intent;

import com.tencent.av.sdk.AVAudioCtrl;
import com.tencent.av.sdk.AVAudioCtrl.Delegate;

/**
 * AVSDK 音频控制器类
 */
public class AVAudioControl {
	private Context mContext = null;

	private Delegate mDelegate = new Delegate() {
		@Override
		protected void onOutputModeChange(int outputMode) {
            super.onOutputModeChange(outputMode);
            mContext.sendBroadcast(new Intent(AvConstants.ACTION_OUTPUT_MODE_CHANGE));
        }
	};
	
	AVAudioControl(Context context) {
		mContext = context;
	}
	
	void initAVAudioSettings() {
		QavsdkControl.getInstance().getAVContext().getAudioCtrl().setDelegate(mDelegate);
	}
	
	boolean getHandfreeChecked() {
		return QavsdkControl.getInstance().getAVContext().getAudioCtrl().getAudioOutputMode() == AVAudioCtrl.OUTPUT_MODE_HEADSET;
	}
	
	String getQualityTips() {
		AVAudioCtrl avAudioCtrl;
		if (QavsdkControl.getInstance() != null) {
			avAudioCtrl = QavsdkControl.getInstance().getAVContext().getAudioCtrl();
			return avAudioCtrl.getQualityTips();
		}
		
		return "";
	}	
}