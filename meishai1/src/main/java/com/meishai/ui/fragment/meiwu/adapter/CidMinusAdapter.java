package com.meishai.ui.fragment.meiwu.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.meishai.app.widget.layout.SpecailItem;
import com.meishai.entiy.CateInfo;
import com.meishai.entiy.MeiwuCidMinusRespData;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件名：CidMinusAdapter
 * 描    述：美物当cid是-1时的适配器 (专辑)
 * 作    者：
 * 时    间：2016/1/20
 * 版    权：
 */
public class CidMinusAdapter extends MeiwuAdapter {

    private MeiwuCidMinusRespData mData;

    public CidMinusAdapter(Activity context, ImageLoader imageLoader) {
        super(context, imageLoader);
    }

    @Override
    protected void initData(String data) {
        if (!TextUtils.isEmpty(data)) {
            MeiwuCidMinusRespData resqData = GsonHelper.parseObject(data,
                    MeiwuCidMinusRespData.class);
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
        return (mData.list.size() + 1) / 2;
    }

    @Override
    public Object getItem(int position) {
        int startPosition = position * 2;
        int endPosition = startPosition + 1;
        List<CateInfo> item = new ArrayList<CateInfo>();
        item.add(mData.list.get(startPosition));
        if (endPosition < mData.list.size()) {
            item.add(mData.list.get(endPosition));
        }

        return item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        List<CateInfo> item = (List<CateInfo>) getItem(position);
        if (convertView == null) {
            convertView = new SpecailItem(mContext);
        }
        if (item.size() < 2) {
            ((SpecailItem) convertView).setData(item.get(0), null, mImageLoader);
        } else {
            ((SpecailItem) convertView).setData(item.get(0), item.get(1), mImageLoader);

        }

        return convertView;
    }

}
