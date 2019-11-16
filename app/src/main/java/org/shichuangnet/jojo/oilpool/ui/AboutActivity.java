package org.shichuangnet.jojo.oilpool.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.qmuiteam.qmui.widget.QMUITopBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.shichuangnet.jojo.oilpool.R;
import org.shichuangnet.jojo.oilpool.api.OilPoolApi;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;
import okhttp3.Call;

public class AboutActivity extends BaseActivity {

    private QMUITopBar mTopBar;
    private LinearLayout mContainerLayout;
    private KonfettiView mKonfettiView;
    private int clickTimes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 设置activity的窗口属性为contentFeature,即可使用切换动画
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.explode);
            // 此处获取了系统内置的explode动画效果设置给了activity的窗口对象
            getWindow().setEnterTransition(transition);
            getWindow().setExitTransition(transition);
            getWindow().setReenterTransition(transition);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_about);
    }

    @Override
    public void findViews() {
        mTopBar = findViewById(R.id.tb_about);
        mContainerLayout = findViewById(R.id.about_layout);
        mKonfettiView = findViewById(R.id.viewKonfetti);
    }

    @Override
    public void getData() {

    }

    @Override
    public void showContent() {

        mTopBar.setTitle("关于");
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> finish());

        //  点击版本时查询当前版本是否是最新版
        Element versionElement = new Element().setTitle("Version 1.0").setOnClickListener(view -> {
            checkAppVersion();
        });

        //  彩蛋，点击三下触发
        Element authorElement = new Element().setTitle("Author JOJO").setOnClickListener(view -> {
            if (clickTimes < 2) {
                clickTimes++;
            } else {
                showToast("HI~你触发了彩蛋!");
                clickTimes = 0;
                mKonfettiView.build()
                        .addColors(getResources().getColor(R.color.lt_yellow),
                                getResources().getColor(R.color.lt_orange),
                                getResources().getColor(R.color.lt_purple),
                                getResources().getColor(R.color.lt_pink))
                        .setDirection(0.0, 359.0)
                        .setSpeed(4f, 7f)
                        .setFadeOutEnabled(true)
                        .setTimeToLive(3000L)
                        .addShapes(Shape.RECT, Shape.CIRCLE)
                        .addSizes(new Size(12, 5), new Size(16, 6f))
                        .setPosition(-50f, mKonfettiView.getWidth() + 50f, -50f, -50f)
                        .streamFor(300, 5000L);
            }
        });
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription("乳化液检测助手")
                .setImage(R.drawable.ic_launcher_she)
                .addItem(versionElement)
                .addItem(authorElement)
                .addGroup("Connect with us")
                .addEmail("heerocarftpace@163.com")
                .addWebsite("https://fir.im/oilpool/")
                .addItem(getQQElement())
                .addItem(getWechatElement())
                .addPlayStore("com.ideashower.readitlater.pro")
                .addGitHub("hiheero")
                .addItem(getCopyRightsElement())
                .create();
        mContainerLayout.addView(aboutPage);
        mKonfettiView.bringToFront();
    }

    Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(getString(R.string.copy_right), Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIconDrawable(R.drawable.about_icon_copy_right);
        copyRightsElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        copyRightsElement.setIconNightTint(android.R.color.white);
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(v -> showToast(copyrights));
        return copyRightsElement;
    }

    //  微信好像不能跳转
    Element getWechatElement() {
        Element e = new Element();
        e.setTitle("关注 微信");
        e.setIconDrawable(R.drawable.ic_wechat);
        e.setIconTint(R.color.overview_green);
        e.setGravity(Gravity.LEFT);
        e.setOnClickListener(view -> {
            Intent lan = getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(lan.getComponent());
            startActivity(intent);
        });
        return e;
    }

    Element getQQElement() {
        Element e = new Element();
        e.setTitle("关注 QQ");
        e.setIconDrawable(R.drawable.ic_qq);
        e.setIconTint(R.color.overview_blue);
        e.setGravity(Gravity.LEFT);
        e.setOnClickListener(view -> {
            String url = "http://wpa.qq.com/msgrd?v=3&uin=2812433253&site=qq&menu=yes";//uin是发送过去的qq号码
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

        });
        return e;
    }

    private void checkAppVersion() {
        OilPoolApi.getInstance(this).checkAppVersion(this, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.d("testeee", "onResponse: "+e);
            }

            @Override
            public void onResponse(String response, int id) {
                Log.d("testeee", "onResponse: "+response);
            }
        });
    }
}
