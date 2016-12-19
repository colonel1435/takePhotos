package com.sunland.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.io.LineNumberInputStream;
import java.util.List;

/**
 * Created by Zero on 2016/12/17.
 */

public abstract class RecyclerViewCommonAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> {
    private Context mContext;
    private int mLayoutId;
    private List<T> mDatas;
    private LayoutInflater mInflater;

    public RecyclerViewCommonAdapter(Context context, int layoutId, List<T> list) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mLayoutId = layoutId;
        mDatas = list;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewHolder viewHolder = RecyclerViewHolder.getViewHolder(mContext, parent, mLayoutId);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position, List<Object> payloads) {
        convert(holder, mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public abstract void convert(RecyclerViewHolder holder, T t);
}
