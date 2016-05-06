package com.tencent.qcloud.suixinbo.utils;


/**
 * 静态函数
 */
public class Constants {

    public static final String USER_INFO = "user_info";

    public static final String USER_ID = "user_id";

    public static final String USER_SIG = "user_sig";

    public static final String USER_NICK = "user_nick";

    public static final String USER_AVATAR = "user_avatar";

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

    public static final int LOCATION_PERMISSION_REQ_CODE = 1;


    public static final String APPLY_CHATROOM = "申请加入";

    public static final int IS_ALREADY_MEMBER = 10013;

    public static final int TEXT_TYPE = 0;

    public static final String ROOT_DIR = "/sdcard/Suixinbo/";


    public static final int AVIMCMD_MULTI = 0x800;             // 多人互动消息类型

    public static final int AVIMCMD_MUlTI_HOST_INVITE = AVIMCMD_MULTI + 1;         // 多人主播发送邀请消息, C2C消息
    public static final int AVIMCMD_MULT_CANCEL_INTERACT = AVIMCMD_MUlTI_HOST_INVITE + 1;       // 断开互动，Group消息，带断开者的imUsreid参数
    public static final int AVIMCMD_MUlTI_JOIN = AVIMCMD_MULT_CANCEL_INTERACT + 1;       // 多人互动方收到AVIMCMD_Multi_Host_Invite多人邀请后，同意，C2C消息
    public static final int AVIMCMD_MUlTI_REFUSE = AVIMCMD_MUlTI_JOIN + 1;      // 多人互动方收到AVIMCMD_Multi_Invite多人邀请后，拒绝，C2C消息

    public static final int AVIMCMD_Multi_Host_EnableInteractMic = AVIMCMD_MUlTI_REFUSE + 1;  // 主播打开互动者Mic，C2C消息
    public static final int AVIMCMD_Multi_Host_DisableInteractMic = AVIMCMD_Multi_Host_EnableInteractMic + 1;// 主播关闭互动者Mic，C2C消息
    public static final int AVIMCMD_Multi_Host_EnableInteractCamera = AVIMCMD_Multi_Host_DisableInteractMic + 1; // 主播打开互动者Camera，C2C消息
    public static final int AVIMCMD_Multi_Host_DisableInteractCamera = AVIMCMD_Multi_Host_EnableInteractCamera + 1; // 主播打开互动者Camera，C2C消息


    public static final int AVIMCMD_Text = -1;         // 普通的聊天消息

    public static final int AVIMCMD_None = AVIMCMD_Text + 1;               // 无事件

    // 以下事件为TCAdapter内部处理的通用事件
    public static final int AVIMCMD_EnterLive = AVIMCMD_None + 1;          // 用户加入直播, Group消息
    public static final int AVIMCMD_ExitLive = AVIMCMD_EnterLive + 1;         // 用户退出直播, Group消息
    public static final int AVIMCMD_Praise = AVIMCMD_ExitLive + 1;           // 点赞消息, Demo中使用Group消息


}
