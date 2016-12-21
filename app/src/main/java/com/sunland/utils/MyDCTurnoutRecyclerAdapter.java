package com.sunland.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
    private View.OnLongClickListener mLongListener;
    private Context mContext;
    private String mDepot;

    public static final int VIEW_POSITION_KEY = 0;
    public static final int VIEW_CONTENT_KEY = 1;

    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
        void OnItemLongClick(View view, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }
    public void setOnItemLongCliclListener(View.OnLongClickListener listener) {
        this.mLongListener = listener;
    }

    public MyDCTurnoutRecyclerAdapter(Context context, List<DCInfo> list, String depot) {
        this.mContext = context;
        this.mListDatas = list;
        this.mDepot = depot;
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
        SharedPreferences sp = mContext.getSharedPreferences(mDepot, Context.MODE_PRIVATE);
        String setThumb = sp.getString(dc+dcItem+mContext.getString(R.string.dc_set_position),"");
        if (setThumb != "") {
            Glide.with(mContext).load(setThumb).crossFade().into(holder.ivSetThumb);
//            holder.ivSetThumb.setImageBitmap(BitmapFactory.decodeFile(setThumb));
        }
        String backThumb = sp.getString(dc+dcItem+mContext.getString(R.string.dc_back_position), "");
        if (backThumb != "") {
            Glide.with(mContext).load(backThumb).crossFade().into(holder.ivBackThumb);
//            holder.ivBackThumb.setImageBitmap(BitmapFactory.decodeFile(backThumb));
        }

        holder.tvDcItem.setTag(R.id.tvTurnoutPositionIndex, position);
        holder.tvDcItem.setTag(R.id.tvTurnoutParentContent, dc);
        holder.ibSetPosition.setTag(R.id.btSetPositionIndex, position);
        holder.ibSetPosition.setTag(R.id.btSetPositionContent, dc+dcItem);
        holder.ibBackPosition.setTag(R.id.btBackPositionIndex, position);
        holder.ibBackPosition.setTag(R.id.btBackPositionContent, dc+dcItem);
        holder.ivSetThumb.setTag(R.id.ivSetPositionIndex, position);
        holder.ivSetThumb.setTag(R.id.ivSetPositionContent, dcSetThumb);
        holder.ivBackThumb.setTag(R.id.ivBackPositionIndex, position);
        holder.ivBackThumb.setTag(R.id.ivBackPositionContent, dcBackThumb);

        /*
        if(mListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mListener.OnItemClick(holder.itemView, pos);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mListener.OnItemLongClick(holder.itemView, pos);
                    return true;
                }
            });
        }
        */
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
        Log.i(TAG, "Del position ->" + position);
//        mListDatas.remove(position);
        notifyItemRemoved(position);
//        notifyDataSetChanged();
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
//                tvDcItem.setText(item);
//                ibSetPosition.setTag(item);
//                ibBackPosition.setTag(item);
//                ivSetThumb.setTag(item);
//                ivBackThumb.setTag(item);
            }
        }
    }
}
