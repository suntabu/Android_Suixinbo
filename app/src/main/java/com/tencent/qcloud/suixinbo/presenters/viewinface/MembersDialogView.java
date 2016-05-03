package com.tencent.qcloud.suixinbo.presenters.viewinface;

import com.tencent.qcloud.suixinbo.model.MemberInfo;

import java.util.ArrayList;

/**
 * Created by admin on 16/5/3.
 */
public interface MembersDialogView extends MvpView {

    void showMembersList(ArrayList<MemberInfo> data);

}
