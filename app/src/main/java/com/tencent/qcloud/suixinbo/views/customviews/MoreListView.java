package com.tencent.qcloud.suixinbo.views.customviews;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.presenters.LiveListViewHelper;


/**
 * {@link android.widget.ListView}模板,可下拉刷新列表控件
 */
public class MoreListView extends SwipeRefreshLayout implements AbsListView.OnScrollListener {

    private View moreView;
    private View mLayView;
    private int visibleLastIndex = 0;   //最后的可视项索引
    private int visibleItemCount;       // 当前窗口可见项总数
    private Adapter adapter;
    private LiveListViewHelper mPresnter;
    private ProgressBar mProgressBar;
    private ListView listView;
    private Context mContext;
    private boolean isEnable = true;

    /**
     * 构造方法.
     *
     * @param context
     * @param attrs
     */
    public MoreListView(Context context, ListView listview, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setColorSchemeColors(getResources().getColor(R.color.colorTheme));
        listView = listview;
        moreView = LayoutInflater.from(context).inflate(R.layout.add_more_view, null);
        mProgressBar = (ProgressBar) moreView.findViewById(R.id.pg);
        listView.setOnScrollListener(this);
    }

//    /**
//     * 获取控件中的listview
//     *
//     */
//    public ListView getListView() {
//        if (mLayView != null) {
//            return (ListView) mLayView.findViewById(R.id.list_view);
//        }
//        return null;
//    }



    public void setAdapter(Adapter mAdapter) {
        adapter = mAdapter;
    }

    /**
     * 设置presenter，用于调用present中逻辑，如加载更多
     *
     */
    public void setPresnter(LiveListViewHelper mPresnter) {
        this.mPresnter = mPresnter;
    }

    /**
     * 滑动监听，用于加载更多
     *
     */
    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        int itemsLastIndex = adapter.getCount() - 1;    //数据集最后一项的索引
        int lastIndex = itemsLastIndex + 1;             //加上底部的loadMoreView项
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == itemsLastIndex) {
            //如果是自动加载,可以在这里放置异步加载数据的代码
            mProgressBar.setVisibility(View.VISIBLE);
            if (mPresnter != null) {
                mPresnter.getMoreData();
            }
        }
    }

    /**
     * 滑动监听，当列表不在最顶端时，滑动不会触发刷新
     *
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        MoreListView.this.visibleItemCount = visibleItemCount;
        visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
        if (listView != null && listView.getChildCount() > 0) {
            boolean firstItemVisible = listView.getFirstVisiblePosition() == 0;
            boolean topOfFirstItemVisible = listView.getChildAt(0).getTop() == 0;
            setEnabled(firstItemVisible && topOfFirstItemVisible);
        } else {
            setEnabled(true);
        }

    }

    public void setProgressBarGone(){
        mProgressBar.setVisibility(View.GONE);
    }
}
