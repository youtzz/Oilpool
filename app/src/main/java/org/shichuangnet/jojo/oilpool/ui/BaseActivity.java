package org.shichuangnet.jojo.oilpool.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.shichuangnet.jojo.oilpool.R;
import org.shichuangnet.jojo.oilpool.utils.ActivityManger;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

/**
 * @author jojo
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  将activity都加入栈中管理
        ActivityManger.getActivityManager().pushActivity(this);
        init();
        //  设置虚拟按键的颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().setNavigationBarColor(getResources().getColor(R.color.navigation_bar_color_white, getTheme()));
            } else {
                getWindow().setNavigationBarColor(getResources().getColor(R.color.navigation_bar_color_white));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManger.getActivityManager().popActivity(this);
    }

    public void init() {
        setContentView();
        findViews();
        getData();
        showContent();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        //  添加自定义字体方式
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    /**
     * 设置layoutRes
     * */
    public abstract void setContentView();

    /**
     * 控件绑定ID
     * */
    public abstract void findViews();

    /**
     * data相关操作
     * */
    public abstract void getData();

    /**
     * 控件相关逻辑控制
     * */
    public abstract void showContent();

    public void showToast(String msg) {
        Toast toast = Toast.makeText(BaseActivity.this, null, Toast.LENGTH_SHORT);
        toast.setText(msg);
        toast.show();
    }

    public void simpleLog(String msg) {
        Log.d("simpleLog", "simpleLog: " + msg);
    }
}
