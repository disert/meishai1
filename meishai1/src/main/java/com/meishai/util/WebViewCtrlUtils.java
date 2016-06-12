package com.meishai.util;

import android.content.Context;

import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.UserInfo;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.home.HomePageActivity;
import com.meishai.ui.fragment.tryuse.DoubleSellWebviewActivity;
import com.meishai.ui.fragment.tryuse.FuliSheDetailActivity1;
import com.meishai.ui.fragment.tryuse.SellGoodsWebviewActivity;
import com.meishai.ui.fragment.usercenter.LoginActivity;

/**
 * Created by Administrator on 2016/5/21.
 */
public class WebViewCtrlUtils {

    public static boolean ctrl(Context context, String url) {
        if (url.startsWith(ConstantSet.MEISHAI_HOME_PAGE)) {//个人主页
            String[] split = url.split("/");
            String userid = split[split.length - 1];
            context.startActivity(HomePageActivity.newIntent(userid));
            return true;
        } else if (url.startsWith(ConstantSet.MEISHAI_POINT_GOODS)) {//积分商城详情
            String[] split = url.split("/");
            String gid = split[split.length - 1];
            context.startActivity(FuliSheDetailActivity1.newIntent(Integer.parseInt(gid), 0));
            return true;
        } else if (url.startsWith(ConstantSet.MEISHAI_QIANG_GOODS)) {//疯抢详情页面
            String[] split = url.split("/");
            String gid = split[split.length - 1];
            context.startActivity(FuliSheDetailActivity1.newIntent(Integer.parseInt(gid), 0));
            return true;
        } else if (url.startsWith(ConstantSet.TMALL_APP) || url.startsWith(ConstantSet.TAOBAO_APP)) {//淘宝的链接,有些会默认的跳转到天猫的客户端,有够坑的,我直接给拦截了
            return true;
        } else if (url.startsWith(ConstantSet.MEISHAI_LOGIN)) {//登陆的链接
            UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
            if (!userInfo.isLogin()) {
                context.startActivity(LoginActivity.newIntent());
                return true;
            }
        } else if (url.startsWith(ConstantSet.MEISHAI_SELL_GOODS)) {//商品销售
            context.startActivity(SellGoodsWebviewActivity.newIntent(url));
            return true;
        } else if (url.startsWith(ConstantSet.MEISHAI_DOUBLE_SELL)) {//团购页面
            context.startActivity(DoubleSellWebviewActivity.newIntent(url));
            return true;

        }
        return false;
    }
}
