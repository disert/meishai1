package com.meishai.ui.fragment.usercenter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.listener.IDialogProtocol;
import com.meishai.app.util.AppUtils;
import com.meishai.app.util.DialogManager;
import com.meishai.app.widget.CustomDialog;
import com.meishai.app.widget.CustomDialog.Builder;
import com.meishai.dao.MeiShaiSP;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.MeishaiWebviewActivity;
import com.meishai.util.CacheConfigUtils;
import com.meishai.util.DataCleanManager;
import com.meishai.util.DiskImageCacheUtil;

import java.io.File;

/**
 * 我的-设置
 *
 * @ClassName: UserSettingActivity
 */
public class UserSettingActivity extends BaseActivity implements
        OnClickListener, IDialogProtocol {

    private Context mContext = UserSettingActivity.this;

    private Button mBtnBack;
    private Button btn_exit;

    // 帮助中心
    private String popUrl = "http://www.meishai.com/index.php?m=app&c=pop&a=index";
    private LinearLayout lay_pop;
    // 用户协议
//    private String proUrl = "http://www.meishai.com/pro";
    private String proUrl = "http://www.meishai.com/index.php?m=app&c=pop&a=show&id=99";
    private RelativeLayout lay_pro;
    // 关于美晒
    private String aboutUrl = "http://www.meishai.com/pop/about";
    private RelativeLayout lay_about;
    private TextView tv_version;
    // 联系我们
    private String contactUrl = "http://www.meishai.com/index.php?m=app&c=pop&a=show&id=100";
    private LinearLayout lay_contact;
    // 应用推荐
    private String linkUrl = "http://www.meishai.com/applink";
    private LinearLayout lay_rec;
    private RelativeLayout lay_clear_cache;
    private TextView lay_cache_memory;
    private String cached = "已经使用";

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                UserSettingActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_setting);
        initView();
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: initView
     */
    private void initView() {
        mBtnBack = (Button) this.findViewById(R.id.backMain);
        mBtnBack.setOnClickListener(this);
        btn_exit = (Button) this.findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(this);

        lay_pop = (LinearLayout) this.findViewById(R.id.lay_pop);
        lay_pro = (RelativeLayout) this.findViewById(R.id.lay_pro);
        lay_clear_cache = (RelativeLayout) this.findViewById(R.id.lay_clear_cache);
        lay_cache_memory = (TextView) this.findViewById(R.id.lay_cache_memory);
        lay_cache_memory.setText(cached + getCacheSize());
        lay_about = (RelativeLayout) this.findViewById(R.id.lay_about);
        tv_version = (TextView) this.findViewById(R.id.tv_version);
        String versionName = AppUtils.getVersionName(mContext);
        tv_version.setText(versionName);
        lay_contact = (LinearLayout) this.findViewById(R.id.lay_contact);
        lay_pop.setOnClickListener(this);
        lay_pro.setOnClickListener(this);
        lay_about.setOnClickListener(this);
        lay_contact.setOnClickListener(this);
        lay_clear_cache.setOnClickListener(this);

        lay_rec = (LinearLayout) this.findViewById(R.id.lay_rec);
        lay_rec.setOnClickListener(this);
    }

    /**
     * <p>
     * Title: onClick
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backMain:
                finish();
                break;
            case R.id.btn_exit:
                exit();
                break;
            case R.id.lay_pop:
                mContext.startActivity(MeishaiWebviewActivity.newIntent(popUrl,
                        mContext.getString(R.string.txt_help)));
                break;
            case R.id.lay_pro:
                mContext.startActivity(MeishaiWebviewActivity.newIntent(proUrl,
                        mContext.getString(R.string.txt_protocol)));
                break;
            case R.id.lay_about:
                mContext.startActivity(MeishaiWebviewActivity.newIntent(aboutUrl,
                        mContext.getString(R.string.txt_about)));
                break;
            case R.id.lay_contact:
                mContext.startActivity(MeishaiWebviewActivity.newIntent(contactUrl,
                        mContext.getString(R.string.txt_contact)));
                break;
            case R.id.lay_rec:
                startActivity(MeishaiWebviewActivity.newIntent(linkUrl,
                        mContext.getString(R.string.txt_applink)));
                break;
            case R.id.lay_clear_cache://清除缓存
                clearCache();
                break;
            default:
                break;
        }
    }


    private static final int DIALOG_EXIT_USER = 200001;

    private void exit() {
        CustomDialog builder = createDialogBuilder(mContext,
                getString(R.string.button_text_tips),
                getString(R.string.exit_login_title),
                getString(R.string.button_text_no),
                getString(R.string.button_text_yes)).create(DIALOG_EXIT_USER);
        builder.show();
    }

    /**
     * 清除缓存
     */
    private void clearCache() {
        showProgress("", "正在清理缓存...");
        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                DataCleanManager.cleanInternalCache(UserSettingActivity.this);//清理内部缓存
                //        DataCleanManager.cleanSharedPreference(UserSettingActivity.this);//清理配置文件缓存
                DataCleanManager.cleanCustomCache(DiskImageCacheUtil.getInstance().getCacheDir(DiskImageCacheUtil.ROOT_DIR));//清理图片内部缓存
                DataCleanManager.cleanCustomCache(DiskImageCacheUtil.getInstance().getCacheDir("meishai/cache"));//清理编辑图片所留下的缓存
                CacheConfigUtils.clearCache();//清理缓存对应的配置文件

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                hideProgress();
                lay_cache_memory.setText(cached + "0M");
                showToast("已经清理");
            }
        }.execute();


    }


    /**
     * 获取缓存大小的字符串,这里包含了应用自身的缓存 以及 自定义的图片缓存,
     *
     * @return
     */
    private String getCacheSize() {
        try {
            double cacheSize = DataCleanManager.getFolderSize(getCacheDir());//应用缓存
//            cacheSize += DataCleanManager.getFolderSize(new File("/data/data/"
//                    + getPackageName() + "/shared_prefs"));//配置文件的缓存
            cacheSize += DataCleanManager.getFolderSize(DiskImageCacheUtil.getInstance().getCacheDir(DiskImageCacheUtil.ROOT_DIR));//自定义图片缓存
            return DataCleanManager.getFormatSize(cacheSize);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public Builder createDialogBuilder(Context context, String title,
                                       String message, String positiveBtnName, String negativeBtnName) {
        return DialogManager.createMessageDialogBuilder(context, title,
                message, positiveBtnName, negativeBtnName, this);
    }

    /**
     * 取消事件
     */
    @Override
    public void onPositiveBtnClick(int id, DialogInterface dialog, int which) {

    }

    /**
     * 确认事件
     */
    @Override
    public void onNegativeBtnClick(int id, DialogInterface dialog, int which) {
        switch (id) {
            case DIALOG_EXIT_USER:
                MeiShaiSP.getInstance().clear();
                clearShareAccount();
                Intent intent = new Intent(ConstantSet.ACTION_SHOW_HOME);
                sendBroadcast(intent);
                finish();
                break;
            default:
                break;
        }
    }

    private void clearShareAccount() {
        Platform qq = ShareSDK.getPlatform(QQ.NAME);
        qq.removeAccount();
        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
        wechat.removeAccount();
        Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
        sina.removeAccount();

    }
}
