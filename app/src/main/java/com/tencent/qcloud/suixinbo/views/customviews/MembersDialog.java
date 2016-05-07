package com.tencent.qcloud.suixinbo.views.customviews;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.ListView;

import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.adapters.MembersAdapter;
import com.tencent.qcloud.suixinbo.model.MemberInfo;
import com.tencent.qcloud.suixinbo.presenters.LiveHelper;
import com.tencent.qcloud.suixinbo.presenters.viewinface.MembersDialogView;

import java.util.ArrayList;

/**
 * 成员列表
 */
public class MembersDialog extends Dialog implements MembersDialogView {
    private Context mContext;
    private LiveHelper mLiveHelper;
    private ListView mMemberList;
    private MembersAdapter mMembersAdapter;

    public MembersDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
        mLiveHelper = new LiveHelper(mContext, this);
        setContentView(R.layout.members_layout);
        mMemberList = (ListView) findViewById(R.id.member_list);
        Window window = getWindow();
        window.setGravity(Gravity.TOP);
        setCanceledOnTouchOutside(true);
//        mMembersAdapter = new MembersAdapter(mContext, R.layout.members_item_layout, members);
//        mMemberList.setAdapter(mMembersAdapter);
    }

    @Override
    protected void onStart() {
        //获取成员信息
        mLiveHelper.getMemberList();
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 通过Helper获得数据
     *
     * @param data
     */
    @Override
    public void showMembersList(ArrayList<MemberInfo> data) {
        if (data == null) return;
        mMembersAdapter = new MembersAdapter(mContext, R.layout.members_item_layout, data);
        mMemberList.setAdapter(mMembersAdapter);
        mMembersAdapter.notifyDataSetChanged();
    }

    public boolean onTouchEvent(MotionEvent event)
    {

        if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
            System.out.println("TOuch outside the dialog ******************** ");
            this.dismiss();
        }
        return false;
    }
}
