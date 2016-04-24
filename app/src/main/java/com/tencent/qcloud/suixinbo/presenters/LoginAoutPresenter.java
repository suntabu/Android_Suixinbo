package com.tencent.qcloud.suixinbo.presenters;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.tencent.TIMCallBack;
import com.tencent.TIMManager;
import com.tencent.TIMUser;
import com.tencent.qcloud.suixinbo.avcontrollers.QavsdkControl;
import com.tencent.qcloud.suixinbo.model.UserInfo;
import com.tencent.qcloud.suixinbo.presenters.viewinface.LoginView;
import com.tencent.qcloud.suixinbo.presenters.viewinface.LogoutView;
import com.tencent.qcloud.suixinbo.utils.Constants;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSPwdLoginListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * 登录的数据处理类
 */
public class LoginAoutPresenter {
    private Context mContext;
    private static final String TAG = LoginAoutPresenter.class.getSimpleName();
    private LoginView mLoginView;
    private LogoutView mLogoutView;
    private QavsdkControl mQavsdkControl;

    public LoginAoutPresenter(Context context, LoginView loginView) {
        mContext = context;
        mLoginView = loginView;
    }

    public LoginAoutPresenter(Context context, LogoutView logoutView) {
        mContext = context;
        mLogoutView =  logoutView;
    }



    /**
     * 登录imsdk
     *
     * @param identify 用户id
     * @param userSig  用户签名
     *
     */
    public void imLogin(String identify, String userSig) {
        TIMUser user = new TIMUser();
        user.setAccountType(String.valueOf(Constants.ACCOUNT_TYPE));
        user.setAppIdAt3rd(String.valueOf(Constants.SDK_APPID));
        user.setIdentifier(identify);
        //发起登录请求
        TIMManager.getInstance().login(
                Constants.SDK_APPID,
                user,
                userSig,                    //用户帐号签名，由私钥加密获得，具体请参考文档
                new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Log.e(TAG ,"IMLogin fail ："+ i+" msg " + s);
                        Toast.makeText(mContext, "IMLogin fail ："+ i+" msg " + s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        Log.i(TAG, "keypath IMLogin succ !");
//                        Toast.makeText(mContext, "IMLogin succ !", Toast.LENGTH_SHORT).show();
                        getMyRoomNum();
                    }
                });
    }


    /**
     * 退出imsdk
     *
     *  退出成功会调用退出AVSDK
     */
    public void imLogout() {
        TIMManager.getInstance().logout(new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Log.e(TAG, "IMLogout fail ：" + i + " msg " + s);
            }

            @Override
            public void onSuccess() {
                Log.i(TAG, "IMLogout succ !");
                //清除本地缓存
                UserInfo.getInstance().clearCache(mContext);
                //反向初始化avsdk
                stopAVSDK();
            }
        });

    }

    /**
     * 登录TLS账号系统
     * @param id
     * @param password
     */
    public void tlsLogin(String id ,String password){
        InitBusinessHelper.getmLoginHelper().TLSPwdLogin(id, password.getBytes(), new TLSPwdLoginListener() {
            @Override
            public void OnPwdLoginSuccess(TLSUserInfo tlsUserInfo) {//获取用户信息
//                Toast.makeText(mContext, "TLS login succ ! " + tlsUserInfo.identifier, Toast.LENGTH_SHORT).show();
                Log.i(TAG, "TLS OnPwdLoginSuccess " + tlsUserInfo.identifier);
                String userSig = InitBusinessHelper.getmLoginHelper().getUserSig(tlsUserInfo.identifier);
                UserInfo.getInstance().setId(tlsUserInfo.identifier);
                UserInfo.getInstance().setUserSig(userSig);
                imLogin(tlsUserInfo.identifier, userSig);
            }

            @Override
            public void OnPwdLoginReaskImgcodeSuccess(byte[] bytes) {

            }

            @Override
            public void OnPwdLoginNeedImgcode(byte[] bytes, TLSErrInfo tlsErrInfo) {

            }

            @Override
            public void OnPwdLoginFail(TLSErrInfo tlsErrInfo) {
                Log.e(TAG, "OnPwdLoginFail " + tlsErrInfo.Msg);
                Toast.makeText(mContext, "OnPwdLoginFail：\n" + tlsErrInfo.Msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnPwdLoginTimeout(TLSErrInfo tlsErrInfo) {
                Log.e(TAG, "OnPwdLoginTimeout " + tlsErrInfo.Msg);
                Toast.makeText(mContext, "OnPwdLoginTimeout：\n" + tlsErrInfo.Msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 向用户服务器获取自己房间号
     */
    private void getMyRoomNum(){
        UserInfo.getInstance().setMyRoomNum(54321);
        UserInfo.getInstance().writeToCache(mContext.getApplicationContext(),UserInfo.getInstance().getId(),UserInfo.getInstance().getUserSig(),UserInfo.getInstance().getMyRoomNum());
        startAVSDK();
    }


    /**
     * 初始化AVSDK
     */
    private void startAVSDK(){
        QavsdkControl.getInstance().setAvConfig(Constants.SDK_APPID,""+Constants.ACCOUNT_TYPE,UserInfo.getInstance().getId(),UserInfo.getInstance().getUserSig());
        QavsdkControl.getInstance().startContext();
        mLoginView.LoginSucc();
    }



    /**
     * 反初始化AVADK
     */
    private void stopAVSDK(){
        QavsdkControl.getInstance().stopContext();
        mLogoutView.LogoutSucc();
    }


    /**
     * 通知用户服务器创建自己房间号
     */
    public void crearServerRoom(){
         getMyRoomNum();
    }

}
