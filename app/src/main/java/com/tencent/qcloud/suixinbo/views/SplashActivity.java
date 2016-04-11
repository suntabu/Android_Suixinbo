package com.tencent.qcloud.suixinbo.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tencent.TIMCallBack;
import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.model.UserInfo;
import com.tencent.qcloud.suixinbo.presenters.InitBusinessHelper;
import com.tencent.qcloud.suixinbo.presenters.SplashPresenter;
import com.tencent.qcloud.suixinbo.presenters.viewinface.SplashView;
import com.tencent.qcloud.tlslibrary.activity.HostLoginActivity;
import com.tencent.qcloud.tlslibrary.service.TLSService;

public class SplashActivity extends AppCompatActivity implements SplashView{
    private int LOGIN_RESULT_CODE = 100;
    SplashPresenter mPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //登录判断
        mPresenter = new SplashPresenter(this);
        mPresenter.loginApp();

    }

    /**
     * IM登录跳转进入主界面
     */
    @Override
    public void navToHome() {
        InitBusinessHelper.loginIm(UserInfo.getInstance().getId(), UserInfo.getInstance().getUserSig(), new TIMCallBack() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess() {

            }
        });
    }

    /**
     * 引导到Tls module 进行注册或者登录
     */
    @Override
    public void navToLogin() {
        Intent intent = new Intent(getApplicationContext(), HostLoginActivity.class);
        startActivityForResult(intent, LOGIN_RESULT_CODE);
    }

    @Override
    public boolean isUserLogin() {
        return UserInfo.getInstance().getId()!= null && (!TLSService.getInstance().needLogin(UserInfo.getInstance().getId()));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (LOGIN_RESULT_CODE == requestCode) {
            if (resultCode == RESULT_OK){
                String id = TLSService.getInstance().getLastUserIdentifier();
                UserInfo.getInstance().setId(id);
                UserInfo.getInstance().setUserSig(TLSService.getInstance().getUserSig(id));
                navToHome();
            }
        }
    }
}
