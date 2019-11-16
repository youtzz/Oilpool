package org.shichuangnet.jojo.oilpool.api;

import android.content.Context;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;
import org.shichuangnet.jojo.oilpool.utils.SharedUtils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;

/**
 *
 * 封装了项目需要的api
 * @author jojo
 * */
public class OilPoolApi {

    private static final String BASE_URL = "http://47.97.158.227:9080/api/";
    //  人工数据相关
    private static final String GET_ALL_DATA_URL = BASE_URL + "manual/" + "all";
    private static final String GET_PAGING_DATA_URL = BASE_URL + "manual/";
    private static final String GET_FILTER_DATA_BY_TIME_URL = BASE_URL + "manual/" + "filter";
    private static final String GET_NEW_DATA_URL = BASE_URL + "manual/" + "new";
    private static final String PUT_DATA_URL = BASE_URL + "manual";

    //  自动数据相关（温度、ph）
    private static final String GET_PAGING_AUTO_DATA_URL = BASE_URL + "auto/" + "page";
    private static final String GET_NEW_AUTO_DATA_URL = BASE_URL + "auto/" + "new";
    private static final String GET_FILTER_AUTO_DATA_BY_TIME_URL = BASE_URL + "auto/" + "filter";

    //  用户登录相关
    private static final String USER_LOGIN_URL = BASE_URL + "account";

    private String token;
    private Map<String, String> headerMap = new HashMap<>();
    private Map<String, String> paramsMap = new HashMap<>();

    private static OilPoolApi oilPoolApi;

    public static OilPoolApi getInstance(Context context) {
        if (oilPoolApi == null) {
            oilPoolApi = new OilPoolApi();
        }
        //  每次都获取一下Token，app运行中发现切换账号后token更新的并不及时，todo 或许应该使用其他方式
        oilPoolApi.token = SharedUtils.getString(context, SharedUtils.USER_TOKEN, "");
        oilPoolApi.headerMap.put("token", oilPoolApi.token);
        return oilPoolApi;
    }

    /**
     * 得到所有人工数据
     * */
    public void getAllData(Object tag, StringCallback callback) {
        OkHttpUtils.get().tag(tag).url(GET_ALL_DATA_URL).build().execute(callback);
    }

    /**
     *
     * 通过时间过滤人工数据
     * @param startTime 起始时间
     * @param endTime 末尾时间
     * */
    public void getFilterDataByTime(Object tag, long startTime, long endTime, Callback callback) {
        paramsMap.clear();
        paramsMap.put("start", String.valueOf(startTime));
        paramsMap.put("end", String.valueOf(endTime));
        OkHttpUtils
                .get()
                .tag(tag)
                .url(GET_FILTER_DATA_BY_TIME_URL)
                .headers(headerMap)
                .params(paramsMap)
                .build()
                .execute(callback);
    }
    /**
     *
     * 通过页码获取人工数据
     * */
    public void getPagingDate(Object tag, int page, int size, StringCallback callback) {
        paramsMap.clear();
        paramsMap.put("page", String.valueOf(page));
        paramsMap.put("size", String.valueOf(size));
        OkHttpUtils.get()
                .tag(tag)
                .url(GET_PAGING_DATA_URL)
                .headers(headerMap)
                .params(paramsMap)
                .build()
                .execute(callback);
    }

    /**
     *
     * 得到最新的一项人工数据
     * */
    public void getNewData(Object tag, StringCallback callback) {
        OkHttpUtils.get().tag(tag).url(GET_NEW_DATA_URL).headers(headerMap).build().execute(callback);
    }
    /**
     *
     * 上传一个人工数据
     * */
    public void putData(Object tag, double conductivity, double density, StringCallback callback) {
        paramsMap.clear();
        paramsMap.put("conductivity", String.valueOf(conductivity));
        paramsMap.put("density", String.valueOf(density));
        JSONObject jsonObject = new JSONObject(paramsMap);
        String jsonStr = jsonObject.toString();
        OkHttpUtils
                .postString()
                .tag(tag)
                .url(PUT_DATA_URL)
                .headers(headerMap)
                .content(jsonStr)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(callback);
    }

    /**
     *
     * 通过时间戳筛选自动数据（ph、温度等）
     * @param startTime 起始时间
     * @param endTime 末尾时间
     * */
    public void getFilterAutoData(Object tag, long startTime, long endTime, Callback callback) {
        paramsMap.clear();
        paramsMap.put("start", String.valueOf(startTime));
        paramsMap.put("end", String.valueOf(endTime));
        OkHttpUtils
                .get()
                .tag(tag)
                .url(GET_FILTER_AUTO_DATA_BY_TIME_URL)
                .headers(headerMap)
                .params(paramsMap)
                .build()
                .execute(callback);
    }

    /**
     *
     * 通过分页方式获取自动数据（ph、温度等）
     * */
    public void getPagingAutoData(Object tag, int page, int size, StringCallback callback) {
        paramsMap.clear();
        paramsMap.put("page", String.valueOf(page));
        paramsMap.put("size", String.valueOf(size));
        OkHttpUtils.get()
                .tag(tag)
                .url(GET_PAGING_AUTO_DATA_URL)
                .headers(headerMap)
                .params(paramsMap)
                .build()
                .execute(callback);
    }

    /**
     *
     * 得到最新的一项自动数据（ph、温度等）
     * */
    public void getNewAutoData(Object tag, StringCallback callback) {
        OkHttpUtils.get().tag(tag).url(GET_NEW_AUTO_DATA_URL).headers(headerMap).build().execute(callback);
    }

    /**
     *
     * 登录成功会在回调中返回用户token
     * */
    public void userLogin(Object tag, String userName, String userPwd, StringCallback callback) {
        paramsMap.clear();
        paramsMap.put("userName", userName);
        paramsMap.put("userPwd", userPwd);
        JSONObject jsonObject = new JSONObject(paramsMap);
        String jsonStr = jsonObject.toString();
        OkHttpUtils
                .postString()
                .tag(tag)
                .url(USER_LOGIN_URL)
                .content(jsonStr)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(callback);
    }

    /**
     * 检查APP版本
     * */
    public void checkAppVersion(Object tag, StringCallback callback) {
        paramsMap.clear();
        paramsMap.put("api_token", "3dd881aa7f0197489e16a517ef49f1ca");  //api_token 在内测平台fir.im上
        OkHttpUtils
                .get()
                .tag(tag)
                .url("http://api.fir.im/apps/latest/5da9dc0823389f491f911815")  //尾号为appid，同样在内测平台fir.im上
                .params(paramsMap)
                .build()
                .execute(callback);
    }
}
