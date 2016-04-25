package com.tencent.qcloud.suixinbo.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tencent.qcloud.suixinbo.QavsdkApplication;
import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.presenters.LoginHeloper;
import com.tencent.qcloud.suixinbo.presenters.viewinface.LoginView;

/**
 * 注册账号类
 */
public class RegisterActivity extends Activity implements View.OnClickListener , LoginView {
    private EditText mUserName, mPassword, mRepassword;
    private TextView mBtnRegister;
    private ImageButton mBtnBack;
    QavsdkApplication mMyApplication;
    LoginHeloper mLoginHeloper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_independent_register);
        mUserName = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mRepassword = (EditText) findViewById(R.id.repassword);
        mBtnRegister = (TextView) findViewById(R.id.btn_register);
        mBtnBack = (ImageButton)findViewById(R.id.back);
        mBtnBack.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);
        mMyApplication =(QavsdkApplication)getApplication();
        mLoginHeloper = new LoginHeloper(this,this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_register) {
            //注册一个账号
            mLoginHeloper.registerTLS(mUserName.getText().toString(),mPassword.getText().toString());
        }
        if(view.getId() == R.id.back){
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void LoginSucc() {
        jumpIntoHomeActivity();
    }

    @Override
    public void LoginFail() {

    }

    /**
     * 直接跳转主界面
     */
    private void jumpIntoHomeActivity(){
        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

}
