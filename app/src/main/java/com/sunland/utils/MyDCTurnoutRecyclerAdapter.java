package com.sunland.utils;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunland.signalinspect.DCInfo;
import com.sunland.signalinspect.R;
import java.util.List;

/**
 * Created by Zero on 2016/10/04.
 */
public class MyDCTurnoutRecyclerAdapter extends RecyclerView.Adapter<MyDCTurnoutRecyclerAdapter.ViewHolder> {

    private static final String TAG = "wumin";
    private List<DCInfo> mListDatas = null;
    private OnItemClickListener mListener;
    private Context mContext;

    public static final int VIEW_POSITION_KEY = 0;
    public static final int VIEW_CONTENT_KEY = 1;

    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }
    public MyDCTurnoutRecyclerAdapter(Context context, List<DCInfo> list) {
        this.mContext = context;
        this.mListDatas = list;
    }
    @Override
    public MyDCTurnoutRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dc_item, parent, false);
            return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyDCTurnoutRecyclerAdapter.ViewHolder holder, int position) {

        String dc = mListDatas.get(position).getDC();
        String dcItem = mListDatas.get(position).getItem();
        String dcSetThumb = mListDatas.get(position).getSetThumb();
        String dcBackThumb = mListDatas.get(position).getBackThumb();
        Log.i(TAG, "Position -> " + position + " DC -> " + dc + " dcItem -> " + dcItem + " setThumb -> " + dcSetThumb + " backThumb -> " + dcBackThumb);
        holder.tvDcItem.setText(dcItem);

        holder.ibSetPosition.setTag(VIEW_POSITION_KEY, position);
        holder.ibSetPosition.setTag(VIEW_CONTENT_KEY, dc+dcItem);
        holder.ibBackPosition.setTag(VIEW_POSITION_KEY, position);
        holder.ibBackPosition.setTag(VIEW_CONTENT_KEY, dc+dcItem);
        holder.ivSetThumb.setTag(VIEW_POSITION_KEY, position);
        holder.ivSetThumb.setTag(VIEW_CONTENT_KEY, dcSetThumb);
        holder.ivBackThumb.setTag(VIEW_POSITION_KEY, position);
        holder.ivBackThumb.setTag(VIEW_CONTENT_KEY, dcBackThumb);

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

    public void addData(int position, DCInfo dc) {
        mListDatas.add(position, dc);
        notifyItemInserted(position);
    }

    public void removeData(int position) {
        mListDatas.remove(position);
        notifyItemRemoved(position);
    }
    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDcItem;
        private ImageButton ibSetPosition;
        private ImageButton ibBackPosition;
        private ImageView ivSetThumb;
        private ImageView ivBackThumb;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDcItem = (TextView) itemView.findViewById(R.id.tv_dc_turnout_name);
            ibSetPosition = (ImageButton) itemView.findViewById(R.id.dc_set_position);
            ibBackPosition = (ImageButton) itemView.findViewById(R.id.dc_back_position);
            ivSetThumb = (ImageView) itemView.findViewById(R.id.iv_set_position_thumb);
            ivBackThumb = (ImageView) itemView.findViewById(R.id.iv_back_position_thumb);
        }
        public void bindData(String item) {
            if (item != null) {
                tvDcItem.setText(item);
//                ibSetPosition.setTag(item);
//                ibBackPosition.setTag(item);
//                ivSetThumb.setTag(item);
//                ivBackThumb.setTag(item);
            }
        }
    }
}
