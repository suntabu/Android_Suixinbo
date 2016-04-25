package com.tencent.qcloud.suixinbo.presenters.viewinface;


/**
 * 创建房间回调接口
 */
public interface EnterQuiteRoomView extends MvpView{


    void EnterRoomComplete(int id_status, boolean succ);

    void QuiteRoomCB(int id_status, boolean succ);


}
