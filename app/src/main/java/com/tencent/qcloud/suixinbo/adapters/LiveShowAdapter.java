package com.tencent.qcloud.suixinbo.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.model.LiveInfoJson;
import com.tencent.qcloud.suixinbo.utils.GlideCircleTransform;
import com.tencent.qcloud.suixinbo.utils.UIUtils;

import java.util.ArrayList;


/**
 * 直播列表的Adapter
 */
public class LiveShowAdapter extends ArrayAdapter<LiveInfoJson> {
    private static String TAG = "LiveShowAdapter";
    private int resourceId;
    private View view;
//    private ImageLoader imageLoader = ImageLoader.getInstance();
    private Activity mActivity;

//    private DisplayImageOptions options = new DisplayImageOptions.Builder()
//            .cacheInMemory(true)
//            .resetViewBeforeLoading(true)
//            .showImageOnLoading(R.drawable.cover_background)
//            .showImageOnFail(R.drawable.cover_background)
//            .cacheOnDisk(true)
//            .build();


    public LiveShowAdapter(Activity activity, int resource, ArrayList<LiveInfoJson> objects) {
        super(activity, resource, objects);
        resourceId = resource;
        mActivity = activity;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            view = convertView;
        } else {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_liveshow, null);
        }
        LiveInfoJson data = getItem(position);

        ImageView avatar = (ImageView) view.findViewById(R.id.avatar);
        if (null == data.getHost() || TextUtils.isEmpty(data.getHost().getAvatar())){
            // 显示默认图片
            Bitmap bitmap = BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.default_avatar);
            Bitmap cirBitMap = UIUtils.createCircleImage(bitmap, 0);
            avatar.setImageBitmap(cirBitMap);
        }else{
            Log.d(TAG, "user avator: "+data.getHost().getAvatar());
            RequestManager req = Glide.with(mActivity);
            req.load(data.getHost().getAvatar()).transform(new GlideCircleTransform(mActivity)).into(avatar);
        }

        TextView hostName = (TextView) view.findViewById(R.id.host_name);
        hostName.setText("@"+data.getHost().getUid());
        TextView title = (TextView) view.findViewById(R.id.live_title);
        title.setText(data.getTitle());
        TextView admire = (TextView) view.findViewById(R.id.praises);
        admire.setText("" + data.getAdmireCount());
        return view;
    }
}
