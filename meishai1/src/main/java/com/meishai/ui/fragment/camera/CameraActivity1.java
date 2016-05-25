package com.meishai.ui.fragment.camera;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.album.utils.ImageLoader;
import com.emoji.MsgEmojiModle;
import com.emoji.SelectFaceHelper;
import com.emoji.SelectFaceHelper.OnFaceOprateListener;
import com.google.gson.reflect.TypeToken;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.location.LocalModel;
import com.meishai.app.location.TsLocation;
import com.meishai.app.util.KeyBoardUtils;
import com.meishai.app.widget.CustomProgress;
import com.meishai.app.widget.progressbar.TextProgressBar;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.CateInfo;
import com.meishai.entiy.PostItem;
import com.meishai.entiy.PostItem.PictureInfo;
import com.meishai.entiy.ReleaseData;
import com.meishai.net.ReqData;
import com.meishai.net.RespData;
import com.meishai.net.RespEmoji;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.service.ReleaseService;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.dialog.ReleaseContrlDialog;
import com.meishai.ui.fragment.camera.req.ReleaseReq;
import com.meishai.ui.fragment.common.req.PublicReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.StringUtils;
import com.meishai.util.UploadUtilsAsync;

/**
 * 发布界面 2.0
 *
 * @author yl
 */
public class CameraActivity1 extends BaseActivity implements OnClickListener {

    private View mRoot;
    private CustomProgress mCustomProgress;

    private Button mCancel;
    private Button mRelease;

    //发布时使用的进度条
    private TextProgressBar mProgress;

    private ImageView mFirstImage;
    private EditText mReleaseEdit;
    private RelativeLayout mLayChooseCate;
    private TextView mAreaname;
    private ImageButton mLocation;
    private LinearLayout mPics;

    private String mPath;

    private LocalModel localModel = null;
    private TsLocation location = null;

    //发布用到的异步任务类
    private UploadUtilsAsync uploadAsync;
    // 修改帖子
    public static final String OPER_POST = "mod_post";
    public static final String OPER_PUBLISH = "publish";

    public static final int REQUESTCODE_MOD = 111;

    public static final int MOD_COVER = 22;// 修改封面
    public static final int ADD_IMAGE = 0;// 添加图片
    public static final int CHOOSE_IMAGE = 11;// 选择图片,来自于修改头像的

    private final int MAX_PIC_NUM = 5;//最大可以添加的图片数

    private ArrayList<String> mPicsDate;//图片数据的集合

    private boolean isChecked;//是否显示地址信息

    //修改帖子时,用来保存帖子信息的实体
    private PostItem postItem;


    //添加图片的图标
    private ImageView mAddPic;

    //点击退出发布界面的对话框
    private PopupWindow hintPop;
    private View mDialogView;


    //当前编辑的图片,传递给图片操作的对话框
    private int currentEditIndex;
    //点击图片显示的操作对话框
    private ReleaseContrlDialog ctrlDialog;


    //分类的数据以及它的适配器
    private GridView mGvCate;
    private List<CateInfo> cateInfos;
    private MyAdapter mAdapter;
    private CateInfo chkCateInfo;//选择的分类

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

    /**
     * 修改帖子时,使用的intent
     *
     * @param pid
     * @return
     */
    public static Intent newPostIntent(int pid) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                CameraActivity1.class);
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
                CameraActivity1.class);
        intent.putExtra("releaseData", releaseData);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.release1);
        initView();
        initPop();
        initData();
        if (mPicsDate == null)
            mPicsDate = new ArrayList<String>();
        if (null == GlobalContext.getInstance().getEmojis()
                || GlobalContext.getInstance().getEmojis().isEmpty()) {
            this.emoji(this);
        } else {
            loadFaceView(GlobalContext.getInstance().getEmojis());
        }
    }

    View.OnClickListener faceClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isVisbilityFace) {
                isVisbilityFace = false;
                addFaceToolView.setVisibility(View.GONE);
                ig_emoji.setImageResource(R.drawable.release_bq_selector);
                KeyBoardUtils.openKeybord(mReleaseEdit, CameraActivity1.this);
            } else {
                isVisbilityFace = true;
                addFaceToolView.setVisibility(View.VISIBLE);
                ig_emoji.setImageResource(R.drawable.release_soft_input);
                KeyBoardUtils.closeKeybord(mReleaseEdit, CameraActivity1.this);
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
        mProgress = (TextProgressBar) findViewById(R.id.pb_progressbar);
        // 文本行部分
        mFirstImage = (ImageView) findViewById(R.id.first_image);
        mFirstImage.setOnClickListener(this);
        mReleaseEdit = (EditText) findViewById(R.id.release_edit);
        mReleaseEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                KeyBoardUtils.openKeybord(mReleaseEdit, CameraActivity1.this);
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

        // 选择分类部分
        mLayChooseCate = (RelativeLayout) findViewById(R.id.lay_choose_cate);
        // 地址
        mAreaname = (TextView) findViewById(R.id.areaname);
        mLocation = (ImageButton) findViewById(R.id.chk_location);
        mLocation.setOnClickListener(this);

        // 选中的图片的容器
        mPics = (LinearLayout) findViewById(R.id.pics);
        mAddPic = (ImageView) findViewById(R.id.add_pic);
        mAddPic.setOnClickListener(this);

        // 输入法部分
        addFaceToolView = (View) this.findViewById(R.id.add_tool);
        lay_emoji = (LinearLayout) findViewById(R.id.lay_emoji);
        ig_emoji = (ImageButton) findViewById(R.id.ig_emoji);
        ig_emoji.setTag("emoji");
        ig_toggle_input = (ImageButton) findViewById(R.id.ig_toggle_input);
        ig_emoji.setOnClickListener(faceClick);
        ig_toggle_input.setOnClickListener(this);

        mGvCate = (GridView) findViewById(R.id.gv_cate);
        mAdapter = new MyAdapter();
        mGvCate.setAdapter(mAdapter);
        mGvCate.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int count = parent.getChildCount();
                for (int i = 0; i < count; i++) {
                    View childAt = parent.getChildAt(i);
                    childAt.setSelected(false);
                }
                view.setSelected(true);
                if (chkCateInfo == null) {
                    chkCateInfo = new CateInfo();
                }
                chkCateInfo.cid = (int) id;
            }
        });

        mPicsDate = new ArrayList<String>();
    }

    private void initData() {
        // ids.pid = getIntent().getIntExtra("pid", 0);

        mReleaseData = getIntent().getParcelableExtra("releaseData");

        if (mReleaseData != null && mReleaseData.getPaths() != null) {
            mPicsDate = (ArrayList<String>) mReleaseData.getPaths();
            chkCateInfo = new CateInfo();
            chkCateInfo.cid = mReleaseData.getCid();
        }
        // OPER_SOURCE = getIntent().getStringExtra("oper");
        if (OPER_POST.equals(mReleaseData.getOper())
                && (mReleaseData == null || mReleaseData.getPaths() == null || mReleaseData
                .getPaths().size() == 0)) {// 修改帖子,并是第一次编辑
            loadEditData();
        } else {// 添加图片
            if (mPicsDate == null) {
                throw new RuntimeException("没有传递可用的图片");
            }

            if (mReleaseData != null) {// 描述文字
                String content = mReleaseData.getContent();
                if (!TextUtils.isEmpty(content)) {
                    mReleaseEdit.setText(content);
                }
                String location2 = mReleaseData.getLocation();
                if (!TextUtils.isEmpty(location2) && !location2.equals("不显示")) {// 位置
                    isChecked = false;
                    showLocation();
                    mAreaname.setText(location2);
                }
                chkCateInfo = new CateInfo();
                chkCateInfo.cid = mReleaseData.getCid();
            }

            initPic(mPicsDate);
        }

    }

    public void initPic(List<String> picsPath) {
        mPath = mPicsDate.get(0);
        mPics.removeAllViews();

        if (!mPath.startsWith(NETWORKPIC)) {// 本地
            ImageLoader.getInstance(3, com.album.utils.ImageLoader.Type.LIFO)
                    .loadImage(mPath, mFirstImage);
        } else {// 网络图片
            mFirstImage.setTag(mPath);
            ListImageListener listener = new ListImageListener(mFirstImage,
                    R.drawable.head_default, R.drawable.head_default, mPath);
            getImageLoader().get(mPath, listener);
        }
        if (picsPath.size() <= 1) {
            return;
        }

        int dip = AndroidUtil.dip2px(65);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dip, dip);
        lp.setMargins(AndroidUtil.dip2px(7), 0, 0, 0);
        for (int i = 1; i < mPicsDate.size(); i++) {
            final int index = i;
            ImageView child = new ImageView(this);
            child.setLayoutParams(lp);

            if (!mPicsDate.get(i).startsWith(NETWORKPIC)) {// 本地图片
                ImageLoader.getInstance(3,
                        com.album.utils.ImageLoader.Type.LIFO).loadImage(
                        mPicsDate.get(i), child);
            } else {// 网络图片
                child.setTag(mPicsDate.get(i));
                ListImageListener listener = new ListImageListener(child,
                        R.drawable.head_default, R.drawable.head_default,
                        mPicsDate.get(i));
                getImageLoader().get(mPicsDate.get(i), listener);
            }
            // 点击图片,进入到图片的编辑页面
            child.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // Intent intent = ImageEditorActivity.newIntent(mPicsDate
                    // .get(index));
                    // startActivityForResult(intent, REQUESTCODE_MOD);
                    ctrlDialog.setContrlData(mReleaseData, index);
                    currentEditIndex = index;
                    showCtrlDialog(currentEditIndex);
                }
            });

            mPics.addView(child);
        }
    }

    private void startLocation() {
        location = new TsLocation(getApplicationContext(), new Handler() {

            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case LocalModel.MSG_WHAT:
                        localModel = (LocalModel) msg.obj;
                        break;
                    default:
                        break;
                }
            }

            ;
        });
        location.startLoction();
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.btn_cancel:// 返回
                showDialog();
                break;
            case R.id.btn_save:// 发布
                hideEmoji();
                release();
                break;
            case R.id.first_image:// 第一张图片
                hideEmoji();
                ctrlDialog.setContrlData(mReleaseData, 0);
                currentEditIndex = 0;
                showCtrlDialog(currentEditIndex);
                break;
            case R.id.release_edit:
                KeyBoardUtils.openKeybord(mReleaseEdit, CameraActivity1.this);
                break;
            case R.id.chk_location:// 是否显示地址
                hideEmoji();
                showLocation();
                break;
            case R.id.add_pic:// 添加图片
                hideEmoji();
                if (mPicsDate.size() >= MAX_PIC_NUM) {
                    AndroidUtil.showToast("最多只能添加" + MAX_PIC_NUM + "张图片");
                    return;
                }
                mReleaseData.setContent(mReleaseEdit.getText().toString());
                mReleaseData.setLocation(mAreaname.getText().toString());
                mReleaseData.setCid(chkCateInfo.cid);
                Intent intent1 = ImageChooseActivity1.newIntent(mReleaseData,
                        ADD_IMAGE);
                startActivity(intent1);
                finish();
                break;
            case R.id.ig_toggle_input:
                hideEmoji();
                break;

            default:
                break;
        }
    }

    public void showCtrlDialog(int position) {
        mReleaseData.setContent(mReleaseEdit.getText().toString());
        mReleaseData.setLocation(mAreaname.getText().toString());
        mReleaseData.setCid(chkCateInfo.cid);

        ctrlDialog.show(position);
    }

    public void release() {
        if (checkcommentData()) {
            mRelease.setClickable(false);
            isRelease = true;
            KeyBoardUtils.closeKeybord(mReleaseEdit, CameraActivity1.this);
//			mProgress.setVisibility(View.VISIBLE);

            //初始化发布信息
            if (chkCateInfo != null) {
                mReleaseData.setCid(chkCateInfo.getCid());//设置选择的分类id
            }
            mReleaseData.setIsShowLocation(isChecked ? 1 : 0);//设置是否显示地址信息
            mReleaseData.setLongLatPoint(localModel.toLongLatPoint());//设置精度纬度
            mReleaseData.setLocation(localModel.getProvince() + "," + localModel.getCity() + ","
                    + localModel.getDistrict());//设置位置信息
            mReleaseData.setContent(mReleaseEdit.getText().toString());//设置文字内容

            AndroidUtil.showToast("晒晒将会在后台为您发布帖子!");
            Intent intent = new Intent();
            intent.setClass(this, ReleaseService.class);
            intent.putExtra("releaseDate", mReleaseData);
            startService(intent);
            finish();
//			if (OPER_POST.equals(mReleaseData.getOper())) {
//				// 修改帖子
//				uploadAsync = new UploadUtilsAsync(this, getReqModPostData(),
//						getNewPath());
//				uploadAsync.setListener(new OnSuccessListener() {
//
//					@Override
//					public void onSuccess(RespData respData) {
//						isRelease = false;//发布完成,把发布状态标记为false
//						if (respData.isSuccess()) {
//							AndroidUtil.showToast(respData.getTips());
//							mProgress.setVisibility(View.GONE);
//							finish();
//						} else if (respData.isLogin()) {
//							startActivity(LoginActivity.newOtherIntent());
//						} else {
//							AndroidUtil.showToast(respData.getTips());
//						}
//					}
//				});
//				uploadAsync.setUpdateListener(new OnUpdateProgress() {
//
//					@Override
//					public void onUpdate(final int count, final int position,
//							final int total, final int currentsize) {
//
//						// TODO 进度条
//						runOnUiThread(new Runnable() {
//
//							@Override
//							public void run() {
//
//								int centi = total * count / 100;//百分之一
//								int current = ((position+1)*total + currentsize) / centi;//当前百分之几
//								mProgress.setProgress(current);
//							}
//						});
//
//					}
//				});
//				uploadAsync.execute();
//			} else {
//				// 发布晒晒
//				uploadAsync = new UploadUtilsAsync(this, getReqcommentData(),
//						mPicsDate);
//				uploadAsync.setListener(new OnSuccessListener() {
//
//					@Override
//					public void onSuccess(RespData respData) {
//						isRelease = false;
//						if (null == respData) {
//							return;
//						}
//						if (respData.isSuccess()) {
//							AndroidUtil.showToast(respData.getTips());
//							mProgress.setVisibility(View.GONE);
//							finish();
//						} else if (respData.isLogin()) {
//							startActivity(LoginActivity.newOtherIntent());
//						} else {
//							AndroidUtil.showToast(respData.getTips());
//						}
//					}
//				});
//				uploadAsync.setUpdateListener(new OnUpdateProgress() {
//
//					@Override
//					public void onUpdate(final int count, final int position, final int total,
//							final int currentsize) {
//						runOnUiThread(new Runnable() {
//							@Override
//							public void run() {
//
//								int centi = total * count / 100;//百分之一
//								int current = ((position+1)*total + currentsize) / centi;//当前百分之几
//								mProgress.setProgress(current);
//							}
//						});
//
//					}
//				});
//				uploadAsync.execute();
//			}

        }
    }

    /**
     * 修改帖子
     *
     * @return
     */
    private Map<String, String> getReqModPostData() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        // 帖子ID
        ArrayList<String> oldpics = getOldPath();
        data.put("pid", String.valueOf(mReleaseData.getPid()));
        // 回复评论ID，如果不是回复则传入0
        data.put("dataid", "0");
        data.put("oldpic", StringUtils.join(oldpics.toArray(), ","));
        // 贴纸id,如果封面设置了贴纸,则传入ptid,否则为0
        // data.put("ptid", String.valueOf(mReleaseData.getPtid()));
        data.put("cid", String.valueOf(chkCateInfo.getCid()));
        // isarea:1//是否显示地区，isarea=1显示地区，isarea=0不显示地区，当不显示地区的时候仍然传入坐标
        data.put("isarea", isChecked ? "1" : "0");
        // 地区坐标，格式：经度,纬度
        data.put("coordinate", localModel.toLongLatPoint());
        // 广东省,深圳市,龙岗区，格式：省,市,区
        data.put("areaname",
                localModel.getProvince() + "," + localModel.getCity() + ","
                        + localModel.getDistrict());
        // 文字内容
        data.put("content", mReleaseEdit.getText().toString());
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("edit_save");
        reqData.setData(data);

        Map<String, String> map = new HashMap<String, String>();
        try {
            map.put("op", reqData.toReqString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private ArrayList<String> getOldPath() {
        ArrayList<String> oldPath = new ArrayList<String>();
        for (String path : mPicsDate) {
            if (path.startsWith(NETWORKPIC)) {// 如果是网络图片,则是旧
                oldPath.add(path);
            }
        }
        return oldPath;
    }

    private ArrayList<String> getNewPath() {
        ArrayList<String> newPath = new ArrayList<String>();
        for (String path : mPicsDate) {
            if (!path.startsWith(NETWORKPIC)) {
                newPath.add(path);
            }
        }
        return newPath;
    }

    private boolean checkcommentData() {
        if (StringUtils.isBlank(mReleaseEdit.getText().toString())) {
            AndroidUtil.showToast(getString(R.string.release_input_tip));
            return false;
        } else if (null == chkCateInfo || chkCateInfo.getCid() == 0) {
            AndroidUtil.showToast(getString(R.string.choose_cate));
            return false;
        }
        return true;
    }

    /**
     * 发布晒晒
     *
     * @return
     */
    private Map<String, String> getReqcommentData() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());

        // 帖子ID
        data.put("pid", String.valueOf(mReleaseData.getPid()));
        // 话题ID，如果是话题里面的，则传入话题ID，否则为0
        data.put("tid", mReleaseData.getTid() + ","
                + mReleaseData.getPtids().get(0));
        // data.put("tid", mReleaseData.getTid() + "");
        // 试用ID，如果是提交试用报告，则传入试用商品ID，否则为0
        data.put("sid", String.valueOf(mReleaseData.getSid()));
        // 活动ID，如果是积分兑换里的晒福利，则传入福利里的aid，否则为0
        data.put("aid", String.valueOf(mReleaseData.getAid()));
        // 贴纸id,如果封面设置了贴纸,则传入ptid,否则为0
        // data.put("ptid", String.valueOf(mReleaseData.getPtid()));
        data.put("cid", String.valueOf(chkCateInfo.getCid()));
        // isarea:1//是否显示地区，isarea=1显示地区，isarea=0不显示地区，当不显示地区的时候仍然传入坐标
        data.put("isarea", isChecked ? "1" : "0");
        // 地区坐标，格式：经度,纬度
        data.put("coordinate", localModel.toLongLatPoint());
        // 广东省,深圳市,龙岗区，格式：省,市,区
        data.put("areaname",
                localModel.getProvince() + "," + localModel.getCity() + ","
                        + localModel.getDistrict());
        // 文字内容
        data.put("content", mReleaseEdit.getText().toString());
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("create");
        reqData.setData(data);

        Map<String, String> map = new HashMap<String, String>();
        try {
            map.put("op", reqData.toReqString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public void showLocation() {
        if (!isChecked) {// false显示
            if (null != localModel) {
                mAreaname.setTextColor(getResources().getColor(
                        R.color.color_666));
                mAreaname
                        .setText(localModel.getProvince() + " "
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
     * 编辑时 获取数据
     */
    private void loadEditData() {
        if (null == mCustomProgress) {
            mCustomProgress = CustomProgress.show(this,
                    getString(R.string.network_wait), true, null);
        } else {
            mCustomProgress.show();
        }
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("pid", String.valueOf(mReleaseData.getPid()));
        ReleaseReq.edit(this, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (null != mCustomProgress) {
                    mCustomProgress.hide();
                }
                if (response.isSuccess()) {
                    Type type = new TypeToken<List<PostItem>>() {
                    }.getType();
                    List<PostItem> resultPostItems = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getData()), type);

                    if (null != resultPostItems && !resultPostItems.isEmpty()) {
                        postItem = resultPostItems.get(0);
                        updateUI();
                    }
                } else {
                    AndroidUtil.showToast(response.getTips());
                }

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (null != mCustomProgress) {
                    mCustomProgress.hide();
                }
                AndroidUtil.showToast(getString(R.string.reqFailed));
            }
        });
    }

    private void updateUI() {

        if (null != postItem.pics && !postItem.pics.isEmpty()) {

            if (mPicsDate == null) {
                mPicsDate = new ArrayList<String>();
            } else {
                mPicsDate.clear();
            }
            for (PictureInfo pic : postItem.pics) {
                mPicsDate.add(pic.url);
            }
            // 初始化发布数据
            mReleaseData.setPaths(mPicsDate);// 图片路径
            ArrayList<Integer> list = new ArrayList<Integer>();
            for (int i = 0; i < mPicsDate.size(); i++) {
                list.add(0);
            }
            mReleaseData.setPtids(list);
            mReleaseData.setLocation(postItem.isarea + "");// 是否显示位置
            mReleaseData.setContent(postItem.content);// 内容
            mReleaseData.setCid(postItem.cid);// cid

            initPic(mPicsDate);
        }
        mReleaseEdit.setText(postItem.content);
        chkCateInfo = new CateInfo();
        chkCateInfo.setName(postItem.catename);
        chkCateInfo.setCid(postItem.cid);
        mAdapter.notifyDataSetChanged();
        // mCate.setText(chkCateInfo.getName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 用于处理编辑图片
        if (resultCode == RESULT_OK) {
            String path = data.getStringExtra("path");
            if (!TextUtils.isEmpty(path)) {
                mPicsDate.remove(currentEditIndex);
                mPicsDate.add(currentEditIndex, path);

                mReleaseData.getPtids().remove(currentEditIndex);
                mReleaseData.getPtids().add(currentEditIndex,
                        data.getIntExtra("ptid", 0));
                // if (currentEditIndex == 0) {
                // mReleaseData.setPtid(data.getIntExtra("ptid", 0));
                // }

                initPic(mPicsDate);
            }
        }
    }

    @Override
    protected void onStart() {
        startLocation();
        if (cateInfos == null) {
            getCateRequst();
        } else {
            mAdapter.notifyDataSetChanged();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != location) {
            location.stopLoction();
        }
        if (isRelease) {//当前是发布状态
            AndroidUtil.showToast("正在后台为您上传帖子");
        }
    }

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

    private void initPop() {

        ctrlDialog = new ReleaseContrlDialog(this);

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
            mFaceHelper = new SelectFaceHelper(CameraActivity1.this,
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
        KeyBoardUtils.closeKeybord(mReleaseEdit, CameraActivity1.this);
    }

    /**
     * 获取分类信息的数据
     */
    private void getCateRequst() {
        mGvCate.setVisibility(View.GONE);
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        ReleaseReq.choosecatalog(this, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (response.isSuccess()) {
                    Type type = new TypeToken<List<CateInfo>>() {
                    }.getType();
                    List<CateInfo> resultCateInfos = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getData()), type);

                    if (null != resultCateInfos && !resultCateInfos.isEmpty()) {
                        cateInfos = resultCateInfos;
                        mAdapter.notifyDataSetChanged();
                        mGvCate.setVisibility(View.VISIBLE);
                    }
                } else {
                    AndroidUtil.showToast(response.getTips());
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

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (cateInfos == null || cateInfos.size() == 0) {
                return 0;
            }
            return cateInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return cateInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return cateInfos.get(position).cid;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = null;
            CateInfo item = (CateInfo) getItem(position);
            if (convertView == null) {
                convertView = View.inflate(getApplication(),
                        R.layout.release_item, null);
                tv = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(tv);
            } else {
                tv = (TextView) convertView.getTag();
            }
            tv.setText(item.name);
            if (chkCateInfo != null && getItemId(position) == chkCateInfo.cid) {
                tv.setSelected(true);
            } else {
                tv.setSelected(false);
            }

            return convertView;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
