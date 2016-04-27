package com.tencent.qcloud.suixinbo.presenters;


import android.content.Context;

import com.tencent.qcloud.suixinbo.presenters.viewinface.LiveListView;

/**
 * 直播列表页Presenter
 */
public class LiveListViewHelper extends Presenter {
    private Context mContext;
    private LiveListView mLiveListView;

    public LiveListViewHelper(Context context, LiveListView view) {
        mContext = context;
        mLiveListView = view;
    }


    public void getPageData() {

    }

    public void getMoreData() {

    }
}
