package com.meishai.app.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.meishai.util.StringUtils;

public class StringUtil {
    private static final int VALUE_10 = 10;
    private static final int VALUE_13 = 13;
    private static final int VALUE_32 = 32;
    private static final int VALUE_60 = 60;
    private static final int VALUE_62 = 62;
    private static final int VALUE_38 = 38;
    private static final int VALUE_34 = 34;
    private static final int VALUE_165 = 165;
    private static final int VALUE_174 = 174;
    private static final int VALUE_8482 = 8482;
    private static final int VALUE_8364 = 8364;
    private static final int VALUE_169 = 169;
    private static final int OffsetBig = 256;
    private static final int OffsetSmall = 16;

    private static final int MOBIL_F_TAG = 3;
    private static final int MOBIL_L_TAG = 7;
    private static final int WEEK_TAG = 7;
    private static final int MOBIL_P_TAG_PREFIX = 6;
    private static final int MOBIL_N_TAG_PREFIX = 10;

    private static final String ENCRYPT_SALTE = "paidui888";

    /**
     * 字符串去空格，回车，换行，制表符
     *
     * @param str 要修改的字符串
     * @return 修改完成的字符串
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 将输入的字符串进行html编码
     *
     * @param input 输入的字符串
     * @return html编码后的结果
     */
    public static String htmEncode(String input) {
        if (null == input || "".equals(input)) {
            return input;
        }

        StringBuffer stringbuffer = new StringBuffer();
        int j = input.length();
        for (int i = 0; i < j; i++) {
            char c = input.charAt(i);
            switch (c) {
                case VALUE_60:
                    stringbuffer.append("&lt;");
                    break;
                case VALUE_62:
                    stringbuffer.append("&gt;");
                    break;
                case VALUE_38:
                    stringbuffer.append("&amp;");
                    break;
                case VALUE_34:
                    stringbuffer.append("&quot;");
                    break;
                case VALUE_169:
                    stringbuffer.append("&copy;");
                    break;
                case VALUE_174:
                    stringbuffer.append("&reg;");
                    break;
                case VALUE_165:
                    stringbuffer.append("&yen;");
                    break;
                case VALUE_8364:
                    stringbuffer.append("&euro;");
                    break;
                case VALUE_8482:
                    stringbuffer.append("&#153;");
                    break;
                case VALUE_13:
                    if (i < j - 1 && input.charAt(i + 1) == VALUE_10) {
                        stringbuffer.append("<br>");
                        i++;
                    }
                    break;
                case VALUE_32:
                    if (i < j - 1 && input.charAt(i + 1) == ' ') {
                        stringbuffer.append(" &nbsp;");
                        i++;
                        break;
                    }
                default:
                    stringbuffer.append(c);
                    break;
            }
        }
        return new String(stringbuffer.toString());
    }

    /**
     * 判断字符串是否为null或者空字符串
     *
     * @param input 输入的字符串
     * @return 如果为null或者空字符串，返回true；否则返回false
     */
    public static boolean isNullOrEmpty(String input) {
        if (null == input || "".equals(input) || input.trim().length() == 0) {
            return true;
        }
        return false;
    }

    public static boolean checkUserNamePwd(String input, int length) {
        if (!isNullOrEmpty(input)) {
            if (input.length() >= length && input.length() <= 32) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkUserIntrol(String input, int length) {
        if (!isNullOrEmpty(input)) {
            if (input.length() >= length && input.length() <= 100) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkZip(String input, int length) {
        if (!isNullOrEmpty(input)) {
            if (input.length() == length) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkQQ(String input) {
        if (isNumberString(input)) {
            if (input.length() >= 5 && input.length() <= 13) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkEmail(String input) {
        if (input != null && input.length() > 5) {
            String emailPattern = "[a-zA-Z0-9][a-zA-Z0-9._-]{2,16}[a-zA-Z0-9]@[a-zA-Z0-9]+.[a-zA-Z0-9]+";
            boolean result = Pattern.matches(emailPattern, input);
            return result;
        }
        return false;
    }

    /**
     * 判断字符串中是否含有中文字符
     *
     * @param s 需要判断的字符串
     * @return true为包含中文
     */
    public static boolean containChinese(String s) {
        if (null == s) {
            return false;
        }

        Pattern pattern = Pattern.compile(".*[\u4e00-\u9fbb]+.*");
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();
    }

    /**
     * 判断输入的是否为汉字字符
     *
     * @return 是否为中文
     */
    public static boolean isChinese(char a) {
        // return (v >=19968 && v <= 171941);
        return String.valueOf(a).matches("[\u4E00-\u9FA5]"); // 利用正则表达式，经测试可以区分开中文符号
    }

    /**
     * 判断输入的是否为汉字字符
     *
     * @return 是否为中文
     */
    public static boolean isChinese(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!isChinese(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取MD5加密后Hash字符串
     *
     * @param strOriginal 初始字符串
     * @return MD5加密后Hash字符串
     */
    private static String getMd5Hash(String strOriginal) {
        StringBuilder sbList = new StringBuilder();
        try {
            MessageDigest mMD5 = MessageDigest.getInstance("MD5");
            byte[] data = strOriginal.getBytes("utf-8");
            byte[] dataPWD = mMD5.digest(data);
            for (int offset = 0; offset < dataPWD.length; offset++) {
                int i = dataPWD[offset];
                if (i < 0) {
                    i += OffsetBig;
                }
                if (i < OffsetSmall) {
                    sbList.append("0");
                }
                sbList.append(Integer.toHexString(i));
            }
            return sbList.toString();
        } catch (NoSuchAlgorithmException e) {
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }

    /**
     * 获取MD5加密后Hash字符串
     *
     * @param strOriginal 初始字符串
     * @param strSalt     种子字符串
     * @return MD5加密后Hash字符串
     */
    public static String getMd5Hash(String strOriginal, String strSalt) {
        String mStrSalt;
        String mStrOriginal = strOriginal;
        // 如果调用未给Salt值,则默认
        if (strSalt == null) {
            mStrSalt = ENCRYPT_SALTE;
        } else {
            mStrSalt = strSalt;
        }
        mStrOriginal = mStrOriginal + mStrSalt;
        return getMd5Hash(mStrOriginal);
    }

    /**
     * @param userMobile 需要处理的手机号码
     * @return String 处理后的手机号码
     * @throws
     * @Method: getProcessedDrawMobile
     * @Description: 处理手机号码
     */
    public static String getProcessedMobile(String userMobile) {
        String mProcessedDrawMobile = "";
        if (!StringUtil.isNullOrEmpty(userMobile)) {
            // 判断是否是+86开头的手机号码
            if ('+' == userMobile.charAt(0)
                    && userMobile.length() >= MOBIL_N_TAG_PREFIX) {
                mProcessedDrawMobile = userMobile.substring(0,
                        MOBIL_P_TAG_PREFIX)
                        + "****"
                        + userMobile.substring(MOBIL_N_TAG_PREFIX);
            } else if ('+' != userMobile.charAt(0)
                    && userMobile.length() >= MOBIL_L_TAG) {
                mProcessedDrawMobile = userMobile.substring(0, MOBIL_F_TAG)
                        + "****" + userMobile.substring(MOBIL_L_TAG);
            } else {
                mProcessedDrawMobile = userMobile;
            }
        }
        return mProcessedDrawMobile;
    }

    /**
     * 获取当期是星期几（从星期天开始）
     *
     * @param weeknum 当前是第几天（0-6）
     * @return 星期*
     */
    public static String getDayOfWeek(int weeknum) {
        weeknum--;
        if (weeknum > WEEK_TAG) {
            weeknum = weeknum % WEEK_TAG;
        }

        if (weeknum < 0) {
            weeknum = -weeknum;
        }

        String[] weekArray = new String[]{"星期日", "星期一", "星期二", "星期三", "星期四",
                "星期五", "星期六"};
        return weekArray[weeknum];

    }

    /**
     * 重载方法，获取当期是星期几
     *
     * @param c 日历
     * @return 星期*
     */
    public static String getDayOfWeek(Calendar c) {
        return getDayOfWeek(c.get(Calendar.DAY_OF_WEEK));
    }

    /**
     * 检查是不是中文
     *
     * @param str 检查字符串
     * @return boolean 是否为中文
     * @throws
     * @Method: checkStringIsChinses
     */
    public static boolean checkStringIsChinese(String str) {
        if (null == str) {
            return false;
        }
        Pattern pattern = Pattern.compile("[\\u4E00-\\u9FA5]+");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 转换保留小数位 字符串
     *
     * @param i      小数位数
     * @param numStr 数字字符串
     * @return String
     */
    public static String getDecimalFormat(int i, String numStr) {
        try {
            if (numStr != null && !"".equals(numStr)) {
                BigDecimal bd = new BigDecimal(numStr);
                bd = bd.setScale(i, BigDecimal.ROUND_HALF_UP);

                return bd.toString();
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 转换保留小数位
     *
     * @param i   小数位数
     * @param num 要转换的数字
     * @return double 返回类型
     */
    public static double decimalFormat(int i, Double num) {
        String temp = getDecimalFormat(i, num.toString());
        double numFormat = Double.valueOf(temp);
        return numFormat;
    }

    /**
     * 检查是否是字符串
     *
     * @param input 被检查字符串
     * @return 是否是数字组成的字符串，包括0开头;<br>
     * 如果为空或者空字符串，返回true；比如："011"返回true，"a11"返回false
     */
    public static boolean isNumberString(String input) {
        if (!isNullOrEmpty(input)) {
            if (input.matches("[0-9]+")) {
                return true;
            }
        }

        return false;
    }

    public static boolean isVerifyCode(String input, int checkLength) {
        if (isNumberString(input)) {
            if (input.length() == checkLength) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查输入字符串是否是合法的http地址，这个检查方法不完整，只是初步的检查是否以http://开头兵器后面有字符串
     *
     * @param input 输入的网址
     * @return 输入字符串是否合法
     */
    public static boolean isHttpUrl(String input) {
        if (!isNullOrEmpty(input)) {
            // String regexExpression =
            // "http://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
            // ((http|ftp|https|file):\/\/([\w\-]+\.)+[\w\-]+((\:)?(\d+)?)+(\/)?)
            String regexExpression = "http://([\\w\\WW-]+\\.)+[\\w\\W-]+(/[\\w\\W- ./?%&=]*)?";
            if (input.matches(regexExpression)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 使用java正则表达式去掉多余的.与0
     *
     * @param str
     * @return String
     */
    public static String filterZeroAndDot(String str) {
        if (!isNullOrEmpty(str) && str.indexOf(".") > 0) {
            // 去掉多余的0
            str = str.replaceAll("0+?$", "");
            // 如最后一位是.则去掉
            str = str.replaceAll("[.]$", "");
        }
        return str;
    }

    public static String getDecimalFormatString(double input, int digits) {
        BigDecimal bd = new BigDecimal(input);
        bd = bd.setScale(digits, BigDecimal.ROUND_HALF_UP);

        return filterZeroAndDot(bd.toString());
    }

    /**
     * 半角转换为全角（用于textview换行不整齐时使用）
     *
     * @param input
     * @return
     */
    public static String toDBC(String input) {
        if (input == null) {
            return "";
        }
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    public static String sortString(String str) {
        String[] orderDishID = str.split(",");
        Arrays.sort(orderDishID);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < orderDishID.length; i++) {
            sb.append(orderDishID[i]);
            if (i + 1 < orderDishID.length) {
                sb.append(",");
            }
        }
        orderDishID = null;
        str = null;
        return sb.toString();
    }

    /**
     * 从第一个分隔符到结尾，去掉分隔符前面的内容(去前缀)
     *
     * @param resStr     内容字符串
     * @param spreateStr 前缀标记
     * @return
     */
    public static String subStringFromSpreateToEnd(String resStr,
                                                   String spreateStr) {
        if (!StringUtil.isNullOrEmpty(resStr)
                && !StringUtil.isNullOrEmpty(spreateStr)
                && resStr.contains(spreateStr)) {
            resStr = resStr.substring(resStr.indexOf(spreateStr) + 1);
        }
        return resStr;
    }

    /**
     * 为字符串加入分隔符，如果为空
     *
     * @param resStr
     * @param appendStr
     * @param spreateStr
     * @return
     */
    public static String appendStringWithSpreate(String resStr,
                                                 String appendStr, String spreateStr) {
        if (!StringUtil.isNullOrEmpty(spreateStr)) {
            if (StringUtil.isNullOrEmpty(resStr)) {
                resStr = appendStr;
            } else {
                resStr += spreateStr + appendStr;
            }
        }
        return resStr;
    }

    public static boolean isTelNumAvailable(String telNum) {
        boolean result = false;
        if (!isNullOrEmpty(telNum) && telNum.matches("^1[3458]{1}[0-9]{9}$")) {
            result = true;
        }
        return result;
    }

    /**
     * 是否是汉字，英文，数字或者其中几个组成
     *
     * @param s
     * @return boolean
     */
    public static boolean isValidEngOrChOrNum(String s) {
        if (null == s) {
            return false;
        }
        Pattern pattern = Pattern.compile("[\u4E00-\u9FA5A-Za-z0-9]*$");
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();
    }

    /**
     * @param
     * @return String
     * @Title: listToString
     * @Description: 把list集合数据转化成带逗号的字符串
     */
    public static String listToString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (i < list.size() - 1) {
                    sb.append(list.get(i) + ",");
                } else {
                    sb.append(list.get(i));
                }
            }
        }
        return sb.toString();
    }

    /**
     * <p>
     * The maximum size to which the padding constant(s) can expand.
     * </p>
     */
    private static final int PAD_LIMIT = 8192;
    /**
     * The empty String <code>""</code>.
     *
     * @since 2.0
     */
    public static final String EMPTY = "";

    // IndexOf
    // -----------------------------------------------------------------------

    /**
     * <p>
     * Finds the first index within a String, handling <code>null</code>. This
     * method uses {@link String#indexOf(int)}.
     * </p>
     * <p/>
     * <p>
     * A <code>null</code> or empty ("") String will return <code>-1</code>.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.indexOf(null, *)         = -1
     * StringUtils.indexOf("", *)           = -1
     * StringUtils.indexOf("aabaabaa", 'a') = 0
     * StringUtils.indexOf("aabaabaa", 'b') = 2
     * </pre>
     *
     * @param str        the String to check, may be null
     * @param searchChar the character to find
     * @return the first index of the search character, -1 if no match or
     * <code>null</code> string input
     * @since 2.0
     */
    public static int indexOf(String str, char searchChar) {
        if (isEmpty(str)) {
            return -1;
        }
        return str.indexOf(searchChar);
    }

    /**
     * <p>
     * Finds the first index within a String from a start position, handling
     * <code>null</code>. This method uses {@link String#indexOf(int, int)}.
     * </p>
     * <p/>
     * <p>
     * A <code>null</code> or empty ("") String will return <code>-1</code>. A
     * negative start position is treated as zero. A start position greater than
     * the string length returns <code>-1</code>.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.indexOf(null, *, *)          = -1
     * StringUtils.indexOf("", *, *)            = -1
     * StringUtils.indexOf("aabaabaa", 'b', 0)  = 2
     * StringUtils.indexOf("aabaabaa", 'b', 3)  = 5
     * StringUtils.indexOf("aabaabaa", 'b', 9)  = -1
     * StringUtils.indexOf("aabaabaa", 'b', -1) = 2
     * </pre>
     *
     * @param str        the String to check, may be null
     * @param searchChar the character to find
     * @param startPos   the start position, negative treated as zero
     * @return the first index of the search character, -1 if no match or
     * <code>null</code> string input
     * @since 2.0
     */
    public static int indexOf(String str, char searchChar, int startPos) {
        if (isEmpty(str)) {
            return -1;
        }
        return str.indexOf(searchChar, startPos);
    }

    /**
     * <p>
     * Finds the first index within a String, handling <code>null</code>. This
     * method uses {@link String#indexOf(String)}.
     * </p>
     * <p/>
     * <p>
     * A <code>null</code> String will return <code>-1</code>.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.indexOf(null, *)          = -1
     * StringUtils.indexOf(*, null)          = -1
     * StringUtils.indexOf("", "")           = 0
     * StringUtils.indexOf("aabaabaa", "a")  = 0
     * StringUtils.indexOf("aabaabaa", "b")  = 2
     * StringUtils.indexOf("aabaabaa", "ab") = 1
     * StringUtils.indexOf("aabaabaa", "")   = 0
     * </pre>
     *
     * @param str       the String to check, may be null
     * @param searchStr the String to find, may be null
     * @return the first index of the search String, -1 if no match or
     * <code>null</code> string input
     * @since 2.0
     */
    public static int indexOf(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return -1;
        }
        return str.indexOf(searchStr);
    }

    // LastIndexOf
    // -----------------------------------------------------------------------

    /**
     * <p>
     * Finds the last index within a String, handling <code>null</code>. This
     * method uses {@link String#lastIndexOf(int)}.
     * </p>
     * <p/>
     * <p>
     * A <code>null</code> or empty ("") String will return <code>-1</code>.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.lastIndexOf(null, *)         = -1
     * StringUtils.lastIndexOf("", *)           = -1
     * StringUtils.lastIndexOf("aabaabaa", 'a') = 7
     * StringUtils.lastIndexOf("aabaabaa", 'b') = 5
     * </pre>
     *
     * @param str        the String to check, may be null
     * @param searchChar the character to find
     * @return the last index of the search character, -1 if no match or
     * <code>null</code> string input
     * @since 2.0
     */
    public static int lastIndexOf(String str, char searchChar) {
        if (isEmpty(str)) {
            return -1;
        }
        return str.lastIndexOf(searchChar);
    }

    /**
     * <p>
     * Finds the last index within a String from a start position, handling
     * <code>null</code>. This method uses {@link String#lastIndexOf(int, int)}.
     * </p>
     * <p/>
     * <p>
     * A <code>null</code> or empty ("") String will return <code>-1</code>. A
     * negative start position returns <code>-1</code>. A start position greater
     * than the string length searches the whole string.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.lastIndexOf(null, *, *)          = -1
     * StringUtils.lastIndexOf("", *,  *)           = -1
     * StringUtils.lastIndexOf("aabaabaa", 'b', 8)  = 5
     * StringUtils.lastIndexOf("aabaabaa", 'b', 4)  = 2
     * StringUtils.lastIndexOf("aabaabaa", 'b', 0)  = -1
     * StringUtils.lastIndexOf("aabaabaa", 'b', 9)  = 5
     * StringUtils.lastIndexOf("aabaabaa", 'b', -1) = -1
     * StringUtils.lastIndexOf("aabaabaa", 'a', 0)  = 0
     * </pre>
     *
     * @param str        the String to check, may be null
     * @param searchChar the character to find
     * @param startPos   the start position
     * @return the last index of the search character, -1 if no match or
     * <code>null</code> string input
     * @since 2.0
     */
    public static int lastIndexOf(String str, char searchChar, int startPos) {
        if (isEmpty(str)) {
            return -1;
        }
        return str.lastIndexOf(searchChar, startPos);
    }

    /**
     * <p>
     * Finds the last index within a String, handling <code>null</code>. This
     * method uses {@link String#lastIndexOf(String)}.
     * </p>
     * <p/>
     * <p>
     * A <code>null</code> String will return <code>-1</code>.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.lastIndexOf(null, *)          = -1
     * StringUtils.lastIndexOf(*, null)          = -1
     * StringUtils.lastIndexOf("", "")           = 0
     * StringUtils.lastIndexOf("aabaabaa", "a")  = 0
     * StringUtils.lastIndexOf("aabaabaa", "b")  = 2
     * StringUtils.lastIndexOf("aabaabaa", "ab") = 1
     * StringUtils.lastIndexOf("aabaabaa", "")   = 8
     * </pre>
     *
     * @param str       the String to check, may be null
     * @param searchStr the String to find, may be null
     * @return the last index of the search String, -1 if no match or
     * <code>null</code> string input
     * @since 2.0
     */
    public static int lastIndexOf(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return -1;
        }
        return str.lastIndexOf(searchStr);
    }

    /**
     * <p>
     * Finds the first index within a String, handling <code>null</code>. This
     * method uses {@link String#lastIndexOf(String, int)}.
     * </p>
     * <p/>
     * <p>
     * A <code>null</code> String will return <code>-1</code>. A negative start
     * position returns <code>-1</code>. An empty ("") search String always
     * matches unless the start position is negative. A start position greater
     * than the string length searches the whole string.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.lastIndexOf(null, *, *)          = -1
     * StringUtils.lastIndexOf(*, null, *)          = -1
     * StringUtils.lastIndexOf("aabaabaa", "a", 8)  = 7
     * StringUtils.lastIndexOf("aabaabaa", "b", 8)  = 5
     * StringUtils.lastIndexOf("aabaabaa", "ab", 8) = 4
     * StringUtils.lastIndexOf("aabaabaa", "b", 9)  = 5
     * StringUtils.lastIndexOf("aabaabaa", "b", -1) = -1
     * StringUtils.lastIndexOf("aabaabaa", "a", 0)  = 0
     * StringUtils.lastIndexOf("aabaabaa", "b", 0)  = -1
     * </pre>
     *
     * @param str       the String to check, may be null
     * @param searchStr the String to find, may be null
     * @param startPos  the start position, negative treated as zero
     * @return the first index of the search String, -1 if no match or
     * <code>null</code> string input
     * @since 2.0
     */
    public static int lastIndexOf(String str, String searchStr, int startPos) {
        if (str == null || searchStr == null) {
            return -1;
        }
        return str.lastIndexOf(searchStr, startPos);
    }

    // Contains
    // -----------------------------------------------------------------------

    /**
     * <p>
     * Checks if String contains a search character, handling <code>null</code>.
     * This method uses {@link String#indexOf(int)}.
     * </p>
     * <p/>
     * <p>
     * A <code>null</code> or empty ("") String will return <code>false</code>.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.contains(null, *)    = false
     * StringUtils.contains("", *)      = false
     * StringUtils.contains("abc", 'a') = true
     * StringUtils.contains("abc", 'z') = false
     * </pre>
     *
     * @param str        the String to check, may be null
     * @param searchChar the character to find
     * @return true if the String contains the search character, false if not or
     * <code>null</code> string input
     * @since 2.0
     */
    public static boolean contains(String str, char searchChar) {
        if (isEmpty(str)) {
            return false;
        }
        return str.indexOf(searchChar) >= 0;
    }

    /**
     * <p>
     * Checks if String contains a search String, handling <code>null</code>.
     * This method uses {@link String#indexOf(String)}.
     * </p>
     * <p/>
     * <p>
     * A <code>null</code> String will return <code>false</code>.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.contains(null, *)     = false
     * StringUtils.contains(*, null)     = false
     * StringUtils.contains("", "")      = true
     * StringUtils.contains("abc", "")   = true
     * StringUtils.contains("abc", "a")  = true
     * StringUtils.contains("abc", "z")  = false
     * </pre>
     *
     * @param str       the String to check, may be null
     * @param searchStr the String to find, may be null
     * @return true if the String contains the search String, false if not or
     * <code>null</code> string input
     * @since 2.0
     */
    public static boolean contains(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        return str.indexOf(searchStr) >= 0;
    }

    // Substring
    // -----------------------------------------------------------------------

    /**
     * <p>
     * Gets a substring from the specified String avoiding exceptions.
     * </p>
     * <p/>
     * <p>
     * A negative start position can be used to start <code>n</code> characters
     * from the end of the String.
     * </p>
     * <p/>
     * <p>
     * A <code>null</code> String will return <code>null</code>. An empty ("")
     * String will return "".
     * </p>
     * <p/>
     * <pre>
     * StringUtils.substring(null, *)   = null
     * StringUtils.substring("", *)     = ""
     * StringUtils.substring("abc", 0)  = "abc"
     * StringUtils.substring("abc", 2)  = "c"
     * StringUtils.substring("abc", 4)  = ""
     * StringUtils.substring("abc", -2) = "bc"
     * StringUtils.substring("abc", -4) = "abc"
     * </pre>
     *
     * @param str   the String to get the substring from, may be null
     * @param start the position to start from, negative means count back from the
     *              end of the String by this many characters
     * @return substring from start position, <code>null</code> if null String
     * input
     */
    public static String substring(String str, int start) {
        if (str == null) {
            return null;
        }
        // handle negatives, which means last n characters
        if (start < 0) {
            start = str.length() + start; // remember start is negative
        }
        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return EMPTY;
        }
        return str.substring(start);
    }

    /**
     * <p>
     * Gets a substring from the specified String avoiding exceptions.
     * </p>
     * <p/>
     * <p>
     * A negative start position can be used to start/end <code>n</code>
     * characters from the end of the String.
     * </p>
     * <p/>
     * <p>
     * The returned substring starts with the character in the
     * <code>start</code> position and ends before the <code>end</code>
     * position. All position counting is zero-based -- i.e., to start at the
     * beginning of the string use <code>start = 0</code>. Negative start and
     * end positions can be used to specify offsets relative to the end of the
     * String.
     * </p>
     * <p/>
     * <p>
     * If <code>start</code> is not strictly to the left of <code>end</code>, ""
     * is returned.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.substring(null, *, *)    = null
     * StringUtils.substring("", * ,  *)    = "";
     * StringUtils.substring("abc", 0, 2)   = "ab"
     * StringUtils.substring("abc", 2, 0)   = ""
     * StringUtils.substring("abc", 2, 4)   = "c"
     * StringUtils.substring("abc", 4, 6)   = ""
     * StringUtils.substring("abc", 2, 2)   = ""
     * StringUtils.substring("abc", -2, -1) = "b"
     * StringUtils.substring("abc", -4, 2)  = "ab"
     * </pre>
     *
     * @param str   the String to get the substring from, may be null
     * @param start the position to start from, negative means count back from the
     *              end of the String by this many characters
     * @param end   the position to end at (exclusive), negative means count back
     *              from the end of the String by this many characters
     * @return substring from start position to end positon, <code>null</code>
     * if null String input
     */
    public static String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        }
        // handle negatives
        if (end < 0) {
            end = str.length() + end; // remember end is negative
        }
        if (start < 0) {
            start = str.length() + start; // remember start is negative
        }
        // check length next
        if (end > str.length()) {
            end = str.length();
        }
        // if start is greater than end, return ""
        if (start > end) {
            return EMPTY;
        }
        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }
        return str.substring(start, end);
    }

    // Left/Right/Mid
    // -----------------------------------------------------------------------

    /**
     * <p>
     * Gets the leftmost <code>len</code> characters of a String.
     * </p>
     * <p/>
     * <p>
     * If <code>len</code> characters are not available, or the String is
     * <code>null</code>, the String will be returned without an exception. An
     * exception is thrown if len is negative.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.left(null, *)    = null
     * StringUtils.left(*, -ve)     = ""
     * StringUtils.left("", *)      = ""
     * StringUtils.left("abc", 0)   = ""
     * StringUtils.left("abc", 2)   = "ab"
     * StringUtils.left("abc", 4)   = "abc"
     * </pre>
     *
     * @param str the String to get the leftmost characters from, may be null
     * @param len the length of the required String, must be zero or positive
     * @return the leftmost characters, <code>null</code> if null String input
     */
    public static String left(String str, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return EMPTY;
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(0, len);
    }

    /**
     * <p>
     * Gets the rightmost <code>len</code> characters of a String.
     * </p>
     * <p/>
     * <p>
     * If <code>len</code> characters are not available, or the String is
     * <code>null</code>, the String will be returned without an an exception.
     * An exception is thrown if len is negative.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.right(null, *)    = null
     * StringUtils.right(*, -ve)     = ""
     * StringUtils.right("", *)      = ""
     * StringUtils.right("abc", 0)   = ""
     * StringUtils.right("abc", 2)   = "bc"
     * StringUtils.right("abc", 4)   = "abc"
     * </pre>
     *
     * @param str the String to get the rightmost characters from, may be null
     * @param len the length of the required String, must be zero or positive
     * @return the rightmost characters, <code>null</code> if null String input
     */
    public static String right(String str, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return EMPTY;
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(str.length() - len);
    }

    /**
     * <p>
     * Gets <code>len</code> characters from the middle of a String.
     * </p>
     * <p/>
     * <p>
     * If <code>len</code> characters are not available, the remainder of the
     * String will be returned without an exception. If the String is
     * <code>null</code>, <code>null</code> will be returned. An exception is
     * thrown if len is negative.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.mid(null, *, *)    = null
     * StringUtils.mid(*, *, -ve)     = ""
     * StringUtils.mid("", 0, *)      = ""
     * StringUtils.mid("abc", 0, 2)   = "ab"
     * StringUtils.mid("abc", 0, 4)   = "abc"
     * StringUtils.mid("abc", 2, 4)   = "c"
     * StringUtils.mid("abc", 4, 2)   = ""
     * StringUtils.mid("abc", -2, 2)  = "ab"
     * </pre>
     *
     * @param str the String to get the characters from, may be null
     * @param pos the position to start from, negative treated as zero
     * @param len the length of the required String, must be zero or positive
     * @return the middle characters, <code>null</code> if null String input
     */
    public static String mid(String str, int pos, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0 || pos > str.length()) {
            return EMPTY;
        }
        if (pos < 0) {
            pos = 0;
        }
        if (str.length() <= (pos + len)) {
            return str.substring(pos);
        }
        return str.substring(pos, pos + len);
    }

    // Joining
    // -----------------------------------------------------------------------

    /**
     * <p>
     * Concatenates elements of an array into a single String. Null objects or
     * empty strings within the array are represented by empty strings.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.concatenate(null)            = null
     * StringUtils.concatenate([])              = ""
     * StringUtils.concatenate([null])          = ""
     * StringUtils.concatenate(["a", "b", "c"]) = "abc"
     * StringUtils.concatenate([null, "", "a"]) = "a"
     * </pre>
     *
     * @param array the array of values to concatenate, may be null
     * @return the concatenated String, <code>null</code> if null array input
     * @deprecated Use the better named {@link #join(Object[])} instead. Method
     * will be removed in Commons Lang 3.0.
     */
    @Deprecated
    public static String concatenate(Object[] array) {
        return join(array, null);
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing
     * the provided list of elements.
     * </p>
     * <p/>
     * <p>
     * No separator is added to the joined String. Null objects or empty strings
     * within the array are represented by empty strings.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.join(null)            = null
     * StringUtils.join([])              = ""
     * StringUtils.join([null])          = ""
     * StringUtils.join(["a", "b", "c"]) = "abc"
     * StringUtils.join([null, "", "a"]) = "a"
     * </pre>
     *
     * @param array the array of values to join together, may be null
     * @return the joined String, <code>null</code> if null array input
     * @since 2.0
     */
    public static String join(Object[] array) {
        return join(array, null);
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing
     * the provided list of elements.
     * </p>
     * <p/>
     * <p>
     * No delimiter is added before or after the list. Null objects or empty
     * strings within the array are represented by empty strings.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.join(null, *)               = null
     * StringUtils.join([], *)                 = ""
     * StringUtils.join([null], *)             = ""
     * StringUtils.join(["a", "b", "c"], ';')  = "a;b;c"
     * StringUtils.join(["a", "b", "c"], null) = "abc"
     * StringUtils.join([null, "", "a"], ';')  = ";;a"
     * </pre>
     *
     * @param array     the array of values to join together, may be null
     * @param separator the separator character to use
     * @return the joined String, <code>null</code> if null array input
     * @since 2.0
     */
    public static String join(Object[] array, char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing
     * the provided list of elements.
     * </p>
     * <p/>
     * <p>
     * No delimiter is added before or after the list. Null objects or empty
     * strings within the array are represented by empty strings.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.join(null, *)               = null
     * StringUtils.join([], *)                 = ""
     * StringUtils.join([null], *)             = ""
     * StringUtils.join(["a", "b", "c"], ';')  = "a;b;c"
     * StringUtils.join(["a", "b", "c"], null) = "abc"
     * StringUtils.join([null, "", "a"], ';')  = ";;a"
     * </pre>
     *
     * @param array      the array of values to join together, may be null
     * @param separator  the separator character to use
     * @param startIndex the first index to start joining from. It is an error to pass
     *                   in an end index past the end of the array
     * @param endIndex   the index to stop joining from (exclusive). It is an error to
     *                   pass in an end index past the end of the array
     * @return the joined String, <code>null</code> if null array input
     * @since 2.0
     */
    public static String join(Object[] array, char separator, int startIndex,
                              int endIndex) {
        if (array == null) {
            return null;
        }
        int bufSize = (endIndex - startIndex);
        if (bufSize <= 0) {
            return EMPTY;
        }
        bufSize *= ((array[startIndex] == null ? 16 : array[startIndex]
                .toString().length()) + 1);
        StringBuffer buf = new StringBuffer(bufSize);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing
     * the provided list of elements.
     * </p>
     * <p/>
     * <p>
     * No delimiter is added before or after the list. A <code>null</code>
     * separator is the same as an empty String (""). Null objects or empty
     * strings within the array are represented by empty strings.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.join(null, *)                = null
     * StringUtils.join([], *)                  = ""
     * StringUtils.join([null], *)              = ""
     * StringUtils.join(["a", "b", "c"], "--")  = "a--b--c"
     * StringUtils.join(["a", "b", "c"], null)  = "abc"
     * StringUtils.join(["a", "b", "c"], "")    = "abc"
     * StringUtils.join([null, "", "a"], ',')   = ",,a"
     * </pre>
     *
     * @param array     the array of values to join together, may be null
     * @param separator the separator character to use, null treated as ""
     * @return the joined String, <code>null</code> if null array input
     */
    public static String join(Object[] array, String separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing
     * the provided list of elements.
     * </p>
     * <p/>
     * <p>
     * No delimiter is added before or after the list. A <code>null</code>
     * separator is the same as an empty String (""). Null objects or empty
     * strings within the array are represented by empty strings.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.join(null, *)                = null
     * StringUtils.join([], *)                  = ""
     * StringUtils.join([null], *)              = ""
     * StringUtils.join(["a", "b", "c"], "--")  = "a--b--c"
     * StringUtils.join(["a", "b", "c"], null)  = "abc"
     * StringUtils.join(["a", "b", "c"], "")    = "abc"
     * StringUtils.join([null, "", "a"], ',')   = ",,a"
     * </pre>
     *
     * @param array      the array of values to join together, may be null
     * @param separator  the separator character to use, null treated as ""
     * @param startIndex the first index to start joining from. It is an error to pass
     *                   in an end index past the end of the array
     * @param endIndex   the index to stop joining from (exclusive). It is an error to
     *                   pass in an end index past the end of the array
     * @return the joined String, <code>null</code> if null array input
     */
    public static String join(Object[] array, String separator, int startIndex,
                              int endIndex) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = EMPTY;
        }
        // endIndex - startIndex > 0: Len = NofStrings *(len(firstString) +
        // len(separator))
        // (Assuming that all Strings are roughly equally long)
        int bufSize = (endIndex - startIndex);
        if (bufSize <= 0) {
            return EMPTY;
        }
        bufSize *= ((array[startIndex] == null ? 16 : array[startIndex]
                .toString().length()) + separator.length());
        StringBuffer buf = new StringBuffer(bufSize);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    // Delete
    // -----------------------------------------------------------------------

    /**
     * <p>
     * Deletes all whitespaces from a String as defined by
     * {@link Character#isWhitespace(char)}.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.deleteWhitespace(null)         = null
     * StringUtils.deleteWhitespace("")           = ""
     * StringUtils.deleteWhitespace("abc")        = "abc"
     * StringUtils.deleteWhitespace("   ab  c  ") = "abc"
     * </pre>
     *
     * @param str the String to delete whitespace from, may be null
     * @return the String without whitespaces, <code>null</code> if null String
     * input
     */
    public static String deleteWhitespace(String str) {
        if (isEmpty(str)) {
            return str;
        }
        int sz = str.length();
        char[] chs = new char[sz];
        int count = 0;
        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                chs[count++] = str.charAt(i);
            }
        }
        if (count == sz) {
            return str;
        }
        return new String(chs, 0, count);
    }

    // Remove
    // -----------------------------------------------------------------------

    /**
     * <p>
     * Removes a substring only if it is at the begining of a source string,
     * otherwise returns the source string.
     * </p>
     * <p/>
     * <p>
     * A <code>null</code> source string will return <code>null</code>. An empty
     * ("") source string will return the empty string. A <code>null</code>
     * search string will return the source string.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.removeStart(null, *)      = null
     * StringUtils.removeStart("", *)        = ""
     * StringUtils.removeStart(*, null)      = *
     * StringUtils.removeStart("www.domain.com", "www.")   = "domain.com"
     * StringUtils.removeStart("domain.com", "www.")       = "domain.com"
     * StringUtils.removeStart("www.domain.com", "domain") = "www.domain.com"
     * StringUtils.removeStart("abc", "")    = "abc"
     * </pre>
     *
     * @param str    the source String to search, may be null
     * @param remove the String to search for and remove, may be null
     * @return the substring with the string removed if found, <code>null</code>
     * if null String input
     * @since 2.1
     */
    public static String removeStart(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        if (str.startsWith(remove)) {
            return str.substring(remove.length());
        }
        return str;
    }

    /**
     * <p>
     * Removes a substring only if it is at the end of a source string,
     * otherwise returns the source string.
     * </p>
     * <p/>
     * <p>
     * A <code>null</code> source string will return <code>null</code>. An empty
     * ("") source string will return the empty string. A <code>null</code>
     * search string will return the source string.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.removeEnd(null, *)      = null
     * StringUtils.removeEnd("", *)        = ""
     * StringUtils.removeEnd(*, null)      = *
     * StringUtils.removeEnd("www.domain.com", ".com.")  = "www.domain.com"
     * StringUtils.removeEnd("www.domain.com", ".com")   = "www.domain"
     * StringUtils.removeEnd("www.domain.com", "domain") = "www.domain.com"
     * StringUtils.removeEnd("abc", "")    = "abc"
     * </pre>
     *
     * @param str    the source String to search, may be null
     * @param remove the String to search for and remove, may be null
     * @return the substring with the string removed if found, <code>null</code>
     * if null String input
     * @since 2.1
     */
    public static String removeEnd(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        if (str.endsWith(remove)) {
            return str.substring(0, str.length() - remove.length());
        }
        return str;
    }

    /**
     * <p>
     * Removes all occurrences of a character from within the source string.
     * </p>
     * <p/>
     * <p>
     * A <code>null</code> source string will return <code>null</code>. An empty
     * ("") source string will return the empty string.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.remove(null, *)       = null
     * StringUtils.remove("", *)         = ""
     * StringUtils.remove("queued", 'u') = "qeed"
     * StringUtils.remove("queued", 'z') = "queued"
     * </pre>
     *
     * @param str    the source String to search, may be null
     * @param remove the char to search for and remove, may be null
     * @return the substring with the char removed if found, <code>null</code>
     * if null String input
     * @since 2.1
     */
    public static String remove(String str, char remove) {
        if (isEmpty(str) || str.indexOf(remove) == -1) {
            return str;
        }
        char[] chars = str.toCharArray();
        int pos = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] != remove) {
                chars[pos++] = chars[i];
            }
        }
        return new String(chars, 0, pos);
    }

    /**
     * <p>
     * Removes all occurrences of a substring from within the source string.
     * </p>
     * <p/>
     * <p>
     * A <code>null</code> source string will return <code>null</code>. An empty
     * ("") source string will return the empty string. A <code>null</code>
     * remove string will return the source string. An empty ("") remove string
     * will return the source string.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.remove(null, *)        = null
     * StringUtils.remove("", *)          = ""
     * StringUtils.remove(*, null)        = *
     * StringUtils.remove(*, "")          = *
     * StringUtils.remove("queued", "ue") = "qd"
     * StringUtils.remove("queued", "zz") = "queued"
     * </pre>
     *
     * @param str    the source String to search, may be null
     * @param remove the String to search for and remove, may be null
     * @return the substring with the string removed if found, <code>null</code>
     * if null String input
     * @since 2.1
     */
    public static String remove(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        return replace(str, remove, EMPTY, -1);
    }

    // Empty checks
    // -----------------------------------------------------------------------

    /**
     * <p>
     * Checks if a String is empty ("") or null.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     * <p/>
     * <p>
     * NOTE: This method changed in Lang version 2.0. It no longer trims the
     * String. That functionality is available in isBlank().
     * </p>
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if the String is empty or null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * <p>
     * Checks if a String is not empty ("") and not null.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.isNotEmpty(null)      = false
     * StringUtils.isNotEmpty("")        = false
     * StringUtils.isNotEmpty(" ")       = true
     * StringUtils.isNotEmpty("bob")     = true
     * StringUtils.isNotEmpty("  bob  ") = true
     * </pre>
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if the String is not empty and not null
     */
    public static boolean isNotEmpty(String str) {
        return !StringUtils.isEmpty(str);
    }

    /**
     * <p>
     * Checks if a String is whitespace, empty ("") or null.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if the String is null, empty or whitespace
     * @since 2.0
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>
     * Checks if a String is not empty (""), not null and not whitespace only.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if the String is not empty and not null and not
     * whitespace
     * @since 2.0
     */
    public static boolean isNotBlank(String str) {
        return !StringUtils.isBlank(str);
    }

    /**
     * <p>
     * Compares two Strings, returning <code>true</code> if they are equal.
     * </p>
     * <p/>
     * <p>
     * <code>null</code>s are handled without exceptions. Two <code>null</code>
     * references are considered to be equal. The comparison is case sensitive.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.equals(null, null)   = true
     * StringUtils.equals(null, "abc")  = false
     * StringUtils.equals("abc", null)  = false
     * StringUtils.equals("abc", "abc") = true
     * StringUtils.equals("abc", "ABC") = false
     * </pre>
     *
     * @param str1 the first String, may be null
     * @param str2 the second String, may be null
     * @return <code>true</code> if the Strings are equal, case sensitive, or
     * both <code>null</code>
     * @see java.lang.String#equals(Object)
     */
    public static boolean equals(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equals(str2);
    }

    /**
     * <p>
     * Compares two Strings, returning <code>true</code> if they are equal
     * ignoring the case.
     * </p>
     * <p/>
     * <p>
     * <code>null</code>s are handled without exceptions. Two <code>null</code>
     * references are considered equal. Comparison is case insensitive.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.equalsIgnoreCase(null, null)   = true
     * StringUtils.equalsIgnoreCase(null, "abc")  = false
     * StringUtils.equalsIgnoreCase("abc", null)  = false
     * StringUtils.equalsIgnoreCase("abc", "abc") = true
     * StringUtils.equalsIgnoreCase("abc", "ABC") = true
     * </pre>
     *
     * @param str1 the first String, may be null
     * @param str2 the second String, may be null
     * @return <code>true</code> if the Strings are equal, case insensitive, or
     * both <code>null</code>
     * @see java.lang.String#equalsIgnoreCase(String)
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equalsIgnoreCase(str2);
    }

    // Replacing
    // -----------------------------------------------------------------------

    /**
     * <p>
     * Replaces a String with another String inside a larger String, once.
     * </p>
     * <p/>
     * <p>
     * A <code>null</code> reference passed to this method is a no-op.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.replaceOnce(null, *, *)        = null
     * StringUtils.replaceOnce("", *, *)          = ""
     * StringUtils.replaceOnce("any", null, *)    = "any"
     * StringUtils.replaceOnce("any", *, null)    = "any"
     * StringUtils.replaceOnce("any", "", *)      = "any"
     * StringUtils.replaceOnce("aba", "a", null)  = "aba"
     * StringUtils.replaceOnce("aba", "a", "")    = "ba"
     * StringUtils.replaceOnce("aba", "a", "z")   = "zba"
     * </pre>
     *
     * @param text         text to search and replace in, may be null
     * @param searchString the String to search for, may be null
     * @param replacement  the String to replace with, may be null
     * @return the text with any replacements processed, <code>null</code> if
     * null String input
     * @see #replace(String text, String searchString, String replacement, int
     * max)
     */
    public static String replaceOnce(String text, String searchString,
                                     String replacement) {
        return replace(text, searchString, replacement, 1);
    }

    /**
     * <p>
     * Replaces all occurrences of a String within another String.
     * </p>
     * <p/>
     * <p>
     * A <code>null</code> reference passed to this method is a no-op.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.replace(null, *, *)        = null
     * StringUtils.replace("", *, *)          = ""
     * StringUtils.replace("any", null, *)    = "any"
     * StringUtils.replace("any", *, null)    = "any"
     * StringUtils.replace("any", "", *)      = "any"
     * StringUtils.replace("aba", "a", null)  = "aba"
     * StringUtils.replace("aba", "a", "")    = "b"
     * StringUtils.replace("aba", "a", "z")   = "zbz"
     * </pre>
     *
     * @param text         text to search and replace in, may be null
     * @param searchString the String to search for, may be null
     * @param replacement  the String to replace it with, may be null
     * @return the text with any replacements processed, <code>null</code> if
     * null String input
     * @see #replace(String text, String searchString, String replacement, int
     * max)
     */
    public static String replace(String text, String searchString,
                                 String replacement) {
        return replace(text, searchString, replacement, -1);
    }

    /**
     * <p>
     * Replaces a String with another String inside a larger String, for the
     * first <code>max</code> values of the search String.
     * </p>
     * <p/>
     * <p>
     * A <code>null</code> reference passed to this method is a no-op.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.replace(null, *, *, *)         = null
     * StringUtils.replace("", *, *, *)           = ""
     * StringUtils.replace("any", null, *, *)     = "any"
     * StringUtils.replace("any", *, null, *)     = "any"
     * StringUtils.replace("any", "", *, *)       = "any"
     * StringUtils.replace("any", *, *, 0)        = "any"
     * StringUtils.replace("abaa", "a", null, -1) = "abaa"
     * StringUtils.replace("abaa", "a", "", -1)   = "b"
     * StringUtils.replace("abaa", "a", "z", 0)   = "abaa"
     * StringUtils.replace("abaa", "a", "z", 1)   = "zbaa"
     * StringUtils.replace("abaa", "a", "z", 2)   = "zbza"
     * StringUtils.replace("abaa", "a", "z", -1)  = "zbzz"
     * </pre>
     *
     * @param text         text to search and replace in, may be null
     * @param searchString the String to search for, may be null
     * @param replacement  the String to replace it with, may be null
     * @param max          maximum number of values to replace, or <code>-1</code> if no
     *                     maximum
     * @return the text with any replacements processed, <code>null</code> if
     * null String input
     */
    public static String replace(String text, String searchString,
                                 String replacement, int max) {
        if (isEmpty(text) || isEmpty(searchString) || replacement == null
                || max == 0) {
            return text;
        }
        int start = 0;
        int end = text.indexOf(searchString, start);
        if (end == -1) {
            return text;
        }
        int replLength = searchString.length();
        int increase = replacement.length() - replLength;
        increase = (increase < 0 ? 0 : increase);
        increase *= (max < 0 ? 16 : (max > 64 ? 64 : max));
        StringBuffer buf = new StringBuffer(text.length() + increase);
        while (end != -1) {
            buf.append(text.substring(start, end)).append(replacement);
            start = end + replLength;
            if (--max == 0) {
                break;
            }
            end = text.indexOf(searchString, start);
        }
        buf.append(text.substring(start));
        return buf.toString();
    }

    /**
     * <p>
     * Left pad a String with spaces (' ').
     * </p>
     * <p/>
     * <p>
     * The String is padded to the size of <code>size</code>.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.leftPad(null, *)   = null
     * StringUtils.leftPad("", 3)     = "   "
     * StringUtils.leftPad("bat", 3)  = "bat"
     * StringUtils.leftPad("bat", 5)  = "  bat"
     * StringUtils.leftPad("bat", 1)  = "bat"
     * StringUtils.leftPad("bat", -1) = "bat"
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size the size to pad to
     * @return left padded String or original String if no padding is necessary,
     * <code>null</code> if null String input
     */
    public static String leftPad(String str, int size) {
        return leftPad(str, size, ' ');
    }

    /**
     * <p>
     * Left pad a String with a specified character.
     * </p>
     * <p/>
     * <p>
     * Pad to a size of <code>size</code>.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.leftPad(null, *, *)     = null
     * StringUtils.leftPad("", 3, 'z')     = "zzz"
     * StringUtils.leftPad("bat", 3, 'z')  = "bat"
     * StringUtils.leftPad("bat", 5, 'z')  = "zzbat"
     * StringUtils.leftPad("bat", 1, 'z')  = "bat"
     * StringUtils.leftPad("bat", -1, 'z') = "bat"
     * </pre>
     *
     * @param str     the String to pad out, may be null
     * @param size    the size to pad to
     * @param padChar the character to pad with
     * @return left padded String or original String if no padding is necessary,
     * <code>null</code> if null String input
     * @since 2.0
     */
    public static String leftPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (pads > PAD_LIMIT) {
            return leftPad(str, size, String.valueOf(padChar));
        }
        return padding(pads, padChar).concat(str);
    }

    /**
     * <p>
     * Left pad a String with a specified String.
     * </p>
     * <p/>
     * <p>
     * Pad to a size of <code>size</code>.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.leftPad(null, *, *)      = null
     * StringUtils.leftPad("", 3, "z")      = "zzz"
     * StringUtils.leftPad("bat", 3, "yz")  = "bat"
     * StringUtils.leftPad("bat", 5, "yz")  = "yzbat"
     * StringUtils.leftPad("bat", 8, "yz")  = "yzyzybat"
     * StringUtils.leftPad("bat", 1, "yz")  = "bat"
     * StringUtils.leftPad("bat", -1, "yz") = "bat"
     * StringUtils.leftPad("bat", 5, null)  = "  bat"
     * StringUtils.leftPad("bat", 5, "")    = "  bat"
     * </pre>
     *
     * @param str    the String to pad out, may be null
     * @param size   the size to pad to
     * @param padStr the String to pad with, null or empty treated as single space
     * @return left padded String or original String if no padding is necessary,
     * <code>null</code> if null String input
     */
    public static String leftPad(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = " ";
        }
        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return leftPad(str, size, padStr.charAt(0));
        }
        if (pads == padLen) {
            return padStr.concat(str);
        } else if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        } else {
            char[] padding = new char[pads];
            char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return new String(padding).concat(str);
        }
    }

    /**
     * <p>
     * Returns padding using the specified delimiter repeated to a given length.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.padding(0, 'e')  = ""
     * StringUtils.padding(3, 'e')  = "eee"
     * StringUtils.padding(-2, 'e') = IndexOutOfBoundsException
     * </pre>
     * <p/>
     * <p>
     * Note: this method doesn't not support padding with <a
     * href="http://www.unicode.org/glossary/#supplementary_character">Unicode
     * Supplementary Characters</a> as they require a pair of <code>char</code>s
     * to be represented. If you are needing to support full I18N of your
     * applications consider using {@link #repeat(String, int)} instead.
     * </p>
     *
     * @param repeat  number of times to repeat delim
     * @param padChar character to repeat
     * @return String with repeated character
     * @throws IndexOutOfBoundsException if <code>repeat &lt; 0</code>
     * @see #repeat(String, int)
     */
    private static String padding(int repeat, char padChar)
            throws IndexOutOfBoundsException {
        if (repeat < 0) {
            throw new IndexOutOfBoundsException(
                    "Cannot pad a negative amount: " + repeat);
        }
        final char[] buf = new char[repeat];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = padChar;
        }
        return new String(buf);
    }

    // Padding
    // -----------------------------------------------------------------------

    /**
     * <p>
     * Repeat a String <code>repeat</code> times to form a new String.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.repeat(null, 2) = null
     * StringUtils.repeat("", 0)   = ""
     * StringUtils.repeat("", 2)   = ""
     * StringUtils.repeat("a", 3)  = "aaa"
     * StringUtils.repeat("ab", 2) = "abab"
     * StringUtils.repeat("a", -2) = ""
     * </pre>
     *
     * @param str    the String to repeat, may be null
     * @param repeat number of times to repeat str, negative treated as zero
     * @return a new String consisting of the original String repeated,
     * <code>null</code> if null String input
     * @since 2.5
     */
    public static String repeat(String str, int repeat) {
        // Performance tuned for 2.0 (JDK1.4)
        if (str == null) {
            return null;
        }
        if (repeat <= 0) {
            return EMPTY;
        }
        int inputLength = str.length();
        if (repeat == 1 || inputLength == 0) {
            return str;
        }
        if (inputLength == 1 && repeat <= PAD_LIMIT) {
            return padding(repeat, str.charAt(0));
        }
        int outputLength = inputLength * repeat;
        switch (inputLength) {
            case 1:
                char ch = str.charAt(0);
                char[] output1 = new char[outputLength];
                for (int i = repeat - 1; i >= 0; i--) {
                    output1[i] = ch;
                }
                return new String(output1);
            case 2:
                char ch0 = str.charAt(0);
                char ch1 = str.charAt(1);
                char[] output2 = new char[outputLength];
                for (int i = repeat * 2 - 2; i >= 0; i--, i--) {
                    output2[i] = ch0;
                    output2[i + 1] = ch1;
                }
                return new String(output2);
            default:
                StringBuffer buf = new StringBuffer(outputLength);
                for (int i = 0; i < repeat; i++) {
                    buf.append(str);
                }
                return buf.toString();
        }
    }

    // Count matches
    // -----------------------------------------------------------------------

    /**
     * <p>
     * Counts how many times the substring appears in the larger String.
     * </p>
     * <p/>
     * <p>
     * A <code>null</code> or empty ("") String input returns <code>0</code>.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.countMatches(null, *)       = 0
     * StringUtils.countMatches("", *)         = 0
     * StringUtils.countMatches("abba", null)  = 0
     * StringUtils.countMatches("abba", "")    = 0
     * StringUtils.countMatches("abba", "a")   = 2
     * StringUtils.countMatches("abba", "ab")  = 1
     * StringUtils.countMatches("abba", "xxx") = 0
     * </pre>
     *
     * @param str the String to check, may be null
     * @param sub the substring to count, may be null
     * @return the number of occurrences, 0 if either String is
     * <code>null</code>
     */
    public static int countMatches(String str, String sub) {
        if (isEmpty(str) || isEmpty(sub)) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }

    // Character Tests
    // -----------------------------------------------------------------------

    /**
     * <p>
     * Checks if the String contains only unicode letters.
     * </p>
     * <p/>
     * <p>
     * <code>null</code> will return <code>false</code>. An empty String ("")
     * will return <code>true</code>.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.isAlpha(null)   = false
     * StringUtils.isAlpha("")     = true
     * StringUtils.isAlpha("  ")   = false
     * StringUtils.isAlpha("abc")  = true
     * StringUtils.isAlpha("ab2c") = false
     * StringUtils.isAlpha("ab-c") = false
     * </pre>
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if only contains letters, and is non-null
     */
    public static boolean isAlpha(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isLetter(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>
     * Checks if the String contains only unicode letters and space (' ').
     * </p>
     * <p/>
     * <p>
     * <code>null</code> will return <code>false</code> An empty String ("")
     * will return <code>true</code>.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.isAlphaSpace(null)   = false
     * StringUtils.isAlphaSpace("")     = true
     * StringUtils.isAlphaSpace("  ")   = true
     * StringUtils.isAlphaSpace("abc")  = true
     * StringUtils.isAlphaSpace("ab c") = true
     * StringUtils.isAlphaSpace("ab2c") = false
     * StringUtils.isAlphaSpace("ab-c") = false
     * </pre>
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if only contains letters and space, and is
     * non-null
     */
    public static boolean isAlphaSpace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((Character.isLetter(str.charAt(i)) == false)
                    && (str.charAt(i) != ' ')) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>
     * Checks if the String contains only unicode letters or digits.
     * </p>
     * <p/>
     * <p>
     * <code>null</code> will return <code>false</code>. An empty String ("")
     * will return <code>true</code>.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.isAlphanumeric(null)   = false
     * StringUtils.isAlphanumeric("")     = true
     * StringUtils.isAlphanumeric("  ")   = false
     * StringUtils.isAlphanumeric("abc")  = true
     * StringUtils.isAlphanumeric("ab c") = false
     * StringUtils.isAlphanumeric("ab2c") = true
     * StringUtils.isAlphanumeric("ab-c") = false
     * </pre>
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if only contains letters or digits, and is
     * non-null
     */
    public static boolean isAlphanumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isLetterOrDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>
     * Checks if the String contains only unicode letters, digits or space (
     * <code>' '</code>).
     * </p>
     * <p/>
     * <p>
     * <code>null</code> will return <code>false</code>. An empty String ("")
     * will return <code>true</code>.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.isAlphanumeric(null)   = false
     * StringUtils.isAlphanumeric("")     = true
     * StringUtils.isAlphanumeric("  ")   = true
     * StringUtils.isAlphanumeric("abc")  = true
     * StringUtils.isAlphanumeric("ab c") = true
     * StringUtils.isAlphanumeric("ab2c") = true
     * StringUtils.isAlphanumeric("ab-c") = false
     * </pre>
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if only contains letters, digits or space, and
     * is non-null
     */
    public static boolean isAlphanumericSpace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((Character.isLetterOrDigit(str.charAt(i)) == false)
                    && (str.charAt(i) != ' ')) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>
     * Checks if the String contains only unicode digits. A decimal point is not
     * a unicode digit and returns false.
     * </p>
     * <p/>
     * <p>
     * <code>null</code> will return <code>false</code>. An empty String ("")
     * will return <code>true</code>.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.isNumeric(null)   = false
     * StringUtils.isNumeric("")     = true
     * StringUtils.isNumeric("  ")   = false
     * StringUtils.isNumeric("123")  = true
     * StringUtils.isNumeric("12 3") = false
     * StringUtils.isNumeric("ab2c") = false
     * StringUtils.isNumeric("12-3") = false
     * StringUtils.isNumeric("12.3") = false
     * </pre>
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if only contains digits, and is non-null
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>
     * Checks if the String contains only unicode digits or space (
     * <code>' '</code>). A decimal point is not a unicode digit and returns
     * false.
     * </p>
     * <p/>
     * <p>
     * <code>null</code> will return <code>false</code>. An empty String ("")
     * will return <code>true</code>.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.isNumeric(null)   = false
     * StringUtils.isNumeric("")     = true
     * StringUtils.isNumeric("  ")   = true
     * StringUtils.isNumeric("123")  = true
     * StringUtils.isNumeric("12 3") = true
     * StringUtils.isNumeric("ab2c") = false
     * StringUtils.isNumeric("12-3") = false
     * StringUtils.isNumeric("12.3") = false
     * </pre>
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if only contains digits or space, and is
     * non-null
     */
    public static boolean isNumericSpace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((Character.isDigit(str.charAt(i)) == false)
                    && (str.charAt(i) != ' ')) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>
     * Checks if the String contains only whitespace.
     * </p>
     * <p/>
     * <p>
     * <code>null</code> will return <code>false</code>. An empty String ("")
     * will return <code>true</code>.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.isWhitespace(null)   = false
     * StringUtils.isWhitespace("")     = true
     * StringUtils.isWhitespace("  ")   = true
     * StringUtils.isWhitespace("abc")  = false
     * StringUtils.isWhitespace("ab2c") = false
     * StringUtils.isWhitespace("ab-c") = false
     * </pre>
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if only contains whitespace, and is non-null
     * @since 2.0
     */
    public static boolean isWhitespace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>
     * Checks if the String contains only lowercase characters.
     * </p>
     * <p/>
     * <p>
     * <code>null</code> will return <code>false</code>. An empty String ("")
     * will return <code>false</code>.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.isAllLowerCase(null)   = false
     * StringUtils.isAllLowerCase("")     = false
     * StringUtils.isAllLowerCase("  ")   = false
     * StringUtils.isAllLowerCase("abc")  = true
     * StringUtils.isAllLowerCase("abC") = false
     * </pre>
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if only contains lowercase characters, and is
     * non-null
     * @since 2.5
     */
    public static boolean isAllLowerCase(String str) {
        if (str == null || isEmpty(str)) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isLowerCase(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>
     * Checks if the String contains only uppercase characters.
     * </p>
     * <p/>
     * <p>
     * <code>null</code> will return <code>false</code>. An empty String ("")
     * will return <code>false</code>.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.isAllUpperCase(null)   = false
     * StringUtils.isAllUpperCase("")     = false
     * StringUtils.isAllUpperCase("  ")   = false
     * StringUtils.isAllUpperCase("ABC")  = true
     * StringUtils.isAllUpperCase("aBC") = false
     * </pre>
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if only contains uppercase characters, and is
     * non-null
     * @since 2.5
     */
    public static boolean isAllUpperCase(String str) {
        if (str == null || isEmpty(str)) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isUpperCase(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    // Reversing
    // -----------------------------------------------------------------------

    /**
     * <p>
     * Reverses a String as per {@link StringBuffer#reverse()}.
     * </p>
     * <p/>
     * <p>
     * A <code>null</code> String returns <code>null</code>.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.reverse(null)  = null
     * StringUtils.reverse("")    = ""
     * StringUtils.reverse("bat") = "tab"
     * </pre>
     *
     * @param str the String to reverse, may be null
     * @return the reversed String, <code>null</code> if null String input
     */
    public static String reverse(String str) {
        if (str == null) {
            return null;
        }
        return new StringBuffer(str).reverse().toString();
    }

    /**
     * 字符串压缩
     *
     * @param @param  str
     * @param @return
     * @return String
     * @throws
     * @Title: compress
     */
    public static String compress(String str) {
        try {
            if (null == str || str.length() <= 0) {
                return str;
            }
            // 创建一个新的 byte 数组输出流
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // 使用默认缓冲区大小创建新的输出流
            GZIPOutputStream gzip = new GZIPOutputStream(out);
            // 将 b.length 个字节写入此输出流
            gzip.write(str.getBytes());
            gzip.close();
            // 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
            return out.toString("ISO-8859-1");
        } catch (Exception e) {
            return str;
        }
    }

    /**
     * 字符串的解压
     *
     * @param str 对字符串解压
     * @return 返回解压缩后的字符串
     * @throws IOException
     */
    public static String unCompress(String str) {
        try {
            if (null == str || str.length() <= 0) {
                return str;
            }
            // 创建一个新的 byte 数组输出流
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // 创建一个 ByteArrayInputStream，使用 buf 作为其缓冲区数组
            ByteArrayInputStream in = new ByteArrayInputStream(
                    str.getBytes("ISO-8859-1"));
            // 使用默认缓冲区大小创建新的输入流
            GZIPInputStream gzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n = 0;
            while ((n = gzip.read(buffer)) >= 0) {// 将未压缩数据读入字节数组
                // 将指定 byte 数组中从偏移量 off 开始的 len 个字节写入此 byte数组输出流
                out.write(buffer, 0, n);
            }
            // 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
            return out.toString("UTF-8");
        } catch (Exception e) {
            return str;
        }
    }
}