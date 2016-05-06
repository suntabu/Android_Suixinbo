package com.tencent.qcloud.suixinbo.presenters.viewinface;

import com.tencent.TIMUserProfile;

/**
 * 个人资料页
 */
public interface ProfileView {
    void updateProfileInfo(TIMUserProfile profile);
}
