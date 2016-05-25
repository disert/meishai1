package com.meishai.dao;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.SharedPreferences;

import com.meishai.GlobalContext;
import com.meishai.entiy.DeliveryAddres;
import com.meishai.entiy.PayInfo;
import com.meishai.entiy.UserInfo;
import com.meishai.entiy.UserIntro;

public class MeiShaiSP {

    public final SharedPreferences USER;
    private static final String RESOURCE = "UserInfo";

    private MeiShaiSP(Context ctx) {
        USER = ctx.getSharedPreferences(RESOURCE, Context.MODE_PRIVATE);
    }

    private static MeiShaiSP accessor;

    public static MeiShaiSP getInstance() {
        if (accessor == null) {
            accessor = new MeiShaiSP(GlobalContext.getInstance());
        }
        return accessor;
    }

    public boolean setUserInfo(UserInfo user) {
        boolean bl = false;
        SharedPreferences.Editor editor = USER.edit();
        editor.putString("USER_ID", user.getUserID());
        editor.putInt("USER_TYPE", user.getUserType());
        editor.putString("USER_NICKNAME", user.getUserNickName());
        editor.putString("USER_MOBILE", user.getUserMobile());
        editor.putString("USER_PWD", user.getUserPWD());
        editor.putString("USER_EMAIL", user.getUserEmail());
        editor.putString("USER_QQ", user.getUserQQ());
        editor.putInt("USER_AREA", user.getUserArea());
        editor.putString("USER_AVATARURL", user.getAvatarUrl());
        editor.putInt("USER_GROUDID", user.getGroupid());
        editor.putBoolean("USER_DAREN", user.isDaren());
        editor.putInt("USER_TRYNUM", user.getTryNum());
        editor.putInt("USER_POSTNUM", user.getPostNum());
        editor.putInt("USER_FAVNUM", user.getFavNum());
        editor.putInt("USER_POINTNUM", user.getPointNum());
        editor.putInt("USER_CREDITNUM", user.getCreditNum());
        editor.putInt("USER_TAYALERTNUM", user.getTryAlertNum());
        editor.putInt("USER_MSGNUM", user.getMsgNum());
        editor.putInt("USER_INBOXNUM", user.getInboxNum());
        editor.putInt("USER_MEIDOU", user.getMeidou());
        editor.putInt("FULI_NUM", user.getFuliNum());

        editor.putString("USER_CODE", user.getCode());
        editor.putString("USER_TEXT", user.getText());

        bl = editor.commit();
        return bl;
    }

    public UserInfo getUserInfo() {
        UserInfo user = new UserInfo();
        user.setUserID(USER.getString("USER_ID", null));
        user.setUserType(USER.getInt("USER_TYPE", 0));
        user.setUserNickName(USER.getString("USER_NICKNAME", null));
        user.setUserMobile(USER.getString("USER_MOBILE", null));
        user.setUserPWD(USER.getString("USER_PWD", null));
        user.setUserEmail(USER.getString("USER_EMAIL", null));
        user.setUserQQ(USER.getString("USER_QQ", null));
        user.setUserArea(USER.getInt("USER_AREA", 1));
        user.setAvatarUrl(USER.getString("USER_AVATARURL", null));
        user.setGroupid(USER.getInt("USER_GROUDID", 0));
        user.setDaren(USER.getBoolean("USER_DAREN", false));
        user.setTryNum(USER.getInt("USER_TRYNUM", 0));
        user.setPostNum(USER.getInt("USER_POSTNUM", 0));
        user.setFavNum(USER.getInt("USER_FAVNUM", 0));
        user.setPointNum(USER.getInt("USER_POINTNUM", 0));
        user.setCreditNum(USER.getInt("USER_CREDITNUM", 0));
        user.setTryAlertNum(USER.getInt("USER_TAYALERTNUM", 0));
        user.setMsgNum(USER.getInt("USER_MSGNUM", 0));
        user.setInboxNum(USER.getInt("USER_INBOXNUM", 0));
        user.setMeidou(USER.getInt("USER_MEIDOU", 0));
        user.setCode(USER.getString("USER_CODE", ""));
        user.setText(USER.getString("USER_TEXT", ""));
        user.setFuliNum(USER.getInt("FULI_NUM", 0));
        return user;
    }

    public PayInfo getPayInfo() {
        PayInfo pay = new PayInfo();
        pay.setPayID(USER.getString("PAY_ID", null));
        pay.setPayName(USER.getString("PAY_NAME", null));
        return pay;
    }

    public boolean setPayInfo(PayInfo pay) {
        boolean bl = false;
        SharedPreferences.Editor editor = USER.edit();
        editor.putString("PAY_ID", pay.getPayID());
        editor.putString("PAY_NAME", pay.getPayName());
        bl = editor.commit();
        return bl;
    }

    public DeliveryAddres getDeliveryAddres() {
        DeliveryAddres da = new DeliveryAddres();
        da.setAreaid(USER.getInt("DA_AREAID", 0));
        da.setAddress(USER.getString("DA_ADDRES", null));
        da.setPhone(USER.getString("DA_ADA_PHONEDDRES", null));
        da.setRealName(USER.getString("DA_REALNAME", null));
        da.setZipCode(USER.getString("DA_ZIPCODE", null));
        return da;
    }

    public boolean setDeliveryAddres(DeliveryAddres da) {
        boolean bl = false;
        SharedPreferences.Editor editor = USER.edit();
        editor.putInt("DA_AREAID", da.getAreaid());
        editor.putString("DA_ADDRES", da.getAddress());
        editor.putString("DA_PHONE", da.getPhone());
        editor.putString("DA_REALNAME", da.getRealName());
        editor.putString("DA_ZIPCODE", da.getZipCode());
        bl = editor.commit();
        return bl;
    }

    public UserIntro getUserIntro(String userID) {
        UserIntro introl = new UserIntro();
        introl.setUserID(USER.getString("USER_INTROL_ID", null));
        introl.setIntrol(USER.getString("USER_INTROL_DETAIL", null));
        if (introl.getUserID() != null && userID != null && introl.getUserID().equals(userID)) {
            return introl;
        } else {
            return null;
        }

    }

    public boolean setUserIntro(UserIntro introl) {
        boolean bl = false;
        SharedPreferences.Editor editor = USER.edit();
        editor.putString("USER_INTROL_ID", introl.getUserID());
        editor.putString("USER_INTROL_DETAIL", introl.getIntrol());
        bl = editor.commit();
        return bl;
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        SharedPreferences.Editor editor = USER.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            editor.commit();
        }
    }

}
