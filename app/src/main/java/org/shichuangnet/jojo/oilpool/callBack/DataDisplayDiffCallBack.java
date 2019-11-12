package org.shichuangnet.jojo.oilpool.callBack;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.diff.BaseQuickDiffCallback;

import org.shichuangnet.jojo.oilpool.beans.DataBean;

import java.util.List;

/**
 * @author jojo
 * DiffCallBack 用于对照datadiplayAdapter中数据更新时的差异
 * */
public class DataDisplayDiffCallBack extends BaseQuickDiffCallback<DataBean> {

    private List<DataBean> oldList;
    private List<DataBean> newList;

    public DataDisplayDiffCallBack(@Nullable List<DataBean> newList) {
        super(newList);
    }

    @Override
    protected boolean areItemsTheSame(@NonNull DataBean oldItem, @NonNull DataBean newItem) {
        return oldItem.getManualId().equals(newItem.getManualId());

    }

    @Override
    protected boolean areContentsTheSame(@NonNull DataBean oldItem, @NonNull DataBean newItem) {
        String oldDensity = oldItem.getDensity();
        String oldConductivity = oldItem.getConductivity();
        String oldEnterpriseId = oldItem.getEnterpriseId();
        long oldCreateTime = oldItem.getCreateTime();
        int oldDataNumber = oldItem.getDataNumber();

        String newDensity = newItem.getDensity();
        String newConductivity = newItem.getConductivity();
        String newEnterpriseId = newItem.getEnterpriseId();
        long newCreateTime = newItem.getCreateTime();
        int newDataNumber = newItem.getDataNumber();

        return oldDensity.equals(newDensity) && oldConductivity.equals(newConductivity)
                && oldEnterpriseId.equals(newEnterpriseId) && oldCreateTime == newCreateTime
                && oldDataNumber == newDataNumber;
    }

    @Nullable
    @Override
    protected Object getChangePayload(@NonNull DataBean oldItem, @NonNull DataBean newItem) {

        Bundle payloads = new Bundle();
        if (oldItem.getDataNumber() != newItem.getDataNumber()) {
            payloads.putInt("dataNumber", newItem.getDataNumber());
        }
        if (!oldItem.getDensity().equals(newItem.getDensity())) {
            payloads.putString("density", newItem.getDensity());
        }
        if (!oldItem.getConductivity().equals(newItem.getConductivity())) {
            payloads.putString("conductivity", newItem.getConductivity());
        }

        if (payloads.size() == 0) {
            return null;
        }
        return payloads;
    }
}
