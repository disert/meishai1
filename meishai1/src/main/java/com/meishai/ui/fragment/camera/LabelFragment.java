package com.meishai.ui.fragment.camera;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.ui.base.BaseFragment;

/**
 * 标签fragment,还没做 现在改成了裁剪
 *
 * @author Administrator
 */
public class LabelFragment extends BaseFragment {

    private View convertView;
    private LinearLayout mSquare;
    private TextView mSquareText;
    private ImageView mSquareIcom;

    private LinearLayout mRect;
    private TextView mRectText;
    private ImageView mRectIcon;

    private LinearLayout mRotate;
    private TextView mRotateText;
    private ImageView mRotateIcom;

    //操作类型
    public static final int CTRL_SQUARE = 0;
    public static final int CTRL_ROTATE = 1;
    public static final int CTRL_RECT = 2;

    //裁剪状态的监听器
    private OnCtrlLinstenner mOnCtrlLinstenner;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        convertView = View.inflate(mContext,
                R.layout.fragment_image_crop, null);
        initView();
        initListenner();

        return convertView;
    }


    private void initView() {
        mSquare = (LinearLayout) convertView.findViewById(R.id.fragment_crop_square);
        mSquareText = (TextView) convertView.findViewById(R.id.fragment_crop_square_title);
        mSquareIcom = (ImageView) convertView.findViewById(R.id.fragment_crop_square_image);

        mRect = (LinearLayout) convertView.findViewById(R.id.fragment_crop_rect);
        mRectText = (TextView) convertView.findViewById(R.id.fragment_crop_rect_title);
        mRectIcon = (ImageView) convertView.findViewById(R.id.fragment_crop_rect_image);

        mRotate = (LinearLayout) convertView.findViewById(R.id.fragment_crop_rotate);
        mRotateText = (TextView) convertView.findViewById(R.id.fragment_crop_rotate_title);
        mRotateIcom = (ImageView) convertView.findViewById(R.id.fragment_crop_rotate_image);
    }

    private void initListenner() {
        mSquare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelected(CTRL_SQUARE);
                if (mOnCtrlLinstenner != null) {
                    mOnCtrlLinstenner.ctrlChange(CTRL_SQUARE);
                }
            }
        });
        mRect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelected(CTRL_RECT);
                if (mOnCtrlLinstenner != null) {
                    mOnCtrlLinstenner.ctrlChange(CTRL_RECT);
                }
            }
        });
        mRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnCtrlLinstenner != null) {
                    mOnCtrlLinstenner.ctrlChange(CTRL_ROTATE);
                }
            }
        });
    }

    public void setSelected(int type) {
        if (mRectIcon == null) {
            return;
        }
        if (type == CTRL_RECT) {
            mRectIcon.setSelected(true);
            mRectText.setSelected(true);

            mSquareIcom.setSelected(false);
            mSquareText.setSelected(false);
        } else if (type == CTRL_SQUARE) {
            mSquareIcom.setSelected(true);
            mSquareText.setSelected(true);

            mRectIcon.setSelected(false);
            mRectText.setSelected(false);
        }
    }

    public void setOnCtrlLinstenner(OnCtrlLinstenner onCtrlLinstenner) {
        mOnCtrlLinstenner = onCtrlLinstenner;
    }

    public interface OnCtrlLinstenner {
        void ctrlChange(int ctrlType);
    }

}
