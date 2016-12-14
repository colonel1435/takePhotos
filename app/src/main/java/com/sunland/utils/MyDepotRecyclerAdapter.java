package com.sunland.utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunland.signalinspect.DepotInfo;
import com.sunland.signalinspect.R;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

/**
 * Created by Zero on 2016/10/04.
 */
public class MyDepotRecyclerAdapter extends RecyclerView.Adapter<MyDepotRecyclerAdapter.ViewHolder> {

    private List<DepotInfo> mListDatas = null;
    private OnItemClickListener mListener;
    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }
    public MyDepotRecyclerAdapter(List<DepotInfo> list) {
        this.mListDatas = list;
    }
    @Override
    public MyDepotRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.depot_item, parent, false);
            return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyDepotRecyclerAdapter.ViewHolder holder, int position) {
        DepotInfo depot = mListDatas.get(position);
        holder.bindData(depot);
        holder.itemView.setTag(depot);
        if(mListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mListener.OnItemClick(holder.itemView, pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mListDatas  == null ? 0:mListDatas.size();
    }

    public void addData(int position, DepotInfo depot) {
        mListDatas.add(position, depot);
        notifyItemInserted(position);
    }

    public void removeData(int position) {
        mListDatas.remove(position);
        notifyItemRemoved(position);
    }
    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImg;
        private TextView mDepot;
        private TextView mDcNum;
        private TextView mDate;

        public ViewHolder(View itemView) {
            super(itemView);
            mImg = (ImageView) itemView.findViewById(R.id.iv_depot);
            mDepot = (TextView) itemView.findViewById(R.id.tv_depot);
            mDcNum = (TextView) itemView.findViewById(R.id.tv_dc);
            mDate = (TextView) itemView.findViewById(R.id.tv_time);
        }
        public void bindData(DepotInfo item) {
            if (item != null) {
                mImg.setImageResource(item.getImg());
                mDepot.setText(item.getDepot());
                mDcNum.setText(item.getDcNum());
                mDate.setText(item.getDate());
            }
        }
    }
}
