package com.meishai.ui.fragment.meiwu;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.util.KeyBoardUtils;
import com.meishai.app.widget.EditTextWithDel;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;

/**
 * Created by Administrator on 2015/11/27.
 * <p/>
 * 美物 - 搜索 对应的activity
 */
public class SearchActivity extends FragmentActivity implements View.OnClickListener {

    private static final int ID_CATE = 0;
    private static final int ID_RESAULT = 2;
    private static final String TAG_RESAULT = "resault";
    private static final String TAG_CATE = "cate";

    //搜索结果默认显示的内容类型,美物或美晒
    public static final int TYPE_SHAISHAI = 0;
    public static final int TYPE_MEIWU = 1;
    private int mType;
    private ImageView mBack;
    private Button mCancel;
    private ImageView mSearchIcon;
    private EditTextWithDel mSearchEdit;

    private MeiWuCateFragment mCate = new MeiWuCateFragment();
    private SearchResaultFragment mResault = new SearchResaultFragment();
    private InputMethodManager imm;

    public static Intent newIntent(int type) {
        Intent intent = new Intent(GlobalContext.getInstance(), SearchActivity.class);
        intent.putExtra("type", type);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        initView();
        showCate();

    }

    private void showCate() {
        mCancel.setVisibility(View.GONE);
        switchFragment(ID_CATE);
        mCate.setShowMode(0);
    }


    private void initView() {
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mType = getIntent().getIntExtra("type", 0);
        Bundle args = new Bundle();
        args.putInt("type", mType);
        mResault.setArguments(args);

        mBack = (ImageView) findViewById(R.id.back);
        mBack.setOnClickListener(this);
        mSearchIcon = (ImageView) findViewById(R.id.search_icon);
        mCancel = (Button) findViewById(R.id.cancel);
        mCancel.setOnClickListener(this);
        mSearchEdit = (EditTextWithDel) findViewById(R.id.search);
        mSearchEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //有焦点-显示分类,隐藏取消,弹出输入框
                    DebugLog.w("有焦点");
                    mCancel.setVisibility(View.VISIBLE);
                    imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
                    switchFragment(ID_CATE);
                    mCate.setShowMode(MeiWuCateFragment.MODE_KEY);
                } else {
                    //没焦点 - 显示关键字,显示取消,隐藏输入框
                    DebugLog.w("没焦点");
                    mCancel.setVisibility(View.GONE);
                    switchFragment(ID_CATE);
                    mCate.setShowMode(MeiWuCateFragment.MODE_CATE);
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

                }
            }
        });
        mSearchEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新获得焦点
                mSearchEdit.setFocusable(true);
                mSearchEdit.setFocusableInTouchMode(true);
                mSearchEdit.requestFocus();
                mSearchEdit.findFocus();
            }
        });

        mSearchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //内容改变,显示结果,显示取消,弹出输入框
//                AndroidUtil.showToast(s.toString());
                switchFragment(ID_RESAULT);
                mCancel.setVisibility(View.VISIBLE);
                if (!AndroidUtil.getSoftInputState(SearchActivity.this) && !TextUtils.isEmpty(s)) {
                    AndroidUtil.showSoftInput(SearchActivity.this);

                }
                if (!TextUtils.isEmpty(s.toString())) {
                    //关键字发生了改变,通知下面的fragment
                    mResault.setKey(s.toString());
//                    //TODO 发送广播
//                    Intent intent = new Intent();
//                    intent.setAction("search_key_change");
//                    intent.putExtra("key",s.toString());
//                    sendBroadcast(intent);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mCate.setOnKeySelectedListener(new MeiWuCateFragment.OnKeySelectedListener() {
            @Override
            public void keySelected(String key) {
                mSearchEdit.setText(key);
            }
        });
//        mResault.setOnKeyChangeListener(new SearchResaultFragment.OnKeyChangeListener() {
//            @Override
//            public void KeyChangeListener(String key) {
//
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                KeyBoardUtils.closeKeybord(mSearchEdit, this);
                finish();
                break;
            case R.id.cancel:

                KeyBoardUtils.closeKeybord(mSearchEdit, this);
                finish();

//            mSearchEdit.setFocusable(false);
//            mSearchEdit.setText("");
//            mCate.setShowMode(MeiWuCateFragment.MODE_CATE);
//            imm.hideSoftInputFromWindow(mSearchEdit.getApplicationWindowToken(), 0);
//            switchFragment(ID_CATE);
//            mCancel.setVisibility(View.GONE);
                break;
            default:
                break;
        }

    }


    private void switchFragment(int tag) {
        if (Build.VERSION.SDK_INT >= 17) {
            if (isDestroyed()) return;
        }
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        Fragment frg;
        switch (tag) {
            case ID_CATE://分类
                frg = getSupportFragmentManager().findFragmentByTag(TAG_CATE);
                if (null == frg) {
                    transaction.add(R.id.frame, mCate, TAG_CATE);
                } else {
                    transaction.show(frg);
                }
                frg = getSupportFragmentManager().findFragmentByTag(TAG_RESAULT);
                if (null != frg) {
                    transaction.hide(frg);
                }
                break;


            case ID_RESAULT://搜索结果
                frg = getSupportFragmentManager().findFragmentByTag(TAG_RESAULT);
                if (null == frg) {
                    transaction.add(R.id.frame, mResault, TAG_RESAULT);

                } else {
                    transaction.show(frg);
                }
                frg = getSupportFragmentManager().findFragmentByTag(TAG_CATE);
                if (null != frg) {
                    transaction.hide(frg);
                }

                break;

        }

        transaction.commitAllowingStateLoss();
    }

    public interface OnKeyChangeListener {
        void KeyChangeListener(String key);
    }
}
