package com.tencent.qcloud.suixinbo.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.model.MemberInfo;
import com.tencent.qcloud.suixinbo.presenters.LiveHelper;
import com.tencent.qcloud.suixinbo.utils.Constants;

import java.util.ArrayList;


/**
 * 成员列表适配器
 */
public class MembersAdapter extends ArrayAdapter<MemberInfo> {
    private Context mContext;
    private LiveHelper liveHelper;
    private static final String TAG = MembersAdapter.class.getSimpleName();

    public MembersAdapter(Context context, int resource, ArrayList<MemberInfo> objects) {
        super(context, resource, objects);
        mContext = context;
        liveHelper = new LiveHelper(mContext);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.members_item_layout, null);
            holder = new ViewHolder();
            holder.id = (TextView) convertView.findViewById(R.id.item_name);
            holder.inviteBtn = (TextView) convertView.findViewById(R.id.item_video_btn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MemberInfo data = getItem(position);
        final String selectId = data.getUserId();
        holder.id.setText(selectId);
        holder.inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "select item:  " + selectId);
                liveHelper.sendC2CMessage(Constants.AVIMCMD_MUlTI_HOST_INVITE, selectId);
            }
        });


        return convertView;
    }

    public final class ViewHolder {
        public TextView id;
        public TextView inviteBtn;
    }

}
