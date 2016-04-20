package com.tencent.qcloud.suixinbo;

import android.app.Application;
import android.content.Context;

import com.tencent.qcloud.suixinbo.presenters.InitBusinessHelper;


/**
 * 全局Application
 */
public class QavsdkApplication extends Application {

    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        //初始化APP
        InitBusinessHelper.initApp(context);

        //创建AVSDK 控制器类


    }

    public static Context getContext() {
        return context;
    }

}
