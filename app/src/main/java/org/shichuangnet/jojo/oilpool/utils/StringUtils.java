package org.shichuangnet.jojo.oilpool.utils;

public class StringUtils {

    /**
     * 确保string转换的数值正确
     * */
    public static float getStr2Float(final String string) {
        return Float.parseFloat(strNumFilter(string));
    }

    public static double getStr2Double(final String string) {
        return Double.parseDouble(strNumFilter(string));
    }

    public static int getStr2Int(final String string) {
        String num = strNumFilter(string);
        int dotIndex = num.indexOf(".");
        if (dotIndex != -1) {
            num = num.substring(0, dotIndex);
        }
        return Integer.parseInt(num);
    }

    //  对string进行过滤
    public static String strNumFilter(final String string) {
        StringBuilder sb = new StringBuilder();
        boolean noDot = true;
        for (int i = 0; i < string.length(); i++) {
            char ch = string.charAt(i);
            if (ch >= '.' && ch <= '9') {
                //  如果已经有小数点了，那么直接跳出循环
                if (ch == '.') {
                    if (noDot) {
                        noDot = false;
                    } else {
                        break;
                    }
                }
                sb.append(ch);
            }
        }
        //  判断小数点的位置
        int dotIndex = sb.indexOf(".");
        //  如果小数点在开头
        if (dotIndex == 0) {
            if (sb.length() == 1) {
                sb.insert(0, '0').append('0');
            } else {
                sb.insert(0, '0');
            }
        }
        //  如果小数点在末尾
        else if (dotIndex == sb.length()-1) {
            sb.append('0');
        }

        return sb.toString();
    }
}
