package com.meishai.ui.fragment.camera.adapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.album.utils.ImageLoader;
import com.meishai.R;
import com.meishai.entiy.ReleaseData;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.camera.BitmapUtils;
import com.meishai.ui.fragment.camera.ClipPictureActivity;
import com.meishai.ui.fragment.camera.CutImageActivity;
import com.meishai.ui.fragment.camera.ImageChooseActivity1;
import com.meishai.util.AndroidUtil;
import com.meishai.util.LoadImageTask;

/**
 * 选择照片页面的适配器
 *
 * @author Administrator yl
 */
public class AlbumAdapter1 extends BaseAdapter {

    private final int TYPE_CAMERA = 0;
    private final int TYPE_PIC = 1;

    private List<String> mData;
    private ImageChooseActivity1 mContext;

    private int mScreenWidth;
    private int count = 3;// 一行显示的图片数量
    private Uri photoUri;
    private ReleaseData mReleaseData;//已经选择的图片,用于传给下一个activity.
    private int mode;//图片的添加模式,启动activity时使用


    public AlbumAdapter1(ImageChooseActivity1 context) {
        mContext = context;

        DisplayMetrics outMetrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
    }


    public void setReleaseDate(ReleaseData releaseData) {
        mReleaseData = releaseData;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setData(List<String> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mData == null || mData.size() == 0) {
            return 0;
        }
        return mData.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        if (position == 0) {
            return null;
        }
        return mData.get(position - 1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
//			convertView = View.inflate(mContext, R.layout.grid_item, null);
            convertView = View.inflate(mContext, R.layout.image, null);
            holder.iv = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
            holder.iv.setImageResource(R.drawable.place_default);
        }

        // 设置控件的宽高
        int size = mScreenWidth / count;
        AbsListView.LayoutParams lp = new LayoutParams(size, size);
        convertView.setLayoutParams(lp);

        if (position == 0) {
            holder.iv.setImageResource(R.drawable.camera_pic);
            holder.iv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    //  打开照相机拍照
                    takeCamera();
                }
            });
        } else {
//			holder.ib.setVisibility(View.VISIBLE);
            final String path = (String) getItem(position);
//			ImageLoader.getInstance().loadImage(path,holder.iv);
            holder.iv.setImageResource(R.drawable.place_default);
            new LoadImageTask(mContext, path, holder.iv, count).execute();
            holder.iv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 进入图片裁剪页面
                    Intent intent2 = CutImageActivity.newIntent(mReleaseData, path, mode);
//					Intent intent2 = ClipPictureActivity.newIntent(mReleaseData, path, mode);
                    mContext.startActivity(intent2);
                    mContext.finish();
                }
            });
        }
        return convertView;
    }

    class Holder {
        public ImageView iv;
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
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
            mContext.startActivityForResult(intent,
                    ConstantSet.SELECT_PIC_BY_TACK_PHOTO);
        } else {
            AndroidUtil.showToast("内存卡不存在");
        }
    }

    public Uri getPhotoUri() {
        return photoUri;
    }


}
