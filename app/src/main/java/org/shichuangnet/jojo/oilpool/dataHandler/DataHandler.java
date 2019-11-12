package org.shichuangnet.jojo.oilpool.dataHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shichuangnet.jojo.oilpool.beans.DataBean;
import org.shichuangnet.jojo.oilpool.beans.UserBean;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jojo
 * todo  这个类的方法名多数不恰当，need rename
 * */
public class DataHandler {

    /**
     *
     * 处理向服务器请求人工数据得到数据
     * @param response 数据字符串
     * @param oldDataCounts 原有数据的总数，用来处理数据的项数
     * @return DataBean List
     * */
    public static List<DataBean> handleDataBeans(String response, int oldDataCounts) {

        List<DataBean> list = new ArrayList<>();

        try {
            JSONArray jArr;
            JSONObject json = new JSONObject(response);
            //  判断json是否存在名为data的json对象
            if (!json.has("data")) {
                return list;
            } else {  //  todo: 解析的方式不是很好，服务器API返回的json格式不够统一，耦合性过高，仍需解耦
                //  以data为数组：API为获取全部数据、以时间戳获取数据
                jArr = json.optJSONArray("data");
                if (jArr == null) {
                    json = json.getJSONObject("data");
                    //  以content为数组：API为分页获取数据
                    if (json.has("content")) {
                        jArr = json.getJSONArray("content");
                    } else {
                        //  没有json数组存在： API为获取最新时间戳数据
                        jArr = new JSONArray().put(json);
                    }
                }
            }
            for (int i = 0; i < jArr.length(); i++) {
                DataBean beans = new DataBean(
                        jArr.getJSONObject(i).getString("manualId"),
                        jArr.getJSONObject(i).getString("density"),
                        jArr.getJSONObject(i).getString("conductivity"),
                        jArr.getJSONObject(i).getString("enterpriseId"),
                        jArr.getJSONObject(i).getLong("createTime"),
                        jArr.getJSONObject(i).getLong("updateTime"),
                        oldDataCounts+ i +1  //  数据在集合中的项数
                );
                list.add(beans);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static UserBean getUserBeans(String response) {
        UserBean ub = null;
        try {
            JSONObject json = new JSONObject(response);
            if (json.optJSONObject("data") != null) {
                json = json.getJSONObject("data");
                if (!json.optString("userToken").isEmpty()) {
                    ub = new UserBean();
                    ub.setUserToken(json.getString("userToken"));
                    ub.setUserName(json.getString("userName"));
                    ub.setUserPsw(json.getString("userPsw"));
                    return ub;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ub;
    }
}
