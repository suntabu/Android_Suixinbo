package com.tencent.qcloud.suixinbo.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.model.UserInfo;
import com.tencent.qcloud.suixinbo.presenters.PublishLivePresenter;
import com.tencent.qcloud.suixinbo.utils.Constants;

/**
 * Created by admin on 16/4/21.
 */
public class PublishLiveActivity extends Activity implements View.OnClickListener{
    private PublishLivePresenter mPublishLivePresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_publish);
        mPublishLivePresenter = new PublishLivePresenter(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_publish:
                mPublishLivePresenter.uploadCover();
                Intent intent= new Intent(this,LivePlayActivity.class);
                intent.putExtra(Constants.ID_STATUS,Constants.HOST);
                UserInfo.getInstance().setIdStatus(Constants.HOST);
                startActivity(intent);
                break;
        }

    }
}
