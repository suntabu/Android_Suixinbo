package com.tencent.qcloud.suixinbo.avcontrollers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.tencent.av.sdk.AVContext;
import com.tencent.av.sdk.AVRoom;
import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.qcloud.suixinbo.model.AvMemberInfo;

import java.util.ArrayList;

class AVRoomControl {
	private static final int TYPE_MEMBER_CHANGE_IN = 1;//进入房间事件。
	private static final int TYPE_MEMBER_CHANGE_OUT = 2;//退出房间事件。
	private static final int TYPE_MEMBER_CHANGE_HAS_CAMERA_VIDEO = 3;//有发摄像头视频事件。
	private static final int TYPE_MEMBER_CHANGE_NO_CAMERA_VIDEO = 4;//无发摄像头视频事件。
	private static final int TYPE_MEMBER_CHANGE_HAS_AUDIO = 5;//有发语音事件。
	private static final int TYPE_MEMBER_CHANGE_NO_AUDIO = 6;//无发语音事件。
	private static final int TYPE_MEMBER_CHANGE_HAS_SCREEN_VIDEO = 7;//有发屏幕视频事件。
	private static final int TYPE_MEMBER_CHANGE_NO_SCREEN_VIDEO = 8;//无发屏幕视频事件。
	private static final String TAG = "AVRoomControl";
	private boolean mIsInCreateRoom = false;
	private boolean mIsInCloseRoom = false;
	private Context mContext;
	private ArrayList<AvMemberInfo> mAudioAndCameraMemberList = new ArrayList<AvMemberInfo>();
	private ArrayList<AvMemberInfo> mScreenMemberList = new ArrayList<AvMemberInfo>();
	private int audioCat = 0;
	public void setAudioCat(int audioCat) {
		this.audioCat = audioCat;
	}


	/**
	 * 房间回调
	 */
	private AVRoomMulti.Delegate mRoomDelegate = new AVRoomMulti.Delegate() {
		// 创建房间成功回调
		public void onEnterRoomComplete(int result) {
			Log.d(TAG, "WL_DEBUG mRoomDelegate.onEnterRoomComplete result = " + result);
			mIsInCreateRoom = false;
			Toast.makeText(mContext, "enter AVroom " + result , Toast.LENGTH_SHORT).show();
			mContext.sendBroadcast(new Intent(AvConstants.ACTION_ROOM_CREATE_COMPLETE).putExtra(AvConstants.EXTRA_AV_ERROR_RESULT, result));
		}
		
		// 离开房间成功回调
		public void onExitRoomComplete(int result) {
			Log.d(TAG, "WL_DEBUG mRoomDelegate.onExitRoomComplete result = " + result);
			mIsInCloseRoom = false;
			mAudioAndCameraMemberList.clear();
			mScreenMemberList.clear();
			mContext.sendBroadcast(new Intent(AvConstants.ACTION_CLOSE_ROOM_COMPLETE));
		}
		public void onEndpointsUpdateInfo(int eventid, String[] updateList) {
			Log.d(TAG, "WL_DEBUG onEndpointsUpdateInfo. eventid = " + eventid);
			onMemberChange(eventid, updateList);
		}
				
		public void OnPrivilegeDiffNotify(int privilege) {
			Log.d(TAG, "OnPrivilegeDiffNotify. privilege = " + privilege);
		}
	};


	AVRoomControl(Context context) {
		mContext = context;
	}

	/**
	 * AVRoom 房间成员变化
	 * @param eventid
	 * @param updateList
	 */
	private void onMemberChange(int eventid, String[] updateList) {
	}

	/**
	 * 创建房间
	 * 
	 * @param relationId
	 *            讨论组号
	 */
	void enterRoom(int relationId, String roomRole, boolean isAutoCreateSDKRoom) {
		Log.d(TAG, "WL_DEBUG enterRoom relationId = " + relationId);
		AVContext avContext =QavsdkControl.getInstance().getAVContext();
		byte[] authBuffer = null;//权限位加密串；TODO：请业务侧填上自己的加密串
		AVRoom.EnterRoomParam enterRoomParam = new AVRoomMulti.EnterRoomParam(relationId, AvConstants.auth_bits, authBuffer, roomRole, audioCat, isAutoCreateSDKRoom);
		// create room
		avContext.enterRoom(AVRoom.AV_ROOM_MULTI, mRoomDelegate, enterRoomParam);
		mIsInCreateRoom = true;
	}

	/** 关闭房间 */
	int exitRoom() {
		Log.d(TAG, "WL_DEBUG exitRoom");
		AVContext avContext = QavsdkControl.getInstance().getAVContext();
		int result = avContext.exitRoom();
		mIsInCloseRoom = true;

		return result;
	}
	
	boolean changeAuthority(long auth_bits, byte[] auth_buffer, AVRoomMulti.ChangeAuthorityCallback callback) {
		Log.d(TAG, "WL_DEBUG changeAuthority");
		AVContext avContext = QavsdkControl.getInstance().getAVContext();
		AVRoomMulti room = (AVRoomMulti)avContext.getRoom();
		return room.changeAuthority(auth_bits,auth_buffer, auth_buffer.length, callback);
	}

	/**
	 * 获取成员列表
	 * 
	 * @return 成员列表
	 */
	ArrayList<AvMemberInfo> getMemberList() {
		ArrayList<AvMemberInfo> memberList = (ArrayList<AvMemberInfo>)mAudioAndCameraMemberList.clone();
		for (int j = 0; j < mScreenMemberList.size(); j++) {
			memberList.add(mScreenMemberList.get(j));
		}
		return memberList;
	}

	ArrayList<AvMemberInfo> getAudioAndCameraMemberList() {
		return mAudioAndCameraMemberList;
	}

	ArrayList<AvMemberInfo> getScreenMemberList() {
		return mScreenMemberList;
	}

	boolean getIsInEnterRoom() {
		return mIsInCreateRoom;
	}

	boolean getIsInCloseRoom() {
		return mIsInCloseRoom;
	}
	
	public void setCreateRoomStatus(boolean status) {
		mIsInCreateRoom = status;
	}
	public void setCloseRoomStatus(boolean status) {
		mIsInCloseRoom = status;
	}
	
	public void setNetType(int netType) {
		AVContext avContext = QavsdkControl.getInstance().getAVContext();
		AVRoomMulti room = (AVRoomMulti)avContext.getRoom();
		if (null != room) {
			room.setNetType(netType);
		}
	}
}