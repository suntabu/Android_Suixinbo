package com.tencent.qcloud.suixinbo.presenters;

import android.os.Handler;

import com.tencent.qcloud.suixinbo.presenters.viewinface.SplashView;


/**
 * 闪屏界面逻辑
 */
public class SplashPresenter extends Presenter {
    SplashView view;
    private static final String TAG = SplashPresenter.class.getSimpleName();

    public SplashPresenter(SplashView view) {
        this.view = view;
    }


    /**
     * 加载页面逻辑
     */
    public void loginApp() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (view.needLogin()) {
                    view.navToHome();
                    view.navToLogin();
                } else {
                    view.navToLogin();
                }
            }
        }, 0);
    }


}
