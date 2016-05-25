package com.meishai.ui.fragment.camera;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.meishai.app.widget.horizontalscrollview.GalleryAdapter;
import com.meishai.app.widget.horizontalscrollview.GalleryAdapter.OnDelClickLitener;
import com.meishai.app.widget.horizontalscrollview.GalleryAdapter.OnItemClickLitener;
import com.meishai.app.widget.horizontalscrollview.MyRecyclerView;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.CateInfo;
import com.meishai.entiy.ImageData;
import com.meishai.entiy.PostItem;
import com.meishai.entiy.PostItem.PictureInfo;
import com.meishai.net.ReqData;
import com.meishai.net.RespData;
import com.meishai.net.RespEmoji;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.dialog.CameraDialog;
import com.meishai.ui.fragment.camera.req.ReleaseReq;
import com.meishai.ui.fragment.common.req.PublicReq;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.ComUtils;
import com.meishai.util.GsonHelper;
import com.meishai.util.ImageUtil;
import com.meishai.util.StringUtils;
import com.meishai.util.UploadUtilsAsync;
import com.meishai.util.UploadUtilsAsync.OnSuccessListener;

/**
 * 拍照主界面
 *
 * @author sh
 */
public class CameraActivity extends BaseActivity implements OnClickListener {
    public final static int RESULT_CAMERA = 0x2001;

    public final static int TID_UNKNOWN = 0;
    private UploadUtilsAsync uploadAsync;
    private CustomProgress mCustomProgress;
    // 最大选择图片
    private int maxSelected = 9;
    private CameraDialog cameraDialog;
    private View view;
    private LocalModel localModel = null;
    private TsLocation location = null;
    private Button btn_cancel;
    private Button btn_save;
    private RelativeLayout mRoot;
    private RelativeLayout lay_choose_cate;
    private TextView txt_cate;
    private EditText release_edit;
    private LinearLayout lay_emoji;
    private ImageButton ig_emoji;
    private ImageButton ig_toggle_input;
    private TextView areaname;
    // 默认未选中
    private boolean isChecked = false;
    private ImageButton chk_location;
    // private EmojiViewEx emojiView;

    private View addFaceToolView;

    private boolean isVisbilityFace;

    // private ChooseCateDialog cateDialog;
    private CateInfo chkCateInfo = null;

    private GalleryAdapter mAdapter;
    private MyRecyclerView camera_photo;
    private List<ImageData> mDatas;

    // private EmojiPopupWindow emojiPopupWindow;
    // 发布晒晒(默认)
    public static final String OPER_PUBLISH = "publish";

    public static final String OPER_PUBLISH_CATE = "publishCate";
    public PostItem postItem = null;
    // 话题
    public static final String OPER_TOPIC = "topic";
    // 修改帖子
    public static final String OPER_POST = "mod_post";
    // 试用报告
    public static final String OPER_TRY = "try";
    // 晒福利
    public static final String OPER_WELFARE = "welfare";

    // 操作方式 默认操作方式 (发布晒晒)
    private String OPER_SOURCE = OPER_PUBLISH;

    // 帖子id
    private int pid = 0;
    // 话题
    private int tid = 0;
    // 试用
    private int sid = 0;
    // 福利
    private long aid = 0;

    // 发布晒晒
    public static Intent newPublishIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                CameraActivity.class);
        intent.putExtra("oper", OPER_PUBLISH);
        return intent;
    }

    public static Intent newPublishIntentAndCate(CateInfo cateInfo) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                CameraActivity.class);
        intent.putExtra("oper", OPER_PUBLISH_CATE);
        intent.putExtra("cateInfo", cateInfo);
        return intent;
    }

    // 修改帖子
    public static Intent newPostIntent(int pid) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                CameraActivity.class);
        intent.putExtra("pid", pid);
        intent.putExtra("oper", OPER_POST);
        return intent;
    }

    // 话题
    public static Intent newIntent(int tid) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                CameraActivity.class);
        intent.putExtra("tid", tid);
        intent.putExtra("oper", OPER_TOPIC);
        return intent;
    }

    // 试用报告
    public static Intent newTryIntent(int sid) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                CameraActivity.class);
        intent.putExtra("sid", sid);
        intent.putExtra("oper", OPER_TRY);
        return intent;
    }

    // 晒福利
    public static Intent newWelfareIntent(long aid) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                CameraActivity.class);
        intent.putExtra("aid", aid);
        intent.putExtra("oper", OPER_WELFARE);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.release);
        view = getWindow().getDecorView();
        registerBoradcastReceiver();
        initView();
        initData();
        initDialog();
        // initPopup();
        if (OPER_POST.equals(OPER_SOURCE)) {
            loadEditData();
        }

        if (null == GlobalContext.getInstance().getEmojis() || GlobalContext.getInstance().getEmojis().isEmpty()) {
            this.emoji(this);
        } else {
            loadFaceView(GlobalContext.getInstance().getEmojis());
        }

        // 更新视图
        // updateUI();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            try {
                pid = bundle.getInt("pid");
                OPER_SOURCE = bundle.getString("oper");

                if (TextUtils.isEmpty(OPER_SOURCE)) {
                    OPER_SOURCE = OPER_PUBLISH;
                } else if (OPER_TOPIC.equals(OPER_SOURCE)) {
                    tid = bundle.getInt("tid");
                } else if (OPER_TRY.equals(OPER_SOURCE)) {
                    sid = bundle.getInt("sid");
                } else if (OPER_WELFARE.equals(OPER_SOURCE)) {
                    aid = bundle.getLong("aid");
                } else if (OPER_PUBLISH_CATE.equals(OPER_SOURCE)) {
                    chkCateInfo = (CateInfo) bundle.getSerializable("cateInfo");
                    txt_cate.setText(chkCateInfo.getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
                pid = 0;
                OPER_SOURCE = OPER_PUBLISH;
            }
        }

    }

    @Override
    protected void onStart() {
        startLocation();
        super.onStart();
    }

    private void updateUI() {
        if (null != postItem && OPER_POST.equals(OPER_SOURCE)) {
            release_edit.setText(postItem.content);
            if (null != postItem.pics && !postItem.pics.isEmpty()) {
                ArrayList<ImageData> imgList = new ArrayList<ImageData>();
                for (PictureInfo info : postItem.pics) {
                    ImageData imageData = ImageData.getNetworkImage(info.url);
                    imgList.add(imageData);
                }
                addAllImageData(imgList);
            }
            chkCateInfo = new CateInfo();
            chkCateInfo.setName(postItem.catename);
            chkCateInfo.setCid(postItem.cid);
            txt_cate.setText(chkCateInfo.getName());
        }
    }

    private void initView() {
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
        btn_save = (Button) view.findViewById(R.id.btn_save);
        mRoot = (RelativeLayout) view.findViewById(R.id.root);
        lay_choose_cate = (RelativeLayout) view
                .findViewById(R.id.lay_choose_cate);
        txt_cate = (TextView) view.findViewById(R.id.txt_cate);

        mDatas = new ArrayList<ImageData>();
        mDatas.add(ImageData.getDefImage());
        camera_photo = (MyRecyclerView) view.findViewById(R.id.camera_photo);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        camera_photo.setLayoutManager(linearLayoutManager);
        mAdapter = new GalleryAdapter(this, mDatas);
        camera_photo.setAdapter(mAdapter);
        mAdapter.setOnItemClickLitener(new OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                hideEmoji();
                ImageData data = mDatas.get(position);
                if (ImageData.IMAGE_DEF == data.getType()) {
                    int c = hasSelectedCount();
                    cameraDialog.setMaxSelected(maxSelected - c);
                    cameraDialog.showDialog();
                }
            }
        });
        mAdapter.setOnDelClickLitener(new OnDelClickLitener() {

            @Override
            public void onDelClick(View view, int position) {
                mDatas.remove(position);
                // 是否需要默认 添加图片 默认需要
                boolean isNeedDef = true;
                if (maxSelected > mDatas.size()) {
                    for (ImageData imgData : mDatas) {
                        if (ImageData.IMAGE_DEF == imgData.getType()) {
                            isNeedDef = false;
                            break;
                        }
                    }
                    if (isNeedDef) {
                        mDatas.add(mDatas.size(), ImageData.getDefImage());
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        lay_emoji = (LinearLayout) view.findViewById(R.id.lay_emoji);
        ig_emoji = (ImageButton) view.findViewById(R.id.ig_emoji);
        ig_emoji.setTag("emoji");
        ig_toggle_input = (ImageButton) view.findViewById(R.id.ig_toggle_input);
        areaname = (TextView) view.findViewById(R.id.areaname);
        chk_location = (ImageButton) view.findViewById(R.id.chk_location);
        release_edit = (EditText) view.findViewById(R.id.release_edit);
        release_edit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                KeyBoardUtils.openKeybord(release_edit, CameraActivity.this);
                lay_emoji.setVisibility(View.VISIBLE);
            }
        });
        release_edit.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isVisbilityFace = false;
                addFaceToolView.setVisibility(View.GONE);
                return false;
            }
        });

        addFaceToolView = (View) this.findViewById(R.id.add_tool);

        // emojiView = (EmojiViewEx) view.findViewById(R.id.emojiView);
        // emojiView.setEditText(release_edit);
        btn_save.setOnClickListener(this);
        ig_emoji.setOnClickListener(faceClick);
        ig_toggle_input.setOnClickListener(this);
        lay_choose_cate.setOnClickListener(this);
        chk_location.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hideEmoji();
                if (!isChecked) {
                    if (null != localModel) {
                        areaname.setTextColor(getResources().getColor(
                                R.color.color_666));
                        areaname.setText(localModel.getProvince() + " "
                                + localModel.getCity() + " "
                                + localModel.getDistrict());
                    }
                    chk_location
                            .setImageResource(R.drawable.release_localtion_show);
                    isChecked = true;
                } else {
                    chk_location
                            .setImageResource(R.drawable.release_localtion_hide);
                    areaname.setTextColor(getResources().getColor(
                            R.color.txt_color));
                    areaname.setText(getResources().getString(
                            R.string.location_hide));
                    isChecked = false;
                }
            }
        });
    }

    // 获取已经选择的图片数量
    private int hasSelectedCount() {
        int c = 0;
        for (ImageData data : mDatas) {
            if (ImageData.IMAGE_DEF != data.getType()) {
                c++;
            }
        }
        return c;
    }

    private void hideEmoji() {
        lay_emoji.setVisibility(View.GONE);
        isVisbilityFace = false;
        addFaceToolView.setVisibility(View.GONE);
        ig_emoji.setImageResource(R.drawable.release_bq_selector);
        // emojiView.hide();
        KeyBoardUtils.closeKeybord(release_edit, CameraActivity.this);
    }

    private SelectFaceHelper mFaceHelper = null;

    View.OnClickListener faceClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isVisbilityFace) {
                isVisbilityFace = false;
                addFaceToolView.setVisibility(View.GONE);
                ig_emoji.setImageResource(R.drawable.release_bq_selector);
                KeyBoardUtils.openKeybord(release_edit, CameraActivity.this);
            } else {
                isVisbilityFace = true;
                addFaceToolView.setVisibility(View.VISIBLE);
                ig_emoji.setImageResource(R.drawable.release_soft_input);
                KeyBoardUtils.closeKeybord(release_edit, CameraActivity.this);
            }
        }
    };

    OnFaceOprateListener mOnFaceOprateListener = new OnFaceOprateListener() {
        @Override
        public void onFaceSelected(String text) {
            if (!TextUtils.isEmpty(text)) {
                release_edit.append(text);
            }
        }

        @Override
        public void onFaceDeleted() {
            int selection = release_edit.getSelectionStart();
            String text = release_edit.getText().toString();
            if (selection > 0) {
                String text2 = text.substring(selection - 1);
                if ("]".equals(text2)) {
                    int start = text.lastIndexOf("[");
                    int end = selection;
                    release_edit.getText().delete(start, end);
                    return;
                }
                release_edit.getText().delete(selection - 1, selection);
            }
        }

    };

    /**
     * 获取提交图片的路径集合
     *
     * @return
     */
    private ArrayList<String> getPicsPath(int type) {
        ArrayList<String> paths = new ArrayList<String>();
        for (ImageData data : mDatas) {
            if (type == data.getType()) {
                paths.add(data.getPath());
            }
        }
        return paths;
    }

    private void initDialog() {
        // cateDialog = new ChooseCateDialog(this, chkCateInfo,
        // new OnCateCheckedListener() {
        //
        // @Override
        // public void checkedCate(CateInfo cateInfo) {
        // chkCateInfo = cateInfo;
        // txt_cate.setText(chkCateInfo.getName());
        // }
        // });
        cameraDialog = new CameraDialog(this);
    }

    // public void toggleEmojiView() {
    // this.emojiView.toggle();
    // }

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

    private boolean checkcommentData() {
        if (StringUtils.isBlank(release_edit.getText().toString())) {
            AndroidUtil.showToast(getString(R.string.release_input_tip));
            return false;
        } else if (null == chkCateInfo) {
            AndroidUtil.showToast(getString(R.string.choose_cate));
            return false;
        }
        return true;
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
        ArrayList<String> oldpics = getPicsPath(ImageData.IMAGE_NETWORK);
        data.put("pid", String.valueOf(pid));
        // 回复评论ID，如果不是回复则传入0
        data.put("dataid", "0");
        data.put("oldpic", StringUtils.join(oldpics.toArray(), ","));
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
        data.put("content", release_edit.getText().toString());
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

    private Map<String, String> getReqcommentData() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        if (OPER_PUBLISH.equals(OPER_SOURCE)
                || OPER_PUBLISH_CATE.equals(OPER_SOURCE)) {
            // 帖子ID
            data.put("pid", String.valueOf(pid));
            // 话题ID，如果是话题里面的，则传入话题ID，否则为0
            data.put("tid", "0");
            // 试用ID，如果是提交试用报告，则传入试用商品ID，否则为0
            data.put("sid", "0");
            // 活动ID，如果是积分兑换里的晒福利，则传入福利里的aid，否则为0
            data.put("aid", "0");
        } else if (OPER_TOPIC.equals(OPER_SOURCE)) {
            // 话题ID，如果是话题里面的，则传入话题ID，否则为0
            data.put("tid", String.valueOf(tid));
            // 试用ID，如果是提交试用报告，则传入试用商品ID，否则为0
            data.put("sid", "0");
            // 活动ID，如果是积分兑换里的晒福利，则传入福利里的aid，否则为0
            data.put("aid", "0");
        } else if (OPER_TRY.equals(OPER_SOURCE)) {
            // 话题ID，如果是话题里面的，则传入话题ID，否则为0
            data.put("tid", "0");
            // 试用ID，如果是提交试用报告，则传入试用商品ID，否则为0
            data.put("sid", String.valueOf(sid));
            // 活动ID，如果是积分兑换里的晒福利，则传入福利里的aid，否则为0
            data.put("aid", "0");
        } else if (OPER_WELFARE.equals(OPER_SOURCE)) {
            // 话题ID，如果是话题里面的，则传入话题ID，否则为0
            data.put("tid", "0");
            // 试用ID，如果是提交试用报告，则传入试用商品ID，否则为0
            data.put("sid", "0");
            // 活动ID，如果是积分兑换里的晒福利，则传入福利里的aid，否则为0
            data.put("aid", String.valueOf(aid));
        }
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
        data.put("content", release_edit.getText().toString());
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
        data.put("pid", String.valueOf(pid));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // case R.id.ig_emoji:
            // String tag = (String) v.getTag();
            // if (tag.equals("emoji")) {
            // KeyBoardUtils.closeKeybord(release_edit, CameraActivity.this);
            // toggleEmojiView();
            // ig_emoji.setImageResource(R.drawable.release_soft_input);
            // ig_emoji.setTag("softInput");
            // } else {
            // toggleEmojiView();
            // KeyBoardUtils.openKeybord(release_edit, CameraActivity.this);
            // ig_emoji.setImageResource(R.drawable.release_bq_selector);
            // ig_emoji.setTag("emoji");
            // }
            // break;
            case R.id.ig_toggle_input:
                hideEmoji();
                break;
            case R.id.lay_choose_cate:
                hideEmoji();
                startActivity(ChooseCateActivity.newIntent(chkCateInfo));
                // cateDialog.show();
                break;
            case R.id.btn_save:
                if (checkcommentData()) {
                    KeyBoardUtils.closeKeybord(release_edit, CameraActivity.this);
                    // 发布晒晒
                    if (OPER_PUBLISH.equals(OPER_SOURCE)
                            || OPER_TOPIC.equals(OPER_SOURCE)
                            || OPER_TRY.equals(OPER_SOURCE)
                            || OPER_WELFARE.equals(OPER_SOURCE)) {
                        uploadAsync = new UploadUtilsAsync(this,
                                getReqcommentData(),
                                getPicsPath(ImageData.IMAGE_LOCAL));
                        uploadAsync.setListener(new OnSuccessListener() {

                            @Override
                            public void onSuccess(RespData respData) {
                                if (null == respData) {
                                    return;
                                }
                                if (respData.isSuccess()) {
                                    AndroidUtil.showToast(respData.getTips());
                                    finish();
                                } else if (respData.isLogin()) {
                                    startActivity(LoginActivity.newOtherIntent());
                                } else {
                                    AndroidUtil.showToast(respData.getTips());
                                }
                            }
                        });
                        uploadAsync.execute();
                    } else if (OPER_POST.equals(OPER_SOURCE)) {
                        // 修改帖子
                        uploadAsync = new UploadUtilsAsync(this,
                                getReqModPostData(),
                                getPicsPath(ImageData.IMAGE_LOCAL));
                        uploadAsync.setListener(new OnSuccessListener() {

                            @Override
                            public void onSuccess(RespData respData) {
                                if (respData.isSuccess()) {
                                    AndroidUtil.showToast(respData.getTips());
                                    finish();
                                } else if (respData.isLogin()) {
                                    startActivity(LoginActivity.newOtherIntent());
                                } else {
                                    AndroidUtil.showToast(respData.getTips());
                                }
                            }
                        });
                        uploadAsync.execute();
                    }
                }
                // addcomment();
                break;
            case R.id.btn_cancel:
                finish();
                break;
            // case R.id.take_camera:
            // takeCamera();
            // cameraDialog.showDialog();
            // break;
            default:
                break;
        }

    }

    private int width = 300; // 裁剪后默认图片宽度
    private int height = 300; // 裁剪后默认图片高度

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            doPhoto(requestCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void doPhoto(int requestCode, Intent intent) {
        if (requestCode == ConstantSet.SELECT_PIC_BY_TACK_PHOTO) {
            if (cameraDialog.getPhotoUri() == null) {
//				AndroidUtil.showToast("拍照图片文件出错");
                return;
            }
            // 将拍照后图片偏差修正
            // String path = AndroidUtil.imageUri2Path(photoUri);
            // int degree = AndroidUtil.readPictureDegree(path);
            // Bitmap bmp = ImageUtil.rotateBitmap(path, degree);
            // File f = ImageUtil.saveBitMap2File(path, bmp);
            // bmp.recycle();
            // bmp = null;
            // cropImage(Uri.fromFile(f));

            // 裁剪
            // cropImage(photoUri);

            String uriPath = AndroidUtil.imageUri2Path(cameraDialog
                    .getPhotoUri());
            final String targetPath = this.getCacheDir() + BitmapUtils.toRegularHashCode(uriPath) + ".jpg";
            BitmapUtils.compressBitmap(uriPath, targetPath, 800);  //压缩
            ImageData data = ImageData.getLocalImage(targetPath);
            addImageData(data);
        } else if (requestCode == ConstantSet.PHOTO_RESOULT) {
            Bundle d = intent.getExtras();
            if (d != null) {
                Bitmap bm = d.getParcelable("data");
                String picN = ComUtils.getRandomAlphamericStr(8) + ".jpg";
                File temp = new File(getCacheDir(), picN);

                try {
                    if (temp.exists()) {
                        temp.delete();
                    }
                    temp.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ImageUtil.saveBitMap2File(temp, bm);
                // camera_photo.setImageBitmap(bm);

                // bm.recycle();
                // bm = null;

                // 缩小图片质量
                // bm = NativeImageLoader.getInstance().decodeBitmapForFile(
                // temp.getAbsolutePath(), 2);
                // ImageUtil.saveBitMap2File(temp, bm);
                // bm.recycle();
                // bm = null;
            }
        }
    }

    private void cropImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*")
                .putExtra("crop", "true")
                        // 发送裁剪信号
                .putExtra("aspectX", 1)
                        // X方向上的比例
                .putExtra("aspectY", 1)
                        // Y方向上的比例
                .putExtra("outputX", width)
                        // 裁剪区的宽
                .putExtra("outputY", height)
                        // 裁剪区的高
                .putExtra("return-data", true)
                        // 是否返回数据
                .putExtra("scale", true)
                        // 是否保留比例
                .putExtra("scaleUpIfNeeded", true)
                        // 黑边
                        // .putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f))
                .putExtra("noFaceDetection", true)
                        // 关闭人脸检测
                .putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, ConstantSet.PHOTO_RESOULT);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        setResult(RESULT_CAMERA);
        finish();
    }

    private void addImageData(ImageData data) {
        mDatas.add(0, data);
        int c = hasSelectedCount();
        if (maxSelected <= c) {
            mDatas.remove(mDatas.size() - 1);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void addAllImageData(List<ImageData> datas) {
        mDatas.addAll(0, datas);
        int c = hasSelectedCount();
        if (maxSelected <= c) {
            mDatas.remove(mDatas.size() - 1);
        }
        mAdapter.notifyDataSetChanged();
    }

    // 修改帖子的时候 新增加的图片排在后面
    private void addAllImageDataAfter(List<ImageData> datas) {
        int index = 0;
        if (null != mDatas && !mDatas.isEmpty()) {
            for (int i = 0; i < mDatas.size(); i++) {
                if (ImageData.IMAGE_DEF == mDatas.get(i).getType()) {
                    index = i;
                    break;
                }
            }
        }
        mDatas.addAll(index, datas);
        int c = hasSelectedCount();
        if (maxSelected <= c) {
            mDatas.remove(mDatas.size() - 1);
        }
        mAdapter.notifyDataSetChanged();
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConstantSet.ACTION_NAME)) {
                ArrayList<String> imagePaths = intent
                        .getStringArrayListExtra(ConstantSet.CHOOSE_DATA);
                List<ImageData> datas = new ArrayList<ImageData>();
                if (null != imagePaths && !imagePaths.isEmpty()) {
                    for (String path : imagePaths) {
                        String targetPath = context.getCacheDir() + BitmapUtils.toRegularHashCode(path) + ".jpg";
                        BitmapUtils.compress(path, targetPath, 800);  //压缩
                        ImageData data = ImageData.getLocalImage(targetPath);
                        datas.add(data);
                    }
                }
                if (OPER_POST.equals(OPER_SOURCE)) {
                    addAllImageDataAfter(datas);
                } else {
                    addAllImageData(datas);
                }
            } else if (action.equals(ConstantSet.ACTION_CATE)) {
                // 分类选择
                chkCateInfo = (CateInfo) intent
                        .getSerializableExtra(ConstantSet.CHOOSE_CATE);
                txt_cate.setText(chkCateInfo.getName());
            }
        }
    };

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(ConstantSet.ACTION_NAME);
        myIntentFilter.addAction(ConstantSet.ACTION_CATE);
        // 注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private void loadFaceView(List<MsgEmojiModle> emojis) {
        if (null == mFaceHelper) {
            mFaceHelper = new SelectFaceHelper(CameraActivity.this,
                    addFaceToolView, emojis);
            mFaceHelper
                    .setFaceOpreateListener(mOnFaceOprateListener);
        }
        ig_emoji.setOnClickListener(faceClick);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        if (null != location) {
            location.stopLoction();
        }
    }
}
