package com.meishai.util;

import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sh
 */
public class ComUtils {

    /**
     * 随机获取UUID字符串(无中划线)
     *
     * @return UUID字符串
     */
    public synchronized static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid.substring(0, 8) + uuid.substring(9, 13) + uuid.substring(14, 18) + uuid.substring(19, 23) + uuid.substring(24);
    }

    /**
     * 产生单个随机数
     */
    public static int randomNum(int total) {
        Random random = new Random();
        return random.nextInt(total);
    }

    /**
     * 产生多个不重复的随机数
     *
     * @throws
     */
    public static int[] randomNums(int total) {
        int[] sequence = new int[total];
        int[] output = new int[total];
        for (int i = 0; i < total; i++) {
            sequence[i] = i;
        }
        Random random = new Random();
        int end = total - 1;
        for (int i = 0; i < total; i++) {
            int num = random.nextInt(end + 1);
            output[i] = sequence[num];
            sequence[num] = sequence[end];
            end--;
        }
        return output;
    }

    /**
     * 产生随机长度的字符串
     */
    public static String randomStr(int length) {
        length = (length > 32) ? 32 : length;
        String value = getUUID().substring(0, length);
        return value;
    }

    /**
     * 获得特定长度的一个随机字母数字混编字符串（所包含的字符包括0-9A-Z)
     *
     * @param length
     * @return
     */
    public static String getRandomAlphamericStr(int length) {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int n = random.nextInt(36);
            if (n < 10)
                builder.append(n);
            else
                builder.append((char) (n + 55));
        }
        return builder.toString();
    }

    /**
     * parameter 2 is contain in parameter 1.
     *
     * @param sourceFlag
     * @param compareFlag
     * @return
     */
    public static boolean isFlagContain(int sourceFlag, int compareFlag) {
        return (sourceFlag & compareFlag) == compareFlag;
    }

    public static boolean isNull(Object o) {
        return o == null ? true : false;
    }

    /**
     * 判断手机号码
     */
    public static boolean isMobileNo(String mobiles) {
        Pattern pattern = Pattern.compile("^(1)\\d{10}$");
        Matcher matcher = pattern.matcher(mobiles);
        return matcher.matches();
    }

    /**
     * 判断手机号码
     */
    public static boolean isEmail(String email) {
        String strPattern = "^[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+$";
        Pattern p = Pattern.compile(strPattern);
        if (p.matcher(email).matches()) {
            return true;
        }
        return false;
    }
}
