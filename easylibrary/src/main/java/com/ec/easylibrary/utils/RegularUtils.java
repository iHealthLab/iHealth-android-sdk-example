package com.ec.easylibrary.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达校验类
 * Created by Administrator on 2018/4/23.
 */

public class RegularUtils {

    /**
     * 验证名字是否是真实姓名
     *
     * @param name
     * @return
     */
    public static boolean verifyRealName(String name) {
        if (name.contains("·") || name.contains("•")) {
            if (name.matches("^[\\u4e00-\\u9fa5]+[·•][\\u4e00-\\u9fa5]+$")) {
                return true;
            } else {
                return false;
            }
        } else {
            if (name.matches("^[\\u4e00-\\u9fa5]+$")) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    // 判断是否为手机号(粗略校验规则)
    public static boolean isPhone(String inputText) {
        Pattern p = Pattern.compile("^(13[0-9]|14[0-9]|15[0-9]|18[0-9]|17[0-9]|16[0-9])\\d{8}$");
        Matcher m = p.matcher(inputText);
        return m.matches();
    }

    // 判断格式是否为email
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    // 判断格式是否为email
    public static boolean isIDCard(String idCard) {
        String str = "(^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)" +
                "|(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{2}$)";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(idCard);
        return m.matches();
    }
}
