package com.tencent.qcloud.suixinbo.presenters;

import android.content.Context;

import com.tencent.TIMCallBack;
import com.tencent.TIMManager;
import com.tencent.TIMUser;
import com.tencent.qcloud.sdk.Constant;
import com.tencent.qcloud.suixinbo.model.UserInfo;
import com.tencent.qcloud.suixinbo.utils.CrashHandler;
import com.tencent.qcloud.tlslibrary.service.TLSService;
import com.tencent.qcloud.tlslibrary.service.TlsBusiness;


/**
 * 初始化
 * 包括imsdk等
 */
public class InitBusinessHelper {

    private InitBusinessHelper(){}

    public static void start(Context context){
        initApp(context);
    }


    /**
     * 初始化App
     */
    private static void initApp(Context context){
        //初始化imsdk
        TIMManager.getInstance().init(context);
        //初始化TLS
        TlsBusiness.init(context);
        String id =  TLSService.getInstance().getLastUserIdentifier();
        UserInfo.getInstance().setId(id);
        UserInfo.getInstance().setUserSig(TLSService.getInstance().getUserSig(id));
        //初始化CrashReport系统
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(context);
    }



    /**
     * 登录imsdk
     *
     * @param identify 用户id
     * @param userSig 用户签名
     * @param callBack 登录后回调
     */
    public static void loginIm(String identify, String userSig, TIMCallBack callBack){
        TIMUser user = new TIMUser();
        user.setAccountType(String.valueOf(Constant.ACCOUNT_TYPE));
        user.setAppIdAt3rd(String.valueOf(Constant.SDK_APPID));
        user.setIdentifier(identify);
        //发起登录请求
        TIMManager.getInstance().login(
                Constant.SDK_APPID,
                user,
                userSig,                    //用户帐号签名，由私钥加密获得，具体请参考文档
                callBack);
    }

    /**
     * 登出imsdk
     *
     * @param callBack 登出后回调
     */
    public static void logout(TIMCallBack callBack){
        TIMManager.getInstance().logout(callBack);
    }





}
