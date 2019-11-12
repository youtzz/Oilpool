package org.shichuangnet.jojo.oilpool.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.shichuangnet.jojo.oilpool.R;
import org.shichuangnet.jojo.oilpool.beans.DataBean;

import java.util.List;

public class DataAddsRvAdapter extends BaseItemDraggableAdapter<DataBean, DataAddsRvAdapter.MyViewHolder> {

    private String redColor = "#e34551";
    private String blueColor = "#57c8f2";

    public DataAddsRvAdapter(int layoutResId, @Nullable List<DataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull MyViewHolder helper, DataBean item) {
        helper.conductivityTv.setText(item.getConductivity());
        helper.densityTv.setText(item.getDensity());
        if (item.getConductivity().equals("?") || item.getDensity().equals("?")) {
            helper.titleBgRv.setBackgroundColor(Color.parseColor(redColor));
            helper.editTitleTv.setText("未编辑");
        } else {
            helper.titleBgRv.setBackgroundColor(Color.parseColor(blueColor));
            helper.editTitleTv.setText("已编辑");
        }
    }

    public class MyViewHolder extends BaseViewHolder {

        private TextView editTitleTv;
        private RelativeLayout titleBgRv;
        private TextView conductivityTv;
        private TextView densityTv;
        public MyViewHolder(View view) {
            super(view);
            titleBgRv = view.findViewById(R.id.rl_data_adds_item_header);
            editTitleTv = view.findViewById(R.id.tv_data_adds_edit_title);
            conductivityTv = view.findViewById(R.id.tv_data_adds_conductivity_value);
            densityTv = view.findViewById(R.id.tv_data_adds_density_value);
        }
    }

}
