package com.tencent.qcloud.suixinbo.presenters.viewinface;

import com.tencent.qcloud.suixinbo.model.LiveInfoJson;

import java.util.ArrayList;

/**
 * Created by admin on 16/4/26.
 */
public interface LiveListView extends MvpView{

    void showFirstPage(ArrayList<LiveInfoJson> livelist);
}
