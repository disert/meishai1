package com.meishai.ui.popup;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zhaohg.emojiview.EmojiViewEx;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.emoji.MsgEmojiModle;
import com.emoji.SelectFaceHelper;
import com.emoji.SelectFaceHelper.OnFaceOprateListener;
import com.google.gson.reflect.TypeToken;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.util.KeyBoardUtils;
import com.meishai.app.widget.horizontalscrollview.GalleryAdapter;
import com.meishai.app.widget.horizontalscrollview.GalleryAdapter.OnDelClickLitener;
import com.meishai.app.widget.horizontalscrollview.MyRecyclerView;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.CommentInfo;
import com.meishai.entiy.ImageData;
import com.meishai.entiy.PostItem;
import com.meishai.net.ReqData;
import com.meishai.net.RespData;
import com.meishai.net.RespEmoji;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.common.req.PublicReq;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.UploadUtilsAsync;
import com.meishai.util.UploadUtilsAsync.OnSuccessListener;

/**
 * 帖子->发表评论
 *
 * @author sh
 */
public class CommentPopupWindow extends PopupWindow {

    /**
     * 评论成功的监听器 它的方法的参数目前看来没用
     */
    public interface OnCommentSuccessListener {
        void onCommentSuccess(CommentInfo info);
    }

    //评论成功后的回调
    private OnCommentSuccessListener mCommentListener;

    private View mPopView;

    private PostItem postItem;
    private CommentInfo commentInfo;
    private Context mContext;
    private UploadUtilsAsync uploadAsync;//上传评论的异步处理类
    private ImageButton mIgEmoji;//切换表情与键盘的按钮
    private ImageButton mIgUpload;//添加图片的按钮
    private ImageButton close_keybord;//添加图片的按钮 废弃了
    private EditText mTvComment;//评论的输入框
    private EmojiViewEx emojiView;//表情的自定义view,废弃了
    private Button mBtnPublish;//发表评论的按钮

    private LinearLayout lay_photo;//提示框的容器
    private LinearLayout lay_down; //提示框下面的小箭头
    private MyRecyclerView recycle_photo;//提示框中的图片容器
    private GalleryAdapter mAdapter;//提示图片的适配器
    private List<ImageData> mDatas;
    private View addFaceToolView;//表情容器
    // 表情数据
    // private List<MsgEmojiModle> emojiDatas;

    private boolean isVisbilityFace;

    private OnClickListener onClickListener;


    public CommentPopupWindow(Context context) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPopView = inflater.inflate(R.layout.home_comment_popup, null);
        this.setContentView(mPopView);
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.MATCH_PARENT);
        this.init();
        this.initViews();
        this.setListener();
        if (null == GlobalContext.getInstance().getEmojis() || GlobalContext.getInstance().getEmojis().isEmpty()) {
            this.emoji(context);
        } else {
            loadFaceView(GlobalContext.getInstance().getEmojis());
        }
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

    /**
     * 设置评论成功后的监听器
     *
     * @param l
     */
    public void setOnCommentSuccessListener(OnCommentSuccessListener l) {
        mCommentListener = l;
    }

    public void setPostItem(PostItem postItem) {
        this.postItem = postItem;
    }

    public void setCommentInfo(CommentInfo commentInfo) {
        this.commentInfo = commentInfo;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        initListener();
    }

    public void addImageData(ImageData data) {
        if (null == this.mDatas) {
            mDatas = new ArrayList<ImageData>();
        }
        this.mDatas.add(0, data);
        lay_photo.setVisibility(View.VISIBLE);
        lay_down.setVisibility(View.VISIBLE);
        mAdapter.setImgDatas(mDatas);
        mAdapter.notifyDataSetChanged();
    }

    public void setImageDatas(List<ImageData> imageDatas) {
        this.mDatas = imageDatas;
        if (null != this.mDatas && !this.mDatas.isEmpty()) {
            lay_photo.setVisibility(View.VISIBLE);
            lay_down.setVisibility(View.VISIBLE);
            // mAdapter = new GalleryAdapter(mContext, mDatas,
            // R.layout.home_comment_gallery_item);
            // recycle_photo.setAdapter(mAdapter);
            mAdapter.setImgDatas(mDatas);
            mAdapter.notifyDataSetChanged();
        } else {
            hidePhotoView();
        }
    }

    private void hidePhotoView() {
        lay_photo.setVisibility(View.GONE);
        lay_down.setVisibility(View.GONE);
    }

    /**
     * 已选择图片的数量
     *
     * @return
     */
    public int hasSelectCount() {
        return null != this.mDatas ? this.mDatas.size() : 0;
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
        addFaceToolView = (View) mPopView.findViewById(R.id.add_tool);
        mIgEmoji = (ImageButton) mPopView.findViewById(R.id.ig_emoji);
        mIgUpload = (ImageButton) mPopView.findViewById(R.id.ig_upload);
        close_keybord = (ImageButton) mPopView.findViewById(R.id.close_keybord);
        mTvComment = (EditText) mPopView.findViewById(R.id.tv_comment);
        mTvComment.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isVisbilityFace = false;
                addFaceToolView.setVisibility(View.GONE);
                return false;
            }
        });
        emojiView = (EmojiViewEx) mPopView.findViewById(R.id.emojiView);
        // emojiView.setEditText(mTvComment);
        mBtnPublish = (Button) mPopView.findViewById(R.id.btn_publish);

        mDatas = new ArrayList<ImageData>();
        lay_photo = (LinearLayout) mPopView.findViewById(R.id.lay_photo);
        lay_down = (LinearLayout) mPopView.findViewById(R.id.lay_down);
        recycle_photo = (MyRecyclerView) mPopView
                .findViewById(R.id.recycle_photo);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycle_photo.setLayoutManager(linearLayoutManager);
        mAdapter = new GalleryAdapter(mContext, mDatas,
                R.layout.home_comment_gallery_item);
        recycle_photo.setAdapter(mAdapter);

        mAdapter.setOnDelClickLitener(new OnDelClickLitener() {

            @Override
            public void onDelClick(View view, int position) {
                mDatas.remove(position);
                mAdapter.notifyDataSetChanged();
                if (null == mDatas || mDatas.isEmpty()) {
                    hidePhotoView();
                }
            }
        });
    }

    private SelectFaceHelper mFaceHelper = null;

    View.OnClickListener faceClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isVisbilityFace) {
                isVisbilityFace = false;
                addFaceToolView.setVisibility(View.GONE);
                mIgEmoji.setImageResource(R.drawable.ic_emoji_comment);
                KeyBoardUtils.openKeybord(mTvComment, mContext);
            } else {
                isVisbilityFace = true;
                addFaceToolView.setVisibility(View.VISIBLE);
                mIgEmoji.setImageResource(R.drawable.release_soft_input);
                KeyBoardUtils.closeKeybord(mTvComment, mContext);
            }
        }
    };

    OnFaceOprateListener mOnFaceOprateListener = new OnFaceOprateListener() {
        @Override
        public void onFaceSelected(String text) {
            if (!TextUtils.isEmpty(text)) {
                mTvComment.append(text);
            }
        }

        @Override
        public void onFaceDeleted() {
            int selection = mTvComment.getSelectionStart();
            String text = mTvComment.getText().toString();
            if (selection > 0) {
                String text2 = text.substring(selection - 1);
                if ("]".equals(text2)) {
                    int start = text.lastIndexOf("[");
                    int end = selection;
                    mTvComment.getText().delete(start, end);
                    return;
                }
                mTvComment.getText().delete(selection - 1, selection);
            }
        }

    };

    public void initListener() {
        if (null != onClickListener) {
            mIgUpload.setOnClickListener(onClickListener);
        }
    }

    private void setListener() {
        //点击发布的事件
        mBtnPublish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkData()) {
                    closeKeybord();
                    uploadAsync = new UploadUtilsAsync(mContext,
                            getAddCommentData(),
                            getPicsPath(ImageData.IMAGE_LOCAL));
                    uploadAsync.setListener(new OnSuccessListener() {

                        @Override
                        public void onSuccess(RespData respData) {
                            //发布评论成功后的处理
                            if (respData.isSuccess()) {
                                AndroidUtil.showToast(respData.getTips());
                                mTvComment.setText("");
                                dismissPop();
                                if (mCommentListener != null) {
                                    CommentInfo info = new CommentInfo();
                                    mCommentListener.onCommentSuccess(info);
                                }

                            } else if (respData.isLogin()) {
                                mContext.startActivity(LoginActivity
                                        .newOtherIntent());
                            } else {
                                AndroidUtil.showToast(respData.getTips());
                            }
                        }
                    });
                    uploadAsync.execute();
                }
            }
        });
    }

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

    private void updateUI() {
        isVisbilityFace = false;
        mIgEmoji.setImageResource(R.drawable.ic_emoji_comment);
        addFaceToolView.setVisibility(View.GONE);
        KeyBoardUtils.openKeybord(mTvComment, mContext);

        if (null != commentInfo) {
            String commentFint = mContext.getString(R.string.comment_hint);
            mTvComment
                    .setHint(String.format(commentFint, commentInfo.username));
        }
    }

    public void showPop(View v) {
        if (!isShowing()) {
            updateUI();
            // AndroidUtil.toggleSoftInput(mContext);
            this.showAtLocation(v, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL,
                    0, 0);
        }
    }

    public void dismissPop() {
        if (isShowing()) {
            KeyBoardUtils.closeKeybord(mTvComment, mContext);
            // AndroidUtil.toggleSoftInput(mContext);
            this.dismiss();
        }
    }

    private boolean checkData() {
        if (TextUtils.isEmpty(mTvComment.getText().toString())) {
            AndroidUtil.showToast(R.string.tip_comment);
            return false;
        }
        return true;
    }

    private Map<String, String> getAddCommentData() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        // 帖子ID
        data.put("pid", null != postItem ? String.valueOf(postItem.pid) : "0");
        // 回复评论ID，如果不是回复则传入0
        data.put("dataid",
                null != commentInfo ? String.valueOf(commentInfo.dataid) : "0");
        // isarea:1//是否显示地区，isarea=1显示地区，isarea=0不显示地区，当不显示地区的时候仍然传入坐标
        data.put("isarea", "0");
        // 地区坐标，格式：经度,纬度
        data.put("coordinate", "");
        // 广东省,深圳市,龙岗区，格式：省,市,区
        data.put("areaname", "");
        // 文字内容
        data.put("content", mTvComment.getText().toString());
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("addcomment");
        reqData.setData(data);
        Map<String, String> map = new HashMap<String, String>();
        try {
            map.put("op", reqData.toReqString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private void loadFaceView(List<MsgEmojiModle> emojis) {
        if (null == mFaceHelper) {
            mFaceHelper = new SelectFaceHelper(mContext,
                    addFaceToolView, emojis);
            mFaceHelper
                    .setFaceOpreateListener(mOnFaceOprateListener);
        }
        mIgEmoji.setOnClickListener(faceClick);
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
                DebugLog.d(error.getMessage());
            }
        });
    }

    public void closeKeybord() {
        KeyBoardUtils.closeKeybord(mTvComment, mContext);
    }
}
