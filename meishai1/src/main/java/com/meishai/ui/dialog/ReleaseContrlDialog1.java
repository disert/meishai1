package com.meishai.ui.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.gallery.GalleryAnimationActivity;
import com.meishai.entiy.ReleaseData;
import com.meishai.lib.photoview.AnimationRect;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageRequest;
import com.meishai.ui.fragment.camera.CameraActivity1;
import com.meishai.ui.fragment.camera.ImageChooseActivity1;
import com.meishai.ui.fragment.camera.ImageEditorActivity;
import com.meishai.ui.fragment.camera.PreviewActivity;
import com.meishai.ui.fragment.camera.ReleaseActivity1;
import com.meishai.util.AndroidUtil;
import com.meishai.util.ComUtils;
import com.meishai.util.ImageUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * 发布界面-点击图片弹出的操作对话框
 *
 * @author Administrator
 */
public class ReleaseContrlDialog1 extends Dialog {

    private ReleaseActivity1 mContext;
    private Button mBtnDelete;
    private Button mBtnEdit;
    private Button mBtnPreview;
    private Button mBtnCancel;
    private Button mBtnFirst;

    private ReleaseData mReleaseDate;// 图片的集合
    private int mPosition;// 当前点击的图片的位置
    private ViewGroup mPicsContainer;

    public ReleaseContrlDialog1(ReleaseActivity1 context) {
        super(context, R.style.dialog_transparent);
        this.mContext = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_release_contrl1);
        this.initView();
        this.setListener();
    }

    private void initView() {
        mBtnDelete = (Button) this.findViewById(R.id.btn_delete);
        mBtnEdit = (Button) this.findViewById(R.id.btn_edit);
        mBtnPreview = (Button) this.findViewById(R.id.btn_preview);
        mBtnCancel = (Button) this.findViewById(R.id.btn_cancel);
        mBtnFirst = (Button) this.findViewById(R.id.btn_first);

        if (mPosition == 0) {
            mBtnFirst.setVisibility(View.GONE);
        } else {
            mBtnFirst.setVisibility(View.VISIBLE);
        }

    }

    private void setListener() {
        mBtnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 删除图片,从集合中删除数据,更新ui
                mContext.onRemoveClick(mPosition);
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

                hide();
            }
        });
        mBtnPreview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 预览

//				ArrayList<AnimationRect> list = new ArrayList<AnimationRect>();
//				for (int i = 0; i < mPicsContainer.getChildCount(); i++) {
//					ImageView image = (ImageView)mPicsContainer.getChildAt(i);
//					AnimationRect rect = AnimationRect.buildFromImageView(image);
//					list.add(rect);
//				}

                Intent intent = PreviewActivity.newIntent(mReleaseDate.getPaths().get(mPosition));
                getContext().startActivity(intent);

                hide();
            }

        });
        mBtnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hide();
            }
        });

        //设为首图
        mBtnFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.onSetFirstClick(mPosition);
                hide();
            }
        });
    }

    public void show(ReleaseData picsData, ViewGroup picContainer, int index) {
        super.show();
        mPosition = index;
        mPicsContainer = picContainer;
        mReleaseDate = picsData;
    }


    public void setContrlData(ReleaseData picsData, int position) {
        mReleaseDate = picsData;
        mPosition = position;
        //不是第一张就显示设为首图按钮,否则不显示
        if (mBtnFirst != null) {
            if (mPosition == 0) {
                mBtnFirst.setVisibility(View.GONE);
            } else {
                mBtnFirst.setVisibility(View.VISIBLE);
            }
        }
    }

}