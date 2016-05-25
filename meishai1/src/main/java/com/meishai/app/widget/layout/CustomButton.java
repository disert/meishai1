package com.meishai.app.widget.layout;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;

/**
 * Created by Administrator on 2015/11/25.
 */
public class CustomButton extends LinearLayout {

    private final Context mContext;
    private View mView;
    private ImageView mIcon;
    private TextView mName;
    private LinearLayout mRoot;
    private int orientation;

    public CustomButton(Context context) {
        this(context, null);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {

        mView = View.inflate(mContext, R.layout.custom_button, this);

        mRoot = (LinearLayout) mView.findViewById(R.id.custom_button_root);
        mIcon = (ImageView) mView.findViewById(R.id.custom_button_icon);
        mName = (TextView) mView.findViewById(R.id.custom_button_name);
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
        mRoot.setOrientation(orientation);
    }

    public void setIcon(Bitmap bitmap) {
        mIcon.setImageBitmap(bitmap);
    }

    public void setIcon(int resId) {
        mIcon.setImageResource(resId);
    }

    public void setName(String name) {
        mName.setText(name);
    }

    public void setNameColor(int color) {
        mName.setTextColor(color);
    }

    public void setNameSize(int size) {
        mName.setTextSize(size);
    }

    public void setpadding(int padding) {
        if (orientation == LinearLayout.HORIZONTAL) {
            mName.setPadding(padding, 0, 0, 0);
        } else if (orientation == LinearLayout.VERTICAL) {
            mName.setPadding(0, padding, 0, 0);
        }
    }

//    public void


}
