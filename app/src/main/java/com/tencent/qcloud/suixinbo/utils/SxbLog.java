package com.tencent.qcloud.suixinbo.utils;

import android.util.Log;

/**
 * 日志输出
 */
public class SxbLog {
	public enum SxbLogLevel {
		eOff,
		eError,
		eWarn,
		eDebug,
		eInfo
	}
	
	static private SxbLogLevel level = SxbLogLevel.eInfo;
	
	static public void setLogLevel(SxbLogLevel newLevel){
		level = newLevel;
		w("Log", "change log level: "+ newLevel);
	}
    public static void v(String strTag, String strInfo){
        Log.v(strTag, strInfo);
        if (level.ordinal() >= SxbLogLevel.eInfo.ordinal()){
            SxbLogImpl.writeLog("I", strTag, strInfo, null);
        }
    }

    public static void i(String strTag, String strInfo){
        v(strTag, strInfo);
    }

    public static void d(String strTag, String strInfo){
        Log.d(strTag, strInfo);
        if (level.ordinal() >= SxbLogLevel.eDebug.ordinal()){
            SxbLogImpl.writeLog("D", strTag, strInfo, null);
        }
    }


    public static void w(String strTag, String strInfo){
        Log.w(strTag, strInfo);
        if (level.ordinal() >= SxbLogLevel.eWarn.ordinal()){
            SxbLogImpl.writeLog("W", strTag, strInfo, null);
        }
    }

    public static void e(String strTag, String strInfo){
        Log.e(strTag, strInfo);
        if (level.ordinal() >= SxbLogLevel.eError.ordinal()){
            SxbLogImpl.writeLog("E", strTag, strInfo, null);
        }
    }

    public static void writeException(String strTag, String strInfo, Exception tr){
        SxbLogImpl.writeLog("C", strTag, strInfo, tr);
    }
}
