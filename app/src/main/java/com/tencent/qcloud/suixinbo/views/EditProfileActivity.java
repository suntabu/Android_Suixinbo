package com.tencent.qcloud.suixinbo.views;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.tencent.TIMUserProfile;
import com.tencent.qcloud.suixinbo.QavsdkApplication;
import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.model.MySelfInfo;
import com.tencent.qcloud.suixinbo.presenters.ProfileInfoHelper;
import com.tencent.qcloud.suixinbo.presenters.viewinface.ProfileView;
import com.tencent.qcloud.suixinbo.utils.GlideCircleTransform;
import com.tencent.qcloud.suixinbo.utils.UIUtils;
import com.tencent.qcloud.suixinbo.views.customviews.LineControllerView;
import com.tencent.qcloud.suixinbo.views.customviews.TemplateTitle;

import java.util.List;

/**
 * Created by xkazerzhang on 2016/5/12.
 */
public class EditProfileActivity extends Activity implements View.OnClickListener, ProfileView{
    private final static int REQ_EDIT_NICKNAME = 0x100;
    private final static int REQ_EDIT_SIGN  = 0x200;

    private ProfileInfoHelper profileInfoHelper;
    private String TAG = "EditProfileActivity";
    private ImageView ivIcon;
    private TemplateTitle ttEdit;
    private LineControllerView lcvNickName;
    private LineControllerView lcvSign;

    private void updateView(){
        lcvNickName.setContent(MySelfInfo.getInstance().getNickName());
        lcvSign.setContent(MySelfInfo.getInstance().getSign());
        if (TextUtils.isEmpty(MySelfInfo.getInstance().getAvatar())){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_avatar);
            Bitmap cirBitMap = UIUtils.createCircleImage(bitmap, 0);
            ivIcon.setImageBitmap(cirBitMap);
        }else{
            Log.d(TAG, "profile avator: " + MySelfInfo.getInstance().getAvatar());
            RequestManager req = Glide.with(this);
            req.load(MySelfInfo.getInstance().getAvatar()).transform(new GlideCircleTransform(this)).into(ivIcon);
        }
    }

    private void initView(){
        ttEdit = (TemplateTitle) findViewById(R.id.tt_edit);
        ivIcon = (ImageView) findViewById(R.id.iv_ep_icon);
        lcvNickName = (LineControllerView) findViewById(R.id.lcv_ep_nickname);
        lcvSign = (LineControllerView) findViewById(R.id.lcv_ep_sign);

        ttEdit.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        updateView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initView();

        profileInfoHelper = new ProfileInfoHelper(this);

        QavsdkApplication.getInstance().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        QavsdkApplication.getInstance().removeActivity(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        case R.id.lcv_ep_nickname:
            EditActivity.navToEdit(this, getString(R.string.profile_nickname), MySelfInfo.getInstance().getNickName(), REQ_EDIT_NICKNAME);
            break;
        case R.id.lcv_ep_sign:
            EditActivity.navToEdit(this, getString(R.string.profile_sign), MySelfInfo.getInstance().getSign(), REQ_EDIT_SIGN);
            break;
        default:
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK){
            Log.e(TAG, "onActivityResult->failed for request: " + requestCode + "/" + resultCode);
            return;
        }
        switch (requestCode){
        case REQ_EDIT_NICKNAME:
            profileInfoHelper.setMyNickName(data.getStringExtra(EditActivity.RETURN_EXTRA));
            break;
        case REQ_EDIT_SIGN:
            profileInfoHelper.setMySign(data.getStringExtra(EditActivity.RETURN_EXTRA));
            break;
        }
    }

    @Override
    public void updateProfileInfo(TIMUserProfile profile) {
        if (TextUtils.isEmpty(profile.getNickName())){
            MySelfInfo.getInstance().setNickName(profile.getIdentifier());
        }else{
            MySelfInfo.getInstance().setNickName(profile.getNickName());
        }
        MySelfInfo.getInstance().setSign(profile.getSelfSignature());
        MySelfInfo.getInstance().setAvatar(profile.getFaceUrl());
        updateView();
    }

    @Override
    public void updateUserInfo(int reqid, List<TIMUserProfile> profiles) {
    }
}
