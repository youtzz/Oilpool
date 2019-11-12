package org.shichuangnet.jojo.oilpool.ui.overview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shichuangnet.jojo.oilpool.R;
import org.shichuangnet.jojo.oilpool.api.OilPoolApi;
import org.shichuangnet.jojo.oilpool.beans.OverViewBean;
import org.shichuangnet.jojo.oilpool.utils.DateUtils;


import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class OverViewFragment extends Fragment {
    private OverViewBean overViewBean;

    private Context mContext;

    private CardView mTmpCv;
    private CardView mPhCv;
    private TextView mTmpTv;
    private TextView mPhTv;

    private LineChart tmpLineChart;
    private LineChart phLineChart;

    private BarChart tmpBarChart;
    private BarChart phBarChart;

    private List<String> title = new ArrayList<>();
    private List<Entry> tmpEntries = new ArrayList<>();
    private List<Entry> phEntries = new ArrayList<>();

    private List<BarEntry> tmpBarEntries = new ArrayList<>();
    private List<BarEntry> phBarEntries = new ArrayList<>();

    private static String TAG = "OverViewFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overViewBean = new OverViewBean();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_over_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTmpCv = view.findViewById(R.id.cv_tmp_value_container);
        mPhCv = view.findViewById(R.id.cv_ph_value_container);
        mTmpTv = view.findViewById(R.id.tv_tmp_value);
        mPhTv = view.findViewById(R.id.tv_ph_value);

        tmpLineChart = view.findViewById(R.id.chart_line_overview_tmp);
        phLineChart = view.findViewById(R.id.chart_overview_ph);

        tmpBarChart = view.findViewById(R.id.chart_bar_overview_tmp);
        phBarChart = view.findViewById(R.id.chart_bar_overview_ph);

        mTmpCv.setOnClickListener(v -> {

            //  切换成折线图或是柱状图
            if (tmpLineChart.getVisibility() == View.VISIBLE && phLineChart.getVisibility() == View.VISIBLE) {
                tmpLineChart.setVisibility(View.GONE);
                phLineChart.setVisibility(View.GONE);
                tmpBarChart.setVisibility(View.VISIBLE);
                phBarChart.setVisibility(View.VISIBLE);
                tmpBarChart.animateY(750);
                phBarChart.animateY(750);
            } else {
                tmpLineChart.setVisibility(View.VISIBLE);
                phLineChart.setVisibility(View.VISIBLE);
                tmpBarChart.setVisibility(View.GONE);
                phBarChart.setVisibility(View.GONE);
                tmpLineChart.animateX(750);
                phLineChart.animateX(750);
            }
        });
        mPhCv.setOnClickListener(v -> {
            mTmpCv.performClick();
        });


        tmpLineChart.getDescription().setEnabled(false);
        phLineChart.getDescription().setEnabled(false);
        tmpBarChart.getDescription().setEnabled(false);
        phBarChart.getDescription().setEnabled(false);

        Typeface mTf = Typeface.createFromAsset(mContext.getAssets(), "opensanslight.ttf");
        XAxis tmpLineXAxis = tmpLineChart.getXAxis();
        tmpLineXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        tmpLineXAxis.setTypeface(mTf);
        tmpLineXAxis.setDrawGridLines(false);
        tmpLineXAxis.setDrawAxisLine(true);

        YAxis tmpLineLeftAxis = tmpLineChart.getAxisLeft();
        tmpLineLeftAxis.setTypeface(mTf);
        tmpLineLeftAxis.setLabelCount(5, false);
//        tmpLineLeftAxis.setAxisMaximum(60f);
//        tmpLineLeftAxis.setAxisMinimum(0f);

        YAxis tmpLineRightAxis = tmpLineChart.getAxisRight();
        tmpLineRightAxis.setTypeface(mTf);
        tmpLineRightAxis.setLabelCount(5, false);
        tmpLineRightAxis.setDrawGridLines(false);
//        tmpLineRightAxis.setAxisMaximum(60f);
//        tmpLineRightAxis.setAxisMinimum(0f);

        XAxis phLineXAxis = phLineChart.getXAxis();
        phLineXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        phLineXAxis.setTypeface(mTf);
        phLineXAxis.setDrawGridLines(false);
        phLineXAxis.setDrawAxisLine(true);

        YAxis phLineLeftAxis = phLineChart.getAxisLeft();
        phLineLeftAxis.setTypeface(mTf);
        phLineLeftAxis.setLabelCount(5, false);
//        phLineLeftAxis.setAxisMaximum(14f);
//        phLineLeftAxis.setAxisMinimum(0f);

        YAxis phRightAxis = phLineChart.getAxisRight();
        phRightAxis.setTypeface(mTf);
        phRightAxis.setLabelCount(5, false);
        phRightAxis.setDrawGridLines(false);
//        phRightAxis.setAxisMaximum(14f);
//        phRightAxis.setAxisMinimum(0f);


        XAxis tmpBarXAxis = tmpBarChart.getXAxis();
        tmpBarXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        tmpBarXAxis.setTypeface(mTf);
        tmpBarXAxis.setDrawGridLines(false);

        YAxis tmpBarLeftAxis = tmpBarChart.getAxisLeft();
        tmpBarLeftAxis.setTypeface(mTf);
        tmpBarLeftAxis.setLabelCount(5, false);
//        tmpBarLeftAxis.setAxisMaximum(60f);
//        tmpBarLeftAxis.setAxisMinimum(0f);

        YAxis tmpBarRightAxis = tmpBarChart.getAxisRight();
        tmpBarRightAxis.setTypeface(mTf);
        tmpBarRightAxis.setLabelCount(5, false);
        tmpBarRightAxis.setDrawGridLines(false);
//        tmpBarRightAxis.setAxisMaximum(60f);
//        tmpBarRightAxis.setAxisMinimum(0f);

        XAxis phBarXAxis = phBarChart.getXAxis();
        phBarXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        phBarXAxis.setTypeface(mTf);
        phBarXAxis.setDrawGridLines(false);

        YAxis phBarLeftAxis = phBarChart.getAxisLeft();
        phBarLeftAxis.setTypeface(mTf);
//        phBarLeftAxis.setAxisMaximum(14f);
//        phBarLeftAxis.setAxisMinimum(0f);

        YAxis phBarRightAxis = phBarChart.getAxisRight();
        phBarRightAxis.setTypeface(mTf);
        phBarRightAxis.setDrawGridLines(false);
//        phBarRightAxis.setAxisMaximum(14f);
//        phBarRightAxis.setAxisMinimum(0f);

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        dataRefresh();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     *
     * */
    private void dataRefresh() {

        OilPoolApi.getInstance(mContext).getPagingAutoData(this, 0, 15, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                //  todo  to do something when get data error .....
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response).optJSONObject("data");
                    if (jsonObject != null) {
                        if (jsonObject.has("content")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("content");
                            JSONObject firstObj = jsonArray.getJSONObject(0);
                            mTmpTv.setText(firstObj.getString("temperature"));
                            mPhTv.setText(firstObj.getString("ph"));
                            //  清空图表数据列表
                            clearList();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                //  设置数据底部的标题
                                String date = DateUtils.getDate2String(obj.getLong("createTime"), "HH:mm");
                                title.add(date);
                                //  设置数据项
                                float tmp = Float.parseFloat(obj.getString("temperature"));
                                float ph = Float.parseFloat(obj.getString("ph"));
                                tmpEntries.add(new Entry(i, tmp));
                                phEntries.add(new Entry(i, ph));

                                tmpBarEntries.add(new BarEntry(i, tmp));
                                phBarEntries.add(new BarEntry(i, ph));
                            }

                            //  折线图
                            LineDataSet tmpLineDataSet = new LineDataSet(tmpEntries, "温度");
                            LineDataSet phLineDataSet = new LineDataSet(phEntries, "ph");

                            tmpLineDataSet.setLineWidth(2.5f);
                            tmpLineDataSet.setCircleRadius(4.5f);
                            tmpLineDataSet.setHighLightColor(Color.parseColor("#03a9f4"));

                            phLineDataSet.setLineWidth(2.5f);
                            phLineDataSet.setCircleRadius(4.5f);
                            phLineDataSet.setHighLightColor(Color.parseColor("#32CC7F"));
                            phLineDataSet.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
                            phLineDataSet.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);

                            LineData tmpLineData = new LineData(tmpLineDataSet);
                            LineData phLineData = new LineData(phLineDataSet);

                            //  柱状图
                            BarDataSet tmpBarDataSet = new BarDataSet(tmpBarEntries, "温度");
                            BarDataSet phBarDataSet = new BarDataSet(phBarEntries, "ph");

                            tmpBarDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
                            tmpBarDataSet.setHighLightAlpha(10);
                            phBarDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
                            phBarDataSet.setHighLightAlpha(10);

                            BarData tmpBarData = new BarData(tmpBarDataSet);
                            BarData phBarData = new BarData(phBarDataSet);

                            //  在formatter中将字符标题与图表x轴label进行对应
                            ValueFormatter formatter = new ValueFormatter() {
                                @Override
                                public String getAxisLabel(float value, AxisBase axis) {
                                    return title.get((int)value);
                                }
                            };

                            tmpLineChart.getXAxis().setValueFormatter(formatter);
                            phLineChart.getXAxis().setValueFormatter(formatter);
                            tmpBarChart.getXAxis().setValueFormatter(formatter);
                            phBarChart.getXAxis().setValueFormatter(formatter);

                            tmpLineChart.setData(tmpLineData);
                            phLineChart.setData(phLineData);
                            tmpBarChart.setData(tmpBarData);
                            phBarChart.setData(phBarData);

                            tmpLineChart.invalidate();
                            phLineChart.invalidate();
                            tmpBarChart.invalidate();
                            phBarChart.invalidate();

                            tmpLineChart.animateX(750);
                            phLineChart.animateX(750);
                            tmpBarChart.animateY(750);
                            phBarChart.animateY(750);
                        }

                    } else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void clearList() {
        if (title != null) title.clear();
        if (tmpEntries != null) tmpEntries.clear();
        if (tmpBarEntries != null) tmpBarEntries.clear();
        if (phEntries != null) phEntries.clear();
        if (phBarEntries != null) phBarEntries.clear();
    }
}
