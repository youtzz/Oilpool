package org.shichuangnet.jojo.oilpool.ui;

import android.content.Intent;
import android.os.CountDownTimer;

import com.jaeger.library.StatusBarUtil;

import org.shichuangnet.jojo.oilpool.R;
import org.shichuangnet.jojo.oilpool.ui.userLogin.UserLoginActivity;
import org.shichuangnet.jojo.oilpool.utils.DateUtils;
import org.shichuangnet.jojo.oilpool.utils.SharedUtils;

/**
 * @author jojo
 * splashActivity 拥有自动登录功能
 */
public class SplashActivity extends BaseActivity {

    private final static long ENABLED_LOGIN_DATE = 3;
    private final static long SPLASH_TIME = 3200;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_splash);
        StatusBarUtil.setTransparent(SplashActivity.this);
    }

    @Override
    public void findViews() {}

    @Override
    public void getData() {}

    @Override
    public void showContent() {
        new CountDownTimer(SPLASH_TIME, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                checkAppVersion();
                checkLoginDate();
            }
        }.start();
    }

    /**
     * todo  添加启动时检查app版本的功能
     */
    private void checkAppVersion() {}

    private void checkLoginDate() {
        String date = SharedUtils.getString(this, SharedUtils.USER_LOGIN_DATE, "");
        if (date.isEmpty()) {
            toLoginActivity();
        } else {
            //  判断登录时间，超过3天未登录需要重新登录
            //  todo unTest
            long loginDate = DateUtils.getString2Date(date, DateUtils.PATTERN);
            long nowDate = DateUtils.getCurTimeLong();
            long diffDate = DateUtils.getTimeDiffDays(loginDate, nowDate);
            if (diffDate > ENABLED_LOGIN_DATE) {
                SharedUtils.deleAll(this);
                toLoginActivity();
            } else {
                toMainActivity();
            }
        }
    }

    private void toLoginActivity() {
        Intent intent = new Intent(this, UserLoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void toMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
