package com.tencent.qcloud.suixinbo.presenters;

import android.content.Context;

import com.tencent.TIMCallBack;
import com.tencent.TIMManager;
import com.tencent.qcloud.suixinbo.utils.Constants;
import com.tencent.qcloud.suixinbo.utils.CrashHandler;

import tencent.tls.platform.TLSAccountHelper;
import tencent.tls.platform.TLSLoginHelper;


/**
 * 初始化
 * 包括imsdk等
 */
public class InitBusinessHelper {

    private InitBusinessHelper() {
    }

    public static TLSLoginHelper getmLoginHelper() {
        return mLoginHelper;
    }

    public static TLSAccountHelper getmAccountHelper() {
        return mAccountHelper;
    }


    private static TLSLoginHelper mLoginHelper;
    private static TLSAccountHelper mAccountHelper;
    private static String appVer = "1.0";


    /**
     * 初始化App
     */
    public static void initApp(Context context) {
        //初始化imsdk

        TIMManager.getInstance().init(context);

        //QAL初始化
        //初始化TLS
        initTls(context);

        //初始化CrashReport系统
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(context);

    }


    /**
     *
     * @param context
     */
    public static void initTls(Context context) {
        mLoginHelper = TLSLoginHelper.getInstance().init(context, Constants.SDK_APPID, Constants.ACCOUNT_TYPE, appVer);
        mLoginHelper.setTimeOut(3000);

        mAccountHelper = TLSAccountHelper.getInstance().init(context, Constants.SDK_APPID, Constants.ACCOUNT_TYPE, appVer);
        mAccountHelper.setTimeOut(3000);
//      UserInfo.getInstance().setId(id);
//      UserInfo.getInstance().setUserSig(TLSService.getInstance().getUserSig(id));
    }


//    /**
//     * 登录imsdk
//     *
//     * @param identify 用户id
//     * @param userSig  用户签名
//     * @param callBack 登录后回调
//     */
//    public static void loginIm(String identify, String userSig, TIMCallBack callBack) {
//        TIMUser user = new TIMUser();
//        user.setAccountType(String.valueOf(Constants.ACCOUNT_TYPE));
//        user.setAppIdAt3rd(String.valueOf(Constants.SDK_APPID));
//        user.setIdentifier(identify);
//        //发起登录请求
//        TIMManager.getInstance().login(
//                Constants.SDK_APPID,
//                user,
//                userSig,                    //用户帐号签名，由私钥加密获得，具体请参考文档
//                callBack);
//    }
//
//
//    public static boolean tlsNeedLogin(String id) {
//        if (mLoginHelper != null) {
//            return mLoginHelper.needLogin(id);
//        }
//        return true;
//    }

    /**
     * 登出imsdk
     *
     * @param callBack 登出后回调
     */
    public static void logout(TIMCallBack callBack) {
        TIMManager.getInstance().logout(callBack);
    }


}
