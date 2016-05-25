package com.meishai.ui.popup;

import zhaohg.emojiview.EmojiViewEx;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.meishai.R;
import com.meishai.app.util.KeyBoardUtils;

/**
 * 表情图片
 *
 * @author sh
 */
public class EmojiPopupWindow extends PopupWindow {
    private View mPopView;
    private LinearLayout pop_layout;
    private Context mContext;
    private ImageButton ig_emoji;
    private EmojiViewEx emojiView;
    private ImageButton ig_toggle_input;
    private OnClickListener clickListener;
    private EditText release_edit;

    public EmojiPopupWindow(Context context, EditText release_edit,
                            OnClickListener clickListener) {
        super(context);
        mContext = context;
        this.clickListener = clickListener;
        this.release_edit = release_edit;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPopView = inflater.inflate(R.layout.popup_emoji, null);
        this.setContentView(mPopView);
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.WRAP_CONTENT);
        this.init();
        this.initViews();
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
        pop_layout = (LinearLayout) mPopView.findViewById(R.id.pop_layout);
        emojiView = (EmojiViewEx) mPopView.findViewById(R.id.emojiView);
        emojiView.setEditText(release_edit);
        ig_emoji = (ImageButton) mPopView.findViewById(R.id.ig_emoji);
        ig_emoji.setTag("emoji");
        ig_toggle_input = (ImageButton) mPopView
                .findViewById(R.id.ig_toggle_input);
        ig_emoji.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                toggleEmojiView();
                String tag = (String) v.getTag();
                if (tag.equals("emoji")) {
                    ig_emoji.setImageResource(R.drawable.release_soft_input);
                    ig_emoji.setTag("softInput");
                    setPopuFocusable();
                } else {
                    ig_emoji.setImageResource(R.drawable.release_bq_selector);
                    ig_emoji.setTag("emoji");
                    setPopuFocusable();
                }
                if (null != clickListener) {
                    clickListener.onClick(v);
                }
            }
        });
        ig_toggle_input.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != clickListener) {
                    clickListener.onClick(v);
                }
            }
        });
    }

    private void setPopuFocusable() {
        this.setFocusable(false);
    }

    public void toggleEmojiView() {
        this.emojiView.toggle();
    }

    public void showPop(View v) {
        if (!isShowing()) {
            this.setFocusable(true);
            KeyBoardUtils.closeKeybord(release_edit, mContext);
            KeyBoardUtils.openKeybord(release_edit, mContext);
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
