package com.tencent.qcloud.suixinbo.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.qcloud.suixinbo.MyApplication;
import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.presenters.InitBusinessHelper;
import com.tencent.qcloud.suixinbo.presenters.LoginPresenter;
import com.tencent.qcloud.suixinbo.presenters.viewinface.LoginView;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSStrAccRegListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * 注册账号类
 */
public class RegisterActivity extends Activity implements View.OnClickListener , LoginView {
    private EditText mUserName, mPassword, mRepassword;
    private TextView mBtnRegister;
    private ImageButton mBtnBack;
    MyApplication mMyApplication;
    TLSStrAccRegListener mStrAccRegListener;
    LoginPresenter mLoginPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_independent_register);
        mUserName = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mRepassword = (EditText) findViewById(R.id.repassword);
        mBtnRegister = (TextView) findViewById(R.id.btn_register);
        mBtnBack = (ImageButton)findViewById(R.id.returnIndependentLoginActivity);
        mBtnBack.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);
        mMyApplication =(MyApplication)getApplication();
        mStrAccRegListener = new StrAccRegListener();
        mLoginPresenter = new LoginPresenter(this,this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_register) {
                InitBusinessHelper.getmAccountHelper().TLSStrAccReg(mUserName.getText().toString(), mPassword.getText().toString(), mStrAccRegListener);
        }
        if(view.getId() == R.id.returnIndependentLoginActivity){
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

    class StrAccRegListener implements TLSStrAccRegListener {
        @Override
        public void OnStrAccRegSuccess(TLSUserInfo userInfo) {
            Toast.makeText(RegisterActivity.this,  userInfo.identifier+ " register a user succ !  " , Toast.LENGTH_SHORT).show();
            String id = userInfo.identifier;
            //继续登录流程
            mLoginPresenter.tlsLogin(id,mRepassword.getText().toString());
        }

        @Override
        public void OnStrAccRegFail(TLSErrInfo errInfo) {
            Toast.makeText(RegisterActivity.this, ""+errInfo.Msg, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void OnStrAccRegTimeout(TLSErrInfo errInfo) {
            Toast.makeText(RegisterActivity.this, ""+errInfo.Msg, Toast.LENGTH_SHORT).show();
        }
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
