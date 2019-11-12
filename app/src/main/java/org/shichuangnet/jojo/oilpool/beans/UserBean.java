package org.shichuangnet.jojo.oilpool.beans;

/**
 * @author jojo
 */
public class UserBean {

    private String userToken;
    private String userId;
    private String userName;
    private String userPsw;
    private String userCreateTime;
    private String userUpdateTime;

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPsw() {
        return userPsw;
    }

    public void setUserPsw(String userPsw) {
        this.userPsw = userPsw;
    }

    public String getUserCreateTime() {
        return userCreateTime;
    }

    public void setUserCreateTime(String userCreateTime) {
        this.userCreateTime = userCreateTime;
    }

    public String getUserUpdateTime() {
        return userUpdateTime;
    }

    public void setUserUpdateTime(String userUpdateTime) {
        this.userUpdateTime = userUpdateTime;
    }
}
