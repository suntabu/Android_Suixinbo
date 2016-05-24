package com.tencent.qcloud.suixinbo.presenters;


import android.os.AsyncTask;

import com.tencent.qcloud.suixinbo.model.LiveInfoJson;
import com.tencent.qcloud.suixinbo.presenters.viewinface.LiveListView;

import java.util.ArrayList;

/**
 * 直播列表页Presenter
 */
public class LiveListViewHelper extends Presenter {
    private LiveListView mLiveListView;
    private GetLiveListTask mGetLiveListTask;

    public LiveListViewHelper(LiveListView view) {
        mLiveListView = view;
    }


    public void getPageData() {
        mGetLiveListTask = new GetLiveListTask();
        mGetLiveListTask.execute(0, 20);
    }

    public void getMoreData() {

    }

    @Override
    public void onDestory() {
    }

    /**
     * 获取后台数据接口
     */
    class GetLiveListTask extends AsyncTask<Integer, Integer, ArrayList<LiveInfoJson>> {

        @Override
        protected ArrayList<LiveInfoJson> doInBackground(Integer... params) {
            return OKhttpHelper.getInstance().getLiveList(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(ArrayList<LiveInfoJson> result) {
            mLiveListView.showFirstPage(result);
        }
    }

}
