package com.tencent.qcloud.suixinbo.utils;


/**
 * 静态函数
 */
public class Constants {

    public static final String USER_INFO = "user_info";

    public static final String USER_ID = "user_id";

    public static final String USER_SIG = "user_sig";

    public static final String USER_ROOM_NUM = "user_room_num";

//    //
//    public static final int ACCOUNT_TYPE = 792;
//    //    //sdk appid 由腾讯分配
//    public static final int SDK_APPID = 1400001533;

    public static final int SDK_APPID = 1400001692;

    public static final int ACCOUNT_TYPE = 884;

    public static final String ID_STATUS = "id_status";

    public static final int HOST = 1;

    public static final int MEMBER = 0;


    public static final String APPLY_CHATROOM = "申请加入";

    public static final int IS_ALREADY_MEMBER = 10013;

    public static final int TEXT_TYPE = 0;

    public static final String ROOT_DIR = "/sdcard/Suixinbo/";


    public static final int AVIMCMD_MULTI = 0x800;             // 多人互动消息类型

    public static final int AVIMCMD_MUlTI_HOST_INVITE = AVIMCMD_MULTI + 1;         // 多人主播发送邀请消息, C2C消息
    public static final int AVIMCMD_MULT_CANCEL_INTERACT = AVIMCMD_MUlTI_HOST_INVITE + 1;       // 断开互动，Group消息，带断开者的imUsreid参数
    public static final int AVIMCMD_MUlTI_JOIN = AVIMCMD_MULT_CANCEL_INTERACT + 1;       // 多人互动方收到AVIMCMD_Multi_Host_Invite多人邀请后，同意，C2C消息
    public static final int AVIMCMD_MUlTI_REFUSE = AVIMCMD_MUlTI_JOIN + 1;      // 多人互动方收到AVIMCMD_Multi_Invite多人邀请后，拒绝，C2C消息


}
