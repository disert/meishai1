package com.meishai.ui.popup;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.meishai.R;
import com.meishai.entiy.SignPoint;
import com.meishai.net.RespData;
import com.meishai.ui.fragment.meiwu.FindPointActivity;
import com.meishai.ui.popup.adapter.SignGridAdapter;
import com.meishai.util.GsonHelper;

/**
 * 签到
 *
 * @author sh
 */
public class HomeSignPopupWindow extends PopupWindow {
    private Context mContext;
    private View mPopView;
    private TextView today_point, tomorrow_point, text, mypoint;
    private GridView gv_date;
    private Button mCloseBtn, mBtnPoint;
    private SignGridAdapter gridAdapter;
    private List<SignPoint> points = null;

    private RespData respData;

    public HomeSignPopupWindow(Context context) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPopView = inflater.inflate(R.layout.home_sign, null);
        this.setContentView(mPopView);
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.MATCH_PARENT);
        this.init();
        this.initViews();
        this.initOperButton();
        this.initAdapter();
        mPopView.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                int height = mPopView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y > height) {
                        dismissPop();
                    }
                }
                return true;
            }
        });
    }

    private void init() {
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.setAnimationStyle(R.style.popupAnimation);
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
        gv_date = (GridView) mPopView.findViewById(R.id.gv_date);
        today_point = (TextView) mPopView.findViewById(R.id.today_point);
        tomorrow_point = (TextView) mPopView.findViewById(R.id.tomorrow_point);
        text = (TextView) mPopView.findViewById(R.id.text);
        mypoint = (TextView) mPopView.findViewById(R.id.mypoint);
    }

    private void initAdapter() {
        points = new ArrayList<SignPoint>();
        gridAdapter = new SignGridAdapter(mContext, points);
        gv_date.setAdapter(gridAdapter);
    }

    private void notifyAdapter() {
        gridAdapter.setPoints(points);
        gridAdapter.notifyDataSetChanged();
    }

    // private void loadData() {
    // Map<String, String> data = new HashMap<String, String>();
    // data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
    // .getUserID());
    // SignReq.sign(mContext, data, new Listener<RespData>() {
    //
    // @Override
    // public void onResponse(RespData response) {
    // if (response.isSuccess()) {
    // updatePopView(response);
    // Type type = new TypeToken<List<SignPoint>>() {
    // }.getType();
    // List<SignPoint> resultSignPoints = GsonHelper.parseObject(
    // GsonHelper.toJson(response.getData()), type);
    //
    // if (null != resultSignPoints && !resultSignPoints.isEmpty()) {
    // points = resultSignPoints;
    // notifyAdapter();
    // }
    // } else if (response.getSuccess().intValue() == 0) {
    // mContext.startActivity(LoginActivity.newOtherIntent());
    // dismissPop();
    // } else {
    // AndroidUtil.showToast(response.getTips());
    // }
    // }
    // }, new ErrorListener() {
    //
    // @Override
    // public void onErrorResponse(VolleyError error) {
    // AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
    // }
    // });
    // }

    private void updatePopView(RespData response) {
        String ftoday_point = mContext.getString(R.string.sign_today_point);
        today_point.setText(String.format(ftoday_point,
                response.getToday_point()));
        String fsign_tomorrow_point = mContext
                .getString(R.string.sign_tomorrow_point);
        tomorrow_point.setText(String.format(fsign_tomorrow_point,
                response.getTomorrow_point()));
        text.setText(response.getText());
        String fmypoint = mContext.getString(R.string.sign_mypoint);
        mypoint.setText(String.format(fmypoint, response.getMypoint()));
    }

    private void initOperButton() {
        this.mCloseBtn = (Button) mPopView.findViewById(R.id.btn_close);
        this.mCloseBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismissPop();
            }
        });
        this.mBtnPoint = (Button) mPopView.findViewById(R.id.btn_point);
        this.mBtnPoint.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mContext.startActivity(FindPointActivity.newIntent());
                dismissPop();
            }
        });
    }

    public void setRespData(RespData respData) {
        this.respData = respData;
    }

    public RespData getRespData() {
        return respData;
    }

    public void showPop(View v) {
        if (!isShowing()) {
            if (null != respData) {
                updatePopView(respData);
                Type type = new TypeToken<List<SignPoint>>() {
                }.getType();
                List<SignPoint> resultSignPoints = GsonHelper.parseObject(
                        GsonHelper.toJson(respData.getData()), type);
                if (null != resultSignPoints && !resultSignPoints.isEmpty()) {
                    points = resultSignPoints;
                    notifyAdapter();
                }
            }
            // this.loadData();
            this.showAsDropDown(v, 0, 0);
            // this.showAtLocation(v, Gravity.BOTTOM |
            // Gravity.CENTER_HORIZONTAL,
            // 0, 0);
        }
    }

    private void dismissPop() {
        if (isShowing()) {
            this.dismiss();
        }
    }
}
