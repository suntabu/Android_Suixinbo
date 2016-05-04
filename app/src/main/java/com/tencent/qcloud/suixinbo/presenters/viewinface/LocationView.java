package com.tencent.qcloud.suixinbo.presenters.viewinface;

/**
 * Created by admin on 2016/5/3.
 */
public interface LocationView extends MvpView{

    void onLocationChanged(int code, String location);

}
