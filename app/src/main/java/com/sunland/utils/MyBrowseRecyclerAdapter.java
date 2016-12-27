package com.sunland.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import com.bumptech.glide.Glide;
import com.sunland.signalinspect.BrowseImageActivity;
import com.sunland.signalinspect.R;
import com.sunland.view.SquareCenterImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static android.view.View.GONE;
import static com.sunland.signalinspect.BrowseImageActivity.NORMAL_CHOICE;
import static com.sunland.signalinspect.BrowseImageActivity.actionMode;
import static com.sunland.signalinspect.BrowseImageActivity.mode;

/**
 * Created by Zero on 2016/10/04.
 */
public class MyBrowseRecyclerAdapter extends RecyclerView.Adapter<MyBrowseRecyclerAdapter.ViewHolder> {

    private static final String TAG = "wumin";
    private List<String> mListDatas = null;
    private SparseBooleanArray mChoiceDatas = new SparseBooleanArray();
    public  static Context mContext;
    public MyBrowseRecyclerAdapter(List<String> list, Context context) {
        this.mContext = context;
        this.mListDatas = list;
    }
    @Override
    public MyBrowseRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.action_browse_item, parent, false);
            return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyBrowseRecyclerAdapter.ViewHolder holder, final int position) {
        File photoFile = new File(mListDatas.get(position));
        Glide.with(MyBrowseRecyclerAdapter.mContext).load(photoFile).crossFade().thumbnail(0.3f).into(holder.mImage);
        holder.mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode != NORMAL_CHOICE) {
                    if (isItemChecked(position)) {
                        setItemChecked(position, false);
                    } else {
                        setItemChecked(position, true);
                    }
                    notifyItemChanged(position);
                    actionMode.setTitle("已选择" + getSelectedItem().size() + "项");
                } else {
                    int position = (int) v.getTag(R.id.ivBrowsePositionIndex);
                    String path = mListDatas.get(position);
                    Uri uri = Uri.fromFile(new File(path));
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "image/*");
                    mContext.startActivity(intent);
                }
            }
        });
        holder.mCheck.setChecked(isItemChecked(position));
        if(BrowseImageActivity.mode == BrowseImageActivity.MULTIPLE_CHOICE) {
            holder.mCheck.setVisibility(View.VISIBLE);
            holder.mCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isItemChecked(position)) {
                        setItemChecked(position, false);
                    } else {
                        setItemChecked(position, true);
                    }
                    actionMode.setTitle("已选择" + getSelectedItem().size() + "项");
                }
            });
        } else {
            holder.mCheck.setVisibility(GONE);
        }
        holder.mImage.setTag(R.id.ivBrowsePositionIndex,position);
    }

    @Override
    public int getItemCount() {
        return mListDatas  == null ? 0:mListDatas.size();
    }


    public void removeData(int position) {
        mListDatas.remove(mListDatas.get(position));
        notifyItemRemoved(position);
    }

    public ArrayList<String> getSelectedItem() {
        ArrayList<String> selectList = new ArrayList<>();
        for (int i = 0; i < mListDatas.size(); i++) {
            if (isItemChecked(i)) {
                selectList.add(mListDatas.get(i));
            }
        }
        return selectList;
    }
    public boolean isItemChecked(int position) {
        return mChoiceDatas.get(position);
    }

    public void setItemChecked(int position, boolean isChecked) {
        mChoiceDatas.put(position, isChecked);
    }
    static class ViewHolder extends RecyclerView.ViewHolder {

        private SquareCenterImageView mImage;
        private CheckBox mCheck;

        public ViewHolder(View itemView) {
            super(itemView);
            mImage = (SquareCenterImageView) itemView.findViewById(R.id.iv_action_browse);
            mCheck = (CheckBox) itemView.findViewById(R.id.action_browse_check_box);

        }

    }

}
