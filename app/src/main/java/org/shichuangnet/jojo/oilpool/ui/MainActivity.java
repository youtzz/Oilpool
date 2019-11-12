package org.shichuangnet.jojo.oilpool.ui;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.view.LayoutInflaterCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.cazaea.sweetalert.SweetAlertDialog;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.qmuiteam.qmui.widget.QMUITopBar;

import org.shichuangnet.jojo.oilpool.R;
import org.shichuangnet.jojo.oilpool.ui.dataAdds.DataAddsActivity;
import org.shichuangnet.jojo.oilpool.ui.dataDisplay.DataDisplayFragment;
import org.shichuangnet.jojo.oilpool.ui.overview.OverViewFragment;
import org.shichuangnet.jojo.oilpool.ui.searchData.SearchDataActivity;
import org.shichuangnet.jojo.oilpool.ui.userLogin.UserLoginActivity;
import org.shichuangnet.jojo.oilpool.utils.ActivityManger;
import org.shichuangnet.jojo.oilpool.utils.SharedUtils;

import java.util.ArrayList;

/**
 * @author jojo
 */
public class MainActivity extends BaseActivity {

    private Drawer mSlideDrawer;
    /** 顶部标题栏  */
    private QMUITopBar mQmuiTopBar;
    /** 分页展示的fragment */
    private Fragment fragment1, fragment2;
    private ArrayList<Fragment> fragmentList;

    private ViewPager mViewPager;
    /** 底部导航栏 */
    private com.flyco.tablayout.CommonTabLayout mCommonTabLayout;
    /** 底部导航栏中间突起的按钮 */
    private ImageButton mAddDateIb;
    /** 第二个文字区域被凸起按钮覆盖 */
    private String[] mTabTitles = {"自动数据", "", "人工数据"};
    /** 顶部标题栏的文字 */
    private String[] mTopBarTitles = {"自动数据", "人工数据", "人工数据"};

    private int[] mIconUnselectedIds = {
            R.mipmap.tab_home_unselect, R.mipmap.tab_empty,
            R.mipmap.tab_speech_unselect};
    private int[] mIconSelectIds = {
            R.mipmap.tab_home_select, R.mipmap.tab_empty,
            R.mipmap.tab_speech_select,};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //  使用icon库
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void findViews() {
        mCommonTabLayout = findViewById(R.id.ctl_main);
        mQmuiTopBar = findViewById(R.id.tb_main);
        mViewPager = findViewById(R.id.vp_main);
        mAddDateIb = findViewById(R.id.tab_main_center);
    }

    @Override
    public void getData() { }

    @Override
    public void showContent() {

        //  初始化左滑菜单栏
        initSlideDrawer();
        //  设置标题栏
        mQmuiTopBar.setTitle(R.string.title_first_name);
        //  标题栏左边的图片按钮将会触发侧滑菜单栏，用于显示用户信息
        //  todo 制作侧滑用户信息栏
        mQmuiTopBar.addLeftImageButton(R.mipmap.ic_left_menu, R.id.tb_main_left_menu_btn_img).setOnClickListener(v -> mSlideDrawer.openDrawer());
        mQmuiTopBar.addRightImageButton(R.mipmap.ic_notice, R.id.tb_main_right_notify_btn_img).setOnClickListener(v -> {
            showToast("暂无通知");
        });
        mQmuiTopBar.addRightImageButton(R.mipmap.ic_search, R.id.tb_data_adds_right_publish_btn_img).setOnClickListener(v -> {
            //  showToast("暂未开放");
            Intent intent = new Intent(this, SearchDataActivity.class);
            //  5.0以上的切换activity过度动画
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Pair<View, String> pair1 = Pair.create(v, "searchBarTransition");
                startActivity(intent,
                        ActivityOptions.makeSceneTransitionAnimation(this, pair1).toBundle());
            } else {
                startActivity(intent);
            }
        });

        //  设置底部菜单栏
        for (int i = 0; i < mTabTitles.length; i++) {
            final int finali = i;
            mTabEntities.add(new CustomTabEntity() {
                @Override
                public String getTabTitle() {
                    return mTabTitles[finali];
                }

                @Override
                public int getTabSelectedIcon() {
                    return mIconSelectIds[finali];
                }

                @Override
                public int getTabUnselectedIcon() {
                    return mIconUnselectedIds[finali];
                }
            });
        }

//        fragment1 = new DataDisplayFragment();
//        fragment2 = new OverViewFragment();
        /*
        * @date 10.31
        * @author jojo
        * 按照最新的需求，总览面板显示在前面，人工数据面板显示在后面
        * */
        fragment1 = new OverViewFragment();
        fragment2 = new DataDisplayFragment();

        fragmentList = new ArrayList<>();
        fragmentList.add(fragment1);
        fragmentList.add(fragment2);

        //  缓存的页面数量
        mViewPager.setOffscreenPageLimit(2);
        //  viewPager切换时更改所管理的 fragment
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        //  底部导航栏点击事件
        mCommonTabLayout.setTabData(mTabEntities);
        mCommonTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mViewPager.setCurrentItem(position, true);
                mQmuiTopBar.setTitle(mTopBarTitles[position]);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });

        //  底部导航栏中心的 “+” 按钮点击事件
        mAddDateIb.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DataAddsActivity.class);
            //  5.0以上的切换activity过度动画
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Pair<View, String> pair1 = Pair.create(v, "tabMainCenterTransition");
                MainActivity.this.startActivity(intent,
                        ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pair1).toBundle());
            } else {
                MainActivity.this.startActivity(intent);
            }

        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                //  tabLayout有三项，viewPager只有两项，所以翻滚到第二页的时候tabLayout要+1才能正确显示
                mCommonTabLayout.setCurrentTab(i==1? i+1 : i);
                mQmuiTopBar.setTitle(mTopBarTitles[i]);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        //  指定 viewPager 开始的界面
        mViewPager.setCurrentItem(0);

    }

    @Override
    public void onBackPressed() {
        //  点击返回时回到用户手机首页
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    /**
     * 初始化左滑抽屉菜单
     * */
    private void initSlideDrawer() {
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(MainActivity.this)
                .withHeaderBackground(R.mipmap.bg_user_login)
                .addProfiles(
                        new ProfileDrawerItem().withName(SharedUtils.getString(MainActivity.this, SharedUtils.USER_NAME, "")).withIcon(FontAwesome.Icon.faw_react).withSelectedColorRes(R.color.app_color_blue).withTextColorRes(R.color.app_color_blue)
                )
                .withOnAccountHeaderSelectionViewClickListener((view, profile) -> true)
                .withSelectionListEnabled(false)
                .build();


        PrimaryDrawerItem itemHome = new PrimaryDrawerItem().withIdentifier(0).withName("主页").withIcon(FontAwesome.Icon.faw_home);
        PrimaryDrawerItem itemSetting = new PrimaryDrawerItem().withIdentifier(1).withName("设置").withIcon(FontAwesome.Icon.faw_cog).withSelectable(false);
        PrimaryDrawerItem itemHelp = new PrimaryDrawerItem().withIdentifier(2).withName("帮助").withIcon(FontAwesome.Icon.faw_question).withSelectable(false);
        PrimaryDrawerItem itemAbout = new PrimaryDrawerItem().withIdentifier(3).withName("关于").withIcon(FontAwesome.Icon.faw_info_circle).withSelectable(false);
        PrimaryDrawerItem itemExit = new PrimaryDrawerItem().withIdentifier(4).withName("退出").withIcon(FontAwesome.Icon.faw_backspace).withSelectable(false);

        mSlideDrawer = new DrawerBuilder()
                .withActivity(MainActivity.this)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new SectionDrawerItem().withName("menu").withDivider(false),
                        itemHome,
                        itemSetting,
                        itemHelp,
                        itemAbout
                        //  ,new DividerDrawerItem()
                )
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    Resources r = getResources();
                    switch ((int) drawerItem.getIdentifier()) {
                        case 0:
                            break;
                        case 1:
                            break;
                        case 2:
                            break;
                        case 3:
                            toAboutActivity();
                            break;
                        case 4:
                            //  todo  添加退出账号时的数据清空逻辑判断
                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText(r.getString(R.string.exit))
                                    .setContentText(r.getString(R.string.if_sure_to_exit_login))
                                    .setCancelText(r.getString(R.string.cancel))
                                    .setConfirmText(r.getString(R.string.confirm))
                                    .setCancelClickListener((Dialog::dismiss))
                                    .setConfirmClickListener(sweetAlertDialog -> {
                                        //  这里不清除用户名，用户回到登录界面后原本的账号名依旧存在，增加交互性
                                        SharedUtils.deleShare(MainActivity.this, SharedUtils.USER_TOKEN);
                                        SharedUtils.deleShare(MainActivity.this, SharedUtils.USER_LOGIN_DATE);
                                        Intent intent = new Intent(MainActivity.this, UserLoginActivity.class);
                                        sweetAlertDialog.dismiss();
                                        startActivity(intent);
                                        ActivityManger.getActivityManager().popActivity(MainActivity.this);
                                    }).show();
                        default:
                            break;
                    }
                    return false;
                })
                .addStickyDrawerItems(
                        itemExit
                )
                .withStickyFooterDivider(true)
                .withStickyFooterShadow(false)
                .build();
        mSlideDrawer.setSelection(itemHome);
    }

    public void toAboutActivity() {
        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
        } else {
            startActivity(intent);
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles[position];
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }
    }

}
