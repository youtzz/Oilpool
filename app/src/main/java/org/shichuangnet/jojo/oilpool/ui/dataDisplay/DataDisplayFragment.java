package org.shichuangnet.jojo.oilpool.ui.dataDisplay;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shichuangnet.jojo.oilpool.adapter.DataDisplayRvAdapter;
import org.shichuangnet.jojo.oilpool.api.OilPoolApi;
import org.shichuangnet.jojo.oilpool.R;
import org.shichuangnet.jojo.oilpool.beans.DataBean;
import org.shichuangnet.jojo.oilpool.callBack.DataDisplayDiffCallBack;
import org.shichuangnet.jojo.oilpool.dataHandler.DataHandler;
import org.shichuangnet.jojo.oilpool.ui.BaseActivity;
import org.shichuangnet.jojo.oilpool.utils.DateUtils;
import org.shichuangnet.jojo.oilpool.utils.ResponseUtils;
import org.shichuangnet.jojo.oilpool.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.*;

/**
 *
 * @author hiheero
 * 展示数据，主要以列表型式，支持权限用户修改数据信息
 * todo  scrollView 与 recyclerView 滑动事件有一些冲突，项目中使用的下拉刷新库会与之有些冲突
 *       应当修改一下下拉的距离，或者切换成另一种下拉方式
 * */
public class DataDisplayFragment extends Fragment {

    private Context mContext;

    private CardView mDataDisplayContainerCv;
    private CardView mEmptyViewContainerCv;
    private ImageButton mEmptyImageBtn;
    private ProgressBar mEmptyLoadingProgressBar;
    private TextView mEmptyLoadingStateTv;

    private CardView mTodayUploadCv;
    private CardView mAllDataCountsCv;

    private TextView mTodayUploadValueTv;
    private TextView mAllDataCountsValueTv;

    private SmartRefreshLayout mRefreshLayout;

    private RecyclerView mDataDisplayListRv;
    private DataDisplayRvAdapter mDataDisplayRvAdapter;
    private List<DataBean> mDataBeanList;  //  mDataDisplayRvAdapter 中的 list

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_data_display, container, false);
        return inflater.inflate(R.layout.fragment_data_display_test_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDataDisplayContainerCv = view.findViewById(R.id.cv_container_data_display_data_list);
        mRefreshLayout = view.findViewById(R.id.refresh_layout_data_display);
        mDataDisplayListRv = view.findViewById(R.id.rv_data_display);
        mEmptyViewContainerCv = view.findViewById(R.id.cv_container_data_display_empty_view);
        mEmptyImageBtn = view.findViewById(R.id.ib_data_display_empty);
        mEmptyLoadingProgressBar = view.findViewById(R.id.pb_data_display_empty);
        mEmptyLoadingStateTv = view.findViewById(R.id.tv_data_display_empty_refresh_state);

        mTodayUploadCv = view.findViewById(R.id.cv_tmp_value_container);
        mAllDataCountsCv = view.findViewById(R.id.cv_ph_value_container);

        mTodayUploadValueTv = view.findViewById(R.id.tv_tmp_value);
        mAllDataCountsValueTv = view.findViewById(R.id.tv_ph_value);


        //  数据列表相关
        mDataBeanList = new ArrayList<>();
        mDataDisplayListRv.setLayoutManager(new LinearLayoutManager(mContext));

        mDataDisplayRvAdapter = new DataDisplayRvAdapter(
                R.layout.item_recycler_view_data_display_layout, mDataBeanList);
        mDataDisplayRvAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);

        mDataDisplayListRv.setAdapter(mDataDisplayRvAdapter);
        //  recyclerView中的Item布局中的点击事件
        mDataDisplayListRv.addOnItemTouchListener(new OnItemChildClickListener() {
            @Override
            public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    //  删除当前的数据项
                    case R.id.btn_data_display_item_fold_delete:
                        ((BaseActivity) Objects.requireNonNull(getActivity())).showToast("暂未开放");
                    default:
                        break;
                }
            }
        });

        //  上拉刷新，加载当前列表最后一位数据时间到当前时间之间的数据，进行增量更新
        //  若列表数据为空，加载一页数据
        mRefreshLayout.setOnRefreshListener(refreshLayout -> {

            checkTodayUploadAndAllCounts();

            //  检查adapter中的list是否有数据
            if (mDataDisplayRvAdapter.getData().size() == 0) {
                mDataDisplayRvAdapter.setDataPage(0);  //  将当前页码设为 0
                OilPoolApi.getInstance(mContext).getPagingDate(this, 0, 10, new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        refreshLayout.finishRefresh(2000, false, false);  //  false参数为失败
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //  解析response的code码，不为200说明访问失败
                        if (!ResponseUtils.checkResponse(response)) {
                            refreshLayout.finishRefresh(2000, false, false);
                        } else {
                            List<DataBean> list = DataHandler.handleDataBeans(response, 0);
                            DataDisplayDiffCallBack callBack = new DataDisplayDiffCallBack(list);
                            refreshLayout.finishRefresh(2000, true, false);
                            mDataDisplayRvAdapter.setNewDiffData(callBack);
                        }
                    }
                });
            } else {  //  得到列表最后一项的时间戳到当前系统时间戳之间的数据
                long lastTime = mDataDisplayRvAdapter.getData().get(mDataDisplayRvAdapter.getData().size()-1).getCreateTime();
                long nowTime = DateUtils.getCurTimeLong();

                OilPoolApi.getInstance(mContext).getFilterDataByTime(this, lastTime, nowTime, new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        refreshLayout.finishRefresh(2000, false, false);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (!ResponseUtils.checkResponse(response)) {
                            refreshLayout.finishRefresh(2000, false, false);
                        } else {
                            List<DataBean> list = DataHandler.handleDataBeans(response, 0);
                            DataDisplayDiffCallBack callBack = new DataDisplayDiffCallBack(list);
                            refreshLayout.finishRefresh();
                            mDataDisplayRvAdapter.setNewDiffData(callBack);
                        }
                    }
                });
            }
        });

        //  下拉加载
        mRefreshLayout.setOnLoadMoreListener(refreshLayout -> {

            checkTodayUploadAndAllCounts();

            OilPoolApi.getInstance(mContext).getPagingDate(this, mDataDisplayRvAdapter.getDataPage() + 1, 10, new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    refreshLayout.finishLoadMore(false);
                }

                @Override
                public void onResponse(String response, int id) {
                    if (!ResponseUtils.checkResponse(response)) {
                        refreshLayout.finishLoadMore(2000, false, false);
                    } else {
                        List<DataBean> list = DataHandler.handleDataBeans(response, mDataDisplayRvAdapter.getData().size());
                        //  得到的 list为空时说明已经服务器已经没有更多的数据了
                        if (list.isEmpty()) {
                            refreshLayout.finishLoadMoreWithNoMoreData();
                        } else {
                            //  把原来的数据加上，不然原数据会被覆盖
                            list.addAll(0, mDataDisplayRvAdapter.getData());
                            DataDisplayDiffCallBack callBack = new DataDisplayDiffCallBack(list);
                            mDataDisplayRvAdapter.setNewDiffData(callBack);
                            //  数据加载是分页加载，只加载新数据，所以可以不采用diffUtil的方法
                            //  int position = mDataDisplayRvAdapter.getData().size();
                            //  mDataDisplayRvAdapter.loadMore(list);
                            //  mDataDisplayRvAdapter.notifyItemRangeInserted(position, list.size());

                            refreshLayout.finishLoadMore(2000, true, false);
                            //  将本地页数+1
                            mDataDisplayRvAdapter.setDataPage(mDataDisplayRvAdapter.getDataPage() + 1);
                        }
                    }
                }
            });
        });

        //  空白页自动刷新失败后，允许用户自己点击去刷新
        mEmptyImageBtn.setOnClickListener(v -> dataFresh());

        mTodayUploadCv.setOnClickListener(v -> {});
        mAllDataCountsCv.setOnClickListener(v -> {});

        //  增加recyclerView的性能
        mDataDisplayListRv.setHasFixedSize(true);
        mDataDisplayListRv.setItemViewCacheSize(20);
        mDataDisplayListRv.setDrawingCacheEnabled(true);
        mDataDisplayListRv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        //  resume 时自动刷新
        if (mDataDisplayContainerCv.getVisibility() == View.VISIBLE) {
            //  一直自动刷新有点消耗客户端流量，体验也不不好
            //  建议使用服务器推送来实现实时刷新
            //  mRefreshLayout.autoRefresh();
        } else {
            dataFresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }

    /**
     * 默认的从服务器加载数据的方法
     * */
    private void dataFresh() {
        mEmptyLoadingStateTv.setText(getResources().getText(R.string.data_is_loading));
        mEmptyLoadingProgressBar.setVisibility(View.VISIBLE);
        mEmptyImageBtn.setEnabled(false);

        checkTodayUploadAndAllCounts();

        OilPoolApi.getInstance(mContext).getPagingDate(this, 0, 10, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mEmptyImageBtn.setEnabled(true);
                mEmptyLoadingProgressBar.setVisibility(View.GONE);
                mEmptyLoadingStateTv.setText(getResources().getText(R.string.data_loaded_error));
                if (false) {
                    //  TODO 离线时用来做测试，正式版本需删除
                    String response = "{\"code\":200,\"msg\":\"请求成功\",\"data\":{\"content\":[{\"manualId\":\"0d548e6009234f4abdcb61cffec33c76\",\"density\":\"100\",\"conductivity\":\"100\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569324529904,\"updateTime\":1569324529904},{\"manualId\":\"4ca65989d23c45169378fd4885bfccc6\",\"density\":\"124\",\"conductivity\":\"24\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569224473838,\"updateTime\":1569224473838},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"469ba82c93974b1191c05a11e12184f6\",\"density\":\"27\",\"conductivity\":\"127\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143839628,\"updateTime\":1569143839628},{\"manualId\":\"71161edf39f7479aa10ebb00dc5269d9\",\"density\":\"26\",\"conductivity\":\"126\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143832542,\"updateTime\":1569143832542},{\"manualId\":\"a2e5ea1457284cd0ad5d09b8a34334d8\",\"density\":\"25\",\"conductivity\":\"125\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143826301,\"updateTime\":1569143826301},{\"manualId\":\"b94678eee945473780a7eec576df2501\",\"density\":\"24\",\"conductivity\":\"124\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143820710,\"updateTime\":1569143820710},{\"manualId\":\"73f74a504eb0498f9605813dd6559c6c\",\"density\":\"23\",\"conductivity\":\"123\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143812770,\"updateTime\":1569143812770},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819},{\"manualId\":\"a9a74681e551420bae7393a9dfe5a134\",\"density\":\"28\",\"conductivity\":\"128\",\"enterpriseId\":\"415b725e9a404d19a40540ed7c1932b3\",\"createTime\":1569143844819,\"updateTime\":1569143844819}],\"pageable\":{\"sort\":{\"sorted\":false,\"unsorted\":true,\"empty\":true},\"offset\":0,\"pageNumber\":0,\"pageSize\":20,\"unpaged\":false,\"paged\":true},\"totalPages\":1,\"totalElements\":8,\"last\":true,\"number\":0,\"size\":20,\"sort\":{\"sorted\":false,\"unsorted\":true,\"empty\":true},\"numberOfElements\":8,\"first\":true,\"empty\":false}}";
                    List<DataBean> list = DataHandler.handleDataBeans(response, 0);
                    DataDisplayDiffCallBack callBack = new DataDisplayDiffCallBack(list);
                    mDataDisplayRvAdapter.setNewDiffData(callBack);
                    mDataDisplayContainerCv.setVisibility(View.VISIBLE);
                    mEmptyViewContainerCv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onResponse(String response, int id) {
                //  解析response的code码，不为200说明访问失败
                if (!ResponseUtils.checkResponse(response)) {
                    mEmptyImageBtn.setEnabled(true);
                    mEmptyLoadingProgressBar.setVisibility(View.GONE);
                    mEmptyLoadingStateTv.setText(getResources().getText(R.string.data_loaded_error));
                } else {
                    List<DataBean> list = DataHandler.handleDataBeans(response, 0);
                    DataDisplayDiffCallBack callBack = new DataDisplayDiffCallBack(list);
                    mDataDisplayRvAdapter.setNewDiffData(callBack);
                    //  更新视图
                    mEmptyViewContainerCv.setVisibility(View.GONE);
                    mEmptyImageBtn.setEnabled(true);
                    mEmptyLoadingProgressBar.setVisibility(View.GONE);
                    mEmptyLoadingStateTv.setText(getResources().getText(R.string.data_loaded_success));
                    mDataDisplayContainerCv.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * 检查今日上传的数据量和总数据量
     * */
    private void checkTodayUploadAndAllCounts() {
        //  获得今日0点的时间戳，得到0点到当前时间点的数据
        long curTime = DateUtils.getCurTimeLong();
        long today = DateUtils.getDate2Date(curTime, "yyyy-MM-dd 00:00:00");
        OilPoolApi.getInstance(mContext).getFilterDataByTime(this, today, curTime, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONArray jArr = new JSONObject(response).optJSONArray("data");
                    if (jArr != null) {
                        mTodayUploadValueTv.setText(String.valueOf(jArr.length()));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        OilPoolApi.getInstance(mContext).getPagingDate(this, 0, 8, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) { }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response).optJSONObject("data");
                    if (jsonObject != null) {
                        if (jsonObject.has("totalElements")) {
                            mAllDataCountsValueTv.setText(jsonObject.getString("totalElements"));
                        }
                        if (jsonObject.has("content")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("content");
                            List<Float> cons = new ArrayList<>();
                            List<Float> dens = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject json = jsonArray.getJSONObject(i);
                                //  为了确保服务器的到值是正确的，采用自己的方法进行number format
                                float conductivity = StringUtils.getStr2Float(json.getString("conductivity"));
                                float density = StringUtils.getStr2Float(json.getString("density"));
                                cons.add(conductivity);
                                dens.add(density);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
