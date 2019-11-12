package org.shichuangnet.jojo.oilpool.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author jojo
 * 处理api得到的response
 */
public class ResponseUtils {

    public static final String CODE = "code";
    public static final int REQUEST_WITH_NO_CODE = -1;
    public static final int UNDEFINE_ERROR = -2;
    public static final int REQUEST_SUCCESS = 200;
    public static final int REQUEST_FAILED = 400;
    public static final int PARAMETER_ERROR = 401;
    public static final int UNLOGIN = 402;
    public static final int INSUFFICIENT_PERMISSION = 403;
    public static final int RESOURCE_NOT_FOUND = 404;
    public static final int SYSTEM_ERROR = 500;

    /**
     *
     * 检查http请求之后的结果是否正常
     * */
    public static boolean checkResponse(String response) {
        try {
            JSONObject json = new JSONObject(response);
            if (!json.has(CODE)) {
                return false;
            } else {
                return json.getInt(CODE) == REQUEST_SUCCESS;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @return 返回response码
     * */
    public static int getResponseCode(String response) {
        JSONObject json = null;
        try {
            json = new JSONObject(response);
            if (!json.has(CODE)) {
                return REQUEST_WITH_NO_CODE;
            } else {
                return json.getInt(CODE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return UNDEFINE_ERROR;
    }

}
