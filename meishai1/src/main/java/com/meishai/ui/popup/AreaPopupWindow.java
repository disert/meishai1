package com.meishai.ui.popup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.meishai.R;
import com.meishai.app.util.ScreenUtils;
import com.meishai.app.widget.wheel.AreaWheel;
import com.meishai.app.widget.wheel.AreaWheel.OnAreaChangeListener;
import com.meishai.entiy.Area;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.fragment.common.req.PublicReq;
import com.meishai.util.AndroidUtil;

/**
 * @author sh
 */
public class AreaPopupWindow extends PopupWindow {
    private View mPopView;
    final View areaView;
    private LinearLayout pop_layout;
    private Context mContext;
    private AreaWheel areaWheel;
    private OnAreaChangeListener areaChangeListener;

    public AreaPopupWindow(Context context,
                           OnAreaChangeListener areaChangeListener) {
        super(context);
        mContext = context;
        this.areaChangeListener = areaChangeListener;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPopView = inflater.inflate(R.layout.popup_area, null);
        areaView = inflater.inflate(R.layout.areawheel, null);
        this.setContentView(mPopView);
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.MATCH_PARENT);
        this.init();
        this.initData();
        this.initViews();
        mPopView.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                int height = mPopView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismissPop();
                    }
                }
                return true;
            }
        });
    }

    private void init() {
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // this.setAnimationStyle(R.style.popupAnimation);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
    }

    private void initData() {
        Map<String, String> data = new HashMap<String, String>();
        data.put("areaid", "0");
        PublicReq.area(mContext, data, new Listener<RespData>() {
            @Override
            public void onResponse(RespData response) {
                if (response.isSuccess()) {
                    List<Area> resultAreas = response.getArea();
                    if (null != resultAreas && !resultAreas.isEmpty()) {
                        updateAreaWheel(resultAreas);
                    }
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    private void initViews() {
        pop_layout = (LinearLayout) mPopView.findViewById(R.id.pop_layout);
    }

    public void updateAreaWheel(List<Area> areas) {
        areaWheel = new AreaWheel(mContext, areaView);
        areaWheel.screenheight = ScreenUtils.getScreenHeight(mContext);
        areaWheel.setAreaChangeListener(areaChangeListener);
        areaWheel.setAreas(areas);
        areaWheel.initAreaPicker();
        pop_layout.addView(areaView);
    }

    public void showPop(View v) {
        if (!isShowing()) {
            this.showAtLocation(v, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL,
                    0, 0);
        }
    }

    private void dismissPop() {
        if (isShowing()) {
            this.dismiss();
        }
    }

}
