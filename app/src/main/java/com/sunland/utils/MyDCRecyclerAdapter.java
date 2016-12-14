package com.sunland.utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
public class MyDCRecyclerAdapter extends RecyclerView.Adapter<MyDCRecyclerAdapter.ViewHolder> {

    private List<String> mListDatas = null;
    private OnItemClickListener mListener;
    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }
    public MyDCRecyclerAdapter(List<String> list) {
        this.mListDatas = list;
    }
    @Override
    public MyDCRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dc_item, parent, false);
            return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyDCRecyclerAdapter.ViewHolder holder, int position) {
        String dc = mListDatas.get(position);
        holder.bindData(dc);
        holder.itemView.setTag(dc);
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

    public void addData(int position, String dc) {
        mListDatas.add(position, dc);
        notifyItemInserted(position);
    }

    public void removeData(int position) {
        mListDatas.remove(position);
        notifyItemRemoved(position);
    }
    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mDC;
        private Button btSetPosition;
        private Button btBackPosition;

        public ViewHolder(View itemView) {
            super(itemView);
            mDC = (TextView) itemView.findViewById(R.id.tv_dc_name);
            btSetPosition =(Button) itemView.findViewById(R.id.dc_set_position);
            btBackPosition = (Button) itemView.findViewById(R.id.dc_back_position);
        }
        public void bindData(String item) {
            if (item != null) {
                mDC.setText(item);
            }
        }
    }
}
