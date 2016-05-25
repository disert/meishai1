package com.meishai.ui.fragment.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.ExifInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.ui.base.BaseFragment;
import com.meishai.util.AndroidUtil;

import org.insta.IF1977Filter;
import org.insta.IFAmaroFilter;
import org.insta.IFBrannanFilter;
import org.insta.IFEarlybirdFilter;
import org.insta.IFHefeFilter;
import org.insta.IFHudsonFilter;
import org.insta.IFInkwellFilter;
import org.insta.IFLomofiFilter;
import org.insta.IFLordKelvinFilter;
import org.insta.IFNashvilleFilter;
import org.insta.IFNormalFilter;
import org.insta.IFRiseFilter;
import org.insta.IFSierraFilter;
import org.insta.IFSutroFilter;
import org.insta.IFToasterFilter;
import org.insta.IFValenciaFilter;
import org.insta.IFWaldenFilter;
import org.insta.IFXproIIFilter;
import org.insta.InstaFilter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 滤镜fragment 实现方式2 通过openGl对图片进行渲染 效果好 目前来看,内存中只能存在一个实例 界面有所改动
 * <p/>
 * 2015年12月24日14:29:55
 *
 * @author Administrator
 */
public class FilterFragment2 extends BaseFragment {

    private LinearLayout mContainer;
    private List<FilterInfo> filterArray = new ArrayList<FilterInfo>();
    private Bitmap mBitmap;// 由mPath加载出来的小图
    private String mPath;// 要进行滤镜操作的图片的地址

    private int mDip;
    private int mSampleSize;

    private OnFilterSelectedListener filterListener;
    private Options mOptions;

//    private GPUImageView gpuImageView;

    public FilterFragment2() {
        mDip = AndroidUtil.dip2px(100);
    }

    public void setFilterListener(OnFilterSelectedListener filterListener) {
        this.filterListener = filterListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View convertView = View.inflate(mContext,
                R.layout.image_editor_fragment, null);
        mContainer = (LinearLayout) convertView
                .findViewById(R.id.image_edit_container);
        init();
        initFilterUI();
        return convertView;
    }

    /**
     * 初始化图片的
     */
    private void initBitmapData() {

        ExifInterface exif;
        try {
            if (!TextUtils.isEmpty(mPath)) {
                exif = new ExifInterface(mPath);
                int bitWidth = exif.getAttributeInt(
                        ExifInterface.TAG_IMAGE_WIDTH, 0);
                int bitHeight = exif.getAttributeInt(
                        ExifInterface.TAG_IMAGE_LENGTH, 0);
                mSampleSize = 0;
                if (bitWidth > bitHeight) {
                    mSampleSize = bitHeight / mDip;
                } else if (bitHeight > bitWidth) {
                    mSampleSize = bitWidth / mDip;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mOptions = new Options();
        // 内存优化
        if (mSampleSize == 0) {
            mSampleSize = 6;
        }
        mOptions.inSampleSize = mSampleSize;// 只加载缩略图
//		mOptions.inPurgeable = true;// 当内存不足时,可以被回收
    }

    private void init() {


        Context context = getActivity();
        filterArray.add(new FilterInfo("原图", R.drawable.filter_normal));
        filterArray.add(new FilterInfo("现代", R.drawable.filter_amaro));
        filterArray.add(new FilterInfo("绚丽", R.drawable.filter_rise));
        filterArray.add(new FilterInfo("时光", R.drawable.filter_hudson));
        filterArray.add(new FilterInfo("清逸", R.drawable.filter_xproii));
        filterArray.add(new FilterInfo("森系", R.drawable.filter_sierra));
        filterArray.add(new FilterInfo("蓝韵", R.drawable.filter_lomofi));
        filterArray.add(new FilterInfo("胶片", R.drawable.filter_earlybird));
        filterArray.add(new FilterInfo("夜晚", R.drawable.filter_sutro));
        filterArray.add(new FilterInfo("甜美", R.drawable.filter_toaster));
        filterArray.add(new FilterInfo("优雅", R.drawable.filter_brannan));
        filterArray.add(new FilterInfo("黑白", R.drawable.filter_inkwell));
        filterArray.add(new FilterInfo("美白", R.drawable.filter_walden));
        filterArray.add(new FilterInfo("静谧", R.drawable.filter_hefe));
        filterArray.add(new FilterInfo("晨光", R.drawable.filter_valencia));
        filterArray.add(new FilterInfo("流年", R.drawable.filter_nashville));
        filterArray.add(new FilterInfo("黄昏", R.drawable.filter_1977));
        filterArray.add(new FilterInfo("小清新", R.drawable.filter_lordkel));


//        gpuImageView = new GPUImageView(context);
//        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(mDip,mDip);
//        gpuImageView.setLayoutParams(layoutParams);
//        if(mBitmap != null){
//            gpuImageView.setImage(mBitmap);
//        }

    }

    private void initFilterUI() {

        mContainer.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
//		for (FilterInfo info : filterArray) {
        for (int i = 0; i < 18; i++) {
            View view = View.inflate(mContext,
                    R.layout.image_edit_fragment_item, null);
            view.setLayoutParams(lp);
            // 绑定view
            ImageView image = (ImageView) view
                    .findViewById(R.id.image_edit_fragment_item_image);
            LinearLayout imageContainer = (LinearLayout) view
                    .findViewById(R.id.image_edit_fragment_item_image_container);
            TextView title = (TextView) view
                    .findViewById(R.id.image_edit_fragment_item_title);
            Holder holder = new Holder();
            holder.iv = image;
            holder.tv = title;
            holder.ll = imageContainer;
            view.setTag(holder);
            // 设置数据
            title.setText(filterArray.get(i).title);
            //设置滤镜效果的图片
//			if(mBitmap != null)
            holder.iv.setImageResource(filterArray.get(i).filterid);
//            holder.iv.setFilter(getFilter(i,getActivity()));
            final int index = i;
//            new processImageTask(index,holder).execute();
            // 设置事件
            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    setselect(v);
//					FilterInfo info = (FilterInfo)v.getTag(2);
                    //  把选中的滤镜效果交给主页,由主页去设置
                    if (filterListener != null)
                        filterListener.SelectedFilter(index, v);
                }
            });

            mContainer.addView(view);

        }
    }

    class Holder {
        ImageView iv;
        TextView tv;
        LinearLayout ll;
    }

    public void setselect(View v) {
        if (mContainer == null) {
            return;
        }
        int count = mContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mContainer.getChildAt(i);
            Holder holder = (Holder) child.getTag();

            child.setSelected(false);
            holder.iv.setSelected(false);
            holder.tv.setSelected(false);
            holder.ll.setSelected(false);
        }
        if (v == null) {
            v = mContainer.getChildAt(0);
        }
        Holder holder = (Holder) v.getTag();
        holder.iv.setSelected(true);
        holder.tv.setSelected(true);
        holder.ll.setSelected(true);
    }


    public class FilterInfo {
        public int filterid;
        private String title;

        public FilterInfo(String title, int filterid) {
            this.title = title;
            this.filterid = filterid;
        }
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
//        gpuImageView.setImage(mBitmap);
    }

    public void setPath(String path) {
        mPath = path;
        initBitmapData();
        if (!TextUtils.isEmpty(mPath)) {
            try {
                mBitmap = BitmapFactory.decodeStream(
                        new FileInputStream(mPath), null, mOptions);

//                gpuImageView.setImage(mBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }


    private InstaFilter getFilter(int index, Context context) {
        InstaFilter filter = null;
        switch (index) {
            case 0:
                filter = new IFNormalFilter(context);
                break;
            case 1:
                filter = new IFAmaroFilter(context);
                break;
            case 2:
                filter = new IFRiseFilter(context);
                break;
            case 3:
                filter = new IFHudsonFilter(context);
                break;
            case 4:
                filter = new IFXproIIFilter(context);
                break;
            case 5:
                filter = new IFSierraFilter(context);
                break;
            case 6:
                filter = new IFLomofiFilter(context);
                break;
            case 7:
                filter = new IFEarlybirdFilter(context);
                break;
            case 8:
                filter = new IFSutroFilter(context);
                break;
            case 9:
                filter = new IFToasterFilter(context);
                break;
            case 10:
                filter = new IFBrannanFilter(context);
                break;
            case 11:
                filter = new IFInkwellFilter(context);
                break;
            case 12:
                filter = new IFWaldenFilter(context);
                break;
            case 13:
                filter = new IFHefeFilter(context);
                break;
            case 14:
                filter = new IFValenciaFilter(context);
                break;
            case 15:
                filter = new IFNashvilleFilter(context);
                break;
            case 16:
                filter = new IF1977Filter(context);
                break;
            case 17:
                filter = new IFLordKelvinFilter(context);
                break;
        }
        return filter;
    }

//    public class processImageTask extends AsyncTask<Void, Void, Bitmap> {
//		private int index;
//        private Holder holder;
//
//		public processImageTask(int index,Holder holder) {
//			this.index = index;
//            this.holder = holder;
//		}
//
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//            gpuImageView.setFilter(getFilter(index, getActivity()));
//
//		}
//
//		public Bitmap doInBackground(Void... params) {
//            Bitmap bitmap = null;
//            try {
    //会oom
//                int dip = AndroidUtil.dip2px(50);
//                bitmap = gpuImageView.capture(dip,dip);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return bitmap;
//		}
//
//		@Override
//		protected void onPostExecute(Bitmap result) {
//
//			hideProgress();
//			if (result != null) {
//				super.onPostExecute(result);
//				holder.iv.setImageBitmap(result);
//			}
//		}
//	}

    /**
     * 滤镜选择的监听器
     */
    public interface OnFilterSelectedListener {
        /**
         * 当选择了某个滤镜是调用的方法
         *
         * @param index 选择的滤镜的信息
         * @param view  选择的滤镜的view
         */
        void SelectedFilter(int index, View view);
    }
}
