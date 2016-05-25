package com.album.imageloader;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.album.utils.CommonAdapter;
import com.meishai.R;
import com.meishai.util.AndroidUtil;

public class AlbumAdapter extends CommonAdapter<String> {

    private OnClickListener clickListener = null;

    /**
     * 用户选择的图片，存储为图片的完整路径
     */
    public static List<String> mSelectedImage = new LinkedList<String>();
    /**
     * 最大可以选择数量 默认无上限
     */
    private int maxSelected = Integer.MAX_VALUE;

    /**
     * 文件夹路径
     */
    private String mDirPath;

    public AlbumAdapter(Context context, List<String> mDatas, int itemLayoutId,
                        String dirPath) {
        super(context, mDatas, itemLayoutId);
        this.mDirPath = dirPath;
    }

    public void setClickListener(OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setMaxSelected(int maxSelected) {
        this.maxSelected = maxSelected;
    }

    @Override
    public void convert(final com.album.utils.ViewHolder helper,
                        final String item) {
        // 设置no_pic
        helper.setImageResource(R.id.id_item_image, R.drawable.head_default);
        // 设置no_selected
        helper.setImageResource(R.id.id_item_select,
                R.drawable.picture_unselected);
        // 设置图片
        helper.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);

        final ImageView mImageView = helper.getView(R.id.id_item_image);
        final ImageView mSelect = helper.getView(R.id.id_item_select);

        mImageView.setColorFilter(null);
        // 设置ImageView的点击事件
        mImageView.setOnClickListener(new OnClickListener() {
            // 选择，则将图片变暗，反之则反之
            @Override
            public void onClick(View v) {

                // 已经选择过该图片
                if (mSelectedImage.contains(mDirPath + "/" + item)) {
                    mSelectedImage.remove(mDirPath + "/" + item);
                    mSelect.setImageResource(R.drawable.picture_unselected);
                    mImageView.setColorFilter(null);
                } else {
                    if (mSelectedImage.size() >= maxSelected) {
                        AndroidUtil.showToast("最多选择" + maxSelected + "张");
                        return;
                    }
                    // 未选择该图片
                    mSelectedImage.add(mDirPath + "/" + item);
                    mSelect.setImageResource(R.drawable.pictures_selected);
                    mImageView.setColorFilter(Color.parseColor("#77000000"));
                }
                if (null != clickListener) {
                    clickListener.onClick(v);
                }
            }
        });

        /**
         * 已经选择过的图片，显示出选择过的效果
         */
        if (mSelectedImage.contains(mDirPath + "/" + item)) {
            mSelect.setImageResource(R.drawable.pictures_selected);
            mImageView.setColorFilter(Color.parseColor("#77000000"));
        }

    }
}
