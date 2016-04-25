package com.tencent.qcloud.suixinbo.model;

/**
 * Created by admin on 16/4/22.
 */
public class LiveRoomInfo {

    public static int RoomNum;

    public static String hostID;


    private static LiveRoomInfo ourInstance = new LiveRoomInfo();

    public static LiveRoomInfo getInstance() {

        return ourInstance;
    }




    public static String getHostID() {
        return hostID;
    }

    public static void setHostID(String hostID) {
        LiveRoomInfo.hostID = hostID;
    }

    public static int getRoomNum() {
        return RoomNum;
    }

    public static void setRoomNum(int roomNum) {
        RoomNum = roomNum;
    }


}
