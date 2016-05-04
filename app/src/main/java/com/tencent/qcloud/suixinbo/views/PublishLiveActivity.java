package com.tencent.qcloud.suixinbo.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.model.MyCurrentLiveInfo;
import com.tencent.qcloud.suixinbo.model.MySelfInfo;
import com.tencent.qcloud.suixinbo.presenters.PublishHelper;
import com.tencent.qcloud.suixinbo.utils.Constants;

import java.io.File;
import java.io.IOException;

/**
 * Created by admin on 16/4/21.
 */
public class PublishLiveActivity extends Activity implements View.OnClickListener {
    private PublishHelper mPublishLivePresenter;
    private TextView BtnBack, BtnPublish;
    private Dialog mPicChsDialog;
    private ImageView cover;
    private Uri fileUri;
    private static final int CAPTURE_IMAGE_CAMERA = 100;
    private static final int IMAGE_STORE = 200;
    private static final String TAG = PublishLiveActivity.class.getSimpleName();

    private static final int CROP_CHOOSE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_publish);
        mPublishLivePresenter = new PublishHelper(this);
        BtnBack = (TextView) findViewById(R.id.btn_cancel);
        BtnPublish = (TextView) findViewById(R.id.btn_publish);
        cover = (ImageView) findViewById(R.id.cover);
        cover.setOnClickListener(this);
        BtnBack.setOnClickListener(this);
        BtnPublish.setOnClickListener(this);

        initExitDialog();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_publish:
                mPublishLivePresenter.uploadCover();
                Intent intent = new Intent(this, LiveActivity.class);
                intent.putExtra(Constants.ID_STATUS, Constants.HOST);
                MySelfInfo.getInstance().setIdStatus(Constants.HOST);
                MyCurrentLiveInfo.setHostID(MySelfInfo.getInstance().getId());
                MyCurrentLiveInfo.setRoomNum(MySelfInfo.getInstance().getMyRoomNum());
                startActivity(intent);
                break;
            case R.id.cover:
                mPicChsDialog.show();
                break;
        }
    }

    /**
     * 图片选择对话框
     */
    private void initExitDialog() {
        mPicChsDialog = new Dialog(this, R.style.dialog);
        mPicChsDialog.setContentView(R.layout.pic_choose);
        TextView camera = (TextView) mPicChsDialog.findViewById(R.id.chos_camera);
        TextView picLib = (TextView) mPicChsDialog.findViewById(R.id.pic_lib);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPicFrom(CAPTURE_IMAGE_CAMERA);
                mPicChsDialog.dismiss();
            }
        });

        picLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPicFrom(IMAGE_STORE);
                mPicChsDialog.dismiss();
            }
        });
    }


    /**
     * 获取图片资源
     *
     * @param type
     */
    private void getPicFrom(int type) {
        switch (type) {
            case CAPTURE_IMAGE_CAMERA:
                Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, createCoverUri());
                startActivityForResult(intent_photo, CAPTURE_IMAGE_CAMERA);
                break;
            case IMAGE_STORE:
                Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
                intent_album.setType("image/*");
                startActivityForResult(intent_album, IMAGE_STORE);
                break;

        }
    }

    private Uri createCoverUri() {
        String filename = MySelfInfo.getInstance().getId() + ".jpg";
        File outputImage = new File(Environment.getExternalStorageDirectory(), filename);
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileUri = Uri.fromFile(outputImage);
        return fileUri;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAPTURE_IMAGE_CAMERA:
                    startPhotoZoom(fileUri);
                    break;
                case IMAGE_STORE:
                    startPhotoZoom(data.getData());
                    break;
                case CROP_CHOOSE:
                    cover.setImageURI(data.getData());
                    break;

            }
        }

    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
//        intent.putExtra("return-data", true);
//        intent.putExtra("output", fileUri);
        startActivityForResult(intent, CROP_CHOOSE);
    }


}
