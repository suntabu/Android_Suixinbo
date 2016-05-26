package com.tencent.qcloud.suixinbo.presenters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.tencent.TIMLogLevel;
import com.tencent.TIMManager;
import com.tencent.TIMUserStatusListener;
import com.tencent.qcloud.suixinbo.QavsdkApplication;
import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.avcontrollers.QavsdkControl;
import com.tencent.qcloud.suixinbo.model.MySelfInfo;
import com.tencent.qcloud.suixinbo.utils.Constants;
import com.tencent.qcloud.suixinbo.utils.CrashHandler;
import com.tencent.qcloud.suixinbo.utils.SxbLog;

import tencent.tls.platform.TLSAccountHelper;
import tencent.tls.platform.TLSLoginHelper;


/**
 * 初始化
 * 包括imsdk等
 */
public class InitBusinessHelper {
    private static String TAG = "InitBusinessHelper";

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
    private static QavsdkControl mQavsdkControl = null;


    /**
     * 初始化App
     */
    public static void initApp(final Context context) {
        //初始化avsdk imsdk
        QavsdkControl.initQavsdk(context);
        TIMManager.getInstance().disableBeaconReport();
        MySelfInfo.getInstance().getCache(context);
        switch(MySelfInfo.getInstance().getLogLevel()){
        case OFF:
            TIMManager.getInstance().setLogLevel(TIMLogLevel.OFF);
            break;
        case WARN:
            TIMManager.getInstance().setLogLevel(TIMLogLevel.WARN);
            break;
        case DEBUG:
            TIMManager.getInstance().setLogLevel(TIMLogLevel.DEBUG);
            break;
        case INFO:
            TIMManager.getInstance().setLogLevel(TIMLogLevel.INFO);
            break;
        }
        TIMManager.getInstance().init(context);

        TIMManager.getInstance().setUserStatusListener(new TIMUserStatusListener() {
            @Override
            public void onForceOffline() {
                SxbLog.w(TAG, "onForceOffline->entered!");
                Toast.makeText(context, context.getString(R.string.tip_force_offline), Toast.LENGTH_SHORT).show();
                context.sendBroadcast(new Intent(Constants.BD_EXIT_APP));
            }
        });

        //QAL初始化
        //初始化TLS
        initTls(context);

        //初始化CrashReport系统
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(context);

    }


    /**
     * 初始化TLS登录模块
     *
     * @param context
     */
    public static void initTls(Context context) {
        mLoginHelper = TLSLoginHelper.getInstance().init(context, Constants.SDK_APPID, Constants.ACCOUNT_TYPE, appVer);
        mLoginHelper.setTimeOut(5000);
        mAccountHelper = TLSAccountHelper.getInstance().init(context, Constants.SDK_APPID, Constants.ACCOUNT_TYPE, appVer);
        mAccountHelper.setTimeOut(5000);
//      MySelfInfo.getInstance().setId(id);
//      MySelfInfo.getInstance().setUserSig(TLSService.getInstance().getUserSig(id));
    }

}
