package org.shichuangnet.jojo.oilpool.adapter;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.material.button.MaterialButton;
import com.ramotion.foldingcell.FoldingCell;

import org.shichuangnet.jojo.dashboardview.DashboardView;
import org.shichuangnet.jojo.oilpool.R;
import org.shichuangnet.jojo.oilpool.beans.DataBean;
import org.shichuangnet.jojo.oilpool.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DataDisplayRvAdapter extends BaseQuickAdapter<DataBean, DataDisplayRvAdapter.MyViewHolder> {

    private String [] colorValSet = {"#EF5362", "#FFCF47", "#9FD661",
            "#3FD0AD", "#2BBDF3", "#5A9AEF",
            "#AC8FEF", "#EE85C1"};


    private int dataPage = 0;

    /**
     *
     * 适用于列表更新的情形
     * @param list 更新的列表
     * */
    public void upDate(List<DataBean> list) {
        mData.clear();
        mData.addAll(list);
//        mData = list;
    }

    /**
     *
     * 适用于列表加载更多数据的情形
     * @param list 需要添加的数据列表
     * */
    public void loadMore(List<DataBean> list) {
        mData.addAll(list);
    }

    public int getDataPage() {
        return dataPage;
    }

    public void setDataPage(int page) {
        this.dataPage = page;
    }

    public DataDisplayRvAdapter(int layoutResId, @Nullable List<DataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull MyViewHolder holder, DataBean item) {
        //  为每一项设置左边区域的颜色
        holder.leftDecorationView.setBackgroundColor(
                Color.parseColor(colorValSet[holder.getLayoutPosition()%colorValSet.length])
        );
        holder.dataNumberTv.setText(String.format(Locale.CHINESE, "NO.%d", item.getDataNumber()));

        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date itemDate = new Date(item.getCreateTime());
        Date curDate = new Date();

        String itemDateStr = sd.format(itemDate);
        String curDateStr = sd.format(curDate);

        holder.uploadDateFoldTv.setText(itemDateStr);

        //  如果数据是同一年的话，仅显示月份，否则展示年份
        if (itemDateStr.substring(0, 4).equals(curDateStr.substring(0, 4))) {
            itemDateStr = itemDateStr.substring(5);
            holder.dataUploadDateTv.setText(itemDateStr);
        } else {
            holder.dataUploadDateTv.setText(itemDateStr);
        }
        //  判断数据日期是否是近期
        long timeDiff = DateUtils.getTimeDiffDays(itemDate.getTime(), curDate.getTime());
        String day;
        sd.applyPattern("E");
        //  如果日期天数差小于1，显示是星期几
        if (timeDiff < 7) {
            String itemWeek = sd.format(itemDate);
            String curWeek = sd.format(curDate);
            if (itemWeek.equals(curWeek) ) {
                day = "今天";
            } else {
                day = sd.format(itemDate);
            }
        } else {
            day = itemDateStr;
        }
        holder.dataUploadDateTv.setText(day);

        sd.applyPattern("h:mm a");
        holder.dataUploadTimeTv.setText(sd.format(itemDate));
        holder.uploadTimeFoldTv.setText(sd.format(itemDate));

        holder.conductivityTv.setText(item.getConductivity());
        holder.densityTv.setText(item.getDensity());
        holder.foldingCell.setOnClickListener(v -> holder.foldingCell.toggle(false));

        holder.dataFoldTitle.setBackgroundColor(
                Color.parseColor(colorValSet[holder.getLayoutPosition()%colorValSet.length])
        );
        //  不知道数据id是什么型式记录的，太长了，算了，随便截取一点吧
//        holder.dataFoldIdTitleTv.setText(String.format("#%s-%s", item.getManualId().substring(0, 4), item.getManualId().substring(4, 8)));
        holder.dataFoldNumberTv.setText(String.format(Locale.getDefault(), "NO.%d", item.getDataNumber()));

        holder.addOnClickListener(R.id.btn_data_display_item_fold_delete);

        //  确保服务器传来的字符串是浮点型
        String con = item.getConductivity();
        String den = item.getDensity();
        if ( !con.contains(".") ) {
            con = con.concat(".0");
        } else if (con.indexOf(".")+1 == con.length()) {
            con = con.concat("0");
        }
        if ( !den.contains(".")) {
            den = den.concat(".0");
        } else if ( den.indexOf(".")+1 == den.length()) {
            den = den.concat("0");
        }
        holder.conductivityFoldDashboard.setRealTimeValue(Double.valueOf(con));
        holder.densityFoldDashboard.setRealTimeValue(Double.valueOf(den));
    }

    @Override
    protected void convertPayloads(@NonNull MyViewHolder holder, DataBean item, @NonNull List<Object> payloads) {
        //  当diffUtil发现数据有变化时，在此处更新视图
        if (payloads.isEmpty()) {
            convert(holder, item);
        } else {
            Bundle bundle = (Bundle) payloads.get(0);
            for (String key : bundle.keySet()) {
                switch (key) {
                    case "dataNumber":
                        holder.leftDecorationView.setBackgroundColor(
                                Color.parseColor(colorValSet[holder.getLayoutPosition()%colorValSet.length])
                        );
                        holder.dataNumberTv.setText(String.format(Locale.getDefault(), "NO.%d", item.getDataNumber()));
                        holder.dataFoldNumberTv.setText(String.format(Locale.getDefault(), "NO.%d", item.getDataNumber()));
                        break;
                    case "density":
                        holder.densityTv.setText(item.getDensity());
                        break;
                    case "conductivity":
                        holder.conductivityTv.setText(item.getConductivity());
                        break;
                    default:
                        break;
                }
            }
        }
    }

    class MyViewHolder extends BaseViewHolder {

        View leftDecorationView;
        TextView dataNumberTv;
        TextView dataUploadDateTv;
        TextView dataUploadTimeTv;

        TextView conductivityTv;
        TextView densityTv;
        FoldingCell foldingCell;

        TextView dataFoldIdTitleTv;
        TextView dataFoldNumberTv;
        RelativeLayout dataFoldTitle;

        MaterialButton dataFoldDeleteBtn;
        DashboardView conductivityFoldDashboard;
        DashboardView densityFoldDashboard;
        TextView uploadDateFoldTv;
        TextView uploadTimeFoldTv;

        public MyViewHolder(View view) {
            super(view);
            leftDecorationView = view.findViewById(R.id.view_left_vertical_decoration_bar);
            dataNumberTv = view.findViewById(R.id.tv_data_display_item_data_unfold_number);
            dataUploadDateTv = view.findViewById(R.id.tv_data_display_item_unfold_date);
            dataUploadTimeTv = view.findViewById(R.id.tv_data_display_item_unfold_time);

            conductivityTv = view.findViewById(R.id.tv_data_display_conductivity_value);
            densityTv = view.findViewById(R.id.tv_data_display_density_value);
            foldingCell = view.findViewById(R.id.folding_cell);

            dataFoldTitle = view.findViewById(R.id.rl_data_display_item_fold_title_bg);
//            dataFoldIdTitleTv = view.findViewById(R.id.tv_data_display_item_fold_data_id);
            dataFoldNumberTv = view.findViewById(R.id.tv_data_display_item_fold_data_number);

            dataFoldDeleteBtn = view.findViewById(R.id.btn_data_display_item_fold_delete);
            conductivityFoldDashboard = view.findViewById(R.id.dashboard_data_display_item_fold_conductivity);
            densityFoldDashboard = view.findViewById(R.id.dashboard_data_display_item_fold_density);
            uploadDateFoldTv = view.findViewById(R.id.tv_data_display_item_fold_upload_date);
            uploadTimeFoldTv = view.findViewById(R.id.tv_data_display_item_fold_upload_time);

        }
    }
}
