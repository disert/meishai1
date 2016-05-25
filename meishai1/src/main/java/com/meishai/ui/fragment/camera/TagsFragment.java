package com.meishai.ui.fragment.camera;

import java.net.URI;
import java.net.URISyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.entiy.StickerRespData;
import com.meishai.entiy.StickerRespData.Sticker;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageRequest;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.fragment.camera.req.ReleaseReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;

/**
 * 贴纸fragment
 *
 * @author Administrator
 */
public class TagsFragment extends BaseFragment {
    private LinearLayout mContainer;
    private StickerRespData mRespData;
    private StickerListener stickerListener;


    public void setStickerListener(StickerListener stickerListener) {
        this.stickerListener = stickerListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View convertView = View.inflate(mContext, R.layout.image_editor_fragment, null);
        mContainer = (LinearLayout) convertView.findViewById(R.id.image_edit_container);
        init();
        return convertView;
    }

    private void init() {
        getRequestData();
    }

    private void getRequestData() {
        showProgress("", "正在加载贴纸");
        ReleaseReq.sticker(mContext, new Listener<String>() {


            @Override
            public void onResponse(String response) {
                hideProgress();
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("success");
                    if (success == 1) {//请求数据成功
                        mRespData = GsonHelper.parseObject(response, StickerRespData.class);
                        initData();
                    } else {//请求数据失败
                        AndroidUtil.showToast(R.string.reqDataFailed);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                AndroidUtil.showToast(R.string.reqFailed);
            }
        });
    }

    protected void initData() {
        if (mRespData == null) {
            return;
        }
        mContainer.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        for (final Sticker sticker : mRespData.list) {
            View view = View.inflate(mContext, R.layout.image_edit_fragment_item1, null);
            view.setLayoutParams(lp);
            //绑定view
            ImageView image = (ImageView) view.findViewById(R.id.image_edit_fragment_item_image);
            TextView title = (TextView) view.findViewById(R.id.image_edit_fragment_item_title);
            Holder holder = new Holder();
            holder.iv = image;
            holder.tv = title;
            view.setTag(holder);
            //设置数据
            title.setText(sticker.name);
            image.setTag(sticker.jpg);
            ListImageListener listener = new ListImageListener(image,
                    R.drawable.default_sticker, R.drawable.default_sticker,
                    sticker.jpg);
            getImageLoader().get(sticker.jpg, listener);

            //设置事件
            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 把贴纸贴通过回调,传入给Activity,让其进行操作
                    setselect(v);
                    showProgress("", "贴纸加载中..");
                    getRequestQueue().add(new ImageRequest(sticker.png, new Listener<Bitmap>() {

                        @Override
                        public void onResponse(Bitmap response) {
                            hideProgress();
                            if (response == null) {
                                AndroidUtil.showToast("加载到的贴纸为null");
                                return;
                            }
                            if (stickerListener != null) {
                                stickerListener.controlBitmap(response, sticker.tid);
                            }
                        }
                    }, 0, 0, Bitmap.Config.ARGB_8888, new ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            hideProgress();
                            AndroidUtil.showToast("加载贴纸失败");
                        }
                    }));
                }
            });

            mContainer.addView(view);
        }
    }

    class Holder {
        ImageView iv;
        TextView tv;
    }

    public void setselect(View v) {
        int count = mContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mContainer.getChildAt(i);
            Holder holder = (Holder) child.getTag();

            child.setSelected(false);
            holder.iv.setSelected(false);
            holder.tv.setSelected(false);
        }
        Holder holder = (Holder) v.getTag();
        holder.iv.setSelected(true);
        holder.tv.setSelected(true);
    }

    public void LoadNetworkBitmap(String path) {
        try {
            URI uri = new URI(path);
//			VolleyHelper.getImageLoader(mContext).
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * 贴纸选中的回调,当用户点击贴纸时会调用此接口的方法把需要展示的bitmap交给我们去操作
     *
     * @author bmp 需要用到的贴纸
     */
    public interface StickerListener {
        void controlBitmap(Bitmap bmp, int tid);
    }


}
