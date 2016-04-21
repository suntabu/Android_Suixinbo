package com.tencent.qcloud.suixinbo.views;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tencent.qcloud.suixinbo.R;

/**
 * Created by admin on 16/4/21.
 */
public class CreateLiveActivity extends Activity implements View.OnClickListener{
    TextView mBtnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_create);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                finish();
                break;
        }
    }
}
