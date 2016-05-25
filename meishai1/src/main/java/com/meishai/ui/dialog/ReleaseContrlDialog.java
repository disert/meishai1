package com.meishai.ui.dialog;

import java.io.File;
import java.util.ArrayList;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.entiy.ReleaseData;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.toolbox.ImageRequest;
import com.meishai.ui.fragment.camera.BitmapUtils;
import com.meishai.ui.fragment.camera.CameraActivity1;
import com.meishai.ui.fragment.camera.ImageChooseActivity1;
import com.meishai.ui.fragment.camera.ImageEditorActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.ComUtils;
import com.meishai.util.ImageUtil;

/**
 * 发布界面-点击图片弹出的操作对话框
 *
 * @author Administrator
 */
public class ReleaseContrlDialog extends Dialog {

    private CameraActivity1 mContext;
    private Button mBtnDelete;
    private Button mBtnEdit;
    private Button mBtnCover;
    private Button mBtnCancel;

    private ReleaseData mReleaseDate;// 图片的集合
    private int mPosition;// 当前点击的图片的位置

    public ReleaseContrlDialog(CameraActivity1 context) {
        super(context, R.style.dialog_transparent);
        this.mContext = context;
    }

    public ReleaseContrlDialog(CameraActivity1 context, int theme) {
        super(context, R.style.dialog_transparent);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_release_contrl);
        this.initView();
        this.setListener();
    }

    private void initView() {
        mBtnDelete = (Button) this.findViewById(R.id.btn_delete);
        mBtnEdit = (Button) this.findViewById(R.id.btn_edit);
        mBtnCover = (Button) this.findViewById(R.id.btn_cover);
        mBtnCancel = (Button) this.findViewById(R.id.btn_cancel);

    }

    private void setListener() {
        mBtnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 删除图片,从集合中删除数据,更新ui
                mReleaseDate.getPaths().remove(mPosition);
                mContext.initPic(mReleaseDate.getPaths());
                hide();
            }
        });
        mBtnEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String path = mReleaseDate.getPaths().get(mPosition);
                // 编辑图片,启动图片编辑界面,等待返回结果,如果是网络图片还得先保存在本地
                if (TextUtils.isEmpty(path)) {
                    throw new RuntimeException("图片地址为空");
                }
                if (path.startsWith(CameraActivity1.NETWORKPIC)) {
                    //  网络图片 把图片保存到本地
                    GlobalContext.getInstance().getRequestQueue()
                            .add(new ImageRequest(
                                    path,
                                    new Listener<Bitmap>() {
                                        @Override
                                        public void onResponse(Bitmap response) {
                                            if (response == null) {
                                                AndroidUtil
                                                        .showToast("返回图片为空!");
                                                return;
                                            }
                                            String path = ComUtils
                                                    .getRandomAlphamericStr(8)
                                                    + ".jpg";
                                            File f = new File(mContext
                                                    .getCacheDir(), path);
                                            path = f.getAbsolutePath();
                                            ImageUtil
                                                    .saveBitMap(path, response);
                                            Intent intent = ImageEditorActivity
                                                    .newIntent(path,
                                                            mReleaseDate.getOper());
                                            mContext.startActivityForResult(
                                                    intent,
                                                    mContext.REQUESTCODE_MOD);
                                        }
                                    }, 0, 0, Bitmap.Config.ARGB_8888,
                                    new ErrorListener() {

                                        @Override
                                        public void onErrorResponse(
                                                VolleyError error) {
                                            AndroidUtil.showToast("编辑图片下载失败");
                                        }
                                    }));
                } else {// 本地图片
                    Intent intent = ImageEditorActivity.newIntent(mReleaseDate
                            .getPaths().get(mPosition), mReleaseDate.getOper());
                    mContext.startActivityForResult(intent,
                            mContext.REQUESTCODE_MOD);
                }
                hide();
            }
        });
        mBtnCover.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 设为封面,改变集合数据,更新UI
                if (mPosition == 0) {// 更换封面
                    Intent intent1 = ImageChooseActivity1.newIntent(mReleaseDate,
                            mContext.MOD_COVER);
                    mContext.startActivity(intent1);
                    mContext.finish();
                } else {// 设为封面
                    String remove = mReleaseDate.getPaths().remove(mPosition);
                    mReleaseDate.getPaths().add(0, remove);
                    mContext.initPic(mReleaseDate.getPaths());
                }
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

    public void show(int index) {
        super.show();
        mPosition = index;
        if (mPosition == 0) {// 封面图片
            mBtnDelete.setVisibility(View.GONE);
            mBtnEdit.setText("编辑封面");
            mBtnCover.setText("更换封面");
        } else {// 普通图片
            mBtnDelete.setVisibility(View.VISIBLE);
            mBtnEdit.setText("编辑图片");
            mBtnCover.setText("设为封面");
        }
    }

    public void showDialog() {
        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
        this.show();
    }

    public void setContrlData(ReleaseData picsData, int position) {
        mReleaseDate = picsData;
        mPosition = position;
    }

}