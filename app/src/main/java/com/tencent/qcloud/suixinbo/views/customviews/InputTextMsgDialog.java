package com.tencent.qcloud.suixinbo.views.customviews;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;
import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.presenters.LiveHelper;
import com.tencent.qcloud.suixinbo.views.LiveActivity;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;


/**
 * 文本输入框
 */
public class InputTextMsgDialog extends Dialog {
    private TextView confirmBtn;
    private EditText messageTextView;
    private static final String TAG = InputTextMsgDialog.class.getSimpleName();
    private Context mContext;
    private LiveHelper mLiveControlHelper;
    private Activity mVideoPlayActivity;
    private InputMethodManager imm;
    private int mViewPositionY = 0;
    private final String reg ="[`~@#$%^&*()-_+=|{}':;,/.<>￥…（）—【】‘；：”“’。，、]";
    private Pattern pattern = Pattern.compile(reg);

    public InputTextMsgDialog(Context context, int theme, LiveHelper presenter, LiveActivity activity) {
        super(context, theme);
        mContext = context;
        mLiveControlHelper = presenter;
        mVideoPlayActivity = activity;
        setContentView(R.layout.input_text_dialog);
        messageTextView = (EditText) findViewById(R.id.input_message);
        confirmBtn = (TextView) findViewById(R.id.confrim_btn);
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (messageTextView.getText().length() > 0) {
                    /*
                    // 过滤特殊字符和表情
                    Matcher matcher = pattern.matcher(messageTextView.getText());
                    if (matcher.find()){
                        Toast.makeText(mContext, mContext.getString(R.string.common_send_invalid), Toast.LENGTH_LONG).show();
                        return;
                    }
                    */
                    sendText("" + messageTextView.getText());
                    imm.showSoftInput(messageTextView, InputMethodManager.SHOW_FORCED);
                    imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
                    dismiss();
                }else{
                    Toast.makeText(mContext, "input can not be empty!", Toast.LENGTH_LONG).show();
                }

//                if (imm.isActive()) {
//                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//                }
//                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//                imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
//                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        final LinearLayout rldlgview = (LinearLayout)findViewById(R.id.rl_inputdlg_view);
        rldlgview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int location[] = new int[2];
                rldlgview.getLocationOnScreen(location);
                if (mViewPositionY <= location[1]) {
                    mViewPositionY = location[1];
                } else {
                    imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
                    dismiss();
                }
            }
        });
        rldlgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
                dismiss();
            }
        });
    }

    /**
     * add message text
     */
    public void setMessageText(String strInfo){
        messageTextView.setText(strInfo);
        messageTextView.setSelection(strInfo.length());
    }

    @Override
    public void dismiss() {
        super.dismiss();
//        mVideoPlayActivity.refreshViewAfterDialog();
    }

    @Override
    public void cancel() {
        super.cancel();
    }


    private void sendText(String msg) {
        if (msg.length() == 0)
            return;
        try {
            byte[] byte_num = msg.getBytes("utf8");
            if (byte_num.length > 160) {
                Toast.makeText(mContext, "input message too long", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
        TIMMessage Nmsg = new TIMMessage();
        TIMTextElem elem = new TIMTextElem();
        elem.setText(msg);
        if (Nmsg.addElement(elem) != 0) {
            return;
        }
        mLiveControlHelper.sendText(Nmsg);
    }

    @Override
    public void show() {
        super.show();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {
                InputMethodManager inputManager = (InputMethodManager) messageTextView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(messageTextView, 0);
            }

        }, 500);
    }
}
