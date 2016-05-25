package com.meishai.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.album.imageloader.ImageChooseActivity;
import com.meishai.R;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.util.AndroidUtil;

/**
 * @ClassName: CameraDialog
 * @Description: 拍照Dialog
 */
public class CameraDialog extends Dialog {

    private Context mContext;
    private Button mBtnCamera;
    private Button mBtnAlbum;
    private Button mBtnCancel;
    // 最大选择图片
    private int maxSelected = 1;

    private String actionName = ConstantSet.ACTION_NAME;

    public CameraDialog(Context context) {
        super(context, R.style.dialog_transparent);
        this.mContext = context;
    }

    public void setMaxSelected(int maxSelected) {
        this.maxSelected = maxSelected;
    }

    public CameraDialog(Context context, int theme) {
        super(context, R.style.dialog_transparent);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_camera);
        this.initView();
        this.setListener();
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    private void initView() {
        mBtnCamera = (Button) this.findViewById(R.id.btn_camera);
        mBtnAlbum = (Button) this.findViewById(R.id.btn_album);
        mBtnCancel = (Button) this.findViewById(R.id.btn_cancel);
    }

    private void setListener() {
        mBtnCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                takeCamera();
                hide();
            }
        });
        mBtnAlbum.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getContext().startActivity(
                        ImageChooseActivity.newIntent(actionName, maxSelected));
                hide();
            }
        });
        mBtnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hide();
            }
        });
    }

    @Override
    public void show() {
        super.show();
    }

    public void showDialog() {
        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
        this.show();
    }

    private Uri photoUri;

    public Uri getPhotoUri() {
        return photoUri;
    }

    /**
     * 拍照获取图片
     */
    private void takeCamera() {
        // 执行拍照前，应该先判断SD卡是否存在
        String SDState = Environment.getExternalStorageState();
        if (SDState.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// "android.media.action.IMAGE_CAPTURE"
            /***
             * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
             * 如果不实用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
             */
            ContentValues values = new ContentValues();
            photoUri = mContext.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
            ((Activity) mContext).startActivityForResult(intent,
                    ConstantSet.SELECT_PIC_BY_TACK_PHOTO);
        } else {
            AndroidUtil.showToast("内存卡不存在");
        }

    }
}