package com.tencent.qcloud.suixinbo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.model.MemberInfo;
import com.tencent.qcloud.suixinbo.presenters.LiveHelper;

import java.util.ArrayList;


/**
 * 成员列表适配器
 */
public class MembersAdapter extends ArrayAdapter<MemberInfo> {
    private View view;
    private Context mContext;
    private TextView itemName, itemBtn;
    private String selectId;
    private LiveHelper liveHelper;

    public MembersAdapter(Context context, int resource, ArrayList<MemberInfo> objects) {
        super(context, resource, objects);
        mContext = context;
        liveHelper = new LiveHelper(mContext);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            view = convertView;
        } else {
            view = LayoutInflater.from(getContext()).inflate(R.layout.members_item_layout, null);
        }
        MemberInfo data = getItem(position);
        itemName = (TextView) view.findViewById(R.id.item_name);
        selectId = data.getUserId();
        itemName.setText(data.getUserId());
        itemBtn = (TextView) view.findViewById(R.id.item_video_btn);
        itemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                liveHelper.inviteVideoChat(selectId);
            }
        });


        return view;
    }

}
