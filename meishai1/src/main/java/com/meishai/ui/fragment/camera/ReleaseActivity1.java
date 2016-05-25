package com.meishai.ui.fragment.camera;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.emoji.MsgEmojiModle;
import com.emoji.SelectFaceHelper;
import com.emoji.SelectFaceHelper.OnFaceOprateListener;
import com.google.gson.reflect.TypeToken;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.location.LocalModel;
import com.meishai.app.location.TsLocation6;
import com.meishai.app.util.KeyBoardUtils;
import com.meishai.app.widget.ProgressAction;
import com.meishai.app.widget.layout.FlowLayout;
import com.meishai.app.widget.progressbar.RoundProgressBarWidthNumber;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.PostData;
import com.meishai.entiy.ReleaseData;
import com.meishai.entiy.ReleaseRespData;
import com.meishai.entiy.TagInfo;
import com.meishai.entiy.TryuseDetail;
import com.meishai.net.RespEmoji;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.service.ReleaseService;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.dialog.ReleaseContrlDialog1;
import com.meishai.ui.fragment.camera.req.ReleaseReq;
import com.meishai.ui.fragment.common.req.PublicReq;
import com.meishai.ui.fragment.tryuse.FuliSheDetailActivity1;
import com.meishai.util.AndroidUtil;
import com.meishai.util.ComUtils;
import com.meishai.util.DebugLog;
import com.meishai.util.DiskImageCacheUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.ImageUtil;
import com.meishai.util.StringUtils;
import com.meishai.util.UploadUtilsAsync;

/**
 * 文件名：ReleaseActivity1
 * 描    述：发布页面的activity
 * 作    者：yl
 * 时    间：2016/1/4
 * 版    权：
 */
public class ReleaseActivity1 extends BaseActivity implements OnClickListener {

    // 修改帖子
    public static final String OPER_POST = "mod_post";
    //发布晒晒
    public static final String OPER_PUBLISH = "publish";
    //添加链接的请求码
    private static final int ADD_LINKED_REQUEST_CODE = 33;

    private View mRoot;

    //标题栏数据
    private Button mCancel;
    private Button mRelease;

    //头部控件
    private TextView mHeaderTitle;
    private TextView mHeaderDesc;
    private LinearLayout mHeaderRoot;
    private ImageView mHeaderMore;
    private ImageView mHeaderImage;
    //发布时使用的进度条
//    	private TextProgressBar mProgress;

    //图片容器
    private LinearLayout mPics;
    //添加图片的图标
    private ImageView mAddPic;

    //输入框
    private EditText mReleaseEdit;

    //选中的标签
    private FlowLayout mFlowSelectedTags;
    private List<TagInfo> mSelectedTags;//选择的标签

    //位置信息
    private TextView mAreaname;
    private ImageButton mLocation;

    //标签的数据以及它的适配器
    private FlowLayout mFlowAllTags;
    private List<TagInfo> mAllTags;//所有标签

    private ImageView mShareWechat;
    private LinearLayout mAddLinked;


    //发布用到的异步任务类
    private UploadUtilsAsync uploadAsync;

    //位置信息
    private LocalModel localModel;
    private TsLocation6 location;


    private ArrayList<String> mPicsDate;//图片数据的集合


    //修改帖子时,用来保存帖子信息的实体
    //    private PostItem postItem;


    //点击退出发布界面的对话框
    private PopupWindow hintPop;
    private View mDialogView;


    //当前编辑的图片,传递给图片操作的对话框
    private int currentEditIndex;
    //点击图片显示的操作对话框
    private ReleaseContrlDialog1 ctrlDialog;


    // 表情相关
    private SelectFaceHelper mFaceHelper = null;
    private View addFaceToolView;
    protected boolean isVisbilityFace;
    private LinearLayout lay_emoji;
    private ImageButton ig_emoji;
    private ImageButton ig_toggle_input;


    public static final String NETWORKPIC = "http";// 网络图片的开头部分
    private ReleaseData mReleaseData;//用于存放发布数据的实体,方便给activity间传输
    private boolean isRelease = false;//标记是否是在发布帖子


    private boolean mIsShare2Wechat = false;//是否分享到微信
    private boolean isChecked;//是否显示地址
    private boolean isFristPost;//是否是从修改进入的

    private ArrayList<String> cachePaths = new ArrayList<String>();//当修改帖子时把网络图片保存到本地的地址集合
    private File cachePath = DiskImageCacheUtil.getInstance().getCacheDir("meishai/image");
    private ReleaseRespData.LinkedData mLinkedData;
    private TextView mAddLinkedText;

    //发布的进度条
    private ProgressAction mProgressAction;
    private RoundProgressBarWidthNumber progress;

    /**
     * 修改帖子时,使用的intent
     *
     * @param pid
     * @return
     */
    public static Intent newPostIntent(int pid) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                ReleaseActivity1.class);
        ReleaseData releaseData = new ReleaseData();
        releaseData.setPid(pid);
        releaseData.setOper(OPER_POST);
        intent.putExtra("releaseData", releaseData);
        // intent.putExtra("oper", OPER_POST);
        return intent;
    }

    /**
     * 从编辑图片界面启动activity的intent
     */
    public static Intent newIntent(ReleaseData releaseData) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                ReleaseActivity1.class);
        intent.putExtra("releaseData", releaseData);
        return intent;
    }

    private BroadcastReceiver mReleaseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            AndroidUtil.showToast("收到广播");
            if (intent != null) {
                //发送发布消息给编辑界面,在编辑界面直接关闭它自己
                ReleaseActivity1.this.setResult(Activity.RESULT_OK);
                ReleaseActivity1.this.finish();
//                ReleaseActivity1.this.unregisterReceiver(mReleaseReceiver);

            }
        }
    };

    public void registerReceive() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstantSet.ACTION_RELEASE);
        registerReceiver(mReleaseReceiver, filter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release);
        //注册一个广播
        registerReceive();
        initView();
        initPop();
        initData();
        if (mPicsDate == null)
            mPicsDate = new ArrayList<String>();


        //获取表情
        if (null == GlobalContext.getInstance().getEmojis()
                || GlobalContext.getInstance().getEmojis().isEmpty()) {
            this.emoji(this);
        } else {
            loadFaceView(GlobalContext.getInstance().getEmojis());
        }
        //获取地址信息
        startLocation();


    }

    OnClickListener faceClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isVisbilityFace) {
                isVisbilityFace = false;
                addFaceToolView.setVisibility(View.GONE);
                KeyBoardUtils.openKeybord(mReleaseEdit, ReleaseActivity1.this);
                ig_emoji.setImageResource(R.drawable.release_bq_selector);
            } else {
                isVisbilityFace = true;
                KeyBoardUtils.closeKeybord(mReleaseEdit, ReleaseActivity1.this);
                addFaceToolView.setVisibility(View.VISIBLE);
                ig_emoji.setImageResource(R.drawable.release_soft_input);
            }
        }
    };
    OnFaceOprateListener mOnFaceOprateListener = new OnFaceOprateListener() {
        @Override
        public void onFaceSelected(String text) {
            if (!TextUtils.isEmpty(text)) {
                mReleaseEdit.append(text);
            }
        }

        @Override
        public void onFaceDeleted() {
            int selection = mReleaseEdit.getSelectionStart();
            String text = mReleaseEdit.getText().toString();
            if (selection > 0) {
                String text2 = text.substring(selection - 1);
                if ("]".equals(text2)) {
                    int start = text.lastIndexOf("[");
                    int end = selection;
                    mReleaseEdit.getText().delete(start, end);
                    return;
                }
                mReleaseEdit.getText().delete(selection - 1, selection);
            }
        }

    };

    private void initView() {

        mRoot = findViewById(R.id.root);
        // 标题栏部分
        mCancel = (Button) findViewById(R.id.btn_cancel);
        mCancel.setOnClickListener(this);
        mRelease = (Button) findViewById(R.id.btn_save);
        mRelease.setOnClickListener(this);
        // 进度条
        //		mProgress = (TextProgressBar) findViewById(R.id.pb_progressbar);
        // 文本行部分
        mHeaderRoot = (LinearLayout) findViewById(R.id.header_root);
        mHeaderImage = (ImageView) findViewById(R.id.header_image);
        mHeaderRoot.setOnClickListener(this);
        mHeaderTitle = (TextView) findViewById(R.id.header_title);
        mHeaderDesc = (TextView) findViewById(R.id.header_desc);
        mHeaderMore = (ImageView) findViewById(R.id.header_more);


        // 选中的图片的容器
        mPics = (LinearLayout) findViewById(R.id.pics);
        mAddPic = (ImageView) findViewById(R.id.add_pic);
        mAddPic.setOnClickListener(this);

        //编辑部分
        mReleaseEdit = (EditText) findViewById(R.id.release_edit);
        mReleaseEdit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                KeyBoardUtils.openKeybord(mReleaseEdit, ReleaseActivity1.this);
                lay_emoji.setVisibility(View.VISIBLE);
            }
        });
        mReleaseEdit.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isVisbilityFace = false;
                addFaceToolView.setVisibility(View.GONE);
                return false;
            }
        });
        //地址部分
        mAreaname = (TextView) findViewById(R.id.areaname);
        mLocation = (ImageButton) findViewById(R.id.chk_location);
        mLocation.setOnClickListener(this);

        //选中的贴纸的容器
        mFlowSelectedTags = (FlowLayout) findViewById(R.id.release_tags);
        //标签部分
        mFlowAllTags = (FlowLayout) findViewById(R.id.gv_cate);


        //分享到微信
        mShareWechat = (ImageView) findViewById(R.id.release_share_wechat);
        mShareWechat.setOnClickListener(this);
        //添加链接
        mAddLinked = (LinearLayout) findViewById(R.id.release_add_linked);
        mAddLinked.setOnClickListener(this);
        mAddLinkedText = (TextView) findViewById(R.id.release_add_linked_text);


        // 输入法部分
        addFaceToolView = (View) this.findViewById(R.id.add_tool);
        lay_emoji = (LinearLayout) findViewById(R.id.lay_emoji);
        ig_emoji = (ImageButton) findViewById(R.id.ig_emoji);
        ig_emoji.setTag("emoji");
        ig_toggle_input = (ImageButton) findViewById(R.id.ig_toggle_input);
        ig_emoji.setOnClickListener(faceClick);
        ig_toggle_input.setOnClickListener(this);


        mPicsDate = new ArrayList<String>();

    }

    /**
     * 点击返回时候的对话框
     */
    private void initPop() {
        //点击图片的对话框
        ctrlDialog = new ReleaseContrlDialog1(this);
        //点击返回的对话框
        mDialogView = View.inflate(this, R.layout.dialog_relaese_exit, null);
        Button btCancel = (Button) mDialogView.findViewById(R.id.cancel);
        Button btconfirm = (Button) mDialogView.findViewById(R.id.confirm);
        // 为取消按钮注册点击事件
        btCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hintPop.dismiss();
            }
        });
        // 为确定按钮注册点击事件
        btconfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // builder.
                hintPop.dismiss();
                finish();
            }
        });
        mDialogView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                hintPop.dismiss();
            }
        });

    }

    private void initData() {

        mReleaseData = getIntent().getParcelableExtra("releaseData");

        if (mReleaseData != null && mReleaseData.getPaths() != null) {//有图片
            mPicsDate = (ArrayList<String>) mReleaseData.getPaths();
        }
        //修复当是发布帖子的时候没有设置oper的值导致无法发布的问题，以前怎么没有出现这个问题？奇怪
        if (TextUtils.isEmpty(mReleaseData.getOper())) {
            mReleaseData.setOper(OPER_PUBLISH);
        }
        // 修改帖子,并是第一次进入发布页面(没有图片)
        if (OPER_POST.equals(mReleaseData.getOper()) && (mReleaseData.getPaths() == null || mReleaseData
                .getPaths().isEmpty())) {
            isFristPost = true;
            loadPostData();
        } else {// 其他情况都不用去拉取帖子数据,只需获取发布需要的数据即可
            //做个空判断
            if (mPicsDate == null)
                throw new RuntimeException("没有传递可用的图片");

            //恢复数据,如果数据中有的话
            if (mReleaseData != null)
                resetReleaseDate();

            //获得标签和头部数据
            getReleaseRequst();

            //初始化图片信息
            initPic(mPicsDate);
        }

    }

    /**
     * 初始化选择的图片列表
     *
     * @param picsPath
     */
    public void initPic(ArrayList<String> picsPath) {
        if (mPicsDate != picsPath) {
            mPicsDate = picsPath;
        }
        mPics.removeAllViews();


        int dip = AndroidUtil.dip2px(70);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dip, dip);
        lp.setMargins(0, 0, AndroidUtil.dip2px(7), 0);
        for (int i = 0; i < mPicsDate.size(); i++) {
            final int index = i;
            final ImageView child = new ImageView(this);
            child.setLayoutParams(lp);

            if (!mPicsDate.get(i).startsWith(NETWORKPIC)) {// 本地图片
                com.album.utils.ImageLoader.getInstance().loadImage(mPicsDate.get(i), child);
            } else {// 网络图片
                getImageLoader().get(mPicsDate.get(i), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        //如果返回的是默认图片,什么都不做
                        if (response.getBitmap() == ConstantSet.defaultBitmap) {
                            return;
                        }
                        //为imageview设置图片
                        child.setImageBitmap(response.getBitmap());
                        //保存图片到本地
                        String picN = ComUtils.getRandomAlphamericStr(10) + ".jpg";
                        File temp = new File(cachePath, picN);
                        ImageUtil.saveBitMap(temp.getAbsolutePath(), response.getBitmap());
                        cachePaths.add(temp.getAbsolutePath());
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        child.setImageResource(R.drawable.place_default);
                    }
                });
                //
            }
            // 点击图片,弹出对应的对话框,进入到图片的编辑页面
            child.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    //TODO 图片的点击事件
                    ctrlDialog.setContrlData(mReleaseData, index);
                    currentEditIndex = index;
                    showCtrlDialog(currentEditIndex);
                }
            });

            mPics.addView(child);

        }

        //是否显示添加图片,默认还是不显示了吧,不太好操作
//        mAddPic.setVisibility(View.GONE);
        if (mPicsDate.size() >= ConstantSet.MAX_IMAGE_COUNT) {
            mAddPic.setVisibility(View.GONE);
        }
    }

    /**
     * 获取用户的位置信息
     */
    private void startLocation() {
        location = new TsLocation6(getApplicationContext(), new Handler() {

            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case LocalModel.MSG_WHAT:
                        localModel = (LocalModel) msg.obj;
                        break;
                    default:
                        break;
                }
            }
        });
        location.startLoction();
    }

    /**
     * 控制位置信息的开关
     */
    public void switchLocation() {
        if (!isChecked) {// false显示
            if (null != localModel) {
                mAreaname.setTextColor(getResources().getColor(
                        R.color.color_666));
                //                mAreaname.setText(localModel.getAddr());
                mAreaname.setText(localModel.getProvince() + " "
                        + localModel.getCity() + " "
                        + localModel.getDistrict());
            }
            mLocation.setImageResource(R.drawable.release_localtion_show);
            isChecked = true;
        } else {// true 隐藏
            mLocation.setImageResource(R.drawable.release_localtion_hide);
            mAreaname.setTextColor(getResources().getColor(R.color.txt_color));
            mAreaname.setText(getResources().getString(R.string.location_hide));
            isChecked = false;
        }
    }


    /**
     * 保存数据到releasedate
     */
    private void saveReleaseDate() {
        //设置文字内容
        mReleaseData.setContent(mReleaseEdit.getText().toString());
        //设置是否分享到微信
        mReleaseData.setIsShare2Wechat(mIsShare2Wechat ? 1 : 0);
        //设置选择的tids
        if (mSelectedTags != null) {
            mReleaseData.setTids(generateTids(mSelectedTags));
        }
        //是否显示地址信息
        mReleaseData.setIsShowLocation(isChecked ? 1 : 0);

        //上传的图片数组,修改时第一次是都是网络图片,之后会都是本地图片
        mReleaseData.setPaths(mPicsDate);

        if (localModel != null) {
            // 地区坐标，格式：经度,纬度
            mReleaseData.setLongLatPoint(localModel.toLongLatPoint());
            // 广东省,深圳市,龙岗区，格式：省,市,区
            mReleaseData.setLocation(localModel.getProvince() + "," + localModel.getCity() + ","
                    + localModel.getDistrict());
        }

    }

    /**
     * 恢复发布页面的数据
     */
    private void resetReleaseDate() {
        //设置文字内容
        if (!TextUtils.isEmpty(mReleaseData.getContent())) {
            mReleaseEdit.setText(mReleaseData.getContent());
        }
        //设置是否分享到微信
        if (mReleaseData.getIsShare2Wechat() == 1) {
            mShareWechat.setSelected(true);
            mIsShare2Wechat = true;
        } else {
            mShareWechat.setSelected(false);
            mIsShare2Wechat = false;
        }

        //是否显示地址信息
        if (mReleaseData.getIsShowLocation() == 1) {
            mLocation.setImageResource(R.drawable.release_localtion_show);
            isChecked = true;
            mAreaname.setText(mReleaseData.getLocation());
        } else {
            mLocation.setImageResource(R.drawable.release_localtion_hide);
            isChecked = false;
            mAreaname.setText("不显示");
        }
        //地址文字信息,在判断是否显示的时候设置

        //是否已添加链接
        if (!TextUtils.isEmpty(mReleaseData.getUrl())) {
            mAddLinkedText.setText("已添加链接");
            mAddLinked.setClickable(false);
            mAddLinked.setSelected(true);
        } else {
            mAddLinkedText.setText("添加链接");
            mAddLinked.setClickable(true);
            mAddLinked.setSelected(false);
        }

    }

    /**
     * 显示点击图片时的对话框
     *
     * @param position
     */
    public void showCtrlDialog(int position) {
        mReleaseData.setContent(mReleaseEdit.getText().toString());
        ctrlDialog.show(mReleaseData, mPics, position);
    }


    /**
     * 退出发布页面时的提示对话框
     */
    private void showDialog() {
        if (hintPop == null) {
            hintPop = new PopupWindow(this);
            hintPop.setContentView(mDialogView);
            // 设置SelectPicPopupWindow弹出窗体的宽
            hintPop.setWidth(LayoutParams.MATCH_PARENT);
            // 设置SelectPicPopupWindow弹出窗体的高
            hintPop.setHeight(LayoutParams.MATCH_PARENT);
            // 设置SelectPicPopupWindow弹出窗体可点击
            hintPop.setFocusable(true);
            // 实例化一个ColorDrawable颜色为半透明
            ColorDrawable dw = new ColorDrawable(0xb0000000);
            // 设置SelectPicPopupWindow弹出窗体的背景
            hintPop.setBackgroundDrawable(dw);
        }
        if (hintPop.isShowing()) {
            hintPop.dismiss();
        } else {
            hintPop.showAtLocation(mRoot, Gravity.BOTTOM
                    | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }


    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.btn_cancel:// 返回
                hideEmoji();
                finish();
                //            showDialog();
                break;

            case R.id.btn_save:// 发布
                hideEmoji();
                release();
                break;

            case R.id.header_root:// 头部信息栏
                hideEmoji();
                break;

            case R.id.release_edit:
                KeyBoardUtils.openKeybord(mReleaseEdit, ReleaseActivity1.this);
                break;

            case R.id.add_pic:// 添加图片
                hideEmoji();
                if (mPicsDate.size() >= ConstantSet.MAX_IMAGE_COUNT) {
                    AndroidUtil.showToast("最多只能添加" + ConstantSet.MAX_IMAGE_COUNT + "张图片");
                    return;
                }
                //保存发布数据
                saveReleaseDate();
                //是否是刚刚从修改过了的
                if (isFristPost) {
                    mReleaseData.setPaths(cachePaths);
                    startActivity(ChooseImageActivity.newIntent(mReleaseData, ConstantSet.MAX_IMAGE_COUNT - mPicsDate.size(), ConstantSet.ADD_MODE));
                    finish();
                } else {

                    Intent bundle = new Intent();
                    bundle.putExtra("releaseData", mReleaseData);
                    setResult(RESULT_OK, bundle);
                    finish();
                }

                break;

            case R.id.release_add_linked://添加链接
                if (mLinkedData == null)
                    break;
                if (mLinkedData.islink == 1) {
                    startActivityForResult(SearchGoodsActivity.newIntent(), ADD_LINKED_REQUEST_CODE);
                } else {
                    AndroidUtil.showToast(mLinkedData.tips);
                }
                break;

            case R.id.release_share_wechat://分享到微信,应该是改变一下状态,在发布前判断该状态
                mIsShare2Wechat = !mIsShare2Wechat;
                mShareWechat.setSelected(mIsShare2Wechat);
                break;

            case R.id.ig_toggle_input:
                hideEmoji();
                break;

            case R.id.chk_location:// 是否显示地址
                hideEmoji();
                switchLocation();
                break;

            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 用于处理编辑图片
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ADD_LINKED_REQUEST_CODE:
                    if (data != null) {
                        String url = data.getStringExtra("url");
                        if (!TextUtils.isEmpty(url)) {
                            DebugLog.w("链接:" + url);
                            mReleaseData.setUrl(url);
                            mAddLinkedText.setText("已添加链接");
                            mAddLinked.setClickable(false);
                            mAddLinked.setSelected(true);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != location) {
            location.stopLoction();
        }
//        if (isRelease) {//当前是发布状态
//            AndroidUtil.showToast("正在后台为您上传帖子");
//        }
        unregisterReceiver(mReleaseReceiver);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //            showDialog();
            hideEmoji();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 获取表情
     *
     * @param context
     */
    public void emoji(Context context) {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        PublicReq.emoji(context, data, new Listener<String>() {
            @Override
            public void onResponse(String resp) {
                RespEmoji response = GsonHelper.parseObject(resp,
                        RespEmoji.class);
                if (response.isSuccess()) {
                    Type type = new TypeToken<List<MsgEmojiModle>>() {
                    }.getType();
                    List<MsgEmojiModle> emojis = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getArea()), type);
                    GlobalContext.getInstance().setEmojis(emojis);
                    loadFaceView(emojis);
                }
            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }

    private void loadFaceView(List<MsgEmojiModle> emojis) {
        if (null == mFaceHelper) {
            mFaceHelper = new SelectFaceHelper(ReleaseActivity1.this,
                    addFaceToolView, emojis);
            mFaceHelper.setFaceOpreateListener(mOnFaceOprateListener);
        }
        ig_emoji.setOnClickListener(faceClick);
    }

    private void hideEmoji() {
        lay_emoji.setVisibility(View.GONE);
        isVisbilityFace = false;
        addFaceToolView.setVisibility(View.GONE);
        ig_emoji.setImageResource(R.drawable.release_bq_selector);
        // emojiView.hide();
        KeyBoardUtils.closeKeybord(mReleaseEdit, ReleaseActivity1.this);
    }

    /**
     * 获取发布页面需要的信息,包括标签和头部的信息的数据
     */
    private void getReleaseRequst() {
        showProgress("", "加载中...");

        //初始化tids
        int tid = mReleaseData.getTid();
        String tids;
        if (tid != 0) {
            if (TextUtils.isEmpty(mReleaseData.getTids())) {
                tids = tid + "";
            } else {
                tids = tid + "," + mReleaseData.getTids();
            }
        } else {
            tids = mReleaseData.getTids();
        }
        //请求数据
        ReleaseReq.pullReleaseData(this, mReleaseData.getGid(), tids, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideProgress();
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getInt("success") == 1) {
                        //解析数据
                        ReleaseRespData releaseRespData = GsonHelper.parseObject(response, ReleaseRespData.class);
                        //初始化3部分的信息
                        initTags(releaseRespData);
                    } else {
                        AndroidUtil.showToast(obj.getString("tips"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    AndroidUtil.showToast("json数据解析失败!");
                }


            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                AndroidUtil.showToast(getString(R.string.reqFailed));
            }
        });

    }

    /**
     * 修改帖子时 获取数据的接口
     */
    private void loadPostData() {
        showProgress("", "请稍候..");

        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("pid", String.valueOf(mReleaseData.getPid()));
        ReleaseReq.edit1(this, data, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideProgress();
                if (!TextUtils.isEmpty(response)) {
                    try {

                        JSONObject object = new JSONObject(response);
                        if (object.getInt("success") == 1) {
                            ReleaseRespData releaseRespData = GsonHelper.parseObject(response, ReleaseRespData.class);
                            //初始化标签
                            initTags(releaseRespData);
                            //其他数据
                            PostData postData = releaseRespData.data.get(0);
                            updateUI(postData);

                        } else {
                            AndroidUtil.showToast(object.getString("tips"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        AndroidUtil.showToast("json数据解析失败!");
                    }

                } else {
                    AndroidUtil.showToast("获取到网络数据为null");
                }
                //                if (response.isSuccess()) {
                //                    Type type = new TypeToken<List<PostItem>>() {
                //                    }.getType();
                //                    List<PostItem> resultPostItems = GsonHelper.parseObject(
                //                            GsonHelper.toJson(response.getData()), type);
                //
                //                    if (null != resultPostItems && !resultPostItems.isEmpty()) {
                //                        postItem = resultPostItems.get(0);
                //                        updateUI();
                //                    }
                //                } else {
                //                    AndroidUtil.showToast(response.getTips());
                //                }

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                AndroidUtil.showToast(getString(R.string.reqFailed));
            }
        });
    }


    /**
     * 更新数据,在修改帖子的第一次进入时会去请求数据,请求成功后会被调用
     *
     * @param postItem
     */
    private void updateUI(PostData postItem) {

        if (null != postItem) {

            //------------初始化照片数据----------
            if (null != postItem.pics && !postItem.pics.isEmpty()) {
                if (mPicsDate == null) {
                    mPicsDate = new ArrayList<String>();
                } else {
                    mPicsDate.clear();
                }
                for (TryuseDetail.Pic pic : postItem.pics) {
                    mPicsDate.add(pic.getUrl());
                }
            }
            //------------初始化发布数据------------
            mReleaseData.setPaths(mPicsDate);// 图片路径
            mReleaseData.setLocation(postItem.isarea + "");// 是否显示位置
            mReleaseData.setContent(postItem.content);// 内容
            mReleaseData.setPid(postItem.pid);// pid帖子id,提交的时候用到
            mReleaseData.setUrl(postItem.itemurl);// 添加的链接,提交的时候用到
            mReleaseData.setIsShowLocation(postItem.isarea);// 是否显示地址,提交的时候用到


            //------------初始化界面----------------
            resetReleaseDate();


            initPic(mPicsDate);
        }
    }

    /**
     * 初始化帖子以及头部分的信息,当请求完成的时候回被调用来初始化这三部分的数据
     *
     * @param releaseRespData
     */
    private void initTags(ReleaseRespData releaseRespData) {
        if (null != releaseRespData) {
            //----------------头部数据---------------------
            upDateHeader(releaseRespData.goodsdata);
            //所有的标签
            mAllTags = releaseRespData.tagsdata;
            //选中的标签
            mSelectedTags = releaseRespData.topicdata;
            if (mSelectedTags == null) {
                mSelectedTags = new ArrayList<TagInfo>();
            }

            //初始化标签
            initFlowLayout(mAllTags, mSelectedTags, mFlowAllTags, mFlowSelectedTags);
            initFlowLayout(mSelectedTags, mAllTags, mFlowSelectedTags, mFlowAllTags);
            //初始化标签的状态,是否被选中
            initTagsStute(mFlowSelectedTags, true);
            initTagsStute(mFlowAllTags, false);

            //链接
            mLinkedData = releaseRespData.linkdata;
        }
    }

    /**
     * 设置指定的流式布局中子textview的状态
     *
     * @param tags       需要进行设置的流式布局
     * @param isSelected textview的选中状态以及字体颜色都由它决定
     */
    private void initTagsStute(FlowLayout tags, boolean isSelected) {
        for (int i = 0; i < tags.getChildCount(); i++) {
            tags.getChildAt(i).setSelected(isSelected);
            ((TextView) tags.getChildAt(i)).setTextColor(isSelected ? 0xFFFFFFFF : 0xFF929292);
        }
    }

    /**
     * 头部的数据
     *
     * @param goodsdata
     */
    private void upDateHeader(ReleaseRespData.SimpleGoddsData goodsdata) {
        if (goodsdata != null) {
            mHeaderRoot.setVisibility(View.VISIBLE);
            mHeaderImage.setTag(goodsdata.thumb);
            ListImageListener listener = new ListImageListener(mHeaderImage, R.drawable.place_default, R.drawable.place_default, goodsdata.thumb);
            getImageLoader().get(goodsdata.thumb, listener);
            mHeaderRoot.setTag(goodsdata.gid);
            mHeaderRoot.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //福利社详情
                    startActivity(FuliSheDetailActivity1.newIntent((Integer) v.getTag()));
                }
            });
            mHeaderTitle.setText(goodsdata.text1);
            mHeaderDesc.setText(goodsdata.text2);
        } else {
            mHeaderRoot.setVisibility(View.GONE);
        }
    }

    /**
     * 生成发布时用到的参数 tids
     */
    private String generateTids(List<TagInfo> cates) {
        StringBuilder tids = new StringBuilder();
        for (int i = 0; i < cates.size(); i++) {
            if (i == 0) {
                tids.append(cates.get(i).tid);
            } else {
                tids.append("," + cates.get(i).tid);
            }
        }

        return tids.toString();
    }

    /**
     * 初始化流式布局控件的数据,同时为其中的item设置点击事件,
     * srcContainer布局中的item在被点击后会从布局中删除该item,并把item加入到other布局中
     * srcTags中的对应数据也会被删除,加入到otherTags集合中
     *
     * @param srcTags      布局需要用到的数据集合
     * @param otherTags    当布局中的item被点击后需要往其中添加数据
     * @param srcContainer 需要被初始化的流式布局
     * @param other        当布局中的item被点击后需要往其中view
     */
    private void initFlowLayout(final List<TagInfo> srcTags, final List<TagInfo> otherTags, final FlowLayout srcContainer, final FlowLayout other) {
        if (srcTags == null || otherTags == null) {
            return;
        }
        if (srcContainer == null || other == null) {
            return;
        }

        srcContainer.removeAllViews();
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        int margin = AndroidUtil.dip2px(5);
        int lr = AndroidUtil.dip2px(10);
        lp.setMargins(margin, margin, margin, margin);

        for (int i = 0; i < srcTags.size(); i++) {
            TagInfo item = srcTags.get(i);

            TextView view = new TextView(this);
            view.setText(item.name);
            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.release_cate_back_selector));
            view.setTextSize(10);
            view.setPadding(lr, margin, lr, margin);

            view.setTag(item);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //由于这个textview会在两个布局中换来换去,所以在这里直接判断它的父控件是谁,就可以跳到另一个布局去就行了
                    TagInfo info = (TagInfo) v.getTag();
                    if (v.getParent() == mFlowAllTags && mSelectedTags.size() >= ConstantSet.MAX_TAGS_COUNT) {
                        AndroidUtil.showToast("最多只能选择5个标签");
                        return;
                    }
                    if (v.getParent() == srcContainer) {
                        srcTags.remove(info);
                        otherTags.add(info);
                        srcContainer.removeView(v);
                        other.addView(v, 0);
                    } else if (v.getParent() == other) {
                        otherTags.remove(info);
                        srcTags.add(info);
                        other.removeView(v);
                        srcContainer.addView(v, 0);
                    }

                    //这里换好了之后再设置一下选中状态,让它样式变得好看
                    if (v.getParent() == mFlowSelectedTags) {
                        v.setSelected(true);
                        ((TextView) v).setTextColor(0xFFFFFFFF);
                    } else {
                        ((TextView) v).setTextColor(0xFF929292);
                        v.setSelected(false);
                    }
                }
            });

            srcContainer.addView(view, lp);

        }

    }


    /**
     * 点击发布后的操作,直接启动一个服务来在后台进行发布
     */
    public void release() {
        if (checkcommentData()) {
            mRelease.setClickable(false);
            isRelease = true;
            KeyBoardUtils.closeKeybord(mReleaseEdit, ReleaseActivity1.this);
            //			mProgress.setVisibility(View.VISIBLE);

            //-------------初始化发布信息----------------
            saveReleaseDate();

            //直接在这里发布,完成之后返回让编辑页面也关闭,分享到微信
//            AndroidUtil.showToast("晒晒将会在后台为您提交帖子!");
            Intent intent = new Intent();
            intent.putExtra("releaseDate", mReleaseData);
            intent.putExtra("receiver", ConstantSet.ACTION_RELEASE);
            intent.setClass(this, ReleaseService.class);
            startService(intent);

            showReleaseProgress();


        }
    }

    /**
     * 显示一个进度条,不过这个进度条的进度是假的
     */
    private void showReleaseProgress() {
        mProgressAction = ProgressAction.show(this);
        progress = mProgressAction.getProgress();
        //假的进度
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    int prog = 0;
                    int i = 5;
                    while (mProgressAction.isShowing()) {
                        prog += i;
                        Thread.sleep(150);
                        if (prog > 90) {
                            prog = 91;
                            i = 1;
                        }
                        if (mProgressAction.isShowing() && progress != null)
                            progress.setProgress(prog);

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    /**
     * 检查是否满足发布的条件,在release()方法中别调用
     *
     * @return
     */
    private boolean checkcommentData() {
        if (StringUtils.isBlank(mReleaseEdit.getText().toString())) {
            AndroidUtil.showToast(getString(R.string.release_input_tip));
            return false;
        } else if (null == mSelectedTags || mSelectedTags.isEmpty()) {
            AndroidUtil.showToast(getString(R.string.choose_cate));
            return false;
        }
        return true;
    }

    /**
     * 对话框点击删除后的处理
     *
     * @param position
     */
    public void onRemoveClick(int position) {
        mReleaseData.getPaths().remove(position);

        initPic(mReleaseData.getPaths());

//        if (mPicsDate.size() < ConstantSet.MAX_IMAGE_COUNT) {
//            mAddPic.setVisibility(View.VISIBLE);
//        }

        // 要通知编辑界面也删除掉这个图片 picsPosition 在保存数据和恢复数据时要清除
        //不太好处理
        //        Intent filter = new Intent();
        //        filter.setAction(ConstantSet.ACTION_PIC_DELETE);
        //        filter.putExtra("position",position);
        //        sendBroadcast(filter);
    }

    /**
     * 点击设为首图按钮后的处理
     *
     * @param position
     */
    public void onSetFirstClick(int position) {
        String temp = mReleaseData.getPaths().remove(position);
        mReleaseData.getPaths().add(0, temp);
        initPic(mReleaseData.getPaths());

        //TODO 设为首图的时候需要发送的广播,保证图片编辑页面与发布页面的数据的同步
        //        Intent intent = new Intent();
        //        intent.setAction(ConstantSet.ACTION_PIC_FIRST);
        //        intent.putExtra("position",position);
        //        sendBroadcast(intent);
    }


}
