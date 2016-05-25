package com.meishai.ui.popup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.meishai.R;

/**
 * @author sh
 */
public class TrialFilterPopupWindow extends PopupWindow {
    private View mPopView;
    private Context mContext;

    // 进行中
    private LinearLayout lay_ing;
    // 订单待提交
    private LinearLayout lay_dtj;
    // 报告待提交
    private LinearLayout lay_bgdtj;
    // 担保金待申请
    private LinearLayout lay_dbj;
    // 取消
    private LinearLayout lay_cacel;
    private OnClickListener clickListener;

    public TrialFilterPopupWindow(Context context) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPopView = inflater.inflate(R.layout.user_trial_popup, null);
        this.setContentView(mPopView);
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.MATCH_PARENT);
        this.init();
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

    public void setClickListener(OnClickListener clickListener) {
        this.clickListener = clickListener;
        initListener();
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

    private void initViews() {
        lay_ing = (LinearLayout) mPopView.findViewById(R.id.lay_ing);
        lay_dtj = (LinearLayout) mPopView.findViewById(R.id.lay_dtj);
        lay_bgdtj = (LinearLayout) mPopView.findViewById(R.id.lay_bgdtj);
        lay_dbj = (LinearLayout) mPopView.findViewById(R.id.lay_dbj);
        lay_cacel = (LinearLayout) mPopView.findViewById(R.id.lay_cacel);
    }

    private void initListener() {
        if (null != clickListener) {
            StatusOnClickListener sClickListener = new StatusOnClickListener();
            lay_ing.setOnClickListener(sClickListener);
            lay_dtj.setOnClickListener(sClickListener);
            lay_bgdtj.setOnClickListener(sClickListener);
            lay_dbj.setOnClickListener(sClickListener);
            lay_cacel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismissPop();
                }
            });
        }
    }

    class StatusOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            dismissPop();
            if (null != clickListener) {
                clickListener.onClick(v);
            }
        }

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
