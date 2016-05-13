package com.tencent.qcloud.suixinbo.presenters.viewinface;

/**
 * Created by admin on 16/4/21.
 */
public interface LiveView extends MvpView {

    void showVideoView(boolean isHost, String id);

    void showInviteDialog();

    void refreshText(String text, String name);

    void refreshThumbUp();

    void refreshUI(String id);

    void showInviteView(String id);

    void cancelInviteView(String id);

    void cancelMemberView(String id);

}
