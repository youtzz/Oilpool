package org.shichuangnet.jojo.oilpool.beans;

import java.util.ArrayList;
import java.util.List;

public class OverViewBean {

    private int todayUploadCount;  //  今日上传的数据量
    private int totalDataCount;      //  服务器中数据总量
    private int errorCount;        //  数据中的错误总量  （暂时没有这个功能，预留了）

    private List<Float> conductivities;//  导电率
    private List<Float> densities;     //  浓度

    public OverViewBean() {
        conductivities = new ArrayList<>();
        densities = new ArrayList<>();
    }

    public int getTodayUploadCount() {
        return todayUploadCount;
    }

    public void setTodayUploadCount(int todayUploadCount) {
        this.todayUploadCount = todayUploadCount;
    }

    public int getTotalDataCount() {
        return totalDataCount;
    }

    public void setTotalDataCount(int totalDataCount) {
        this.totalDataCount = totalDataCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public void setConductivities(List<Float> conductivities) {
        this.conductivities = conductivities;
    }

    public List<Float> getConductivities() {
        return this.conductivities;
    }

    public void setDensities(List<Float> densities) {
        this.densities = densities;
    }

    public List<Float> getDensities() {
        return this.densities;
    }

}
