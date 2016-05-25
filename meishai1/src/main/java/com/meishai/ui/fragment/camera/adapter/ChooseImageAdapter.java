package com.meishai.ui.fragment.camera.adapter;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.album.utils.ImageLoader;
import com.meishai.R;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.camera.ChooseImageActivity;
import com.meishai.util.AndroidUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件名：ChooseImageAdapter
 * 描    述：选择多张图片的适配器
 * 作    者：yl
 * 时    间：2015/12/22
 * 版    权：
 */
public class ChooseImageAdapter extends BaseAdapter {

    private final int TYPE_CAMERA = 0;
    private final int TYPE_PIC = 1;

    private List<String> mData;
    private ChooseImageActivity mContext;

    private int mScreenWidth;
    private int count = 3;// 一行显示的图片数量
    private int maxSelected = 1;// 最大选择图片数量
    private Uri photoUri;
    private ArrayList<String> mSelectedImage;
    private OnSelectedLisntenner mSelectedListener;


    public ChooseImageAdapter(ChooseImageActivity context, int maxSelected) {
        mContext = context;
        this.maxSelected = maxSelected;
        mSelectedImage = new ArrayList<String>();
        DisplayMetrics outMetrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
    }

    public ArrayList<String> getSelectedImage() {
        return mSelectedImage;
    }

    public void setData(List<String> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mData == null || mData.size() == 0) {
            return 1;
        }
        return mData.size() + 1;
    }

    public void setSelectedListener(OnSelectedLisntenner selectedListener) {
        mSelectedListener = selectedListener;
    }

    @Override
    public Object getItem(int position) {
        if (position == 0) {
            return null;
        }
        return mData.get(position - 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_CAMERA;
        } else {
            return TYPE_PIC;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int size = (mScreenWidth - AndroidUtil.dip2px(3) * 3) / count;
        //-------------拍照-------------
        if (getItemViewType(position) == TYPE_CAMERA) {
            ImageView imageView;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.image, null);
                imageView = (ImageView) convertView.findViewById(R.id.image);
                convertView.setTag(imageView);
            } else {
                imageView = (ImageView) convertView.getTag();
            }


            LayoutParams lp = new LayoutParams(size, size);
            convertView.setLayoutParams(lp);

            imageView.setImageResource(R.drawable.camera_pic);
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  打开照相机拍照
                    takeCamera();
                }
            });
            //---------------图片----------------------
        } else {
            Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = View.inflate(mContext, R.layout.grid_item, null);
                holder.iv = (ImageView) convertView.findViewById(R.id.id_item_image);
                holder.ib = (ImageButton) convertView.findViewById(R.id.id_item_select);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            // 设置控件的宽高
            LayoutParams lp = new LayoutParams(size, size);
            convertView.setLayoutParams(lp);


            final String path = (String) getItem(position);
            ImageLoader.getInstance().loadImage(path, holder.iv);
//            new LoadImageTask(mContext,path,holder.iv,3).execute();
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 进入图片编辑页面
                    Holder holder1 = (Holder) v.getTag();
                    // 已经选择过该图片
                    if (mSelectedImage.contains(path)) {
                        mSelectedImage.remove(path);
                        holder1.ib.setImageResource(R.drawable.picture_unselected);
                        holder1.iv.setColorFilter(null);
                    } else {
                        if (mSelectedImage.size() >= maxSelected) {
                            AndroidUtil.showToast("最多选择" + maxSelected + "张");
                            return;
                        }
                        // 未选择该图片
                        mSelectedImage.add(path);
                        holder1.ib.setImageResource(R.drawable.pictures_selected);
                        holder1.iv.setColorFilter(Color.parseColor("#77000000"));
                    }
                    if (null != mSelectedListener) {
                        mSelectedListener.onSelected(path);
                    }

                }
            });
            /**
             * 已经选择过的图片，显示出选择过的效果
             */
            if (mSelectedImage.contains(path)) {
                holder.ib.setImageResource(R.drawable.pictures_selected);
                holder.iv.setColorFilter(Color.parseColor("#77000000"));
            } else {
                holder.ib.setImageResource(R.drawable.picture_unselected);
                holder.iv.setColorFilter(null);
            }
        }
        return convertView;
    }

    class Holder {

        public ImageView iv;
        public ImageButton ib;
    }

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
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            mContext.startActivityForResult(intent,
                    ConstantSet.SELECT_PIC_BY_TACK_PHOTO);
        } else {
            AndroidUtil.showToast("内存卡不存在");
        }
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public interface OnSelectedLisntenner {
        void onSelected(String path);
    }

}
