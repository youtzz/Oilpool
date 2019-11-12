package org.shichuangnet.jojo.oilpool.ui.searchData;

import android.os.Bundle;

import com.qmuiteam.qmui.widget.QMUITopBar;

import org.shichuangnet.jojo.oilpool.R;
import org.shichuangnet.jojo.oilpool.ui.BaseActivity;

public class SearchDataActivity extends BaseActivity {

    private QMUITopBar mTopBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //  5.0 版本以上的activity切换过度动画
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            // 设置activity的窗口属性为contentFeature,即可使用切换动画
//            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//            Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.slide);
//            // 此处获取了系统内置的explode动画效果设置给了activity的窗口对象
//            getWindow().setEnterTransition(transition);
//            getWindow().setExitTransition(transition);
//            getWindow().setReenterTransition(transition);
//        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_search_datas);
    }

    @Override
    public void findViews() {
        mTopBar = findViewById(R.id.tb_search_data);
    }

    @Override
    public void getData() {

    }

    @Override
    public void showContent() {

        mTopBar.setTitle(R.string.search);
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> finish());

    }
}
