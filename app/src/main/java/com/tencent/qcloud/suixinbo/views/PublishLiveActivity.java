package com.tencent.qcloud.suixinbo.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.model.LiveRoomInfo;
import com.tencent.qcloud.suixinbo.model.UserInfo;
import com.tencent.qcloud.suixinbo.presenters.PublishHelper;
import com.tencent.qcloud.suixinbo.utils.Constants;

import java.io.File;
import java.io.FileNotFoundException;
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
    private static String IMAGE_FILE_LOCATION = "file:///sdcard/temp.jpg";
    private static final int CAPTURE_IMAGE_CAMERA = 100;
    private static final int IMAGE_STORE = 200;
    private static final String TAG = PublishLiveActivity.class.getSimpleName();

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
        IMAGE_FILE_LOCATION = "file:///sdcard/" + UserInfo.getInstance().getId() + "_cover.jpg";
        fileUri = Uri.parse(IMAGE_FILE_LOCATION);
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
                Intent intent = new Intent(this, LivePlayActivity.class);
                intent.putExtra(Constants.ID_STATUS, Constants.HOST);
                UserInfo.getInstance().setIdStatus(Constants.HOST);
                LiveRoomInfo.getInstance().setHostID(UserInfo.getInstance().getId());
                LiveRoomInfo.getInstance().setRoomNum(UserInfo.getInstance().getMyRoomNum());
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
                File outputImage = new File(Environment.getExternalStorageDirectory(), "willguo.jpg");
                String ss = outputImage.getAbsolutePath();
                Log.i(TAG, "getPicFrom:  "+ss);
                File destDir = new File(Constants.ROOT_DIR);
                if (!destDir.exists()) {
                    destDir.mkdirs();
                }
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //create new Intent
                fileUri = Uri.fromFile(outputImage);
                Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                startActivityForResult(intent_photo, CAPTURE_IMAGE_CAMERA);
                break;
            case IMAGE_STORE:
                Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
                intent_album.setType("image/*");
                intent_album.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent_album, IMAGE_STORE);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_CAMERA) {
            if (resultCode == RESULT_OK) {
//                data.getData()
                cover.setImageURI(fileUri);
            }

        } else if (requestCode == IMAGE_STORE) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                cover.setImageURI(selectedImage);
            }
        }

    }

    /**
     * 把Uri 转换成bitmap
     *
     * @param uri
     * @return
     */
    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            // 先通过getContentResolver方法获得一个ContentResolver实例，
            // 调用openInputStream(Uri)方法获得uri关联的数据流stream
            // 把上一步获得的数据流解析成为bitmap
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }



}
