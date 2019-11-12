package org.shichuangnet.jojo.oilpool.ui.userLogin;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cazaea.sweetalert.SweetAlertDialog;
import com.jaeger.library.StatusBarUtil;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.shichuangnet.jojo.oilpool.api.OilPoolApi;
import org.shichuangnet.jojo.oilpool.beans.UserBean;
import org.shichuangnet.jojo.oilpool.dataHandler.DataHandler;
import org.shichuangnet.jojo.oilpool.ui.MainActivity;
import org.shichuangnet.jojo.oilpool.R;
import org.shichuangnet.jojo.oilpool.ui.BaseActivity;
import org.shichuangnet.jojo.oilpool.utils.DateUtils;
import org.shichuangnet.jojo.oilpool.utils.ResponseUtils;
import org.shichuangnet.jojo.oilpool.utils.SharedUtils;

import okhttp3.Call;

/**
 * @author jojo
 */
public class UserLoginActivity extends BaseActivity {

    private EditText mUserNameET;
    private EditText mUserPswEt;
    private Button mLoginBtn;
    private QMUITipDialog mLoadingTipDialog;
    private TextView mSingInTv;

    /** 用于记录用户连续按两下返回键之间的时间 */
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }

    @Override
    public void setContentView() {
//        setContentView(R.layout.activity_user_login_2);
        setContentView(R.layout.activity_user_login);
        StatusBarUtil.setTransparent(UserLoginActivity.this);
    }

    @Override
    public void findViews() {
        mUserNameET = findViewById(R.id.et_user_login_user_name);
        mUserPswEt = findViewById(R.id.et_user_login_user_psw);
        mLoginBtn = findViewById(R.id.btn_user_login_enter);
        mSingInTv = findViewById(R.id.tv_user_login_sign_in);
    }

    @Override
    public void getData() {

    }

    @Override
    public void showContent() {

        String userName = SharedUtils.getString(this, SharedUtils.USER_NAME, "");
        if (!userName.isEmpty()) {
            mUserNameET.setText(userName);
        }
        mUserNameET.setOnFocusChangeListener((v, hasFocus) -> {
            EditText _v = (EditText) v;
            if (hasFocus) {
                String hint = _v.getHint().toString();
                _v.setTag(hint);
                _v.setHint("");
            } else {
                (_v).setHint((_v).getTag().toString());
            }
        });
        mUserPswEt.setOnFocusChangeListener((v, hasFocus) -> {
            EditText _v = (EditText) v;
            if (hasFocus) {
                String hint = _v.getHint().toString();
                _v.setTag(hint);
                _v.setHint("");
            } else {
                (_v).setHint((_v).getTag().toString());
            }
        });

        //  todo 添加登录的条件判断
        mLoginBtn.setOnClickListener(view -> checkUserEnter());

        //  注册账号
        mSingInTv.setOnClickListener(v -> showToast("暂未开放"));
        //  增加渐变式的出场动画
        QMUIViewHelper.fadeIn(findViewById(R.id.qmuiLayout_user_login_container),
                2500,
                new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                }, true);
    }


    @Override
    public void onBackPressed() {

        if (mLoadingTipDialog != null && mLoadingTipDialog.isShowing()) {

            OkHttpUtils.getInstance().cancelTag(this);
        } else if ((System.currentTimeMillis() - exitTime) > 2000) {
            showToast("再按一次返回键退出");
            exitTime = System.currentTimeMillis();
        } else {
            //彻底关闭整个APP
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            System.exit(0);
        }
    }

    private void checkUserEnter() {
        final String userName = mUserNameET.getText().toString();
        final String userPsw = mUserPswEt.getText().toString();
        //  todo 后门，方便离线，正式发布应删除
        if (userName.equals("jojo") && userPsw.equals("1")) {
            toMainActivity();
        } else

        if (userName.isEmpty() || userPsw.isEmpty()) {
            // todo  输入框为空时不显示
            showLoginFailedDialog("用户名以及密码不能为空！");
        } else {
            showLoginProgressDialog();

            OilPoolApi.getInstance(this).userLogin(this, userName, userPsw, new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    //  因为网络问题造成的登录失败
                    //  todo  交互性不是特别好，还可以优化一下
                    mLoadingTipDialog.dismiss();
                    showLoginFailedDialog("请检查网络连接是否正确。");
                }

                @Override
                public void onResponse(String response, int id) {
                    //  倒计时两秒再计算结果，增长loading的时间，提升动画效果
                    new CountDownTimer(2000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) { }

                        @Override
                        public void onFinish() {
                            int responseCode = ResponseUtils.getResponseCode(response);
                            //  请求失败
                            if (responseCode == ResponseUtils.REQUEST_FAILED) {
                                mLoadingTipDialog.dismiss();
                                showLoginFailedDialog("账号或密码错误，请重新输入。");
                            } else if (responseCode == ResponseUtils.REQUEST_SUCCESS) {
                                mLoadingTipDialog.dismiss();
                                // 请求成功  todo  用户信息是通过shared存储在本地还是通过对象在activity中传递，需要研究
                                UserBean userBean = DataHandler.getUserBeans(response);
                                userBean.setUserName(userName);
                                userBean.setUserPsw(userPsw);

                                SharedUtils.putString(UserLoginActivity.this, SharedUtils.USER_NAME, userName);
                                SharedUtils.putString(UserLoginActivity.this, SharedUtils.USER_TOKEN, userBean.getUserToken());
                                SharedUtils.putString(UserLoginActivity.this, SharedUtils.USER_LOGIN_DATE, String.valueOf(DateUtils.getCurTimeLong()));
                                toMainActivity();
                            } else {
                                mLoadingTipDialog.dismiss();
                            }
                        }
                    }.start();
                }
            });
        }
    }

    /**
     * 展示loadingDialog
     * */
    private void showLoginProgressDialog() {
        if (mLoadingTipDialog == null) {
            mLoadingTipDialog = new QMUITipDialog.Builder(UserLoginActivity.this)
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                    .setTipWord("登陆中")
                    .create();
        }
        mLoadingTipDialog.show();
    }

    /**
     * 展示登录成功dialog
     * */
    private void showLoginSuccessDialog() {
        new  QMUITipDialog.Builder(UserLoginActivity.this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                .setTipWord("登录成功")
                .create()
                .show();
    }



    /**
     * @param errorMessage 描述失败的原因
     * */
    private void showLoginFailedDialog(String errorMessage) {
        new SweetAlertDialog(UserLoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(getResources().getString(R.string.login_failed))
                .setContentText(errorMessage)
                .setConfirmText(getResources().getString(R.string.confirm))
                .setConfirmClickListener(Dialog::dismiss).show();

    }

    /**
     * 跳转activity
     * */
    private void toMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
