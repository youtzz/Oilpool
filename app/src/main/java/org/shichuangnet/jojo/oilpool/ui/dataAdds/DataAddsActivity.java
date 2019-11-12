package org.shichuangnet.jojo.oilpool.ui.dataAdds;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.cazaea.sweetalert.SweetAlertDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.shichuangnet.jojo.oilpool.R;
import org.shichuangnet.jojo.oilpool.adapter.DataAddsRvAdapter;
import org.shichuangnet.jojo.oilpool.api.OilPoolApi;
import org.shichuangnet.jojo.oilpool.beans.DataBean;
import org.shichuangnet.jojo.oilpool.ui.BaseActivity;
import org.shichuangnet.jojo.oilpool.utils.DateUtils;
import org.shichuangnet.jojo.oilpool.utils.ResponseUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;

/**
 * @author jojo
 */
public class DataAddsActivity extends BaseActivity {

    private QMUITopBar mTopBar;
    private RecyclerView mDataAddsRv;
    private DataAddsRvAdapter mDataAddsRvAdapter;
    private FloatingActionButton mFloatingActionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //  5.0 版本以上的activity切换过度动画
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            // 设置activity的窗口属性为contentFeature,即可使用切换动画
//            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//            Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fade);
//            // 此处获取了系统内置的explode动画效果设置给了activity的窗口对象
//            getWindow().setEnterTransition(transition);
//            getWindow().setExitTransition(transition);
//            getWindow().setReenterTransition(transition);
//        }
        super.onCreate(savedInstanceState);
    }

    //  todo finish这个页面之后进行一些判断，释放一些资源
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_data_adds);
    }

    @Override
    public void findViews() {
        mTopBar = findViewById(R.id.tb_data_adds);
        mDataAddsRv = findViewById(R.id.rv_data_adds_content);
        mFloatingActionBtn = findViewById(R.id.fab_data_adds);
    }

    @Override
    public void getData() {

    }

    @Override
    public void showContent() {

        mTopBar.setTitle(R.string.add);
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> {
            //  todo  使用back按钮返回上一个activity时没有触发过渡动画，原因暂不明
            DataAddsActivity.this.finish();
        });
        mTopBar.addRightTextButton("提交", R.id.tb_data_adds_right_publish_btn_img).setOnClickListener(v -> {
            if (mDataAddsRvAdapter.getData().size() == 0) {
                showNoDataDialog();
            } else {
               if (checkData()) {
                   computedData();
               }
            }
        });

        StaggeredGridLayoutManager mSgLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mDataAddsRv.setLayoutManager(mSgLayoutManager);

        //  设置 recyclerView 的 Adapter
        mDataAddsRvAdapter = new DataAddsRvAdapter(R.layout.item_recycler_view_data_adds_layout, new ArrayList<>());
        mDataAddsRvAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        //  当recyclerView中无数据时，展示空白页
        mDataAddsRvAdapter.setEmptyView(R.layout.empty_view_data_adds_list_layout, mDataAddsRv);
        mDataAddsRvAdapter.setOnItemClickListener((adapter, view, position) -> showDataAddsDialog((DataBean) adapter.getData().get(position), position));

        mDataAddsRv.setAdapter(mDataAddsRvAdapter);

        //  todo 给 recyclerView 中的 Item 添加滑动删除的效果 待完善
        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(mDataAddsRvAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(mDataAddsRv);
        mDataAddsRvAdapter.enableSwipeItem();
        mDataAddsRvAdapter.setOnItemSwipeListener(new OnItemSwipeListener() {
            @Override
            public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int i) {

            }

            @Override
            public void clearView(RecyclerView.ViewHolder viewHolder, int i) {

            }

            @Override
            public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int i) {

            }

            @Override
            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float v, float v1, boolean b) {

            }
        });

        //  按钮点击后添加一个列表项，并将列表移动至添加的位置
        mFloatingActionBtn.setOnClickListener(v -> showDataAddsDialog());

    }

    private void showNoDataDialog() {
        new SweetAlertDialog(DataAddsActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getResources().getString(R.string.warming))
                .setContentText(getResources().getString(R.string.you_do_not_add_data))
                .setConfirmText(getResources().getString(R.string.i_kow))
                .show();
    }

    /**
     * 点击添加按钮时的 dialog,使用了 QMUI 的自定义 layout dialog
     * */
    private void showDataAddsDialog() {
        final QMUIDialog.CustomDialogBuilder builder = new QMUIDialog.CustomDialogBuilder(DataAddsActivity.this);
        builder.setLayout(R.layout.dialog_enter_datas_layout)
                .setTitle("添加您的数据")
                .addAction("取消", (dialog, index) -> dialog.dismiss())
                .addAction("确定", (dialog, index) -> {
                    String conductivity = Objects.requireNonNull(((TextInputEditText) dialog.findViewById(R.id.et_conductivity)).getText()).toString();
                    String density = Objects.requireNonNull(((TextInputEditText) dialog.findViewById(R.id.et_density)).getText()).toString();
                    DataBean db = new DataBean();
                    if (conductivity.isEmpty()) conductivity = "?";
                    if (density.isEmpty()) density = "?";
                    db.setConductivity(conductivity);
                    db.setDensity(density);
                    mDataAddsRvAdapter.addData(db);
                    dialog.dismiss();
                    mDataAddsRv.smoothScrollToPosition(mDataAddsRvAdapter.getData().size()-1);
                })
                .show();
    }

    /**
     * 用户修改数据时弹出的 dialog
     * @param db 需要修改的item数据
     * @param position 需要修改的item项数
     * */
    private void showDataAddsDialog(final DataBean db, final int position) {
        final QMUIDialog.CustomDialogBuilder builder = new QMUIDialog.CustomDialogBuilder(DataAddsActivity.this);
        QMUIDialog dialog = builder.setLayout(R.layout.dialog_enter_datas_layout)
                .setTitle("修改您的数据")
                .addAction("取消", (dialog1, index) -> dialog1.dismiss())
                .addAction("确定", (dialog2, index) -> {
                    String conductivity = Objects.requireNonNull(((TextInputEditText) dialog2.findViewById(R.id.et_conductivity)).getText()).toString();
                    String density = Objects.requireNonNull(((TextInputEditText) dialog2.findViewById(R.id.et_density)).getText()).toString();
                    if (conductivity.isEmpty()) conductivity = "?";
                    if (density.isEmpty()) density = "?";
                    db.setConductivity(conductivity);
                    db.setDensity(density);
                    mDataAddsRvAdapter.notifyItemChanged(position);
                    dialog2.dismiss();
                }).create();
        //  将原有的数据添加到展示的dialog中，增强用户体验
        //  因为未输入的字符是？所以要手动把这个问号清除掉
        String con = db.getConductivity();
        String den = db.getDensity();
        if (con.equals("?")) con = "";
        if (den.equals("?")) den = "";
        ((TextInputEditText)dialog.findViewById(R.id.et_conductivity)).setText(con);
        ((TextInputEditText)dialog.findViewById(R.id.et_density)).setText(den);
        dialog.show();
    }

    /**
     *
     * 检测用户输入的数据格式是否正确
     * @return 有误返回false，无误返回true
     * */
    private boolean checkData() {
        List<DataBean> dataBeans = mDataAddsRvAdapter.getData();
        for (DataBean db : dataBeans) {
            String conductivity = db.getConductivity();
            String density = db.getDensity();
            if (conductivity.isEmpty() || density.isEmpty() || conductivity.equals("?") || density.equals("?")) {
                mDataAddsRv.smoothScrollToPosition(dataBeans.indexOf(db));
                showToast("数据不能为空！");
                return false;
            }
        }
        return true;
    }

    /**
     *
     * 数据上传前计算数据的平均值
     * */
    private void computedData() {
        double conductivity = 0;
        double density = 0;
        List<DataBean> dataBeans = mDataAddsRvAdapter.getData();
        for (DataBean db : dataBeans) {
            conductivity += Double.valueOf(db.getConductivity());
            density += Double.valueOf(db.getDensity());
        }
        //  平均值保留两位小数
        conductivity /= dataBeans.size();
        density /= dataBeans.size();
        conductivity = new BigDecimal(conductivity).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        density = new BigDecimal(density).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        StringBuilder sb = new StringBuilder();
        sb.append("平均导电率：").append(conductivity).append(" m/s ").append("\n").append("平均浓度：").append(density).append(" mol/L");
        final double finalConductivity = conductivity;
        final double finalDensity = density;

        final SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("是否上传数据？")
                .setContentText(sb.toString())
                .setConfirmText(getResources().getString(R.string.confirm))
                .setCancelText(getResources().getString(R.string.cancel))
                .setCancelClickListener(Dialog::dismiss)
                .setConfirmClickListener(sweetAlertDialog -> {
                    //  将dialog切换成progress的型态
                    sweetAlertDialog.showCancelButton(false);
                    sweetAlertDialog.setCancelable(false);
                    sweetAlertDialog.setCanceledOnTouchOutside(false);
                    sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                    sweetAlertDialog.setTitleText(getResources().getString(R.string.data_is_uploading))
                            .setContentText(getResources().getString(R.string.please_wait))
                            .setConfirmClickListener(Dialog::dismiss);
                    //  将数据上传至服务器
                    OilPoolApi.getInstance(this).putData(this, finalConductivity, finalDensity, new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            if (sweetAlertDialog.isShowing()) {
                                sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                sweetAlertDialog.setTitleText(getResources().getString(R.string.upload_error))
                                        .setContentText(getResources().getString(R.string.please_check_network_is_working))
                                        .setConfirmText(getResources().getString(R.string.confirm));
                            }
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            int resCode = ResponseUtils.getResponseCode(response);
                            Resources r = getResources();
                            switch (resCode) {
                                case ResponseUtils.REQUEST_SUCCESS:
                                    sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    sweetAlertDialog.setTitleText(r.getString(R.string.upload_success))
                                            //  显示上传时间
                                            .setContentText(DateUtils.getCurDate(DateUtils.PATTERN))
                                            .setConfirmText(r.getString(R.string.confirm));
                                    //  上传成功后清空原有数据列表
                                    int endPosition = mDataAddsRvAdapter.getData().size();
                                    mDataAddsRvAdapter.getData().clear();
                                    mDataAddsRvAdapter.notifyItemRangeRemoved(0, endPosition);
                                    break;
                                case ResponseUtils.REQUEST_FAILED:
                                    sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                    sweetAlertDialog.setTitleText(r.getString(R.string.upload_error))
                                            .setContentText(r.getString(R.string.system_error))
                                            .setConfirmText(r.getString(R.string.confirm));
                                    break;
                                case ResponseUtils.INSUFFICIENT_PERMISSION:
                                    sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                    sweetAlertDialog.setTitleText(r.getString(R.string.upload_error))
                                            .setContentText(r.getString(R.string.inadequate_user_rights))
                                            .setConfirmText(r.getString(R.string.confirm));
                                    break;
                                default:
                                    sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                    sweetAlertDialog.setTitleText(r.getString(R.string.upload_error))
                                            .setContentText(r.getString(R.string.unknown_error))
                                            .setConfirmText(r.getString(R.string.confirm));

                            }
                        }
                    });
                });

        dialog.show();
    }


}


