package org.shichuangnet.jojo.oilpool;

import android.app.Application;

import androidx.core.view.LayoutInflaterCompat;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

/**
 * @author jojo
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //  使用自定义字体
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
    }
    

}
