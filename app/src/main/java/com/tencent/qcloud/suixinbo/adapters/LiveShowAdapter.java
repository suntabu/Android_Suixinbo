package com.tencent.qcloud.suixinbo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.model.LiveShowModel;

import java.util.List;


/**
 *  直播列表的Adapter
 */
public class LiveShowAdapter extends ArrayAdapter<LiveShowModel> {

    private int resourceId;
    private View view;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private Context mContext;
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .resetViewBeforeLoading(true)
            .showImageOnLoading(R.drawable.cover_background)
            .showImageOnFail(R.drawable.cover_background)
            .cacheOnDisk(true)
            .build();


    public LiveShowAdapter(Context context, int resource, List<LiveShowModel> objects) {
        super(context, resource, objects);
        resourceId = resource;
        mContext = context;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null){
            view = convertView;
        }else{
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_liveshow, null);
        }
        final LiveShowModel data = getItem(position);
        TextView hostName = (TextView) view.findViewById(R.id.host_name);
        hostName.setText(data.getHostName());
        TextView title = (TextView) view.findViewById(R.id.live_title);
        title.setText(data.getTitle());

        TextView admire = (TextView) view.findViewById(R.id.praises);
        admire.setText(String.valueOf(data.getAdmireCount()));
        return view;
    }
}
