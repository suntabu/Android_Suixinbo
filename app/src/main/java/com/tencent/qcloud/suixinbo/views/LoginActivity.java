package com.tencent.qcloud.suixinbo.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.model.MySelfInfo;
import com.tencent.qcloud.suixinbo.presenters.LoginHeloper;
import com.tencent.qcloud.suixinbo.presenters.viewinface.LoginView;

public class LoginActivity extends Activity implements View.OnClickListener, LoginView {
    TextView mBtnLogin, mBtnRegister;
    EditText mPassWord, mUserName;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private LoginHeloper mLoginHeloper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoginHeloper = new LoginHeloper(this, this);
        //获取个人数据本地缓存
        MySelfInfo.getInstance().getCache(getApplicationContext());
        if (needLogin() == true) {//本地没有账户需要登录
            setContentView(R.layout.activity_independent_login);
            mBtnLogin = (TextView) findViewById(R.id.btn_login);
            mUserName = (EditText) findViewById(R.id.username);
            mPassWord = (EditText) findViewById(R.id.password);
            mBtnRegister = (TextView) findViewById(R.id.registerNewUser);
            mBtnRegister.setOnClickListener(this);
            mBtnLogin.setOnClickListener(this);
        } else {
            //有账户登录直接IM登录
            mLoginHeloper.imLogin(MySelfInfo.getInstance().getId(), MySelfInfo.getInstance().getUserSig());
        }


    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.registerNewUser) {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
            finish();
        }
        if (view.getId() == R.id.btn_login) {//登录账号系统TLS
            if (mUserName.getText().equals("")) {
                Toast.makeText(LoginActivity.this, "name can not be empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (mPassWord.getText().equals("")) {
                Toast.makeText(LoginActivity.this, "password can not be empty!", Toast.LENGTH_SHORT).show();
                return;
            }
//            tlsLogin(mUserName.getText().toString(),mPassWord.getText().toString());
            mLoginHeloper.tlsLogin(mUserName.getText().toString(), mPassWord.getText().toString());
        }
    }



    /**
     * 判断是否需要登录
     *
     * @return true 代表需要重新登录
     */
    public boolean needLogin() {
        if (MySelfInfo.getInstance().getId() != null) {
            return false;//有账号不需要登录
        } else {
            return true;//需要登录
        }

    }


    /**
     * 直接跳转主界面
     */
    private void jumpIntoHomeActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void LoginSucc() {
        Toast.makeText(LoginActivity.this, "" + MySelfInfo.getInstance().getId() + " login ", Toast.LENGTH_SHORT).show();
        jumpIntoHomeActivity();
    }

    @Override
    public void LoginFail() {

    }
}
