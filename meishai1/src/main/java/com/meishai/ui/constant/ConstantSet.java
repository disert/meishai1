/**
 */
package com.meishai.ui.constant;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

import com.meishai.GlobalContext;
import com.meishai.R;

/**
 * @author shaohu
 * @ClassName: ConstantSet
 * @Description: 保存常量的类
 * @date 2015-3-1 下午03:06:24
 */
public class ConstantSet {

    public static final int MAX_IMAGE_COUNT = 6;//最大上传图片的数量
    public static final int MAX_TAGS_COUNT = 5;//最大选择的标签的数量
    public static int SCREEN_WIDTH = 0; // 屏幕宽度
    public static int SCREEN_HEIGHT = 0; // 屏幕高度
    public static final String APP_SIGN = "Meishai";
    public static final String TIME_PATTERN = "yyyy-MM-dd HH:mm";
    public static final String APP_ID_QQ = "1104295927";// qq第三方登录的appid
    public static final String APP_ID_WX = "wx6e03e62c61953966";// 微信第三方登录的appid
    // 其他变量
    public static final String ACTION_WEIXIN_LOGIN = "weixin login";
    public static final String EXTRA_OPENAPI_AUTH_RESPONSE = "token";
    /**
     * 新浪微博第三方登录
     */
    public static final String APP_KEY = "2045436852";
    /**
     * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
     * <p/>
     * <p>
     * 注：关于授权回调页对移动客户端应用来说对用户是不可见的，所以定义为何种形式都将不影响， 但是没有定义将无法使用 SDK 认证登录。
     * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
     * </p>
     */
    public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    /**
     * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
     * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利 选择赋予应用的功能。
     * <p/>
     * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的 使用权限，高级权限需要进行申请。
     * <p/>
     * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
     * <p/>
     * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
     * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
     */
    public static final String SCOPE = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";

    // 每页数据数量
    public static final String PAGE_SIZE = "10";
    public static final String USERID = "userid";
    public static final String C_MEMBER = "member";
    public static final String BUNDLE_ID = "ID";
    // 图片选择 广播ACTION
    public static final String ACTION_NAME = "IMAGE_CHOOSE";

    // 图片选择 评价截图
    public static final String IMAGE_SCREEN = "IMAGE_SCREEN";
    // 分类选择
    public static final String ACTION_CATE = "CATE_CHOOSE";
    public static final String CHOOSE_CATE = "choose_cate";
    public static final String CHOOSE_DATA = "choose_data";
    // 登录成功广播
    public static final String ACTION_LOGIN_SUCCESS = "LOGIN_SUCCESS";
    // 显示主页广播
    public static final String ACTION_SHOW_HOME = "SHOW_HOME";
    // 使用照相机拍照获取图片
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1001;
    // 裁剪相片通知
    public static final int PHOTO_RESOULT = 1003;

    // 下拉
    public static final int PULLTOREFRESH_START = 0;
    // 上拉
    public static final int PULLTOREFRESH_END = 0;


    //裁剪下来的图片存放地址的key
    public static final String CROP_IMAGE_PATH = "CROP_FILE_PATH";
    //编辑后的图片存放的地址
    public static final String EDITED_FILE_PATH = "EDITED_FILE_PATH";

    //当用户修改或者删除了帖子时的action
    public static final String POST_MOD = "ctrl_mod_post";
    public static final String POST_DELETE = "ctrl_delete_post";

    //启动图片保存的位置
    public static final String SPLASH_PATH = "/meishai";
    //编辑图片保持的位置
    public static final String EDIT_PATH = "meishai/cache";
    //

    //启动activity的模式
    public static int ADD_MODE = 2;//发布界面直达的添加图片模式
    public static int RETURN_MODE = 1;//等待返回模式
    public static int COMMEND_MODE = 0;//普通启动模式

    //h5页面中的一些特殊的链接的前缀
    public static final String MEISHAI_HOME_PAGE = "http://www.meishai.com/u/";//美晒个人主页
    public static final String MEISHAI_POINT_GOODS = "http://www.meishai.com/goods/point/";//美晒积分商城
    public static final String MEISHAI_QIANG_GOODS = "http://www.meishai.com/goods/qiang/";//美晒疯抢详情
    public static final String MEISHAI_SHARE_FRIEND = "http://www.meishai.com/invite/";//邀请好友
    public static final String MEISHAI_LOGIN = "http://www.meishai.com/login";//美晒登陆
    public static final String MEISHAI_SELL_GOODS = "http://www.meishai.com/goods/item/";//商品销售


    public static final String TMALL_APP = "tmall://";//天猫app
    public static final String TAOBAO_APP = "taobao://";//淘宝app
    public static final String TAOBAO_DETAIL = "http://h5.m.taobao.com/awp/core/detail.htm";//淘宝商品详情
    public static final String TMALL_DETAIL = "https://detail.m.tmall.com/item.htm";//天猫商品详情
    public static final String JD_DETAIL = "http://item.m.jd.com/product/";//京东商品详情

    //默认图片的bitmap
    public static Bitmap defaultBitmap = BitmapFactory.decodeResource(GlobalContext.getInstance().getResources(), R.drawable.place_default);


    //DragTopLayout的是否滚动顶部view的广播
    public static final String ACTION_SCRLL_TOPVIEW = "ACTION_SCRLL_TOPVIEW";

    //编辑页面的图片被删除的广播
    public static final String ACTION_PIC_DELETE = "ACTION_PIC_DELETE";
    //发布页面设置某个图片为首图时的广播
    public static final String ACTION_PIC_FIRST = "ACTION_PIC_FIRST";
    //发布晒晒的完成的广播
    public static final String ACTION_RELEASE = "ACTION_RELEASE";

    public static LruCache<String, Bitmap> MEMORY_CACHE;
}
