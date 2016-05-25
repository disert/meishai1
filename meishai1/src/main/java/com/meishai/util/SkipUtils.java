package com.meishai.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.H5Data;
import com.meishai.entiy.PostItem;
import com.meishai.entiy.SplashData;
import com.meishai.entiy.UserInfo;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.fragment.MeishaiWebviewActivity;
import com.meishai.ui.fragment.common.req.PublicReq;
import com.meishai.ui.fragment.home.DaysTopicActivity;
import com.meishai.ui.fragment.home.DisCloseActivity;
import com.meishai.ui.fragment.meiwu.MeiWuCateDetailActivity;
import com.meishai.ui.fragment.meiwu.MeiWuSpecialShowActivity;
import com.meishai.ui.fragment.meiwu.MeiWuStratDetailActivity1;
import com.meishai.ui.fragment.home.HomePageActivity;
import com.meishai.ui.fragment.home.HomePageActivity1;
import com.meishai.ui.fragment.home.HomeTopicListActivity;
import com.meishai.ui.fragment.home.PostShowActivity;
import com.meishai.ui.fragment.home.TopicShowActivity;
import com.meishai.ui.fragment.meiwu.MeiWuStratListActivity;
import com.meishai.ui.fragment.tryuse.FuliSheDetailActivity1;
import com.meishai.ui.fragment.tryuse.PointRewardCateActivity;
import com.meishai.ui.fragment.usercenter.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 幻灯片和小应用的跳转类型的约定
 *
 * @author Administrator yl
 */
public class SkipUtils {

    private static SplashData mSplashData;
    private static Response.ErrorListener mErrorListenr;

    /**
     * @param value 根据类型的不同,所提供的不同的标记,当type是pc
     *              类型时,value是一个访问网络对应的URL,其他时候为id值,如果不需要可以传入""
     * @param type  要跳转数据的类型
     */
    public static void type2Do(Context context, String value, String type) {
        String title = context.getString(R.string.shaishai);

        if ("post_view".equals(type)) {
            // 晒晒详情 已改
            PostItem item = new PostItem();
            item.pid = getIdByString(value);
            context.startActivity(PostShowActivity.newIntent(item,
                    PostShowActivity.FROM_ADS));
        } else if ("topic_view".equals(type)) {
            // 话题讨论 已改
            int tid = getIdByString(value);
            Intent intent = TopicShowActivity.newIntent(tid);
            context.startActivity(intent);
        } else if ("user_index".equals(type)) {
            // 会员首页 已改
            Intent intent = HomePageActivity1.newIntent(value);
            context.startActivity(intent);
        } else if ("point_index".equals(type)) {
            // 积分首页,默认积分兑换,传4
            context.startActivity(PointRewardCateActivity.newIntent(4));
        } else if ("point_view".equals(type)) {
            // 积分详情 已改,还需商定value的值
            context.startActivity(FuliSheDetailActivity1.newIntent(Integer.parseInt(value),
                    0));
        } else if ("pc".equals(type)) {
            // pc端页面
            context.startActivity(MeishaiWebviewActivity.newIntent(value));
        } else {
            slideSkip(context, type, value);
        }
    }

    public static void slideSkip(Context context, String type, String value) {
        // g_list:攻略分类列表 g_list:攻略分类列表
        if ("g_list".equals(type)) {
            if (TextUtils.isEmpty(value)) {
                return;
            }
            Intent intent = MeiWuCateDetailActivity.newIntent(Integer
                    .parseInt(value));
            context.startActivity(intent);
            // p_show:品质详情页面
        } else if ("p_list".equals(type)) {
            if (TextUtils.isEmpty(value)) {
                return;
            }
            context.startActivity(MeiWuSpecialShowActivity.newIntent(Integer
                    .parseInt(value)));
            // t_list:话题列表页面
        } else if ("t_list".equals(type)) {
            context.startActivity(new Intent(context,
                    HomeTopicListActivity.class));
            // s_show:晒晒详情页面
        } else if ("s_show".equals(type)) {
            if (TextUtils.isEmpty(value)) {
                return;
            }
            PostItem item = new PostItem();
            item.pid = Integer.parseInt(value);
            context.startActivity(PostShowActivity.newIntent(item,
                    PostShowActivity.FROM_POST));
            // u_index:会员主页
        } else if ("u_index".equals(type)) {
            if (TextUtils.isEmpty(value)) {
                return;
            }
            context.startActivity(HomePageActivity.newIntent(value));
            // f_show:免费试用详情页面
        } else if ("f_show".equals(type)) {
            if (TextUtils.isEmpty(value)) {
                return;
            }
            context.startActivity(FuliSheDetailActivity1.newIntent(
                    Integer.parseInt(value), 0));
            // q_show:限时疯抢详情页面
        } else if ("q_show".equals(type)) {
            if (TextUtils.isEmpty(value)) {
                return;
            }
            context.startActivity(FuliSheDetailActivity1.newIntent(
                    Integer.parseInt(value), 0));
            // g_show:攻略详情页面
        } else if ("g_show".equals(type)) {
            if (TextUtils.isEmpty(value)) {
                return;
            }
            context.startActivity(MeiWuStratDetailActivity1.newIntent(Integer
                    .parseInt(value)));
            // p_show:品质详情页面
        } else if ("p_show".equals(type)) {
            if (TextUtils.isEmpty(value)) {
                return;
            }
            context.startActivity(MeiWuSpecialShowActivity.newIntent(Integer.parseInt(value)));
            //h_show 好店详情
        } else if ("h_show".equals(type)) {
            if (TextUtils.isEmpty(value)) {
                return;
            }
            context.startActivity(MeiWuSpecialShowActivity.newIntent(Integer.parseInt(value), MeiWuSpecialShowActivity.TYPE_SHOPS));
            // h5:html5页面
        } else if ("h5".equals(type)) {
            if (TextUtils.isEmpty(value)) {
                return;
            }
            //TODO 这里要先发送请求去获取链接?
            if (mSplashData != null && mSplashData.value.equals(value)) {
                loadUrl(context, mSplashData);

            } else {
                context.startActivity(MeishaiWebviewActivity.newIntent(value));
            }
        }

    }


    private static void loadUrl(final Context context, SplashData splashData) {
        PublicReq.h5Req(splashData.controller, splashData.action, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (TextUtils.isEmpty(response)) {
                    context.startActivity(MeishaiWebviewActivity.newIntent(response));
                } else {
                    AndroidUtil.showToast("返回的链接为null");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast("请求网络链接失败");
            }
        });
    }

    /**
     * 根据splashData.type的值做出对应的处理,
     *
     * @param context
     * @param splashData
     */
    public static void skipSplash(Context context, SplashData splashData) {
        mSplashData = splashData;
        skip(context, splashData.type, splashData.value, splashData.tempid);
    }


    /**
     * 美物 点击图片后的响应
     *
     * @param context
     * @param tempid
     * @param tid
     */
    public static void skipMeiwu(Context context, int tempid, int tid) {
//		switch (typeid) {
//		case 4://专场详情
//			context.startActivity(MeiWuSpecialShowActivity.newIntent(tid));
//			break;
//		case 0://每日十件的,没有typeid,跳到攻略详情
//		case 5://攻略详情
//			Intent intent = MeiWuStratDetailActivity1.newIntent(tid);
//			context.startActivity(intent);
//			break;
//		case 6://好店详情
//			Intent intent1 = MeiWuSpecialShowActivity.newIntent(tid, MeiWuSpecialShowActivity.TYPE_SHOPS);
//			context.startActivity(intent1);
//			break;
//		default:
//			break;
//		}

        switch (tempid) {
            case 1://攻略详情
                context.startActivity(MeiWuStratDetailActivity1.newIntent(tid));
                break;
            case 2://专场详情
                context.startActivity(MeiWuSpecialShowActivity.newIntent(tid));
                break;
            case 3:
                context.startActivity(MeiWuStratListActivity.newIntent(tid));
                break;
            case 99:
//				if(islogin == 1) {
//					UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
//					if (!userInfo.isLogin()) {
//						context.startActivity(LoginActivity.newIntent());
//					}else {
//						context.startActivity(MeishaiWebviewActivity.newIntent(url));
//					}
//				}else {
//					context.startActivity(MeishaiWebviewActivity.newIntent(url));
//				}
                break;
            default:
                break;
        }
    }

    /**
     * 2016年1月21日18:22:13
     * <p/>
     * 新的跳转约定
     *
     * @param context
     * @param type
     * @param value
     * @param tempid
     */
    private static void skip(Context context, String type, String value, int tempid) {
        if ("c_list".equals(type)) {
            if (TextUtils.isEmpty(value)) {
                return;
            }
            context.startActivity(TopicShowActivity.newIntent(Integer.parseInt(value)));
            //晒晒详情
        } else if ("c_show".equals(type)) {
            if (TextUtils.isEmpty(value)) {
                return;
            }
            PostItem postItem = new PostItem();
            postItem.pid = Integer.parseInt(value);
            context.startActivity(PostShowActivity.newIntent(postItem, PostShowActivity.FROM_POST));
            //美物分类 品质专场
        } else if ("m_list".equals(type)) {
            if (TextUtils.isEmpty(value)) {
                return;
            }
            context.startActivity(MeiWuCateDetailActivity.newIntent(Integer.parseInt(value)));
            // 攻略详情
        } else if ("m_show".equals(type)) {
            if (TextUtils.isEmpty(value)) {
                return;
            }
            skipMeiwu(context, tempid, Integer.parseInt(value));
            //会员主页
        } else if ("u_index".equals(type)) {
            if (TextUtils.isEmpty(value)) {
                return;
            }
            context.startActivity(HomePageActivity.newIntent(value));
            //福利详情
        } else if ("f_show".equals(type)) {
            if (TextUtils.isEmpty(value)) {
                return;
            }
            context.startActivity(FuliSheDetailActivity1.newIntent(Integer.parseInt(value)));
        } else if ("m_create".equals(type)) {
            //TODO 我要爆料
            if (!MeiShaiSP.getInstance().getUserInfo().isLogin()) {
                context.startActivity(LoginActivity.newOtherIntent());
            } else {
//        		context.startActivity(DisCloseActivity.newIntent());
                context.startActivity(DaysTopicActivity.newIntent());
            }
        }
    }

    private static Context getContext() {
        return GlobalContext.getInstance();
    }

    private static int getIdByString(String value) {
        int id = 0;
        try {
            id = Integer.parseInt(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }


    /**
     * 2016年3月21日17:10:41
     *
     * @param context
     * @param type
     * @param value
     * @param tempid
     * @param data
     */
    public static void skip(final Context context, String type, final String value, int tempid, H5Data data) {
        if ("h5".equals(type)) {
            if (data == null) {
                return;
            }
            UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
            if (data.islogin == 1 && !userInfo.isLogin()) {
                context.startActivity(LoginActivity.newIntent());
            } else {
                loadUrl(context, data);

            }
        } else {
            skip(context, type, value, tempid);
        }


    }

    private static void loadUrl(final Context context, H5Data data) {
        PublicReq.h5Requst(data, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DebugLog.w("返回结果:" + response);
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj != null) {//解析成功
                            if (obj.getInt("success") == 1) {
                                String url = obj.getString("url");
                                if (!TextUtils.isEmpty(url)) {
                                    context.startActivity(MeishaiWebviewActivity.newIntent(url));
                                } else {
                                    AndroidUtil.showToast("返回的URL为null!");
                                }
                            } else
                                AndroidUtil.showToast(obj.getString("tips"));

                        } else {//解析失败
                            AndroidUtil.showToast("json解析失败!");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
//							context.startActivity(MeishaiWebviewActivity.newIntent(value));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast("网络请求失败!请检查网络状态");
            }
        });
    }
}
