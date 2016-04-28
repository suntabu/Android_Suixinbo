package com.tencent.qcloud.suixinbo.presenters;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tencent.qcloud.suixinbo.model.LiveInfoJson;
import com.tencent.qcloud.suixinbo.model.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * 网络请求类
 */
public class OKhttpHelper {
    private static final String TAG = OKhttpHelper.class.getSimpleName();
    private static OKhttpHelper instance = null;
    public static final String GET_MYROOMID = "http://203.195.167.34/sxb/index.php?svc=user_av_room&cmd=get";
    public static final String NEW_ROOM_INFO = "http://203.195.167.34/sxb/index.php?svc=live&cmd=start";
    public static final String STOP_ROOM = "http://203.195.167.34/sxb/index.php?svc=live&cmd=end";
    public static final String GET_LIVELIST = "http://203.195.167.34/sxb/index.php?svc=live&cmd=list";
    public static final String SEND_HEARTBEAT = "http://203.195.167.34/sxb/index.php?svc=live&cmd=host_heartbeat";

    public static OKhttpHelper getInstance() {
        if (instance == null) {
            instance = new OKhttpHelper();
        }
        return instance;
    }


    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient();

    public String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            return "";
        }
    }

    /**
     * 同步Server 新创建房间信息
     */
    public int notifyServerNewLiveInfo(JSONObject reg) {
        try {


            Log.i(TAG, "notifyServer live start  liveinfo: " + reg);

            String res = post(NEW_ROOM_INFO, reg.toString());
            Log.i(TAG, "notifyServer live start  liveinfo: " + res);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();
            int code = response.getInt("errorCode");
            if (code == 0) {
                return code;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * 同步Server 关闭房间信息
     */
    public LiveInfoJson notifyServerLiveStop(String id) {
        try {
            JSONObject stopLive = new JSONObject();
            stopLive.put("hostUid", id);
            String json = stopLive.toString();

            String res = post(STOP_ROOM, json);
            Log.i(TAG, "notifyServer live stop  liveinfo: " + res);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();

            int code = response.getInt("errorCode");
            if (code == 0) {
                JSONObject data = response.getJSONObject("data");
                JSONObject record = data.getJSONObject("record");
                String recordS = record.toString();
                Gson gson = new GsonBuilder().create();
                LiveInfoJson result = gson.fromJson(recordS, LiveInfoJson.class);
                Log.i(TAG, "notifyServer live stop  Object p : " + result.toString());
                return result;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取自己的房间
     */
    public void getMyRoomId(final Context context) {
        try {
            JSONObject myId = new JSONObject();
            myId.put("uid", UserInfo.getInstance().getId());
            String response = OKhttpHelper.getInstance().post(GET_MYROOMID, myId.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                JSONObject data = reg_response.getJSONObject("data");
                String id = data.getString("chatRoomId");
                Log.i(TAG, "getMyRoomId " + id);
                UserInfo.getInstance().setMyRoomNum(Integer.parseInt(id));
                UserInfo.getInstance().writeToCache(context.getApplicationContext(), UserInfo.getInstance().getId(), UserInfo.getInstance().getUserSig(), UserInfo.getInstance().getMyRoomNum());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取直播列表
     *
     * @param page     页数
     * @param pagesize 每页个数
     * @return 返回直播列表
     */
    public ArrayList<LiveInfoJson> getLiveList(int page, int pagesize) {
        try {
            JSONObject req = new JSONObject();
            req.put("pageIndex", page);
            req.put("pageSize", pagesize);
            String response = OKhttpHelper.getInstance().post(GET_LIVELIST, req.toString());

            Log.i(TAG, "getLiveList " + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                JSONObject data = reg_response.getJSONObject("data");
                JSONArray record = data.getJSONArray("recordList");
                Type listType = new TypeToken<ArrayList<LiveInfoJson>>() {
                }.getType();
                ArrayList<LiveInfoJson> result = new Gson().fromJson(record.toString(), listType);
                return result;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    public void sendHeartBeat(String userid, int watchCount, int admireCount, int timeSpan) {
        try {
            JSONObject req = new JSONObject();
            req.put("uid", userid);
            req.put("watchCount", watchCount);
            req.put("admireCount", admireCount);
            req.put("timeSpan", timeSpan);
            String response = OKhttpHelper.getInstance().post(SEND_HEARTBEAT, req.toString());

            Log.i(TAG, "getLiveList " + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                Log.i(TAG, "sendHeartBeat is Ok");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
