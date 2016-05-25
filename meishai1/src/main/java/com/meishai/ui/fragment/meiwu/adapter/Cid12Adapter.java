package com.meishai.ui.fragment.meiwu.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.entiy.MeiwuCid12RespData;
import com.meishai.entiy.StratDetailRespData;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.SkipUtils;

/**
 * 文件名：Cid12Adapter
 * 描    述：美物 当cid为12时的适配器 (每日一色)
 * 作    者：
 * 时    间：2016/1/20
 * 版    权：
 */
public class Cid12Adapter extends MeiwuAdapter {


    private MeiwuCid12RespData mData;

    public Cid12Adapter(Activity context, ImageLoader imageLoader) {
        super(context, imageLoader);
    }

    @Override
    protected void initData(String data) {
        if (!TextUtils.isEmpty(data)) {
            MeiwuCid12RespData resqData = GsonHelper.parseObject(data,
                    MeiwuCid12RespData.class);
            if (resqData == null) {
                AndroidUtil.showToast("数据解析错误!");
                DebugLog.w("数据解析错误:" + data);
                return;
            }
            if (resqData.page < 1) {
                return;
            } else if (resqData.page == 1) {
                mData = resqData;
            } else {
                if (mData == null || mData.list == null) {
                    return;
                }
                if (!mData.list.containsAll(resqData.list)) {
                    mData.list.addAll(resqData.list);
                }
                mData.page = resqData.page;
            }
        }
    }

    @Override
    public int getCount() {
        if (mData == null || mData.list == null || mData.list.isEmpty()) {
            return 0;
        }
        return mData.list.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MeiwuCid12RespData.ColorCate item = (MeiwuCid12RespData.ColorCate) getItem(position);
        MyHolder holder;
        if (convertView == null) {
            holder = new MyHolder();
            convertView = View.inflate(mContext, R.layout.view_cid12, null);
            holder.colorText = (TextView) convertView.findViewById(R.id.cid12_color_text);
            holder.reviewNum = (TextView) convertView.findViewById(R.id.cid12_review_num);
            holder.likeNum = (TextView) convertView.findViewById(R.id.cid12_like_num);
            holder.pics = (LinearLayout) convertView.findViewById(R.id.cid12_pics);
            convertView.setTag(holder);

        } else {
            holder = (MyHolder) convertView.getTag();
        }
        holder.mColorCate = item;

        holder.colorText.setText(item.title);
        holder.reviewNum.setText(item.view_num + "");
        holder.likeNum.setText(item.follow_num + "");

        initPics(holder.pics, item);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 根据typeID跳转到对应的页面
                MyHolder holder1 = (MyHolder) v.getTag();
                SkipUtils.skipMeiwu(mContext, holder1.mColorCate.tempid, holder1.mColorCate.tid);
            }
        });


        return convertView;
    }

    private void initPics(LinearLayout pics, MeiwuCid12RespData.ColorCate item) {
        if (item == null || item.pics == null) return;

        pics.removeAllViews();
        int margin = AndroidUtil.dip2px(5);
        //图片的宽高 = 屏幕宽度 - 间隔
        int width = (mScreenWidth - 5 * margin) / 4;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, width);
        lp.setMargins(0, 0, margin, 0);
        for (int i = 0; i < item.pics.size(); i++) {
            StratDetailRespData.Picture pic = item.pics.get(i);

            ImageView imageView = new ImageView(mContext);
            imageView.setLayoutParams(lp);
            imageView.setPadding(1, 1, 1, 1);
            imageView.setBackgroundResource(R.drawable.border_f7f7f7);
            imageView.setTag(pic.pic_path);
            ListImageListener listener = new ListImageListener(imageView, R.drawable.place_default, R.drawable.place_default, pic.pic_path);
            mImageLoader.get(pic.pic_path, listener);

            pics.addView(imageView);
        }
    }

    class MyHolder {
        TextView colorText;
        TextView reviewNum;
        TextView likeNum;
        LinearLayout pics;
        MeiwuCid12RespData.ColorCate mColorCate;
    }

}
