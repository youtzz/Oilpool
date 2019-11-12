package org.shichuangnet.jojo.oilpool.beans;

/**
 * @author heero
 * 用来保存数据的 bean
 * */
public class DataBean {

    private String manualId;      //  数据ID
    private String density;        //  浓度
    private String conductivity;   //  导电率
    private String enterpriseId;  //  公司ID
    private long createTime;    //  数据创建时间戳
    private long updateTime;    //  数据更新时间戳
    private int dataNumber;  //  当前数据在数据集中的序号

    public DataBean() {}

    public DataBean(String manualId, String density, String conductivity, String enterpriseId, long createTime, long updateTime, int dataNumber) {
        this.manualId = manualId;
        this.density = density;
        this.conductivity = conductivity;
        this.enterpriseId = enterpriseId;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.dataNumber = dataNumber;
    }

    public String getManualId() {
        return manualId;
    }

    public void setManualId(String manualId) {
        this.manualId = manualId;
    }

    public String getDensity() {
        return density;
    }

    public void setDensity(String density) {
        this.density = density;
    }

    public String getConductivity() {
        return conductivity;
    }

    public void setConductivity(String conductivity) {
        this.conductivity = conductivity;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public void setDataNumber(int dataNumber) {
        this.dataNumber = dataNumber;
    }

    public void addDataNumber(int addNumber) {
        this.dataNumber += addNumber;
    }

    public int getDataNumber() {
        return this.dataNumber;
    }
}

